package com.debalink.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.R;
import com.debalink.RecommendedUserListFragment;
import com.debalink.models.HeadlinesModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HeadlinesAdapter extends ArrayAdapter<HeadlinesModel> {
	private ArrayList<HeadlinesModel> headlinesList;

	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public HeadlinesAdapter(FragmentActivity activity, ArrayList<HeadlinesModel> headlinesList) {

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

		HeadlinesModel planet = (HeadlinesModel) this.getItem(position);
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

		viewHolder.txtUserName.setText(Fonts.toUpper(planet.getUserNameHeadline()));
		viewHolder.txtDateTime.setText(planet.getDateTime());
		viewHolder.txtDesc.setText(planet.getDescription());

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

		}
	}
	class PlanetViewHolder {
		TextView txtUserName, txtDesc, txtDateTime;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
	}
	
	private class OnLongPress implements OnLongClickListener {
		private int position;

		public OnLongPress(int position) {
			this.position = position;
		}

		@Override
		public boolean onLongClick(View arg0) {
			showPicDialog(headlinesList.get(position));
			return true;
		}

	}
	
	private void showPicDialog(final HeadlinesModel item) {
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
				new Async(activity, "Adding to pinsafe", item.getHeadlineId()).execute();
				dialog.dismiss();
			}
		});
		
		
		btnRecommended.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment recommendedUserListFragment = new RecommendedUserListFragment();
				ft.add(R.id.inner_content, recommendedUserListFragment, "user_list");
				Bundle bundle=new Bundle();
				bundle.putString("id", item.getHeadlineId());
				bundle.putString("from","headline");			
				recommendedUserListFragment.setArguments(bundle);
			

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

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, String headlineId) {
			super(ctx, message);
			this.headlineId = headlineId;
		}

		private String res;
		private int errorFlag = 0;
		private String headlineId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(activity.getApplicationContext()));
				cv.put("HeadlineId", headlineId);
				res = hr.getDataFromServer(cv, "InsertHeadlinePin");
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
					showMessage("Headline pinned successfully");
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
}
