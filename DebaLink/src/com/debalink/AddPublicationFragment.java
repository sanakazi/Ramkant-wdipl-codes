package com.debalink;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.adapters.CategoryAdapter;
import com.debalink.adapters.GenderAdapter;
import com.debalink.customviews.CustomEditText;
import com.debalink.customviews.DrawableClickListener;
import com.debalink.models.CategoryModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class AddPublicationFragment extends Fragment implements OnDateSetListener, OnClickListener {

	private EditText edtTitle, edtDescription, edtVideoTitle, edtVideoUrl, edtPassword;
	private CustomEditText edtStartDate, edtEndDate;
	private AutoCompleteTextView edtIsAccess, edtCategory;
	private ImageView imgPublicPicture, imgDocUpload;
	private TextView txtFileUpload, txtImgUpload;
	private ImageButton btnCancel, btnSubmit;
	private int year, month, day;
	private boolean fromDate = false;
	private ArrayList<CategoryModel> categoryList;
	private int CAMERA_REQUEST = 1888, GALLERY_REQUEST = 1999, FILE_REQUEST = 1777;
	private Bitmap bitmap;
	private byte[] imgArr, fileArr;
	private String docName, fileFormat, fileName, selectedCatId = "0", selectedAccessType = "0";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_new_publication, null);

		edtTitle = (EditText) view.findViewById(R.id.edtTitle);
		edtDescription = (EditText) view.findViewById(R.id.edtDescription);
		edtVideoTitle = (EditText) view.findViewById(R.id.edtVideoTitle);
		edtVideoUrl = (EditText) view.findViewById(R.id.edtVideoUrl);
		edtPassword = (EditText) view.findViewById(R.id.edtPassword);
		imgPublicPicture = (ImageView) view.findViewById(R.id.imgPicUpload);
		imgDocUpload = (ImageView) view.findViewById(R.id.imgFileUpload);

		edtStartDate = (CustomEditText) view.findViewById(R.id.edtStartDate);
		edtEndDate = (CustomEditText) view.findViewById(R.id.edtEndDate);

		txtFileUpload = (TextView) view.findViewById(R.id.txtFileUpload);
		txtImgUpload = (TextView) view.findViewById(R.id.txtPicUpload);

		edtIsAccess = (AutoCompleteTextView) view.findViewById(R.id.edtIsAccess);
		edtCategory = (AutoCompleteTextView) view.findViewById(R.id.edtCategory);

		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnSubmit = (ImageButton) view.findViewById(R.id.btnSubmit);

		edtTitle.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtDescription.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtVideoTitle.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtVideoUrl.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtPassword.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtStartDate.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtEndDate.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtIsAccess.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtCategory.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtFileUpload.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtImgUpload.setTypeface(Fonts.getRobotoMedium(getActivity()));

		edtIsAccess.setOnClickListener(this);
		edtCategory.setOnClickListener(this);
		imgPublicPicture.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		imgDocUpload.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		edtIsAccess.setAdapter(new GenderAdapter(getActivity(), new String[] { "Public", "Private" }));

		edtIsAccess.setSelection(0);

		new AsyncCategory(getActivity(), "Loading category").execute();

		edtStartDate.setDrawableClickListener(new DrawableClickListener() {
			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:
					fromDate = false;
					FragmentTransaction ftStartDate = getFragmentManager().beginTransaction();
					DialogFragment newFragmentStartDate = new DatePickerDialogFragment(AddPublicationFragment.this);
					newFragmentStartDate.show(ftStartDate, "dialog");
					break;
				default:
				}
			}

		});

		edtEndDate.setDrawableClickListener(new DrawableClickListener() {
			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:
					fromDate = true;
					FragmentTransaction ftEndDate = getFragmentManager().beginTransaction();
					DatePickerDialogFragment newFragmentEndDate = new DatePickerDialogFragment(AddPublicationFragment.this);
					newFragmentEndDate.setMinStartDate(year, month, day);
					newFragmentEndDate.show(ftEndDate, "dialog");
					break;
				default:
				}
			}
		});
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
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

		if (fromDate) {
			edtEndDate.setText(String.format("%02d", monthOfYear + 1) + "/" + String.format("%02d", dayOfMonth) + "/" + year);
		} else {
			edtStartDate.setText(String.format("%02d", monthOfYear + 1) + "/" + String.format("%02d", dayOfMonth) + "/" + year);
		}

		this.year = year;
		this.month = monthOfYear;
		this.day = dayOfMonth;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.edtIsAccess:
			edtIsAccess.showDropDown();
			break;

		case R.id.edtCategory:
			edtCategory.showDropDown();
			break;

		case R.id.imgPicUpload:
			showPicDialog();
			break;

		case R.id.btnSubmit:
			checkValidation();
			// new AsyncFileUpload(getActivity(), "Uploading").execute();
			break;

		case R.id.btnCancel:
			getFragmentManager().beginTransaction().remove(AddPublicationFragment.this).commit();
			((HomeActivity) getActivity()).showLastFramgent();
			break;

		case R.id.imgFileUpload:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*;file/*");
			try {

				intent.getType();

				startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_REQUEST);
			} catch (android.content.ActivityNotFoundException ex) {
				// Potentially direct the user to the Market with a
				// Dialog

			}
			break;
		}
	}

	private void checkValidation() {

		if (imgArr == null) {
			showMessage("Please select display image");
			return;
		}

		if (edtTitle.getText().toString().isEmpty()) {
			showMessage("Please enter title");
			return;
		}

		if (edtDescription.getText().toString().isEmpty()) {
			showMessage("Please enter description");
			return;
		}

		if (!edtVideoUrl.getText().toString().isEmpty() && edtVideoTitle.getText().toString().isEmpty()) {
			showMessage("Please enter video title");
			return;
		}

		if (!edtVideoTitle.getText().toString().isEmpty() && edtVideoUrl.getText().toString().isEmpty()) {
			showMessage("Please enter video url");
			return;
		}

		if (edtCategory.getText().toString().isEmpty()) {
			showMessage("Please select category");
			return;
		}

		if (edtStartDate.getText().toString().isEmpty()) {
			showMessage("Please enter start date");
			return;
		}

		if (edtEndDate.getText().toString().isEmpty()) {
			showMessage("Please enter end date");
			return;
		}

		if (validateDate(edtStartDate.getText().toString(), edtEndDate.getText().toString())) {
			showMessage("StartDate must be less than or equal to EndDate");
			return;
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
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			edtCategory.setAdapter(new CategoryAdapter(getActivity(), categoryList));
		}

	}

	private class AsyncFileUpload extends AsynDownloader {

		public AsyncFileUpload(Context ctx, String message) {
			super(ctx, message);
		}

		//private SoapObject res;
		private int errorFlag = 0;
		private String imgUploadResp, fileImageUploadRes, fileSubRes, idPublicationSubResp;

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
				imgUploadResp = hr.uploadFile("DisplayImagePublication", cv, imgArr);// true for success

				// upload doc file
				if (fileArr != null) {
					AddPublicationFragment.this.fileName = "publication" + filename + "." + fileFormat;
					cv.put("Imagename", AddPublicationFragment.this.fileName);
					fileImageUploadRes = hr.uploadFile("DisplayImagePublication", cv, fileArr);// true for success
				}

				// submit publication details
				if (edtIsAccess.getText().toString().equalsIgnoreCase("private")) {
					selectedAccessType = "1";
				} else {
					selectedAccessType = "0";
				}

				ContentValues cvDetails = new ContentValues();
				cvDetails.put("PublicationName", edtTitle.getText().toString());
				cvDetails.put("VideoURL", edtVideoUrl.getText().toString());
				cvDetails.put("VideoTitle", edtVideoTitle.getText().toString());
				cvDetails.put("PublicationContain", edtDescription.getText().toString());
				cvDetails.put("UserID", UserPreferences.getUserId(getActivity()));
				cvDetails.put("CategoryId", selectedCatId);
				cvDetails.put("IsAccess", selectedAccessType);
				cvDetails.put("StartPublishing", edtStartDate.getText().toString());
				cvDetails.put("FinishPublishing", edtEndDate.getText().toString());
				cvDetails.put("DisplayImage", "PA" + filename + ".jpg");
				cvDetails.put("LockPassword", edtPassword.getText().toString());
				idPublicationSubResp = hr.getDataFromServer(cvDetails, "InsertPublicationDetails");//id for success

				// submit doc file details

				if (fileArr != null) {
					ContentValues cvFileDetails = new ContentValues();
					cvFileDetails.put("PublicationId", idPublicationSubResp);
					cvFileDetails.put("PFileURL", AddPublicationFragment.this.fileName);
					fileSubRes = hr.getDataFromServer(cvFileDetails, "InsertPublicationFiles");
					// 0 for success
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
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {
				showMessage("Publication submitted successfully");
				getFragmentManager().beginTransaction().remove(AddPublicationFragment.this).commit();
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

			} else if (requestCode == FILE_REQUEST && resultCode == Activity.RESULT_OK) {
				Uri Fpath = data.getData();
				String filePath = getPath(Fpath);
				docName = filePath.substring(filePath.lastIndexOf("/") + 1);

				fileFormat = docName.substring(docName.lastIndexOf(".") + 1);

				if (fileFormat.equalsIgnoreCase("rar") || fileFormat.equalsIgnoreCase("zip")) {

				}

				File file = new File(filePath);
				Log.i("File path" + docName, filePath);

				byte[] b = new byte[(int) file.length()];
				FileInputStream fileInputStream = new FileInputStream(file);
				try {

					fileInputStream.read(b);

					fileArr = b;

					txtFileUpload.setText("1 file selected");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (fileInputStream != null)
						fileInputStream.close();
				}

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

	private boolean validateDate(String startDate, String endDate) {
		Date dtStart, dtEnd;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			dtStart = sdf.parse(startDate);
			dtEnd = sdf.parse(endDate);
			return dtStart.after(dtEnd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
