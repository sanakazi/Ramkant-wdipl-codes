package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.MergeAdapter;
import com.debalink.adapters.SubscriptionListAdapter;
import com.debalink.adapters.SubscriptionRequestAdapter;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SubscriptionListFragment extends Fragment {
	private ListView lvSubscriptionList;
	private ArrayList<SubscribersModel> subscriptionsList, requestSubscriptionsList;
	private CustomEditText edtSearch;
	private Button btnSearch;
	private ImageButton btnLoadMore;
	private int next = 0;
	private View footer;
	private MergeAdapter mergeAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_subscribers, null);
		lvSubscriptionList = (ListView) view.findViewById(R.id.list);
		edtSearch = (CustomEditText) view.findViewById(R.id.editText1);
		btnSearch = (Button) view.findViewById(R.id.button1);
		footer = getActivity().getLayoutInflater().inflate(R.layout.list_more_footer_view, null);

		subscriptionsList = new ArrayList<SubscribersModel>();
		edtSearch.setTypeface(Fonts.getBold(getActivity()));

		btnLoadMore = (ImageButton) footer.findViewById(R.id.button1);
		new Async(getActivity(), "Loading Subscribers List").execute();
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

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

		return view;
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;
		private ArrayList<SubscribersModel> currentList;

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
				cv.put("SubscriberSenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Next", next + "");
				res = hr.getSoapObjectFromServer(cv, "SelectSubscriptionList");
				currentList = hr.parseSubscribersListResponse(res);
				subscriptionsList.addAll(currentList);

				ContentValues cv1 = new ContentValues();
				cv1.put("SubscriberReceiverId", UserPreferences.getUserId(getActivity().getApplicationContext()));

				res = hr.getSoapObjectFromServer(cv1, "SubcriptionRequestList");
				requestSubscriptionsList = hr.parseSubscribersListResponse(res);

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
			if (currentList.size() == 5) {

				if (lvSubscriptionList.getFooterViewsCount() == 0)
					lvSubscriptionList.addFooterView(footer);
				Log.i("TAG", "Loading123");
			} else {
				lvSubscriptionList.removeFooterView(footer);
			}

			if (errorFlag == 0) {
				View v = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt1 = (TextView) v.findViewById(R.id.textView1);

				txt1.setTypeface(Fonts.getRobotoMedium(getActivity()));
				txt1.setText("SUBSCRIBERS REQUESTS");
				txt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

				View v2 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt2 = (TextView) v2.findViewById(R.id.textView1);

				txt2.setTypeface(Fonts.getRobotoMedium(getActivity()));
				txt2.setText("SUBSCRIPTIONS");
				txt2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

				mergeAdapter = new MergeAdapter();

				requestSubscriptionsList.size();
				if (requestSubscriptionsList != null && requestSubscriptionsList.size() > 0) {
					mergeAdapter.addView(v);
					mergeAdapter.addAdapter(new SubscriptionRequestAdapter(getActivity(), SubscriptionListFragment.this,
							requestSubscriptionsList));
				}
				
				if (subscriptionsList != null && subscriptionsList.size() > 0) {
					mergeAdapter.addView(v2);
					mergeAdapter.addAdapter(new SubscriptionListAdapter(getActivity(), subscriptionsList));
				}
				lvSubscriptionList.setAdapter(mergeAdapter);

			}

		}

	}

	private class Search extends AsynDownloader {
		public Search(Context ctx, String message, String searchText) {
			super(ctx, message);
			this.searchText = searchText;
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
				cv.put("SubscriberSenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Search", searchText);
				res = hr.getSoapObjectFromServer(cv, "SelectSubscriptionSearch");
				subscriptionsList = hr.parseSubscribersListResponse(res);

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
				TextView txt1 = new TextView(getActivity());
				txt1.setText("Requests");
				txt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);

				TextView txt2 = new TextView(getActivity());
				txt2.setText("Subscriptions");
				txt2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);

				// lvSubscriptionList.setAdapter(new
				// SubscriptionListAdapter(getActivity(), subscriptionsList));

				mergeAdapter = new MergeAdapter();
				if (requestSubscriptionsList != null && requestSubscriptionsList.size() > 0) {
					mergeAdapter.addView(txt1);
					mergeAdapter.addAdapter(new SubscriptionRequestAdapter(getActivity(), SubscriptionListFragment.this,
							requestSubscriptionsList));
				}
				if (subscriptionsList != null && subscriptionsList.size() > 0) {
					mergeAdapter.addView(txt2);
					mergeAdapter.addAdapter(new SubscriptionListAdapter(getActivity(), subscriptionsList));
				}
				lvSubscriptionList.setAdapter(mergeAdapter);
				
				
				if (subscriptionsList.size() == 5) {

					if (lvSubscriptionList.getFooterViewsCount() == 0)
						lvSubscriptionList.addFooterView(footer);
					Log.i("TAG", "Loading123");
				} else {
					if (lvSubscriptionList.getFooterViewsCount() == 1)
					lvSubscriptionList.removeFooterView(footer);
				}
				
				next=0;
			}
		}

	}

	public void refreshList() {
		new RefreshList(getActivity(), "Loading Subscribers List").execute();
	}

	private class RefreshList extends AsynDownloader {
		public RefreshList(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;
		//private ArrayList<SubscribersModel> currentList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			next++;
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv1 = new ContentValues();
				cv1.put("SubscriberReceiverId", UserPreferences.getUserId(getActivity().getApplicationContext()));

				res = hr.getSoapObjectFromServer(cv1, "SubcriptionRequestList");
				requestSubscriptionsList = hr.parseSubscribersListResponse(res);

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
				TextView txt1 = new TextView(getActivity());
				txt1.setText("Requests");
				txt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);

				TextView txt2 = new TextView(getActivity());
				txt2.setText("Subscriptions");
				txt2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);

				// lvSubscriptionList.setAdapter(new
				// SubscriptionListAdapter(getActivity(), subscriptionsList));

				mergeAdapter = new MergeAdapter();
				if (requestSubscriptionsList != null && requestSubscriptionsList.size() > 0) {
					mergeAdapter.addView(txt1);
					mergeAdapter.addAdapter(new SubscriptionRequestAdapter(getActivity(), SubscriptionListFragment.this,
							requestSubscriptionsList));
				}
				if (subscriptionsList != null && subscriptionsList.size() > 0) {
					mergeAdapter.addView(txt2);
					mergeAdapter.addAdapter(new SubscriptionListAdapter(getActivity(), subscriptionsList));
				}
				lvSubscriptionList.setAdapter(mergeAdapter);

			}

		}

	}

}
