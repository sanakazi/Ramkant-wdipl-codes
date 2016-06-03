
package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.models.OwnHeadlineModel;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class OwnHeadlineAdapter extends ArrayAdapter<OwnHeadlineModel> {
	private ArrayList<OwnHeadlineModel> headlinesList;

	private LayoutInflater inflater;
	Context context;
	private Activity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public OwnHeadlineAdapter(Activity activity, ArrayList<OwnHeadlineModel> headlinesList) {

		super(activity, R.layout.list_headline_row, headlinesList);
		this.headlinesList = headlinesList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = (width / 5)-plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return headlinesList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public static class ViewHolder

	{

		public TextView txtName, txtType, txtRating, txtImdb, txtRottenTomato, txtDesc;
		public ImageView movieImage;

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		OwnHeadlineModel planet = (OwnHeadlineModel) this.getItem(position);
		PlanetViewHolder viewHolder;
		if (convertView == null) {

			viewHolder = new PlanetViewHolder();
			convertView = inflater.inflate(R.layout.own_headline_list_row, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtText);
			
			convertView.setTag(viewHolder);

		}

		else {
			viewHolder = (PlanetViewHolder) convertView.getTag();

		}
		viewHolder.txtUserName.setText(planet.getName());
		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
		

		return convertView;

	}

	
	class PlanetViewHolder {
		TextView txtUserName, txtDesc, txtDateTime;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;

	}

}
