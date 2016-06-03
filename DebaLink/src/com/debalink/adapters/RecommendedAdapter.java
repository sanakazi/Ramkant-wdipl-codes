package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.TextView;
import com.debalink.DiscussionDetailsFragment;
import com.debalink.PublicationDetailsFragment;
import com.debalink.R;
import com.debalink.models.RecommendedModel;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class RecommendedAdapter extends ArrayAdapter<RecommendedModel> {
	private static final int HEADLINE = 0, DISCUSSION = 1;
	private static int VIEW_TYPE_COUNT = 2;
	private ArrayList<RecommendedModel> recommendedList;
	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader, bigImageDownloader;
	int imageHeight, imageWidth, bigImageHeight, bigImageWidth;

	// ImageButton imgButton;

	public RecommendedAdapter(FragmentActivity activity, ArrayList<RecommendedModel> recommendedList) {
		super(activity, R.layout.list_discussion_row, recommendedList);
		this.recommendedList = recommendedList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.15));
		System.out.println("plus " + plus);
		imageWidth = width / 5;
		bigImageHeight = width / 2 - plus;
		bigImageWidth = width / 2 - plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);
		bigImageDownloader = new ImageThreadDownloader(activity, activity, bigImageWidth, bigImageHeight);

	}

	public int getCount() {
		return recommendedList.size();
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

		if (recommendedList.get(position).getType().equalsIgnoreCase("Headline")) {
			return HEADLINE;
		} else {
			return DISCUSSION;
		}

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RecommendedModel planet = recommendedList.get(position);
		ViewHolder viewHolder;

		int viewType = getItemViewType(position);

		if (convertView == null) {
			viewHolder = new ViewHolder();

			switch (viewType) {
			case HEADLINE:
				convertView = inflater.inflate(R.layout.list_recommended_headline_row, null);
				viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtText);
				viewHolder.txtAddedBy = (TextView) convertView.findViewById(R.id.txtAddedBy);
				viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtAddedByName);
				viewHolder.txtSeeMore = (TextView) convertView.findViewById(R.id.txtSeeMore);

				break;
			case DISCUSSION:
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.list_recommended_discussion_row, null);
				viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtAddedByName);
				viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
				viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
				viewHolder.txtAddedBy = (TextView) convertView.findViewById(R.id.txtAddedBy);
				viewHolder.txtNoOfComments = (TextView) convertView.findViewById(R.id.txtNoOfComments);
				viewHolder.txtViews = (TextView) convertView.findViewById(R.id.txtViews);
				viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);
				viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imgDisplayPic);
				viewHolder.txtSeeMore = (TextView) convertView.findViewById(R.id.txtSeeMore);
				viewHolder.softImgView = new WeakReference<ImageView>(viewHolder.profilePic);

			}

			convertView.setTag(viewHolder);

		}

		else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		if (recommendedList.get(position).getType().equalsIgnoreCase("discussion")) {

			viewHolder.txtUserName.setText(planet.getSenderName());
			viewHolder.txtDateTime.setText(planet.getCreatedDate());
			viewHolder.txtDesc.setText(planet.getDiscussionTitle());
			viewHolder.txtAddedBy.setText("Discussion Recommended By ");
			viewHolder.softImgView.get().setTag(planet.getDisplayImageUrl());
			// viewHolder.txtViews.setText(planet.getNoOfView());
			// viewHolder.txtNoOfComments.setText(planet.getCommentCnt());
			viewHolder.txtCategory.setText(planet.getCategoryName());
			try {
				if (planet.getDisplayImageUrl() != null)
					imageDownloader.displayImage(planet.getDisplayImageUrl(), viewHolder.softImgView);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			// viewHolder.txtUserName.setTypeface(Fonts.getFranklingGothicHeavy(activity));
			viewHolder.txtDateTime.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
			viewHolder.txtAddedBy.setTypeface(Fonts.getRobotoRegular(activity));

			viewHolder.txtViews.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtCategory.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtNoOfComments.setTypeface(Fonts.getRobotoRegular(activity));
			// viewHolder.txtSeeMore.setTypeface(Fonts.getRobotoRegular(activity));
			// viewHolder.txtSeeMore.setText("See More");
			convertView.setOnClickListener(new OnProfileClickListener(position));

		} else if (recommendedList.get(position).getType().equalsIgnoreCase("publication")) {

			viewHolder.txtUserName.setText(planet.getSenderName());
			viewHolder.txtDateTime.setText(planet.getCreatedDate());
			viewHolder.txtDesc.setText(planet.getPublicationTitle());
			viewHolder.txtAddedBy.setText("Publication Recommended By ");
			viewHolder.softImgView.get().setTag(planet.getDisplayImageUrl());

			viewHolder.txtCategory.setText(planet.getCategoryName());
			try {
				if (planet.getDisplayImageUrl() != null)
					imageDownloader.displayImage(planet.getDisplayImageUrl(), viewHolder.softImgView);
			} catch (Exception e) {
				e.printStackTrace();
			}

			viewHolder.txtDateTime.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
			viewHolder.txtAddedBy.setTypeface(Fonts.getRobotoRegular(activity));

			viewHolder.txtViews.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtCategory.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtNoOfComments.setTypeface(Fonts.getRobotoRegular(activity));

			convertView.setOnClickListener(new OnProfileClickListener(position));

		}

		else if (viewType == HEADLINE) {
			viewHolder.txtDesc.setText(planet.getDescription());
			viewHolder.txtAddedBy.setText("Headline Recommended By ");
			viewHolder.txtUserName.setText(planet.getSenderName());
			viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
			viewHolder.txtAddedBy.setTypeface(Fonts.getRobotoMedium(activity));
			viewHolder.txtSeeMore.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));
			// viewHolder.txtDateTime.setText(getDateFormated(planet.getCreatedDate()));
			// viewHolder.txtSeeMore.setText("See More");
		}

		return convertView;

	}

	private class OnProfileClickListener implements OnClickListener {
		private int position;

		OnProfileClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
			if (recommendedList.get(position).getType().equalsIgnoreCase("discussion")) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment discussionFragment = new DiscussionDetailsFragment();

				ft.add(R.id.rl, discussionFragment, "discussion_details");

				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("discussion_id", recommendedList.get(position).getDiscussionId());
				// bundle.putString("no_of_comments",
				// recommendedList.get(position).getCommentCnt());
				bundle.putString("from", "discussion");
				discussionFragment.setArguments(bundle);
				ft.show(discussionFragment);

				ft.commit();

			} else if (recommendedList.get(position).getType().equalsIgnoreCase("publication")) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment publicationFragment = new PublicationDetailsFragment();
				ft.add(R.id.rl, publicationFragment, "publication_details");

				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("publication_id", recommendedList.get(position).getPublicationId());
				// bundle.putString("no_of_comments",
				// recommendedList.get(position).getCommentCnt());
				bundle.putString("user_id", recommendedList.get(position).getUserId());
				bundle.putString("from", "publication");
				publicationFragment.setArguments(bundle);
				ft.commit();
			}

		}
	}

	class ViewHolder {

		TextView txtUserName, txtDesc, txtDateTime, txtViews, txtNoOfComments, txtAddedBy, txtCategory, txtSeeMore;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
	}

	private String getFullUrl(int position, String imgUrl) {

		if (recommendedList.get(position).getType().equalsIgnoreCase("ProfilePic")) {

			return Contant.PIC_URL + imgUrl;

		} else if (recommendedList.get(position).getType().equalsIgnoreCase("CoverPhoto")) {

			return Contant.COVER_PIC_URL + imgUrl;

		} else if (recommendedList.get(position).getType().equalsIgnoreCase("Headline")) {

			return "";

		} else if (recommendedList.get(position).getType().equalsIgnoreCase("Publication")) {

			return Contant.PUBLICATION_PIC_URL + imgUrl;

		} else if (recommendedList.get(position).getType().equalsIgnoreCase("Discussion")) {

			return Contant.DISCUSSION_PIC_URL + imgUrl;

		} else {
			return "";
		}
	}

	private String getDateFormated(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:sssZ");// 2014-01-31T11:50:26+05:30
		
		
		
		SimpleDateFormat newSdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

		try {
			Date dt = sdf.parse(date);
			String newDate = newSdf.format(dt);

			return newDate;
		} catch (Exception e) {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sssz");
			e.printStackTrace();
			Date dt=null;
			try {
				dt = sdf.parse(date);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String newDate = newSdf.format(dt);
			
			return newDate;
			
			
		}

	}
}
