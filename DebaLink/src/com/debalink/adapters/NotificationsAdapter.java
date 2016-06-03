package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debalink.DiscussionDetailsFragment;
import com.debalink.PublicationDetailsFragment;
import com.debalink.R;
import com.debalink.models.NotificationModel;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class NotificationsAdapter extends ArrayAdapter<NotificationModel> {

	private ArrayList<NotificationModel> notificationsList;
	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth, bigImageHeight, bigImageWidth;

	// ImageButton imgButton;

	public NotificationsAdapter(FragmentActivity activity, ArrayList<NotificationModel> notificationsList) {
		super(activity, R.layout.list_discussion_row, notificationsList);
		this.notificationsList = notificationsList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = width / 5-plus;
		bigImageHeight = width / 2 - plus;
		bigImageWidth = width / 2 - plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);
		

	}

	public int getCount() {
		return notificationsList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		NotificationModel planet = notificationsList.get(position);
		ViewHolder holder;

		

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.notification_list_row, null);
			holder=new ViewHolder();
			holder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			holder.txtType = (TextView) convertView.findViewById(R.id.txtType);
			holder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			holder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
			holder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);
			holder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
			holder.softImgView = new WeakReference<ImageView>(holder.profilePic);
			convertView.setTag(holder);

		}

		else {
			holder = (ViewHolder) convertView.getTag();

			
		}

		holder.txtUserName.setText(planet.getUserName());
		holder.txtType.setText(planet.getType1());
		
		holder.txtDateTime.setText(planet.getCreateDate());
		holder.txtDesc.setText(planet.getDocumentTitle());

		holder.softImgView.get().setTag(planet.getProfilePic());
		
		
		holder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
		holder.txtType.setTypeface(Fonts.getRobotoRegular(activity));
		holder.txtDateTime.setTypeface(Fonts.getRobotoRegular(activity));
		holder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));

		try {
			if (planet.getProfilePic() != null)
				imageDownloader.displayImage(planet.getProfilePic(), holder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		

		convertView.setOnClickListener(new OnProfileClickListener(position));

		return convertView;

	}

	private class OnProfileClickListener implements OnClickListener {
		private int position;

		OnProfileClickListener(int position) {
			this.position = position;
			
			

		}

		@Override
		public void onClick(View v) {
			if(notificationsList.get(position).getPubOrDis().equalsIgnoreCase("p")){
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment publicationFragment = new PublicationDetailsFragment();
				ft.add(R.id.inner_content, publicationFragment, "publication_details");
				ft.addToBackStack(null);
				Bundle bundle=new Bundle();
				bundle.putString("publication_id",notificationsList.get(position).getDocumentId());
				//bundle.putString("no_of_comments",notificationsList.get(position).getCommentCnt());
				bundle.putString("user_id",notificationsList.get(position).getUserId());
				publicationFragment.setArguments(bundle);
				ft.commit();
			}else if(notificationsList.get(position).getPubOrDis().equalsIgnoreCase("d")){
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment discussionFragment = new DiscussionDetailsFragment();
				ft.add(R.id.inner_content, discussionFragment, "discussion_details");
				ft.addToBackStack(null);
				Bundle bundle=new Bundle();
				bundle.putString("discussion_id",notificationsList.get(position).getDocumentId());
				//bundle.putString("no_of_comments",notificationsList.get(position).getCommentCnt());
				discussionFragment.setArguments(bundle);
				ft.commit();
			} 
		}
	}

	class ViewHolder {

		TextView txtUserName, txtDesc, txtDateTime,txtType;
		ImageView profilePic, displayPic;
		WeakReference<ImageView> softImgView, softDisplayPic;
	}
}
