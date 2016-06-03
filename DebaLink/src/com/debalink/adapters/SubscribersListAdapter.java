package com.debalink.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.R;
import com.debalink.models.SubscribersModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SubscribersListAdapter extends ArrayAdapter<SubscribersModel> {
	private ArrayList<SubscribersModel> subscribersList;

	private LayoutInflater inflater;
	Context context;
	private Activity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public SubscribersListAdapter(Activity activity, ArrayList<SubscribersModel> subscribersList) {

		super(activity, R.layout.list_subscriber_row, subscribersList);
		this.subscribersList = subscribersList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = width / 5 - plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return subscribersList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public static class ViewHolder

	{
		TextView txtUserName;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
		ImageButton btnSubscribe;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		SubscribersModel planet = (SubscribersModel) this.getItem(position);

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_subscriber_row, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);
			viewHolder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
			viewHolder.btnSubscribe = (ImageButton) convertView.findViewById(R.id.button1);
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
		viewHolder.btnSubscribe.setOnClickListener(new OnSubcribeClickListener(position));
		return convertView;

	}

	private class OnSubcribeClickListener implements OnClickListener {
		private int position;

		OnSubcribeClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			new Async(activity, "Subscribing", position).execute();
		}
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, int position) {
			super(ctx, message);
			this.position = position;
		}

		private String res = null;
		private int errorFlag = 0, position;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("SubscriberSenderId", UserPreferences.getUserId(context));
				cv.put("SubscriberSenderName", UserPreferences.getUserName(context));
				cv.put("SubscriberSenderEmail", UserPreferences.getEmail(context));
				cv.put("SubscriberReceiverId", subscribersList.get(position).getUserId());
				cv.put("SubscriberReceiverName", subscribersList.get(position).getUserName());
				cv.put("SubscriberReceiverEmail", subscribersList.get(position).getEmailId());//

				res = hr.getDataFromServer(cv, "InsertSubscribeUser");
				// subscribersList = hr.parseSubscribersListResponse(res);

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
			return res;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0 && res != null && res.equals("0")) {
				showMessage("Request sent successffuly");
			} else if (errorFlag == 0 && res != null && res.equals("-2")) {
				showMessage("Error");
			}

		}

	}

	private void showMessage(String message) {
		View view = activity.getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView txtMessage = (TextView) view.findViewById(R.id.textView1);
		txtMessage.setTypeface(Fonts.getBold(activity));

		txtMessage.setText(message);
		Toast toast = new Toast(activity);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}
}
