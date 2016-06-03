package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import com.debalink.R;
import com.debalink.models.CommentModel;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentsAdapter extends ArrayAdapter<CommentModel> {
	private ArrayList<CommentModel> headlinesList;

	private LayoutInflater inflater;
	Context context;
	private Activity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public CommentsAdapter(Activity activity, ArrayList<CommentModel> headlinesList) {

		super(activity, R.layout.list_comment_row, headlinesList);
		this.headlinesList = headlinesList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.10));
		System.out.println("plus " + plus);
		imageWidth = (width / 5) - plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return headlinesList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		CommentModel planet = (CommentModel) this.getItem(position);
		PlanetViewHolder viewHolder;
		if (convertView == null) {

			viewHolder = new PlanetViewHolder();
			convertView = inflater.inflate(R.layout.list_comment_row, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			viewHolder.txtComment = (TextView) convertView.findViewById(R.id.txtComment);

			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);

			viewHolder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

			viewHolder.softImgView = new WeakReference<ImageView>(viewHolder.profilePic);

			convertView.setTag(viewHolder);

		}

		else {
			viewHolder = (PlanetViewHolder) convertView.getTag();

		}

		viewHolder.txtUserName.setText(Fonts.toUpper(planet.getUserName()));
		viewHolder.txtDateTime.setText(planet.getCreatedDate());
		viewHolder.txtComment.setText(planet.getDescription());

		viewHolder.softImgView.get().setTag(planet.getProfilePic());

		try {
			if (planet.getProfilePic() != null)
				imageDownloader.displayImage(planet.getProfilePic(), viewHolder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));

		viewHolder.txtUserName.setIncludeFontPadding(false);
		viewHolder.txtComment.setIncludeFontPadding(false);
		viewHolder.txtDateTime.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtComment.setTypeface(Fonts.getRobotoRegular(activity));
		
		return convertView;
	}

	

	class PlanetViewHolder {
		TextView txtUserName, txtComment, txtDateTime;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
