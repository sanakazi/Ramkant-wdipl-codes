package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import com.debalink.adapters.NotificationsAdapter;
import com.debalink.models.NotificationModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class NotificationFragment extends Fragment {
	private ListView lvSubscribersList;
	private ArrayList<NotificationModel> notificationsList;
	private ImageButton  btnLoadMore;
	//private int next = 0;
	private View footer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, null);
		lvSubscribersList = (ListView) view.findViewById(R.id.list);
		//edtSearch = (EditText) view.findViewById(R.id.editText1);
		//btnSearch = (Button) view.findViewById(R.id.button1);

		footer = getActivity().getLayoutInflater().inflate(R.layout.list_more_footer_view, null);

		notificationsList = new ArrayList<NotificationModel>();

		btnLoadMore = (ImageButton) footer.findViewById(R.id.button1);

		new Async(getActivity(), "Loading Notifications List").execute();
		

		btnLoadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncSeeAll(getActivity(), "Loading Notifications List").execute();
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


			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				

				res = hr.getSoapObjectFromServer(cv, "FillNotification");
				notificationsList = hr.parseNotificationResponse(res);

			

				

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

			//if (currentList.size() == 5) {

				if (lvSubscribersList.getFooterViewsCount() == 0)
					lvSubscribersList.addFooterView(footer);
				Log.i("TAG", "Loading123");
			//} else {
			//}

			if (errorFlag == 0){
				lvSubscribersList.setAdapter(new NotificationsAdapter(getActivity(), notificationsList));
				//lvSubscribersList.addFooterView(footer);
			}

		}

	}
	
	private class AsyncSeeAll extends AsynDownloader {
		public AsyncSeeAll(Context ctx, String message) {
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

		//	next++;
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));

				res = hr.getSoapObjectFromServer(cv, "SelectSeeAllNotificationUserWise");
				notificationsList = hr.parseNotificationResponse(res);


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


			if (errorFlag == 0){   
				lvSubscribersList.setAdapter(new NotificationsAdapter(getActivity(), notificationsList));
				lvSubscribersList.removeFooterView(footer);
			}

		}

	}

/*	private class Search extends AsynDownloader {
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

				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));//
				

				res = hr.getSoapObjectFromServer(cv, "SelectTop10NotificationbyUserWise");
				subscribersList = hr.parseSubscribersListResponse(res);

				Log.i("SelectTop10NotificationbyUserWise", res.toString());

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
	}*/
}
