package com.debalink.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.DiscussionDetailsFragment;
import com.debalink.PublicationDetailsFragment;
import com.debalink.R;
import com.debalink.models.SearchModel;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;

public class GlobalSearchDetailAdapter extends ArrayAdapter<SearchModel> {
	private ArrayList<SearchModel> searchList;

	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public GlobalSearchDetailAdapter(FragmentActivity activity, ArrayList<SearchModel> searchList) {

		super(activity, R.layout.list_headline_row, searchList);
		this.searchList = searchList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = (width / 5) - plus;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return searchList.size();
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

		SearchModel planet = (SearchModel) this.getItem(position);
		PlanetViewHolder viewHolder;
		if (convertView == null) {

			viewHolder = new PlanetViewHolder();
			convertView = inflater.inflate(R.layout.list_headline_row, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
			viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtHeadlineDesc);

			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imgProfilePic);

			viewHolder.profilePic.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

			viewHolder.softImgView = new WeakReference<ImageView>(viewHolder.profilePic);

			convertView.setTag(viewHolder);

		}

		else {
			viewHolder = (PlanetViewHolder) convertView.getTag();

		}

		viewHolder.txtUserName.setText(Fonts.toUpper(planet.getType()));
		viewHolder.txtDateTime.setText(planet.getName());
		// viewHolder.txtDesc.setText(planet.getDescription());

		viewHolder.softImgView.get().setTag(planet.getProfilePic());

		try {
			if (planet.getProfilePic() != null)
				imageDownloader.displayImage(planet.getProfilePic(), viewHolder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));

		viewHolder.txtUserName.setIncludeFontPadding(false);
		viewHolder.txtDesc.setIncludeFontPadding(false);
		viewHolder.txtDateTime.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));
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
			if (searchList.get(position).getType().equalsIgnoreCase("Publication")) {
				if (searchList.get(position).getPassword() != null && searchList.get(position).getPassword().length() > 0) {
					showPasswordPopup(searchList.get(position), "Publication");
					return;
				} else {
					FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
					Fragment publicationFragment = new PublicationDetailsFragment();
					ft.add(R.id.rl, publicationFragment, "publication_details");

					ft.addToBackStack(null);
					Bundle bundle = new Bundle();
					bundle.putString("publication_id", searchList.get(position).getId());
					bundle.putString("no_of_comments", "1");
					bundle.putString("user_id", searchList.get(position).getUserId());
					bundle.putString("from", "publication");
					publicationFragment.setArguments(bundle);
					ft.commit();
				}
			} else if (searchList.get(position).getType().equalsIgnoreCase("Discussion")) {
				if (searchList.get(position).getPassword() != null && searchList.get(position).getPassword().length() > 0) {
					showPasswordPopup(searchList.get(position), "Discussion");
					return;
				} else {
					FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
					Fragment discussionFragment = new DiscussionDetailsFragment();

					ft.add(R.id.rl, discussionFragment, "discussion_details");

					ft.addToBackStack(null);
					Bundle bundle = new Bundle();
					bundle.putString("discussion_id", searchList.get(position).getId());
					bundle.putString("no_of_comments", "1");
					bundle.putString("from", "discussion");
					discussionFragment.setArguments(bundle);
					ft.show(discussionFragment);

					ft.commit();
				}
			} else if (searchList.get(position).getType().equalsIgnoreCase("Headline")) {

			}
		}
	}

	class PlanetViewHolder {
		TextView txtUserName, txtDesc, txtDateTime;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
	}

	private void showPasswordPopup(final SearchModel item, final String from) {
		ImageButton btnSubmit;
		TextView txtInfoMessage, txtHeaderMessage;
		View view = activity.getLayoutInflater().inflate(R.layout.password_popup, null);

		btnSubmit = (ImageButton) view.findViewById(R.id.btnSubmit);
		txtInfoMessage = (TextView) view.findViewById(R.id.txtInfoMessage);
		txtHeaderMessage = (TextView) view.findViewById(R.id.txtHeaderMessage);
		txtInfoMessage.setTypeface(Fonts.getRobotoMedium(activity));
		txtHeaderMessage.setTypeface(Fonts.getRobotoMedium(activity));

		final EditText edtPassword = (EditText) view.findViewById(R.id.edtPassword);
		edtPassword.setTypeface(Fonts.getRobotoMedium(activity));
		final Dialog dialog = new Dialog(activity);
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (item.getPassword().equals(edtPassword.getText().toString())) {
					dialog.dismiss();

					if (item.getType().equalsIgnoreCase("Publication")) {

						FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
						Fragment publicationFragment = new PublicationDetailsFragment();
						ft.add(R.id.rl, publicationFragment, "publication_details");

						ft.addToBackStack(null);
						Bundle bundle = new Bundle();
						bundle.putString("publication_id", item.getId());
						bundle.putString("no_of_comments", "1");
						bundle.putString("user_id", item.getUserId());
						bundle.putString("from", "publication");
						publicationFragment.setArguments(bundle);
						ft.commit();
					} else if (item.getType().equalsIgnoreCase("Discussion")) {
						FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
						Fragment discussionFragment = new DiscussionDetailsFragment();

						ft.add(R.id.rl, discussionFragment, "discussion_details");

						ft.addToBackStack(null);
						Bundle bundle = new Bundle();
						bundle.putString("discussion_id", item.getId());
						bundle.putString("no_of_comments", "1");
						bundle.putString("from", from);
						discussionFragment.setArguments(bundle);
						ft.show(discussionFragment);

						ft.commit();
					}
				} else {
					showMessage("Wrong password");
				}

			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
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
