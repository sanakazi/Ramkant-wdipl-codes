package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.FeedsAdapter;
import com.debalink.models.FeedsModel;
import com.debalink.utils.AsynDownloader;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class FeedsFragment extends Fragment {
	private ListView lvFeeds;
	private ArrayList<FeedsModel> currentList, feedsList;
	private int next = 0;
	private View footer;
	private ImageButton btnLoadMore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, null);
		lvFeeds = (ListView) view.findViewById(R.id.list);
		footer = getActivity().getLayoutInflater().inflate(R.layout.list_more_footer_view, null);

		feedsList = new ArrayList<FeedsModel>();
		btnLoadMore = (ImageButton) footer.findViewById(R.id.button1);
		new Async(getActivity(), "Loading Feeds").execute();
		btnLoadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Async(getActivity(), "Loading Feeds").execute();
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
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Next", next + "");
				res = hr.getSoapObjectFromServer(cv, "SelectAllFeedUserWise");
				currentList = hr.parseFeedsResponse(res);
				feedsList.addAll(currentList);

				Log.i("SelectAllFeedUserWise", res.toString());

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
				if (currentList.size() == 5) {

					if (lvFeeds.getFooterViewsCount() == 0)
						lvFeeds.addFooterView(footer);
					Log.i("TAG", "Loading123");
				} else {
					lvFeeds.removeFooterView(footer);
				}
				
				
				lvFeeds.setAdapter(new FeedsAdapter(getActivity(), feedsList));
				lvFeeds.setSelection(feedsList.size()-currentList.size());
			}

		}

	}

}
