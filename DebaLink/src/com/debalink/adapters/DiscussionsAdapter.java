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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.DiscussionDetailsFragment;
import com.debalink.PublicationDetailsFragment;
import com.debalink.R;
import com.debalink.RecommendedUserListFragment;
import com.debalink.models.DiscussionModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class DiscussionsAdapter extends ArrayAdapter<DiscussionModel> {
	private ArrayList<DiscussionModel> discussionsList;
	public static View pinnedView;
	private LayoutInflater inflater;
	private Context context;
	private FragmentActivity activity;
	int imageHeight, imageWidth;
	private int containerId = 0;
	private String from = "";

	public DiscussionsAdapter(FragmentActivity activity, ArrayList<DiscussionModel> discussionsList) {

		super(activity, R.layout.list_discussion_row, discussionsList);
		this.discussionsList = discussionsList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = (width / 5) - plus;
		from = "discussion";

	}

	public DiscussionsAdapter(FragmentActivity activity, ArrayList<DiscussionModel> discussionsList, View pinnedView) {

		super(activity, R.layout.list_discussion_row, discussionsList);
		this.discussionsList = discussionsList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		this.pinnedView = pinnedView;
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.05));
		System.out.println("plus " + plus);
		imageWidth = (width / 5) - plus;

		from = "pinned";

	}

	public int getCount() {
		return discussionsList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		DiscussionModel planet = (DiscussionModel) this.getItem(position);
		PlanetViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new PlanetViewHolder();
			convertView = inflater.inflate(R.layout.list_discussion_row_new, null);
			viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txtAddedByName);
			viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
			viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtHeadlineDesc);
			viewHolder.txtAddedBy = (TextView) convertView.findViewById(R.id.txtAddedBy);
			viewHolder.txtNoOfComments = (TextView) convertView.findViewById(R.id.txtNoOfComments);
			viewHolder.txtViews = (TextView) convertView.findViewById(R.id.txtViews);
			viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);
			viewHolder.btnLockKey = (ImageButton) convertView.findViewById(R.id.btnLockKey);
			convertView.setTag(viewHolder);

		}

		else {
			viewHolder = (PlanetViewHolder) convertView.getTag();

		}

		viewHolder.txtUserName.setText(Fonts.toUpper(planet.getUserName()));
		viewHolder.txtDateTime.setText(planet.getCreatedDate());
		viewHolder.txtDesc.setText(planet.getDiscussinTitle());
		viewHolder.txtAddedBy.setText("Added By ");
		// viewHolder.softImgView.get().setTag(planet.getProfilePic());
		viewHolder.txtViews.setText(planet.getNoOfView());
		viewHolder.txtNoOfComments.setText(planet.getCommentCnt());
		viewHolder.txtCategory.setText(planet.getCategoryName());
		try {
			// if (planet.getProfilePic() != null)
			// imageDownloader.displayImage(planet.getProfilePic(),
			// viewHolder.softImgView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// viewHolder.txtUserName.setTypeface(Fonts.getFranklingGothicHeavy(activity));
		viewHolder.txtDateTime.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtDesc.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtUserName.setTypeface(Fonts.getRobotoMedium(activity));
		viewHolder.txtAddedBy.setTypeface(Fonts.getRobotoRegular(activity));

		if (planet.getLockPassword() != null && planet.getLockPassword().length() > 0) {
			viewHolder.btnLockKey.setVisibility(View.VISIBLE);
		} else {
			viewHolder.btnLockKey.setVisibility(View.INVISIBLE);
		}

		viewHolder.txtViews.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtCategory.setTypeface(Fonts.getRobotoRegular(activity));
		viewHolder.txtNoOfComments.setTypeface(Fonts.getRobotoRegular(activity));
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

			if (discussionsList.get(position).getLockPassword() != null && discussionsList.get(position).getLockPassword().length() > 0) {
				showPasswordPopup(discussionsList.get(position));
				return;
			} else {

				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment discussionFragment = new DiscussionDetailsFragment();

				if (containerId == 0) {
					ft.add(R.id.inner_content, discussionFragment, "discussion_details");
				} else {
					ft.add(R.id.inner_content, discussionFragment, "discussion_details");
				}

				if (pinnedView != null)
					pinnedView.setVisibility(View.GONE);

				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("discussion_id", discussionsList.get(position).getDiscussionId());
				bundle.putString("no_of_comments", discussionsList.get(position).getCommentCnt());
				bundle.putString("from", from);
				discussionFragment.setArguments(bundle);
				ft.show(discussionFragment);

				ft.commit();

			}
		}
	}

	class PlanetViewHolder {
		TextView txtUserName, txtDesc, txtDateTime, txtViews, txtNoOfComments, txtAddedBy, txtCategory;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
		public ImageButton btnLockKey;
	}

	private class OnLongPress implements OnLongClickListener {
		private int position;

		public OnLongPress(int position) {
			this.position = position;
		}

		@Override
		public boolean onLongClick(View arg0) {
			showPicDialog(discussionsList.get(position));
			return true;
		}

	}

	private void showPicDialog(final DiscussionModel item) {
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
				new Async(activity, "Adding to pinsafe", item.getDiscussionId()).execute();
				dialog.dismiss();
			}
		});

		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ReportPost(activity, "Reporting Disscussion", item.getDiscussionId()).execute();
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
				bundle.putString("id", item.getDiscussionId());
				bundle.putString("from", "discussion");
				recommendedUserListFragment.setArguments(bundle);
				if (pinnedView != null)
					pinnedView.setVisibility(View.GONE);

				ft.addToBackStack(null);
				ft.commit();
				dialog.dismiss();
			}
		});

		btnComments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment discussionFragment = new DiscussionDetailsFragment();

				if (containerId == 0) {
					ft.add(R.id.inner_content, discussionFragment, "discussion_details");
				} else {
					ft.add(R.id.inner_content, discussionFragment, "discussion_details");
				}

				if (pinnedView != null)
					pinnedView.setVisibility(View.GONE);

				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("discussion_id", item.getDiscussionId());
				bundle.putString("no_of_comments", item.getCommentCnt());
				bundle.putString("from", from);
				discussionFragment.setArguments(bundle);
				ft.show(discussionFragment);

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

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, String discussionId) {
			super(ctx, message);
			this.discussionId = discussionId;
		}

		private String res;
		private int errorFlag = 0;
		private String discussionId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("PinUserId", UserPreferences.getUserId(activity.getApplicationContext()));
				cv.put("DiscussionId", discussionId);
				res = hr.getDataFromServer(cv, "InsertDiscussionPinsafe");
				Log.i("InsertHeadlinePin", res.toString());

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
					showMessage("Discussion pinned successfully");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void showMessage(String message) {
		// Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		// //toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();

		View view = activity.getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView txtMessage = (TextView) view.findViewById(R.id.textView1);
		txtMessage.setTypeface(Fonts.getBold(activity));

		txtMessage.setText(message);
		Toast toast = new Toast(activity);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void showPasswordPopup(final DiscussionModel item) {
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

				if (item.getLockPassword().equals(edtPassword.getText().toString())) {
					dialog.dismiss();
					
					FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
					Fragment discussionFragment = new DiscussionDetailsFragment();

					if (containerId == 0) {
						ft.add(R.id.inner_content, discussionFragment, "discussion_details");
					} else {
						ft.add(R.id.inner_content, discussionFragment, "discussion_details");
					}

					if (pinnedView != null)
						pinnedView.setVisibility(View.GONE);

					ft.addToBackStack(null);
					Bundle bundle = new Bundle();
					bundle.putString("discussion_id", item.getDiscussionId());
					bundle.putString("no_of_comments", item.getCommentCnt());
					bundle.putString("from", from);
					discussionFragment.setArguments(bundle);
					ft.show(discussionFragment);

					ft.commit();
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

	private class ReportPost extends AsynDownloader {
		private int errorFlag = 0;
		private String res = "";
		private String discussionId;

		public ReportPost(Context ctx, String message, String discussionId) {
			super(ctx, message);
			this.discussionId = discussionId;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("DiscussionId", discussionId);
				cv.put("UserId", UserPreferences.getUserId(activity));
				res = hr.getDataFromServer(cv, "InsertUpdateDiscussionReport");

				Log.i("discussionId", discussionId);

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
