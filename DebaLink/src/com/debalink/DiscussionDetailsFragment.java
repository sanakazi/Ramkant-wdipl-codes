package com.debalink;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.CommentsAdapter;
import com.debalink.adapters.DiscussionsAdapter;
import com.debalink.adapters.PollOptionsAdapter;
import com.debalink.models.CommentModel;
import com.debalink.models.PollOptionModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DiscussionDetailsFragment extends Fragment implements View.OnClickListener {
	private TextView txtLike, txtReport, txtSharing, txtRating, txtComments, txtPollHeading, txtDiscussionTitle, txtAvgRating, txtDiscussionDetails;
	private ImageView imgDiscussionPic, imgProfileic;
	private String discussionId, noOfComments,from;
	private ImageThreadDownloader imgDownloader;
	private LinearLayout discussionOptions;
	private ArrayList<PollOptionModel> pollList;
	private ArrayList<CommentModel> commentList;
	private PollOptionsAdapter pollAdapter;
	private ListView lvComments;
	private View headerAddComment;
	private EditText edtComment;
	private ImageButton btnPostComment;
	private ImageButton btnLoadMore;
	private View footer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.discussion_details, null);

		view.bringToFront();

		txtLike = (TextView) view.findViewById(R.id.txtLike);
		txtReport = (TextView) view.findViewById(R.id.txtReport);
		txtRating = (TextView) view.findViewById(R.id.txtRatings);
		txtSharing = (TextView) view.findViewById(R.id.txtSharing);
		txtComments = (TextView) view.findViewById(R.id.txtComments);
		txtPollHeading = (TextView) view.findViewById(R.id.txtPollHeading);
		txtDiscussionTitle = (TextView) view.findViewById(R.id.txtDiscussionTitle);
		txtAvgRating = (TextView) view.findViewById(R.id.txtAvgRating);
		txtDiscussionDetails = (TextView) view.findViewById(R.id.txtDiscussionDetails);
		discussionOptions = (LinearLayout) view.findViewById(R.id.discussion_options);
		lvComments = (ListView) view.findViewById(R.id.lvComments);

		footer = getActivity().getLayoutInflater().inflate(R.layout.list_more_footer_view, null);
		btnLoadMore = (ImageButton) footer.findViewById(R.id.button1);

		txtPollHeading.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtDiscussionTitle.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtAvgRating.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtLike.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtReport.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtRating.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtSharing.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtAvgRating.setTypeface(Fonts.getRobotoRegular(getActivity()));

		txtComments.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtReport.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtSharing.setTypeface(Fonts.getRobotoMedium(getActivity()));

		txtDiscussionDetails.setTypeface(Fonts.getRobotoRegular(getActivity()));
		txtPollHeading.setOnClickListener(this);

		imgDiscussionPic = (ImageView) view.findViewById(R.id.imgDiscussionPic);

		discussionId = getArguments().getString("discussion_id");
		noOfComments = getArguments().getString("no_of_comments");
		from= getArguments().getString("from");

		txtComments.setText("Comments" + "(" + noOfComments + ")");

		imgDownloader = new ImageThreadDownloader(getActivity(), getActivity(), getImageWidth(), getImageWidth());

		imgDiscussionPic.setLayoutParams(new LinearLayout.LayoutParams(getImageWidth(), getImageWidth()));

		txtDiscussionDetails.setClickable(true);
		txtDiscussionDetails.setMovementMethod(LinkMovementMethod.getInstance());

		headerAddComment = getActivity().getLayoutInflater().inflate(R.layout.add_comment, null);
		lvComments.addHeaderView(headerAddComment);

		edtComment = (EditText) headerAddComment.findViewById(R.id.edtComment);
		btnPostComment = (ImageButton) headerAddComment.findViewById(R.id.btnPostComment);
		imgProfileic = (ImageView) headerAddComment.findViewById(R.id.imgProfilePic);

		displayImg();

		view.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if (keyCode == KeyEvent.KEYCODE_BACK) {
				// if(DiscussionsAdapter.pinnedView!=null){
				DiscussionsAdapter.pinnedView.setVisibility(View.VISIBLE);
				// }

				// return true;
				// }
				return false;
			}
		});
		
		
		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				
				return true;
			}
			
		});
		

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

				new ReportPost(getActivity(), "Reporting Disscussion").execute();

			}
		});

		btnLoadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncMoreComments(getActivity(), "Loading More comments").execute();
			}
		});

		new AsyncDiscussionDetails(getActivity(), "Loading").execute();

		return view;
	}

	@Override
	public void onDetach() {

		super.onDetach();
		
		if (DiscussionsAdapter.pinnedView != null&&from.equals("pinned")) {
			DiscussionsAdapter.pinnedView.setVisibility(View.VISIBLE);
			
		}

	}

	private class AsyncDiscussionDetails extends AsynDownloader {
		private SoapObject res, pollRes, commentRes;
		private int errorFlag = 0;

		public AsyncDiscussionDetails(Context ctx, String message) {
			super(ctx, message);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();

				cv.put("DiscussionId", discussionId);

				res = hr.getSoapObjectFromServer(cv, "ViewDiscussion");

				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				pollRes = hr.getSoapObjectFromServer(cv, "SelectDiscussionPollVoteName");
				pollList = hr.parsePollList(pollRes);

				cv.put("Mode", "Top");

				commentRes = hr.getSoapObjectFromServer(cv, "DisplayDiscussionComment");
				commentList = hr.parseDiscussionCommentsResponse(commentRes);
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

			if (errorFlag == 0) {
				try {
					parseViewDiscussionResponse(res);
				} catch (Exception e) {
				}

				for (int i = 0; i < pollList.size(); i++) {
					View row = getActivity().getLayoutInflater().inflate(R.layout.discussion_poll_options, null);
					TextView txtOption1 = (TextView) row.findViewById(R.id.txtOption1);
					txtOption1.setText(pollList.get(i).getVoteName());
					RadioButton rbAnswer1 = (RadioButton) row.findViewById(R.id.rbAnswer1);
					rbAnswer1.setText(pollList.get(i).getVoteCount() + " Vote");
					if (pollList.get(i).getIsVote().equalsIgnoreCase("1")) {
						rbAnswer1.setChecked(true);

					}
					rbAnswer1.setEnabled(false);
					discussionOptions.addView(row);
				}

				pollAdapter = new PollOptionsAdapter(getActivity(), pollList);

				if (pollList.size() == 0) {
					txtPollHeading.setVisibility(View.INVISIBLE);
				}

				lvComments.setAdapter(new CommentsAdapter(getActivity(), commentList));

				if (commentList.size() > 0)
					txtComments.setText("Comments" + "(" + commentList.get(0).getCommentCnt() + ")");

				if (commentList.size() > 3) {
					if (lvComments.getFooterViewsCount() == 0)
						lvComments.addFooterView(footer);
				}
			}
		}

	}

	private void parseViewDiscussionResponse(SoapObject res) {

		SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		// if (diffgramObject.hasProperty("NewDataSet")) {
		newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		// } //else {
		// return new ArrayList<HeadlinesModel>();
		// }

		SoapObject resDiscussionDetails, resAvgRating, resLikeStatus;
		resDiscussionDetails = (SoapObject) newDataSetObject.getProperty(0);
		resLikeStatus = (SoapObject) newDataSetObject.getProperty(1);
		resAvgRating = (SoapObject) newDataSetObject.getProperty(2);

		txtDiscussionTitle.setText(resDiscussionDetails.getPropertySafelyAsString("DiscussionTitle", ""));
		txtDiscussionDetails.setText(Html.fromHtml(resDiscussionDetails.getPropertySafelyAsString("DiscussionMainContain", "")));
		txtLike.setText(resLikeStatus.getPropertySafelyAsString("IslikeStatus", ""));
		// txtComments.setText(resCommentsCount.getPropertySafelyAsString("DiscussionTitle",
		// ""));
		txtAvgRating.setText(resAvgRating.getPropertySafelyAsString("RatingCount", "") + " User have rated,Avg rating is "
				+ resAvgRating.getPropertySafelyAsString("AverageRating", ""));
		// txtDiscussionTitle.setText(resDiscussionDetails.getPropertySafelyAsString("DiscussionTitle",
		// ""));

		String imgUrl;
		imgUrl = Contant.DISCUSSION_PIC_URL + resDiscussionDetails.getPropertySafelyAsString("DisplayImageUrl", "");

		imgDiscussionPic.setTag(imgUrl);
		imgDownloader.displayImage(imgUrl, new WeakReference<ImageView>(imgDiscussionPic));

	}

	private int getImageWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.00));

		int imageWidth = (width / 4) - plus;

		return imageWidth;
	}

	private void showChoiceDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Share your opinion");

		final ListView listview = new ListView(getActivity());
		listview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		listview.setCacheColorHint(0);
		listview.setBackgroundColor(Color.WHITE);
		listview.setAdapter(pollAdapter);
		builder.setView(listview);

		final Dialog dialog = builder.create();
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				dialog.dismiss();
				listview.setSelection(position);

				for (int i = 0; i < pollList.size(); i++) {
					if (i == position) {
						pollList.get(i).setSelected(true);
						Log.i("if", position + " " + pollList.get(i).isSelected() + "");
					} else {
						pollList.get(i).setSelected(false);
						Log.i("else", position + " " + pollList.get(i).isSelected() + "");
					}
				}
				Log.i("onItemClick", position + " " + pollList.get(position).isSelected() + "");
				// pollAdapter.notifyDataSetChanged();

				pollAdapter = null;
				pollAdapter = new PollOptionsAdapter(getActivity(), pollList);
				listview.setAdapter(pollAdapter);

				if (pollList.size() == 0) {
					txtPollHeading.setVisibility(View.INVISIBLE);
				}

				new UpdateVotes(getActivity(), "Updating vote", pollList.get(position).getVoteTypeId()).execute();

			}
		});

		listview.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		dialog.show();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.txtPollHeading:
			showChoiceDialog();
			break;

		default:
			break;
		}

	}

	private class UpdateVotes extends AsynDownloader {
		private SoapObject pollRes;
		private int errorFlag = 0;
		private String voteId;
		private String res;

		public UpdateVotes(Context ctx, String message, String voteId) {
			super(ctx, message);
			this.voteId = voteId;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cvPoll = new ContentValues();

				cvPoll.put("DVoteTypeId", voteId);
				cvPoll.put("DiscussionId", discussionId);
				cvPoll.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cvPoll.put("IsVote", "1");
				res = hr.getDataFromServer(cvPoll, "InsertUpdateDiscussioPoll");

				ContentValues cv = new ContentValues();
				cv.put("DiscussionId", discussionId);
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				pollRes = hr.getSoapObjectFromServer(cv, "SelectDiscussionPollVoteName");
				pollList = hr.parsePollList(pollRes);

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

			if (errorFlag == 0) {

				// for(int i=1;i<discussionOptions.getChildCount();i++){
				discussionOptions.removeViewsInLayout(1, discussionOptions.getChildCount() - 1);
				// }

				for (int i = 0; i < pollList.size(); i++) {
					View row = getActivity().getLayoutInflater().inflate(R.layout.discussion_poll_options, null);
					TextView txtOption1 = (TextView) row.findViewById(R.id.txtOption1);
					txtOption1.setText(pollList.get(i).getVoteName());
					RadioButton rbAnswer1 = (RadioButton) row.findViewById(R.id.rbAnswer1);
					rbAnswer1.setText(pollList.get(i).getVoteCount() + " Vote");
					if (pollList.get(i).getIsVote().equalsIgnoreCase("1")) {
						rbAnswer1.setChecked(true);

					}
					rbAnswer1.setEnabled(false);
					discussionOptions.addView(row);
				}
				if (pollList.size() == 0) {
					txtPollHeading.setVisibility(View.INVISIBLE);
				}
				pollAdapter = new PollOptionsAdapter(getActivity(), pollList);
			}

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
				cv.put("DiscussionId", discussionId);
				cv.put("Description", comment);
				cv.put("SenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				res = hr.getDataFromServer(cv, "InsertDiscussionComment");

				cv.put("Mode", "Top");
				commentRes = hr.getSoapObjectFromServer(cv, "DisplayDiscussionComment");
				commentList = hr.parseDiscussionCommentsResponse(commentRes);

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
				cv.put("DiscussionId", discussionId);
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
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
			}else if(errorFlag == 0 && res.equals("2")){
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
		private SoapObject res, pollRes, commentRes;
		private int errorFlag = 0;

		public AsyncMoreComments(Context ctx, String message) {
			super(ctx, message);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();

				cv.put("DiscussionId", discussionId);
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("Mode", "More");

				commentRes = hr.getSoapObjectFromServer(cv, "DisplayDiscussionComment");
				commentList = hr.parseDiscussionCommentsResponse(commentRes);
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

			if (errorFlag == 0) {
				lvComments.setAdapter(new CommentsAdapter(getActivity(), commentList));
				int commentCount = commentList.size();

				txtComments.setText("Comments" + "(" + commentCount + ")");

				if (lvComments.getFooterViewsCount() != 0)
					lvComments.removeFooterView(footer);

			}
		}

	}
}
