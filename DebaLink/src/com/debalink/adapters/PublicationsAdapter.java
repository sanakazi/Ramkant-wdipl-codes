package com.debalink.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.PublicationDetailsFragment;
import com.debalink.R;
import com.debalink.RecommendedUserListFragment;
import com.debalink.models.PublicationModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class PublicationsAdapter extends ArrayAdapter<PublicationModel> {
	private ArrayList<PublicationModel> publicationsList;
	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;
	public static View pinnedView;
	private String from = "";

	public PublicationsAdapter(FragmentActivity activity, ArrayList<PublicationModel> publicationsList) {

		super(activity, R.layout.list_discussion_row, publicationsList);
		this.publicationsList = publicationsList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = width / 2;

		Drawable d = activity.getResources().getDrawable(R.drawable.bg_publication_thum);
		imageWidth = d.getIntrinsicWidth();

		imageHeight = d.getIntrinsicHeight();

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageHeight);
		from = "publication";
	}

	public PublicationsAdapter(FragmentActivity activity, ArrayList<PublicationModel> publicationsList, View pinnedView) {

		super(activity, R.layout.list_discussion_row, publicationsList);
		this.publicationsList = publicationsList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		this.pinnedView = pinnedView;
		int width = metrics.widthPixels;
		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = width / 2;
		Drawable d = activity.getResources().getDrawable(R.drawable.bg_publication_thum);
		imageWidth = d.getIntrinsicWidth();
		imageHeight = d.getIntrinsicHeight();
		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageHeight);
		from = "pinned";

	}

	public int getCount() {
		return publicationsList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		PublicationModel planet = (PublicationModel) this.getItem(position);

		PlanetViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new PlanetViewHolder();
			convertView = inflater.inflate(R.layout.list_publication_row_new, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtUsername);
			viewHolder.txtNoOfComments = (TextView) convertView.findViewById(R.id.txtNoOfComments);
			viewHolder.txtViews = (TextView) convertView.findViewById(R.id.txtViews);
			viewHolder.rating = (RatingBar) convertView.findViewById(R.id.rtbAvgRating);
			viewHolder.txtAddedByName = (TextView) convertView.findViewById(R.id.txtAddedByName);
			viewHolder.txtAddedBy = (TextView) convertView.findViewById(R.id.txtAddedBy);
			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.imageView1);
			viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);
			viewHolder.profilePic.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageHeight));

			viewHolder.softImgView = new WeakReference<ImageView>(viewHolder.profilePic);
			viewHolder.btnLockKey = (ImageButton) convertView.findViewById(R.id.btnLockKey);

			try {
				viewHolder.softImgView.get().setTag(planet.getDisplayImageUrl());
				if (planet.getDisplayImageUrl() != null)
					imageDownloader.displayImage(planet.getDisplayImageUrl(), viewHolder.softImgView);
			} catch (Exception e) {
				e.printStackTrace();
			}

			convertView.setTag(viewHolder);

		}

		else {
			viewHolder = (PlanetViewHolder) convertView.getTag();

		}
		viewHolder.txtUserName.setText(planet.getPublicationName());

		viewHolder.txtAddedBy.setText("Added By ");
		viewHolder.txtAddedByName.setText(Fonts.toUpper(planet.getCreatedBy()));

		viewHolder.txtViews.setText(planet.getNoOfView());
		viewHolder.txtNoOfComments.setText(planet.getCommentCnt());
		viewHolder.txtCategory.setText(planet.getCategoryName());

		viewHolder.txtUserName.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtAddedByName.setTypeface(Fonts.getRobotoMedium(activity));
		viewHolder.txtAddedBy.setTypeface(Fonts.getRobotoRegular(activity));

		viewHolder.txtViews.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtCategory.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtNoOfComments.setTypeface(Fonts.getRobotoRegular(activity));

		viewHolder.rating.setRating(Float.parseFloat(planet.getAverageRating()));

		if (planet.getLockPassword() != null && planet.getLockPassword().length() > 0) {
			viewHolder.btnLockKey.setVisibility(View.VISIBLE);
		} else {
			viewHolder.btnLockKey.setVisibility(View.INVISIBLE);
		}
		
		
		convertView.setOnClickListener(new OnProfileClickListener(position));
		convertView.setOnLongClickListener(new OnLongPress(position));
		return convertView;

	}

	private class OnProfileClickListener implements OnClickListener {
		private int position;

		OnProfileClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {

			if (publicationsList.get(position).getLockPassword() != null && publicationsList.get(position).getLockPassword().length() > 0) {
				showPasswordPopup(publicationsList.get(position));
				return;
			} else {

				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment publicationFragment = new PublicationDetailsFragment();
				ft.add(R.id.inner_content, publicationFragment, "publication_details");

				if (pinnedView != null)
					pinnedView.setVisibility(View.GONE);

				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("publication_id", publicationsList.get(position).getPublicationId());
				bundle.putString("no_of_comments", publicationsList.get(position).getCommentCnt());
				bundle.putString("user_id", publicationsList.get(position).getUserId());
				bundle.putString("from", from);
				publicationFragment.setArguments(bundle);
				ft.commit();
			}
		}
	}

	private class OnLongPress implements OnLongClickListener {
		private int position;

		public OnLongPress(int position) {
			this.position = position;
		}

		@Override
		public boolean onLongClick(View arg0) {
			showPicDialog(publicationsList.get(position));
			return true;
		}

	}

	class PlanetViewHolder {
		TextView txtUserName, txtViews, txtNoOfComments, txtAddedBy, txtCategory, txtAddedByName;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
		RatingBar rating;
		public ImageButton btnLockKey;

	}

	private void showPicDialog(final PublicationModel item) {
		ImageButton btnPinsafe, btnRecommended, btnComments, btnReport;
		View view = activity.getLayoutInflater().inflate(R.layout.long_press_menu, null);

		btnPinsafe = (ImageButton) view.findViewById(R.id.btnPinsafe);
		btnRecommended = (ImageButton) view.findViewById(R.id.btnRecommend);
		btnComments = (ImageButton) view.findViewById(R.id.btnComments);
		btnReport = (ImageButton) view.findViewById(R.id.btnReport);

		final Dialog dialog = new Dialog(activity);

		btnPinsafe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Async(activity, "Adding to pinsafe", item.getPublicationId()).execute();
				dialog.dismiss();
			}
		});

		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ReportPost(activity, "Reporting Publication", item.getPublicationId()).execute();
				dialog.dismiss();
			}
		});

		btnComments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment publicationFragment = new PublicationDetailsFragment();
				ft.add(R.id.inner_content, publicationFragment, "publication_details");

				if (pinnedView != null)
					pinnedView.setVisibility(View.GONE);

				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("publication_id", item.getPublicationId());
				bundle.putString("no_of_comments", item.getCommentCnt());
				bundle.putString("user_id", item.getUserId());
				bundle.putString("from", from);
				publicationFragment.setArguments(bundle);
				ft.commit();

				dialog.dismiss();
			}
		});
		btnRecommended.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment recommendedUserListFragment = new RecommendedUserListFragment();
				ft.add(R.id.inner_content, recommendedUserListFragment, "user_list");
				Bundle bundle = new Bundle();
				bundle.putString("id", item.getPublicationId());
				bundle.putString("from", "publication");

				recommendedUserListFragment.setArguments(bundle);
				if (pinnedView != null)
					pinnedView.setVisibility(View.GONE);

				ft.addToBackStack(null);

				ft.commit();
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();

	}

	private void showPasswordPopup(final PublicationModel item) {
		final Dialog dialog = new Dialog(activity);
		
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
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (item.getLockPassword().equals(edtPassword.getText().toString())) {
					dialog.dismiss();
					
					FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
					Fragment publicationFragment = new PublicationDetailsFragment();
					ft.add(R.id.inner_content, publicationFragment, "publication_details");

					if (pinnedView != null)
						pinnedView.setVisibility(View.GONE);

					ft.addToBackStack(null);
					Bundle bundle = new Bundle();
					bundle.putString("publication_id", item.getPublicationId());
					bundle.putString("no_of_comments", item.getCommentCnt());
					bundle.putString("user_id", item.getUserId());
					bundle.putString("from", from);
					publicationFragment.setArguments(bundle);
					ft.commit();
				} else {
					showMessage("Wrong password");
					edtPassword.setText("");
				}

			}
		});

		

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, String publicationId) {
			super(ctx, message);
			this.publicationId = publicationId;
		}

		private String res;
		private int errorFlag = 0;
		private String publicationId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserID", UserPreferences.getUserId(activity.getApplicationContext()));
				cv.put("PublicationId", publicationId);
				res = hr.getDataFromServer(cv, "InsertPublicationPinsafe");
				Log.i("InsertPublicationPinsafe", res.toString());

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
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (errorFlag == 0) {
					showMessage("Publication pinned successfully");
				}

			} catch (Exception e) {
				e.printStackTrace();
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

	private class ReportPost extends AsynDownloader {
		private int errorFlag = 0;
		private String res = "";
		private String publicationId;

		public ReportPost(Context ctx, String message, String publicationId) {
			super(ctx, message);
			this.publicationId = publicationId;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("PublicationId", publicationId);
				cv.put("UserId", UserPreferences.getUserId(activity));
				res = hr.getDataFromServer(cv, "InsertReport");

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
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0 && res.equals("0")) {
				showMessage("Reported successfully");
			} else if (errorFlag == 0 && res.equals("2")) {
				showMessage("Already reported");
			}

		}

	}

}
