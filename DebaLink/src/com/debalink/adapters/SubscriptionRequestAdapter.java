package com.debalink.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.SubscriptionListFragment;
import com.debalink.models.SubscribersModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class SubscriptionRequestAdapter extends ArrayAdapter<SubscribersModel> {
	private ArrayList<SubscribersModel> subscriptionList;
	private LayoutInflater inflater;
	private Context context;
	private ImageThreadDownloader imageDownloader;
	private int imageHeight, imageWidth;
	private Activity activity;
	private SubscriptionListFragment frg;
	
	
	public SubscriptionRequestAdapter(Activity activity,SubscriptionListFragment frg, ArrayList<SubscribersModel> subscriptionList) {

		super(activity, R.layout.list_subscriber_row, subscriptionList);
		this.subscriptionList = subscriptionList;
		context = activity.getApplicationContext();
		this.activity=activity;
		this.frg=frg;
		inflater = LayoutInflater.from(context);

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = width / 5-plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return subscriptionList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public static class ViewHolder

	{
		TextView txtUserName;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
		ImageButton btnAccept, btnReject;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		SubscribersModel planet = (SubscribersModel) this.getItem(position);

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.subscription_request_row, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);
			viewHolder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
			viewHolder.btnAccept = (ImageButton) convertView.findViewById(R.id.btnAccept);
			viewHolder.btnReject = (ImageButton) convertView.findViewById(R.id.btnReject);

			viewHolder.softImgView = new WeakReference<ImageView>(viewHolder.profilePic);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtUserName.setText(planet.getName());
		viewHolder.softImgView.get().setTag(planet.getProfilePicUrl());
		try {
			if (planet.getProfilePicUrl() != null)
				imageDownloader.displayImage(planet.getProfilePicUrl(), viewHolder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
		viewHolder.btnAccept.setOnClickListener(new OnAcceptClickListener(position));
		viewHolder.btnReject.setOnClickListener(new OnRejectClickListener(position));
		return convertView;

	}

	private class OnAcceptClickListener implements OnClickListener {
		private int position;

		OnAcceptClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
			new Async(activity,"Loading",subscriptionList.get(position).getUserId(),"AC").execute();
		}
	}

	private class OnRejectClickListener implements OnClickListener {
		private int position;

		OnRejectClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
			new Async(activity,"Loading",subscriptionList.get(position).getUserId(),"RC").execute();
		}
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message,String senderId,String status) {
			super(ctx, message);
			this.senderId=senderId;
			this.status=status;
		}

		private String res;
		private int errorFlag = 0;
		private String senderId,status;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("SubscriberReceiverId", UserPreferences.getUserId(activity));
				cv.put("SubscriberSenderId",senderId);
				cv.put("Type",status);
				
				res = hr.getDataFromServer(cv, "FriendRequestStatus");
				

				Log.i("FriendRequestAccepted", res.toString());

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
				frg.refreshList();
			}
		}

	}
}
