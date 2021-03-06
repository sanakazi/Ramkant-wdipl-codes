package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.SubscribersListAdapter;
import com.debalink.customviews.CustomEditText;
import com.debalink.customviews.DrawableClickListener;
import com.debalink.models.SubscribersModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class SubscribersListFragment extends Fragment implements OnScrollListener {
	private ListView lvSubscribersList;
	private ArrayList<SubscribersModel> subscribersList;
	private CustomEditText edtSearch;
	private Button btnSearch ;
	private ImageButton btnLoadMore;
	private int next = 0;
	private View footer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_subscribers, null);
		lvSubscribersList = (ListView) view.findViewById(R.id.list);
		edtSearch = (CustomEditText) view.findViewById(R.id.editText1);
		btnSearch = (Button) view.findViewById(R.id.button1);
		edtSearch.setTypeface(Fonts.getBold(getActivity()));
		footer = getActivity().getLayoutInflater().inflate(R.layout.list_more_footer_view, null);

		subscribersList = new ArrayList<SubscribersModel>();

		btnLoadMore = (ImageButton) footer.findViewById(R.id.button1);

		new Async(getActivity(), "Loading Subscribers List").execute();
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (edtSearch.getText().toString().length() > 0) {
					new Search(getActivity(), "Loading Search List", edtSearch.getText().toString()).execute();
				} else {
					subscribersList.clear();
					new Async(getActivity(), "Loading Subscribers List").execute();
				}
			}
		});

		btnLoadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Async(getActivity(), "Loading Subscribers List").execute();
			}
		});

		edtSearch.setDrawableClickListener(new DrawableClickListener() {

			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:
					if (edtSearch.getText().toString().length() > 0) {
						new Search(getActivity(), "Loading Search List", edtSearch.getText().toString()).execute();
					} else {
						new Async(getActivity(), "Loading Subscription List").execute();
					}
					break;
				default:
					break;
				}
			}

		});

		lvSubscribersList.setOnScrollListener(this);

		return view;
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message) {
			super(ctx, message);

		}

		private ArrayList<SubscribersModel> currentList;
		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {

			next++;
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("SubscriberId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Next", next + "");

				res = hr.getSoapObjectFromServer(cv, "SelectSubscriberList");
				currentList = hr.parseSubscribersListResponse(res);

				subscribersList.addAll(currentList);

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (currentList.size() == 5) {

				if (lvSubscribersList.getFooterViewsCount() == 0)
					lvSubscribersList.addFooterView(footer);
				Log.i("TAG", "Loading123");
			} else {
				lvSubscribersList.removeFooterView(footer);
			}

			if (errorFlag == 0)
				lvSubscribersList.setAdapter(new SubscribersListAdapter(getActivity(), subscribersList));

		}

	}

	private class Search extends AsynDownloader {
		public Search(Context ctx, String message, String searchText) {
			super(ctx, message);
			this.searchText = searchText;
			lvSubscribersList.removeFooterView(footer);
		}

		private SoapObject res;
		private int errorFlag = 0;
		private String searchText;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();

				cv.put("SubscriberId", UserPreferences.getUserId(getActivity().getApplicationContext()));//
				cv.put("Search", searchText);

				res = hr.getSoapObjectFromServer(cv, "SelectSubscriberSearch");
				subscribersList = hr.parseSubscribersListResponse(res);

				Log.i("SelectAllHeadlinesForDashboard", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				lvSubscribersList.setAdapter(new SubscribersListAdapter(getActivity(), subscribersList));
				next = 0;
			}
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// leave this empty
	}

	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (listView.getLastVisiblePosition() >= listView.getCount() - 1) {
				Log.i("TAG", "Loading");
			}
		}
	}
}
