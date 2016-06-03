package com.debalink.adapters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.debalink.EditPublicationFragment;
import com.debalink.PublicationDetailsFragment;
import com.debalink.R;
import com.debalink.models.PublicationModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class ManagePublicationAdapter extends ArrayAdapter<PublicationModel> {
	private ArrayList<PublicationModel> publicationsList;

	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	// private ImageButton imgButton;
	private String from = "";

	public ManagePublicationAdapter(FragmentActivity activity, ArrayList<PublicationModel> publicationsList) {

		super(activity, R.layout.list_publication_row_new, publicationsList);
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
			convertView = inflater.inflate(R.layout.manage_publication_row, null);
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
			viewHolder.btnDownArrow = (ImageButton) convertView.findViewById(R.id.btnDownArrow);
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

		viewHolder.btnDownArrow.setOnClickListener(new OnArrowClickListener(position));
		convertView.setOnClickListener(new OnRowClickListener(position));

		return convertView;

	}

	private class OnRowClickListener implements OnClickListener {
		private int position;

		OnRowClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
			FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			Fragment publicationFragment = new PublicationDetailsFragment();
			ft.add(R.id.inner_content, publicationFragment, "publication_details");
			ft.addToBackStack(null);
			Bundle bundle = new Bundle();
			bundle.putString("publication_id", publicationsList.get(position).getPublicationId());
			bundle.putString("no_of_comments", publicationsList.get(position).getCommentCnt());
			bundle.putString("user_id", UserPreferences.getUserId(activity));
			bundle.putString("from", from);
			publicationFragment.setArguments(bundle);
			ft.commit();
		}
	}

	private class OnArrowClickListener implements OnClickListener {
		private int position;

		OnArrowClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
			showPicDialog(publicationsList.get(position));
		}
	}

	class PlanetViewHolder {
		TextView txtUserName, txtViews, txtNoOfComments, txtAddedBy, txtCategory, txtAddedByName;
		ImageView profilePic;
		WeakReference<ImageView> softImgView;
		RatingBar rating;
		ImageButton btnDownArrow;

	}

	private void showPicDialog(final PublicationModel item) {

		item.getPublicationId();

		ImageButton btnEdit, btnDonwload;
		TextView txtEdit, txtDownload, txtIsActive;
		CheckBox cbk;
		View view = activity.getLayoutInflater().inflate(R.layout.popup_manage, null);

		btnEdit = (ImageButton) view.findViewById(R.id.btnEdit);
		btnDonwload = (ImageButton) view.findViewById(R.id.btnDownload);

		txtEdit = (TextView) view.findViewById(R.id.txtEdit);
		txtDownload = (TextView) view.findViewById(R.id.txtDownload);
		txtIsActive = (TextView) view.findViewById(R.id.txtIsActive);

		cbk = (CheckBox) view.findViewById(R.id.ckbSelect);

		cbk.setChecked(item.isActive());

		final Dialog dialog = new Dialog(activity);

		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment editFragment = new EditPublicationFragment();
				ft.add(R.id.relativeLayoutFragment, editFragment, "edit_publication");
				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("publication_id", item.getPublicationId());
				editFragment.setArguments(bundle);
				ft.commit();
				dialog.dismiss();
			}
		});

		txtEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Fragment editFragment = new EditPublicationFragment();
				ft.add(R.id.relativeLayoutFragment, editFragment, "edit_publication");
				ft.addToBackStack(null);
				Bundle bundle = new Bundle();
				bundle.putString("publication_id", item.getPublicationId());
				editFragment.setArguments(bundle);
				ft.commit();
				dialog.dismiss();
			}
		});

		txtDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DownloadFileFromURL(activity, "Downloading file", item.getPublicationId()).execute();
				dialog.dismiss();
			}
		});

		btnDonwload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DownloadFileFromURL(activity, "Downloading file", item.getPublicationId()).execute();
				dialog.dismiss();
			}
		});

		cbk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					new AsyncIsActive(activity, "Enabling publication", item.getPublicationId(), 1).execute();
				} else {
					new AsyncIsActive(activity, "Disabling publication", item.getPublicationId(), 0).execute();
				}
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
		dialog.show();

	}

	class DownloadFileFromURL extends AsynDownloader {
		private String publicationId;
		private int errorFlag = 0;
		private String fileName = "";

		public DownloadFileFromURL(Context ctx, String message, String publicationId) {
			super(ctx, message);
			this.publicationId = publicationId;
		}

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... f_url) {
			int count;
			try {

				Log.i("publicationId", publicationId);

				HttpRequest hr = new HttpRequest();
				ContentValues cv = new ContentValues();
				cv.put("PublicationId", publicationId);
				cv.put("UserId", UserPreferences.getUserId(activity));

				SoapObject res = hr.getSoapObjectFromServer(cv, "SelectPublicationFile");

				SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

				SoapObject newDataSetObject;

				if (!diffgramObject.hasProperty("NewDataSet")) {
					return "";
				}
				newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
				newDataSetObject = (SoapObject) newDataSetObject.getProperty(0);
				fileName = newDataSetObject.getPropertySafelyAsString("PFileURL", "");

				File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "cache_dir_img");
				File f = new File(cacheDir, fileName);

				InputStream is = new URL(Contant.PUBLICATION_PIC_URL + fileName).openStream();
				OutputStream os = new FileOutputStream(f);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = is.read(data)) != -1) {
					total += count;

					// writing data to file
					os.write(data, 0, count);
				}

				// flushing output
				os.flush();

				// closing streams
				os.close();
				is.close();

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

			return fileName;
		}

		@Override
		protected void onPostExecute(String fileName) {
			super.onPostExecute(fileName);

			if (errorFlag == 0 && fileName.length() > 0) {

				File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "cache_dir_img");
				File f = new File(cacheDir, fileName);

				MimeTypeMap myMime = MimeTypeMap.getSingleton();

				Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);

				// Intent newIntent = new Intent(Intent.ACTION_VIEW);
				String mimeType = myMime.getMimeTypeFromExtension(fileExt(fileName).substring(1));
				newIntent.setDataAndType(Uri.fromFile(f), mimeType);

				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					activity.startActivity(newIntent);
				} catch (android.content.ActivityNotFoundException e) {
					showMessage("No handler for this type of file.");
				}
			} else if (fileName.equals("")) {
				showMessage("No file to download");
			} else if (errorFlag > 0) {
				showMessage("Unknown error");
			}
		}

	}

	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();

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
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();

	}

	private class AsyncIsActive extends AsynDownloader {
		private String publicationId;
		private int isActive;

		public AsyncIsActive(Context ctx, String message, String publicationId, int isActive) {
			super(ctx, message);
			this.publicationId = publicationId;
			this.isActive = isActive;
		}

		private String res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				cv.put("PublicationId", publicationId);
				cv.put("UserID", UserPreferences.getUserId(activity));
				cv.put("IsActive", isActive + "");

				res = hr.getDataFromServer(cv, "ManagePublicationIsActive");

				Log.i("getCategory", res.toString());

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

			if (errorFlag == 0) {

			}
		}

	}
}
