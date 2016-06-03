
package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
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
import com.debalink.models.SubscribersModel;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class SubscriptionListAdapter extends ArrayAdapter<SubscribersModel> {
	private ArrayList<SubscribersModel> subscriptionList;

	private LayoutInflater inflater;
	Context context;
	private Activity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public SubscriptionListAdapter(Activity activity, ArrayList<SubscribersModel> subscriptionList) {

		super(activity, R.layout.list_subscriber_row, subscriptionList);
		this.subscriptionList = subscriptionList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

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
		
		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
		
		viewHolder.softImgView.get().setTag(planet.getProfilePicUrl());
		try {
			if (planet.getProfilePicUrl() != null)
				imageDownloader.displayImage(planet.getProfilePicUrl(), viewHolder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewHolder.btnSubscribe.setVisibility(View.INVISIBLE);
		viewHolder.btnSubscribe.setOnClickListener(new OnProfileClickListener(position));
		return convertView;

	}

	private class OnProfileClickListener implements OnClickListener {
		private int position;

		OnProfileClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {

		}
	}
}
