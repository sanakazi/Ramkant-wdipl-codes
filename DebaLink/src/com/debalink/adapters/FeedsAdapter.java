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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.models.FeedsModel;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class FeedsAdapter extends ArrayAdapter<FeedsModel> {
	private static final int PROFILEPIC = 1, COVERPHOTO = 2, HEADLINE = 0, DISCUSSION = 4, PUBLICATION = 5, OTHER = 1;
	private static int VIEW_TYPE_COUNT = 2;
	private ArrayList<FeedsModel> feedsList;
	private LayoutInflater inflater;
	Context context;
	private Activity activity;
	private ImageThreadDownloader imageDownloader, bigImageDownloader;
	int imageHeight, imageWidth, bigImageHeight, bigImageWidth;

	// ImageButton imgButton;

	public FeedsAdapter(Activity activity, ArrayList<FeedsModel> feedsList) {
		super(activity, R.layout.list_discussion_row, feedsList);
		this.feedsList = feedsList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.15));
		int plus2= (int) (width * (0.05));
		
		System.out.println("plus " + plus);
		imageWidth = width / 5-plus2;
		bigImageHeight = width / 2 - plus;
		bigImageWidth = width / 2 - plus;
		
		

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);
		bigImageDownloader = new ImageThreadDownloader(activity, activity, width-plus, bigImageHeight);

	}

	public int getCount() {
		return feedsList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
	

		if (feedsList.get(position).getType().equalsIgnoreCase("Headline")) {
			return HEADLINE;
		} else {
			return OTHER;
		}

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		FeedsModel planet = feedsList.get(position);
		ViewHolder holder;

		int viewType = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.feeds_profile_pic, null);

			switch (viewType) {
			case HEADLINE:
				convertView = inflater.inflate(R.layout.feeds_headline_row, null);

				break;
			case OTHER:
				holder.displayPic = (ImageView) convertView.findViewById(R.id.imgDisplayPic);
				//holder.displayPic.setLayoutParams(new LinearLayout.LayoutParams(bigImageWidth, bigImageHeight));
				holder.softDisplayPic = new WeakReference<ImageView>(holder.displayPic);
				

			}

			holder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			holder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			holder.txtDesc = (TextView) convertView.findViewById(R.id.txtHeadlineDesc);
			holder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);
			holder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
			holder.softImgView = new WeakReference<ImageView>(holder.profilePic);

			convertView.setTag(holder);

		}

		else {
			holder = (ViewHolder) convertView.getTag();

		}

		holder.txtUserName.setText(planet.getName());
		holder.txtDesc.setText(planet.getAction() + " On " + planet.getCreateDate());
		holder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
		holder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));
		holder.softImgView.get().setTag(planet.getProfilePic());

		try {
			if (planet.getProfilePic() != null)
				imageDownloader.displayImage(planet.getProfilePic(), holder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (viewType == OTHER) {
			String url=getFullUrl(position,planet.getDisplayImageUrl());
			holder.softDisplayPic.get().setTag(url);

			try {
				if (url!= null)
					bigImageDownloader.displayImage(url, holder.softDisplayPic);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		}
	}

	class ViewHolder {

		TextView txtUserName, txtDesc, txtDateTime;
		ImageView profilePic, displayPic;
		WeakReference<ImageView> softImgView, softDisplayPic;
	}

	private String getFullUrl(int position, String imgUrl) {
		
		if (feedsList.get(position).getType().equalsIgnoreCase("ProfilePic")) {
			
			return Contant.PIC_URL + imgUrl;
			
		} else if (feedsList.get(position).getType().equalsIgnoreCase("CoverPhoto")) {
			
			return Contant.COVER_PIC_URL + imgUrl;
			
		} else if (feedsList.get(position).getType().equalsIgnoreCase("Headline")) {
			
			return "";
			
		} else if (feedsList.get(position).getType().equalsIgnoreCase("Publication")) {
			
			return Contant.PUBLICATION_PIC_URL + imgUrl;
			
		} else if (feedsList.get(position).getType().equalsIgnoreCase("Discussion")) {
			
			return Contant.DISCUSSION_PIC_URL + imgUrl;
			
		} else {
			return "";
		}
	}
}
