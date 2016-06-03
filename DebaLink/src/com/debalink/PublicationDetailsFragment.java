package com.debalink;

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
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.adapters.CommentsAdapter;
import com.debalink.adapters.PublicationsAdapter;
import com.debalink.models.CommentModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class PublicationDetailsFragment extends Fragment implements View.OnClickListener {
	private TextView txtReport, txtSharing, txtRating, txtComments, txtPublicationTitle, txtAvgRating, txtPublicationDetails, txtDownload, txtView;
	private ImageView imgPublicationPic, imgProfileic;
	private String publicationId, noOfComments, userId, from;
	private ImageThreadDownloader imgDownloader;
	private ArrayList<CommentModel> commentList;
	private ListView lvComments;
	private View headerAddComment;
	private EditText edtComment;
	private ImageButton btnPostComment, btnLoadMore, btnCommentArrow, btnRatingArrow, btnStar1, btnStar2, btnStar3, btnStar4, btnStar5;
	private View footer, ratingLayout;
	private RelativeLayout rlComments;
	private int rating;
	private String downloadDocFileName,isAccess;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.publication_details, null);

		txtReport = (TextView) view.findViewById(R.id.txtReport);
		txtRating = (TextView) view.findViewById(R.id.txtRatings);
		txtSharing = (TextView) view.findViewById(R.id.txtSharing);
		txtComments = (TextView) view.findViewById(R.id.txtComments);
		txtDownload = (TextView) view.findViewById(R.id.txtDownload);
		txtView = (TextView) view.findViewById(R.id.txtView);

		btnCommentArrow = (ImageButton) view.findViewById(R.id.btnComments);
		btnRatingArrow = (ImageButton) view.findViewById(R.id.btnRatings);

		txtPublicationTitle = (TextView) view.findViewById(R.id.txtDiscussionTitle);
		txtAvgRating = (TextView) view.findViewById(R.id.txtAvgRating);
		txtPublicationDetails = (TextView) view.findViewById(R.id.txtDiscussionDetails);

		lvComments = (ListView) view.findViewById(R.id.lvComments);
		rlComments = (RelativeLayout) view.findViewById(R.id.rlComment);
		ratingLayout = (View) view.findViewById(R.id.layoutRating);

		btnStar1 = (ImageButton) view.findViewById(R.id.btnStart1);
		btnStar2 = (ImageButton) view.findViewById(R.id.btnStart2);
		btnStar3 = (ImageButton) view.findViewById(R.id.btnStart3);
		btnStar4 = (ImageButton) view.findViewById(R.id.btnStart4);
		btnStar5 = (ImageButton) view.findViewById(R.id.btnStart5);

		btnStar1.setOnClickListener(new OnRatingClickListerner());
		btnStar2.setOnClickListener(new OnRatingClickListerner());
		btnStar3.setOnClickListener(new OnRatingClickListerner());
		btnStar4.setOnClickListener(new OnRatingClickListerner());
		btnStar5.setOnClickListener(new OnRatingClickListerner());

		ratingLayout.setVisibility(View.GONE);
		btnRatingArrow.setVisibility(View.INVISIBLE);

		footer = getActivity().getLayoutInflater().inflate(R.layout.list_more_footer_view, null);
		btnLoadMore = (ImageButton) footer.findViewById(R.id.button1);

		txtPublicationTitle.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtAvgRating.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtDownload.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtReport.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtRating.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtSharing.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtAvgRating.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtView.setTypeface(Fonts.getRobotoRegular(getActivity()));

		txtComments.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtReport.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtSharing.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtRating.setTypeface(Fonts.getRobotoMedium(getActivity()));

		txtPublicationDetails.setTypeface(Fonts.getRobotoRegular(getActivity()));

		imgPublicationPic = (ImageView) view.findViewById(R.id.imgDiscussionPic);

		publicationId = getArguments().getString("publication_id");
		noOfComments = getArguments().getString("no_of_comments");
		userId = getArguments().getString("user_id");
		from = getArguments().getString("from");
		txtComments.setText("Comments" + "(" + noOfComments + ")");

		txtRating.setVisibility(View.VISIBLE);

		imgDownloader = new ImageThreadDownloader(getActivity(), getActivity(), getImageWidth(), getImageWidth());

		imgPublicationPic.setLayoutParams(new LinearLayout.LayoutParams(getImageWidth(), getImageWidth()));

		txtPublicationDetails.setClickable(true);
		txtPublicationDetails.setMovementMethod(LinkMovementMethod.getInstance());

		headerAddComment = getActivity().getLayoutInflater().inflate(R.layout.add_comment, null);
		lvComments.addHeaderView(headerAddComment);

		txtRating.setOnClickListener(this);
		txtComments.setOnClickListener(this);
		txtSharing.setOnClickListener(this);
		rlComments.setVisibility(View.VISIBLE);
		btnCommentArrow.setVisibility(View.VISIBLE);
		ratingLayout.setVisibility(View.GONE);
		btnRatingArrow.setVisibility(View.INVISIBLE);
		txtDownload.setOnClickListener(this);
		txtRating.setOnClickListener(this);
		txtView.setOnClickListener(this);
		edtComment = (EditText) headerAddComment.findViewById(R.id.edtComment);
		btnPostComment = (ImageButton) headerAddComment.findViewById(R.id.btnPostComment);
		imgProfileic = (ImageView) headerAddComment.findViewById(R.id.imgProfilePic);

		displayImg();

		btnPostComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!edtComment.getText().toString().isEmpty()) {
					new PostComment(getActivity(), "Posting comment", edtComment.getText().toString()).execute();
				}

			}
		});

		txtReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new ReportPost(getActivity(), "Reporting Publication").execute();

			}
		});

		btnLoadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncMoreComments(getActivity(), "Loading More comments").execute();
			}
		});

		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				return true;
			}

		});

		new AsyncPublicationDetails(getActivity(), "Loading").execute();

		return view;
	}

	@Override
	public void onDetach() {

		super.onDetach();

		if (PublicationsAdapter.pinnedView != null && from.equals("pinned")) {
			PublicationsAdapter.pinnedView.setVisibility(View.VISIBLE);

		}

	}

	private class AsyncPublicationDetails extends AsynDownloader {
		private SoapObject res, commentRes, resAvg, userRating;
		private int errorFlag = 0;

		public AsyncPublicationDetails(Context ctx, String message) {
			super(ctx, message);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				downloadDocFileName = "";

				ContentValues cv = new ContentValues();

				cv.put("PublicationId", publicationId);
				cv.put("UserID", userId);

				res = hr.getSoapObjectFromServer(cv, "ViewPublicationDetails");

				resAvg = hr.getSoapObjectFromServer(cv, "ViewRatingByPublicationId");

				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));

				userRating = hr.getSoapObjectFromServer(cv, "SelectRatingByUserId");

				cv.put("Mode", "Top");
				cv.put("UserID", UserPreferences.getUserId(getActivity().getApplicationContext()));
				commentRes = hr.getSoapObjectFromServer(cv, "ViewPublicationComment");
				commentList = hr.parsePublicationCommentsResponse(commentRes);

				ContentValues cvDoc = new ContentValues();
				cvDoc.put("PublicationId", publicationId);
				cvDoc.put("UserId", UserPreferences.getUserId(getActivity()));

				SoapObject res = hr.getSoapObjectFromServer(cvDoc, "SelectPublicationFile");

				SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

				SoapObject newDataSetObject;

				if (!diffgramObject.hasProperty("NewDataSet")) {
					downloadDocFileName = "";
					return "";
				}
				newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
				newDataSetObject = (SoapObject) newDataSetObject.getProperty(0);
				downloadDocFileName = newDataSetObject.getPropertySafelyAsString("PFileURL", "");

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

			if (errorFlag == 0) {

				try {
					parseViewPublicationResponse(res, resAvg, userRating);
				} catch (Exception e) {
					e.printStackTrace();
				}
				lvComments.setAdapter(new CommentsAdapter(getActivity(), commentList));

				if (commentList.size() > 0)
					txtComments.setText("Comments" + "(" + commentList.get(0).getCommentCnt() + ")");

				if (commentList.size() > 3) {
					if (lvComments.getFooterViewsCount() == 0)
						lvComments.addFooterView(footer);
				}

				if (downloadDocFileName.equals("")) {
					txtView.setVisibility(View.INVISIBLE);
					txtDownload.setVisibility(View.INVISIBLE);

				}
			}
		}

	}

	private void parseViewPublicationResponse(SoapObject res, SoapObject resAvg, SoapObject resUserRating) {

		SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");
		SoapObject newDataSetObject;
		newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		SoapObject resDiscussionDetails, resAvgRating, userRating;

		resDiscussionDetails = (SoapObject) newDataSetObject.getProperty(0);
		txtPublicationTitle.setText(resDiscussionDetails.getPropertySafelyAsString("PublicationName", ""));
		txtPublicationDetails.setText(Html.fromHtml(resDiscussionDetails.getPropertySafelyAsString("PublicationMainContain", "")));
		isAccess=resDiscussionDetails.getPropertySafelyAsString("isaccess", "");
		

		resAvgRating = (SoapObject) ((SoapObject) ((SoapObject) resAvg.getProperty("diffgram")).getProperty("NewDataSet")).getProperty(0);

		txtAvgRating.setVisibility(View.VISIBLE);
		txtAvgRating.setText(resAvgRating.getPropertySafelyAsString("RatingCount", "") + " User have rated,Avg rating("
				+ resAvgRating.getPropertySafelyAsString("AverageRating", "") + ")");

		String imgUrl;
		imgUrl = Contant.PUBLICATION_PIC_URL + resDiscussionDetails.getPropertySafelyAsString("DisplayImageUrl", "");

		imgPublicationPic.setTag(imgUrl);
		imgDownloader.displayImage(imgUrl, new WeakReference<ImageView>(imgPublicationPic));

		try {
			String rating;
			userRating = (SoapObject) ((SoapObject) ((SoapObject) resUserRating.getProperty("diffgram")).getProperty("NewDataSet")).getProperty(0);
			rating = userRating.getPropertySafelyAsString("Rating", "0");
			updateRating(Integer.parseInt(rating));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getImageWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.00));

		int imageWidth = (width / 4) - plus;

		return imageWidth;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.txtRatings:

			if (ratingLayout.getVisibility() == View.VISIBLE) {
				btnRatingArrow.setVisibility(View.INVISIBLE);
				ratingLayout.setVisibility(View.GONE);
			} else {
				// rlComments.setVisibility(View.VISIBLE);
				btnCommentArrow.setVisibility(View.VISIBLE);
				ratingLayout.setVisibility(View.VISIBLE);
				ratingLayout.bringToFront();
				btnRatingArrow.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.txtComments:

			rlComments.setVisibility(View.VISIBLE);
			btnCommentArrow.setVisibility(View.VISIBLE);
			ratingLayout.setVisibility(View.GONE);
			btnRatingArrow.setVisibility(View.INVISIBLE);

		case R.id.txtDownload:
			
			if(isAccess.equalsIgnoreCase("false")&&!userId.equals(UserPreferences.getUserId(getActivity()))){
				showMessage("Not allowed to download this file");
				return;
			}
			
			
			new DownloadFileFromURL(getActivity(), "Downloading", publicationId);
			
			
			break;
		case R.id.txtView:
			if(isAccess.equalsIgnoreCase("false")&&!userId.equals(UserPreferences.getUserId(getActivity()))){
				showMessage("Not allowed to view this file");
				return;
			}
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment viewDocFragment = new ViewDocumentFragment();
			ft.replace(R.id.inner_content, viewDocFragment, "view_doc");
			Bundle bundle = new Bundle();
			bundle.putString("doc_name", downloadDocFileName);
			ft.addToBackStack(null);
			viewDocFragment.setArguments(bundle);
			ft.commit();

			break;
		}

	}

	private class PostComment extends AsynDownloader {
		private int errorFlag = 0;
		private String comment;
		private String res = "";
		private SoapObject commentRes;

		public PostComment(Context ctx, String message, String comment) {
			super(ctx, message);
			this.comment = comment;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("PublicationId", publicationId);
				cv.put("Description", comment);
				cv.put("SenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				res = hr.getDataFromServer(cv, "InsertPublicationComment");

				cv.put("Mode", "Top");
				commentRes = hr.getSoapObjectFromServer(cv, "ViewPublicationComment");
				commentList = hr.parsePublicationCommentsResponse(commentRes);

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
				showMessage("Comment posted successfully");
				edtComment.setText("");
				lvComments.setAdapter(new CommentsAdapter(getActivity(), commentList));

				// int commentCount = Integer.parseInt(noOfComments);
				// commentCount = commentCount + 1;
				// txtComments.setText("Comments" + "(" + commentCount + ")");

				if (commentList.size() > 0)
					txtComments.setText("Comments" + "(" + commentList.get(0).getCommentCnt() + ")");

				if (commentList.size() > 3) {
					if (lvComments.getFooterViewsCount() == 0)
						lvComments.addFooterView(footer);
				}

			}

		}

	}

	private class ReportPost extends AsynDownloader {
		private int errorFlag = 0;
		private String res = "";

		public ReportPost(Context ctx, String message) {
			super(ctx, message);

		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("PublicationId", publicationId);
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
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

	private void showMessage(String message) {
		// Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		// //toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();

		View view = getActivity().getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView txtMessage = (TextView) view.findViewById(R.id.textView1);
		txtMessage.setTypeface(Fonts.getBold(getActivity()));

		txtMessage.setText(message);
		Toast toast = new Toast(getActivity());
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	private void displayImg() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.10));
		System.out.println("plus " + plus);
		int imageWidth = (width / 5) - plus;

		imgProfileic.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageWidth));
		imgDownloader = new ImageThreadDownloader(getActivity(), getActivity(), imageWidth, imageWidth);

		imgProfileic.setTag(Contant.PIC_URL + UserPreferences.getProfilePic(getActivity()));

		WeakReference<ImageView> softImgView = new WeakReference<ImageView>(imgProfileic);

		imgDownloader.displayImage(Contant.PIC_URL + UserPreferences.getProfilePic(getActivity()), softImgView);
	}

	private class AsyncMoreComments extends AsynDownloader {
		private SoapObject commentRes;
		private int errorFlag = 0;

		public AsyncMoreComments(Context ctx, String message) {
			super(ctx, message);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();

				cv.put("PublicationId", publicationId);
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Mode", "More");

				commentRes = hr.getSoapObjectFromServer(cv, "ViewPublicationComment");
				commentList = hr.parsePublicationCommentsResponse(commentRes);

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

			if (errorFlag == 0) {
				lvComments.setAdapter(new CommentsAdapter(getActivity(), commentList));
				// int commentCount = commentList.size();

				// txtComments.setText("Comments" + "(" + commentCount + ")");
				if (commentList.size() > 0)
					txtComments.setText("Comments" + "(" + commentList.get(0).getCommentCnt() + ")");

				if (lvComments.getFooterViewsCount() != 0)
					lvComments.removeFooterView(footer);

			}
		}

	}

	private class AsyncUpdateRating extends AsynDownloader {
		private String res;
		private int errorFlag = 0;

		public AsyncUpdateRating(Context ctx, String message) {
			super(ctx, message);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();

				cv.put("PublicationId", publicationId);
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Rating", rating + "");

				res = hr.getDataFromServer(cv, "InsertUpdateRating");

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

			if (errorFlag == 0) {
				updateRating(rating);
			}
		}

	}

	private class OnRatingClickListerner implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			btnStar1.setSelected(false);
			btnStar2.setSelected(false);
			btnStar3.setSelected(false);
			btnStar4.setSelected(false);
			btnStar5.setSelected(false);

			int id = v.getId();

			switch (id) {

			case R.id.btnStart1:
				btnStar1.setSelected(true);
				rating = 1;

				break;
			case R.id.btnStart2:
				btnStar1.setSelected(true);
				btnStar2.setSelected(true);
				rating = 2;
				break;
			case R.id.btnStart3:
				btnStar1.setSelected(true);
				btnStar2.setSelected(true);
				btnStar3.setSelected(true);
				rating = 3;
				break;
			case R.id.btnStart4:

				btnStar1.setSelected(true);
				btnStar2.setSelected(true);
				btnStar3.setSelected(true);
				btnStar4.setSelected(true);
				rating = 4;
				break;
			case R.id.btnStart5:

				btnStar1.setSelected(true);
				btnStar2.setSelected(true);
				btnStar3.setSelected(true);
				btnStar4.setSelected(true);
				btnStar5.setSelected(true);
				rating = 5;
				break;
			}
			new AsyncUpdateRating(getActivity(), "Updating rating").execute();
		}

	}

	private void updateRating(int rate) {
		if (rate == 1) {
			btnStar1.setSelected(true);
		} else if (rate == 2) {
			btnStar1.setSelected(true);
			btnStar2.setSelected(true);
		} else if (rate == 3) {
			btnStar1.setSelected(true);
			btnStar2.setSelected(true);
			btnStar3.setSelected(true);
		} else if (rate == 4) {
			btnStar1.setSelected(true);
			btnStar2.setSelected(true);
			btnStar3.setSelected(true);
			btnStar4.setSelected(true);
		} else if (rate == 5) {
			btnStar1.setSelected(true);
			btnStar2.setSelected(true);
			btnStar3.setSelected(true);
			btnStar4.setSelected(true);
			btnStar5.setSelected(true);
		}
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
				cv.put("UserId", UserPreferences.getUserId(getActivity()));

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

			downloadDocFileName = fileName;

			if (errorFlag == 0 && fileName.length() > 0) {

			} else if (fileName.equals("")) {
				showMessage("No file to download");
			} else if (errorFlag > 0) {
				showMessage("Unknown error");
			}
		}

	}

}
