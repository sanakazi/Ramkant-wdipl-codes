package com.debalink;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.adapters.CategoryAdapter;
import com.debalink.customviews.CustomEditText;
import com.debalink.customviews.CustomPopup;
import com.debalink.customviews.DrawableClickListener;
import com.debalink.interfaces.RetryAlertCallback;
import com.debalink.models.CategoryModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class AddDiscussionFragment extends Fragment implements OnClickListener, RetryAlertCallback {
	private EditText edtAskQuestion, edtDescription, edtPassword;
	private AutoCompleteTextView edtCategory;
	private ImageView imgPublicPicture;
	private TextView txtFileUpload, txtImgUpload;
	private ImageButton btnCancel, btnSubmit, btnInsertPoll, btnAddMore;
	private ArrayList<CategoryModel> categoryList;
	private int CAMERA_REQUEST = 1888, GALLERY_REQUEST = 1999;
	private Bitmap bitmap;
	private byte[] imgArr;
	private String selectedCatId = "0";
	private LinearLayout llPollOptions;
	private ArrayList<String> optionList;
	private int retryFrom = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_new_discussion, null);

		edtAskQuestion = (EditText) view.findViewById(R.id.edtAskQuestion);
		edtDescription = (EditText) view.findViewById(R.id.edtDescription);
		edtPassword = (EditText) view.findViewById(R.id.edtPassword);
		imgPublicPicture = (ImageView) view.findViewById(R.id.imgPicUpload);
		txtFileUpload = (TextView) view.findViewById(R.id.txtFileUpload);
		txtImgUpload = (TextView) view.findViewById(R.id.txtPicUpload);
		edtCategory = (AutoCompleteTextView) view.findViewById(R.id.edtCategory);

		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnInsertPoll = (ImageButton) view.findViewById(R.id.btnInsertPoll);
		btnSubmit = (ImageButton) view.findViewById(R.id.btnSubmit);
		btnAddMore = (ImageButton) view.findViewById(R.id.btnAddMore);
		llPollOptions = (LinearLayout) view.findViewById(R.id.llPollOptions);

		edtAskQuestion.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtDescription.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtPassword.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtCategory.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtFileUpload.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtImgUpload.setTypeface(Fonts.getRobotoMedium(getActivity()));

		edtCategory.setOnClickListener(this);
		imgPublicPicture.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnInsertPoll.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnAddMore.setOnClickListener(this);
		new AsyncCategory(getActivity(), "Loading category").execute();
		btnAddMore.setVisibility(View.GONE);
		edtCategory.setDropDownHeight(300);
		edtCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (categoryList != null) {
					edtCategory.setText(categoryList.get(position).getCategoryName());
					selectedCatId = categoryList.get(position).getCategoryId();
				}
			}
		});

		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.edtCategory:
			edtCategory.showDropDown();
			break;

		case R.id.imgPicUpload:
			showPicDialog();
			break;

		case R.id.btnSubmit:
			checkValidation();
			break;

		case R.id.btnCancel:
			getFragmentManager().beginTransaction().remove(AddDiscussionFragment.this).commit();
			((HomeActivity) getActivity()).showLastFramgent();
			break;

		case R.id.btnInsertPoll:
			btnInsertPoll.setSelected(!btnInsertPoll.isSelected());

			if (btnInsertPoll.isSelected()) {
				btnAddMore.setVisibility(View.VISIBLE);

				View view = getActivity().getLayoutInflater().inflate(R.layout.insert_poll_row, null);
				CustomEditText edt = (CustomEditText) view.findViewById(R.id.edtOptionName);
				edt.setDrawableClickListener(new OnRemoveClickListener(view));
				llPollOptions.addView(view);

			} else {
				btnAddMore.setVisibility(View.INVISIBLE);
				llPollOptions.removeAllViews();
			}

			break;
		case R.id.btnAddMore:

			if (llPollOptions.getChildCount() < 5) {
				View view = getActivity().getLayoutInflater().inflate(R.layout.insert_poll_row, null);
				CustomEditText edt = (CustomEditText) view.findViewById(R.id.edtOptionName);
				edt.setDrawableClickListener(new OnRemoveClickListener(view));
				llPollOptions.addView(view);
			}

			break;
		}
	}

	private void checkValidation() {

		if (imgArr == null) {
			showMessage("Please select display image");
			return;
		}

		if (edtAskQuestion.getText().toString().isEmpty()) {
			showMessage("Please enter Question");
			return;
		}

		if (edtDescription.getText().toString().isEmpty()) {
			showMessage("Please enter description");
			return;
		}

		if (edtCategory.getText().toString().isEmpty()) {
			showMessage("Please select category");
			return;
		}

		if (btnInsertPoll.isSelected()) {

			optionList = new ArrayList<String>();

			for (int i = 0; i < llPollOptions.getChildCount(); i++) {
				EditText edt = (EditText) llPollOptions.getChildAt(i).findViewById(R.id.edtOptionName);

				if (!edt.getText().toString().isEmpty()) {
					optionList.add(edt.getText().toString());
				} else {
					showMessage("Please enter poll option");
					return;
				}

			}
		}

		new AsyncFileUpload(getActivity(), "Uploading").execute();
	}

	private class AsyncCategory extends AsynDownloader {

		public AsyncCategory(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				res = hr.getSoapObjectFromServer(null, "getCategory");
				categoryList = hr.parseCategoryList(res);
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
			} finally {
				retryFrom = 0;
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {
				edtCategory.setAdapter(new CategoryAdapter(getActivity(), categoryList));
			} else {
				CustomPopup.showPicDialog(getActivity(), errorFlag, AddDiscussionFragment.this);
			}

		}

	}

	private class AsyncFileUpload extends AsynDownloader {

		public AsyncFileUpload(Context ctx, String message) {
			super(ctx, message);
		}

		// private SoapObject res;
		private int errorFlag = 0;
		private String imgUploadResp, resPoll, idDiscussionSubResp;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {
				Locale current = getResources().getConfiguration().locale;
				ContentValues cv = new ContentValues();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmsss", current);
				Date dt = new Date();
				String filename = sdf.format(dt);

				// upload display image
				cv.put("Imagename", "PA" + filename + ".jpg");
				imgUploadResp = hr.uploadFile("DisplayImageDiscussion", cv, imgArr);// true

				// upload details
				ContentValues cvDetails = new ContentValues();
				cvDetails.put("DiscussionTitle", edtAskQuestion.getText().toString());
				cvDetails.put("DiscussionMainContain", edtDescription.getText().toString());
				cvDetails.put("UserId", UserPreferences.getUserId(getActivity()));
				cvDetails.put("CategoryId", selectedCatId);
				cvDetails.put("DisplayImageUrl", "PA" + filename + ".jpg");
				cvDetails.put("LockPassword", edtPassword.getText().toString());
				idDiscussionSubResp = hr.getDataFromServer(cvDetails, "InsertDiscussion");// id
																							// for
																							// success

				// upload poll options
				if (btnInsertPoll.isSelected()) {
					for (int i = 0; i < optionList.size(); i++) {
						ContentValues cvPollOptions = new ContentValues();
						cvPollOptions.put("DiscussionId", idDiscussionSubResp);
						cvPollOptions.put("VoteName", optionList.get(i));
						resPoll = hr.getDataFromServer(cvPollOptions, "InsertDiscussionPoll");
					}

				}

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
			} finally {
				retryFrom = 1;
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {
				showMessage("Discussion submitted successfully");
				getFragmentManager().beginTransaction().remove(AddDiscussionFragment.this).commit();
				((HomeActivity) getActivity()).showLastFramgent();
			}
		}

	}

	private void showPicDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Choose Image Source");
		builder.setItems(new CharSequence[] { "Gallery", "Camera" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:

					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

					break;

				case 1:
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);

					break;

				}
			}
		});

		builder.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {

			if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				photo = Bitmap.createScaledBitmap(photo, imgPublicPicture.getWidth(), imgPublicPicture.getWidth(), true);
				bitmap = photo;
				imgPublicPicture.setImageBitmap(photo);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imgArr = baos.toByteArray();
			}

			else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				// String selectedImagePath = getPath(selectedImageUri);

				Bitmap photo = decodeUri(selectedImageUri);

				photo = Bitmap.createScaledBitmap(photo, imgPublicPicture.getWidth(), imgPublicPicture.getWidth(), true);
				bitmap = photo;
				imgPublicPicture.setImageBitmap(photo);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imgArr = baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public String getPath(Uri photoUri) {
		String filePath = null;
		if (photoUri != null) {
			try {
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(photoUri, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				filePath = cursor.getString(columnIndex);

			} catch (Exception e) {
			}
		}
		return filePath;
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);

		final int REQUIRED_SIZE = 240;

		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);
	}

	private void showMessage(String message) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView txtMessage = (TextView) view.findViewById(R.id.textView1);
		txtMessage.setTypeface(Fonts.getBold(getActivity()));

		txtMessage.setText(message);
		Toast toast = new Toast(getActivity());
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	private class OnRemoveClickListener implements DrawableClickListener {
		private View view;

		public OnRemoveClickListener(View v) {
			view = v;
		}

		@Override
		public void onClick(DrawablePosition target) {

			llPollOptions.removeView(view);
		}

	}

	@Override
	public void retry() {
		if (retryFrom == 0) {
			new AsyncCategory(getActivity(), "Loading category").execute();
		} else if (retryFrom == 1) {
			new AsyncFileUpload(getActivity(), "Uploading").execute();
		}
	}
}
