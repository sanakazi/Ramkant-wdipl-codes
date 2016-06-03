
package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import com.debalink.R;
import com.debalink.adapters.PublicationsAdapter.PlanetViewHolder;
import com.debalink.models.PublicationModel;
import com.debalink.utils.ImageThreadDownloader;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unchecked")
public class PublicPublicationAdapter extends BaseExpandableListAdapter {

	public ArrayList<String> groupItem;
	ArrayList<ArrayList<PublicationModel>> childItem;

	public Activity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	private LayoutInflater inflater;
	Context context;

	public PublicPublicationAdapter(Activity activity, ArrayList<String> grList, ArrayList<ArrayList<PublicationModel>> childItem) {
		groupItem = grList;
		// this.Childtem = childItem;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;
		this.childItem=childItem;
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.15));
		System.out.println("plus " + plus);
		imageWidth = width / 5;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public void setInflater(LayoutInflater mInflater, Activity act) {
		this.inflater = mInflater;
		activity = act;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		PublicationModel planet = (PublicationModel) childItem.get(groupPosition).get(childPosition);
		TextView txtUserName, txtDesc, txtDateTime;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_discussion_row, null);
			txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			txtDesc = (TextView) convertView.findViewById(R.id.txtHeadlineDesc);

			profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);

			profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

			softImgView = new WeakReference<ImageView>(profilePic);

			convertView.setTag(new PlanetViewHolder(txtUserName, txtDesc, txtDateTime, profilePic));

		}

		else {
			PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();

			txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			txtDesc = (TextView) convertView.findViewById(R.id.txtHeadlineDesc);

			profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);
			softImgView = new WeakReference<ImageView>(profilePic);

		}

		txtUserName.setText(planet.getCreatedBy());
		txtDateTime.setText(planet.getCreatedDate());
		txtDesc.setText(planet.getPublicationName());


		softImgView.get().setTag(planet.getDisplayImageUrl());

		try {
			if (planet.getDisplayImageUrl() != null)
				imageDownloader.displayImage(planet.getDisplayImageUrl(), softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// convertView.setOnClickListener(new OnProfileClickListener(position));

		return convertView;

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childItem.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupItem.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupItem.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_group_row, null);
		}
		((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	class PlanetViewHolder {
		private TextView txtUserName, txtDesc, txtDateTime;
		private ImageView profilePic;

		public PlanetViewHolder(TextView txtUserName, TextView txtDesc, TextView txtDateTime, ImageView profilePic) {

			this.txtUserName = txtUserName;
			this.txtDesc = txtDesc;
			this.txtDateTime = txtDateTime;
			this.profilePic = profilePic;
		}

		public TextView getTxtUserName() {
			return txtUserName;
		}

		public TextView getTxtDesc() {
			return txtDesc;
		}

		public TextView getTxtDateTime() {
			return txtDateTime;
		}

	}
}