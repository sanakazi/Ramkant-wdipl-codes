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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.models.UserModel;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class RecommendUserListAdapter extends ArrayAdapter<UserModel> {
	private ArrayList<UserModel> userList;

	private LayoutInflater inflater;
	Context context;
	private Activity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public RecommendUserListAdapter(Activity activity, ArrayList<UserModel> userList) {

		super(activity, R.layout.recommend_user_list_row, userList);
		this.userList = userList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.10));
		System.out.println("plus " + plus);
		imageWidth = width / 5 - plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return userList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public static class ViewHolder

	{
		TextView txtUserName;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
		CheckBox chkChecked;
		boolean isChecked;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final UserModel planet = (UserModel) this.getItem(position);

		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.recommend_user_list_row, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);
			viewHolder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
			viewHolder.chkChecked = (CheckBox) convertView.findViewById(R.id.chkChecked);
			viewHolder.softImgView = new WeakReference<ImageView>(viewHolder.profilePic);

			/*
			 * viewHolder.chkChecked.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onCheckedChanged(CompoundButton buttonView,
			 * boolean isChecked) {
			 * userList.get(position).setChecked(isChecked); }
			 * 
			 * @Override public void onClick(View v) {
			 * planet.setChecked(viewHolder.chkChecked.isChecked()); } });
			 */

			

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtUserName.setText(planet.getUserName());

		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));

		viewHolder.softImgView.get().setTag(planet.getUserProfilePic());
		try {
			if (planet.getUserProfilePic() != null)
				imageDownloader.displayImage(planet.getUserProfilePic(), viewHolder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		viewHolder.chkChecked.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				userList.get(position).setChecked(cb.isChecked());
			}
		});
		

		viewHolder.chkChecked.setChecked(userList.get(position).isChecked());

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
