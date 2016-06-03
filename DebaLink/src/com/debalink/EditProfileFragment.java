package com.debalink;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.CountryAdapter;
import com.debalink.adapters.GenderAdapter;
import com.debalink.models.CountryModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EditProfileFragment extends Fragment implements OnClickListener {
	private ImageButton btnEditBiography, btnEditAccount, btnEditLocation, btnEditProfileUrl, btnImageUpload, btnSave, btnCancel, btnEditPrivacySetting;
	private TextView txtBiography, txtAccount, txtLocation, txtProfileUrl, txtProfilePic, txtPrivacySetting, txtProfileSetting, txtAllowDownload;
	private EditText edtName, edtEmail, edtPassword, edtRetypePassword, edtLocation, edtProfileUrl;
	private ImageView imgProfilePic;
	private AutoCompleteTextView edtGender, edtCountry, edtProfileSetting, edtAllowDownload;
	private ArrayList<CountryModel> countryList;
	private String selectedCatId, pictureId;
	private ImageThreadDownloader imgDownloader;
	private int CAMERA_REQUEST = 1888, GALLERY_REQUEST = 1999;
	private Bitmap bitmap;
	private byte[] imgArr;

	// private CustomEditText edtCountry
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.manage_profile, null);
		btnEditBiography = (ImageButton) view.findViewById(R.id.btnEditBiography);
		btnEditAccount = (ImageButton) view.findViewById(R.id.btnEditAccount);
		btnEditLocation = (ImageButton) view.findViewById(R.id.btnEditLocation);
		btnEditProfileUrl = (ImageButton) view.findViewById(R.id.btnEditProfileUrl);
		btnImageUpload = (ImageButton) view.findViewById(R.id.btnUploadProfilePic);
		btnSave = (ImageButton) view.findViewById(R.id.btnSave);
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnEditPrivacySetting = (ImageButton) view.findViewById(R.id.btnEditPrivacySetting);
		imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);

		txtBiography = (TextView) view.findViewById(R.id.txtBiography);
		txtAccount = (TextView) view.findViewById(R.id.txtAccount);
		txtLocation = (TextView) view.findViewById(R.id.txtLocation);
		txtProfileUrl = (TextView) view.findViewById(R.id.txtProfileUrl);
		txtProfilePic = (TextView) view.findViewById(R.id.txtProfilePic);

		txtPrivacySetting = (TextView) view.findViewById(R.id.txtPrivacySetting);
		txtProfileSetting = (TextView) view.findViewById(R.id.txtProfileSetting);
		txtAllowDownload = (TextView) view.findViewById(R.id.txtDownloadSetting);
		// txtPrivacySetting = (TextView)
		// view.findViewById(R.id.txtPrivacySetting);

		edtName = (EditText) view.findViewById(R.id.edtName);
		edtEmail = (EditText) view.findViewById(R.id.edtEmail);
		edtPassword = (EditText) view.findViewById(R.id.edtPassword);
		edtRetypePassword = (EditText) view.findViewById(R.id.edtRetypePassword);
		edtLocation = (EditText) view.findViewById(R.id.edtLocation);
		edtProfileUrl = (EditText) view.findViewById(R.id.edtProfileUrl);

		edtGender = (AutoCompleteTextView) view.findViewById(R.id.edtGender);
		edtCountry = (AutoCompleteTextView) view.findViewById(R.id.edtCountry);
		edtProfileSetting = (AutoCompleteTextView) view.findViewById(R.id.edtProfileSetting);
		edtAllowDownload = (AutoCompleteTextView) view.findViewById(R.id.edtDownloadSetting);

		edtGender.setAdapter(new GenderAdapter(getActivity(), new String[] { "Male", "Female", "Unspecified" }));

		edtProfileSetting.setAdapter(new GenderAdapter(getActivity(), new String[] { "Private", "Public" }));

		edtAllowDownload.setAdapter(new GenderAdapter(getActivity(), new String[] { "Allow", "Do not allow" }));

		edtCountry.setOnClickListener(this);
		edtGender.setOnClickListener(this);
		edtAllowDownload.setOnClickListener(this);
		edtProfileSetting.setOnClickListener(this);
		btnImageUpload.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		edtName.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtEmail.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtPassword.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtRetypePassword.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtLocation.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtLocation.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtProfileUrl.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtProfileSetting.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtAllowDownload.setTypeface(Fonts.getRobotoMedium(getActivity()));

		txtBiography.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtAccount.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtLocation.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtProfileUrl.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtProfilePic.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtPrivacySetting.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtProfileSetting.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtAllowDownload.setTypeface(Fonts.getRobotoMedium(getActivity()));

		imgDownloader = new ImageThreadDownloader(getActivity(), getActivity(), getImageWidth(), getImageWidth());

		imgProfilePic.setLayoutParams(new RelativeLayout.LayoutParams(getImageWidth(), getImageWidth()));

		btnEditBiography.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnEditBiography.setSelected(!btnEditBiography.isSelected());

				if (btnEditBiography.isSelected()) {
					edtName.setEnabled(false);
					edtGender.setEnabled(false);
					edtCountry.setEnabled(false);
				} else {
					edtName.setEnabled(true);
					edtGender.setEnabled(true);
					edtCountry.setEnabled(true);
				}

			}
		});

		btnEditBiography.performClick();

		btnEditAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnEditAccount.setSelected(!btnEditAccount.isSelected());

				if (btnEditAccount.isSelected()) {
					edtEmail.setEnabled(false);
					edtPassword.setEnabled(false);
					edtRetypePassword.setEnabled(false);
				} else {
					edtEmail.setEnabled(true);
					edtPassword.setEnabled(true);
					edtRetypePassword.setEnabled(true);
				}

			}
		});

		btnEditAccount.performClick();

		btnEditLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnEditLocation.setSelected(!btnEditLocation.isSelected());

				if (btnEditLocation.isSelected()) {
					edtLocation.setEnabled(false);

				} else {
					edtLocation.setEnabled(true);

				}

			}
		});

		btnEditLocation.performClick();
		;

		btnEditProfileUrl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnEditProfileUrl.setSelected(!btnEditProfileUrl.isSelected());

				if (btnEditProfileUrl.isSelected()) {
					edtProfileUrl.setEnabled(false);

				} else {
					edtProfileUrl.setEnabled(true);

				}

			}
		});

		btnEditProfileUrl.performClick();

		edtCountry.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (countryList != null) {
					edtCountry.setText(countryList.get(position).getCountryName());
					selectedCatId = countryList.get(position).getCountryId();
				}
			}
		});

		btnEditPrivacySetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnEditPrivacySetting.setSelected(!btnEditPrivacySetting.isSelected());

				if (btnEditPrivacySetting.isSelected()) {
					edtProfileSetting.setEnabled(false);
					edtAllowDownload.setEnabled(false);

				} else {
					edtProfileSetting.setEnabled(true);
					edtAllowDownload.setEnabled(true);
				}

			}
		});

		btnEditPrivacySetting.performClick();
		new AsyncCountry(getActivity(), "Loading").execute();

		return view;
	}

	private class AsyncCountry extends AsynDownloader {

		public AsyncCountry(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res, resPrivacy;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				res = hr.getSoapObjectFromServer(null, "SelectAllCountry");
				countryList = hr.parseCountryList(res);
				ContentValues cv = new ContentValues();
				cv.put("UserID", UserPreferences.getUserId(getActivity().getApplicationContext()));

				res = hr.getSoapObjectFromServer(cv, "SelectUserProfile");

				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				resPrivacy = hr.getSoapObjectFromServer(cv, "SelectUserSetting");

				Log.i("SelectAllCountry", res.toString());

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
				edtCountry.setAdapter(new CountryAdapter(getActivity(), countryList));
				parseViewDiscussionResponse(res);
				parsePrivacySettingResponse(resPrivacy);
			}

		}

	}

	private class AsyncUpdateProfile extends AsynDownloader {

		public AsyncUpdateProfile(Context ctx, String message) {
			super(ctx, message);
		}

		private String res, insertPrfilePicRes, uploadPicResp = "false";
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				String name, gender;
				name = edtName.getText().toString();
				gender = edtGender.getText().toString();

				// upload picture

				Date dt = new Date();

				Locale current = getResources().getConfiguration().locale;
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmsss", current);
				String filename = sdf.format(dt);
				filename = "profilepic" + filename + ".jpeg";
				ContentValues cvPicUpload = new ContentValues();
				cvPicUpload.put("docname", filename);

				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// profilePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				// byte[] b = baos.toByteArray();

				if (imgArr != null) {
					uploadPicResp = hr.uploadFile("SaveProfilePicture", cvPicUpload, imgArr);
				}

				if (uploadPicResp.equals("true")) {
					ContentValues c = new ContentValues();
					c.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					c.put("PhotoUrl", filename);
					c.put("ProfilePictureFlag", "0");
					c.put("CurrentProfilePictureFlag", "1");
					c.put("PictureId", pictureId);
					insertPrfilePicRes = hr.getDataFromServer(c, "InsertUserPicture");
					Log.i("insertPrfilePicRes", insertPrfilePicRes);
					UserPreferences.setProfilePicture(getActivity(), filename);
				}

				// update biography

				if (!btnEditBiography.isSelected()) {
					ContentValues cv = new ContentValues();
					cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cv.put("Gender", gender);
					cv.put("CountryId", selectedCatId);
					res = hr.getDataFromServer(cv, "UpdateUserBiographyDetails");
				}

				if (!btnEditAccount.isSelected()) {
					// update account
					ContentValues cvAccount = new ContentValues();
					cvAccount.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cvAccount.put("Password", edtPassword.getText().toString());
					cvAccount.put("EmailId", edtEmail.getText().toString());
					res = hr.getDataFromServer(cvAccount, "UpdateUserAccountDetails");
				}

				if (!btnEditLocation.isSelected()) {
					// update current location
					ContentValues cvCurrentLocation = new ContentValues();
					cvCurrentLocation.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cvCurrentLocation.put("CurrentLocation", edtLocation.getText().toString());
					res = hr.getDataFromServer(cvCurrentLocation, "UpdateUserCurrentLocation");
				}

				if (!btnEditProfileUrl.isSelected()) {
					// update profile url
					ContentValues cvProfileUrl = new ContentValues();
					cvProfileUrl.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cvProfileUrl.put("CurrentLocation", edtProfileUrl.getText().toString());
					res = hr.getDataFromServer(cvProfileUrl, "UpdateUserProfilUrl");
				}

				if (!btnEditPrivacySetting.isSelected()) {
					// update profile url
					String strProfileSetting = edtProfileSetting.getText().toString();
					ContentValues cvPrivacy = new ContentValues();
					cvPrivacy.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					if (strProfileSetting.equals("Private")) {
						cvPrivacy.put("IsAccess", "true");
					} else {
						cvPrivacy.put("IsAccess", "false");
					}
					res = hr.getDataFromServer(cvPrivacy, "UpdatePrivacySettings");
				}

				if (!btnEditPrivacySetting.isSelected()) {
					// update profile url
					String strProfileSetting = edtProfileSetting.getText().toString();
					ContentValues cvPrivacy = new ContentValues();
					cvPrivacy.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					if (strProfileSetting.equals("Private")) {
						cvPrivacy.put("IsAccess", "true");
					} else {
						cvPrivacy.put("IsAccess", "false");
					}
					res = hr.getDataFromServer(cvPrivacy, "UpdatePrivacySettings");
				}

				if (!btnEditPrivacySetting.isSelected()) {
					// update profile url
					String strProfileSetting = edtAllowDownload.getText().toString();
					ContentValues cvPrivacy = new ContentValues();
					cvPrivacy.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					if (strProfileSetting.equals("Allow")) {
						cvPrivacy.put("IsAccess", "0");
					} else {
						cvPrivacy.put("IsAccess", "1");
					}
					res = hr.getDataFromServer(cvPrivacy, "InsertUpdateFileDownloadSetting");
				}

				Log.i("UpdateUserBiographyDetails", res.toString());

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

			if (errorFlag == 0 && res != null && res.equals("0")) {
				btnEditBiography.setSelected(true);
				btnEditLocation.setSelected(true);
				btnEditAccount.setSelected(true);
				btnEditProfileUrl.setSelected(true);
				showMessage("Profile updated successfully");
				getFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
				((HomeActivity) getActivity()).showLastFramgent();
			}
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.edtCountry:
			edtCountry.showDropDown();
			break;

		case R.id.edtGender:
			edtGender.showDropDown();
			break;

		case R.id.btnUploadProfilePic:
			showPicDialog();
			break;

		case R.id.edtProfileSetting:
			edtProfileSetting.showDropDown();
			break;

		case R.id.edtDownloadSetting:
			edtAllowDownload.showDropDown();
			break;

		case R.id.btnSave:
			checkValidation();
			break;

		case R.id.btnCancel:

			getFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
			((HomeActivity) getActivity()).showLastFramgent();
			break;

		}

	}

	private void parseViewDiscussionResponse(SoapObject res) {

		SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

		SoapObject newDataSetObject;

		newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		newDataSetObject = (SoapObject) newDataSetObject.getProperty(0);

		edtName.setText(newDataSetObject.getPropertySafelyAsString("Name", ""));
		edtEmail.setText(newDataSetObject.getPropertySafelyAsString("EmailId", ""));
		edtGender.setText(newDataSetObject.getPropertySafelyAsString("Gender", ""));
		edtProfileUrl.setText(newDataSetObject.getPropertySafelyAsString("ProfileUrl", ""));
		edtLocation.setText(newDataSetObject.getPropertySafelyAsString("CurrentLocation", ""));

		pictureId = newDataSetObject.getPropertySafelyAsString("PictureId", "0");

		for (CountryModel c : countryList) {
			if (c.getCountryId().equalsIgnoreCase(newDataSetObject.getPropertySafelyAsString("CountryId", ""))) {
				edtCountry.setText(c.getCountryName());
				selectedCatId = newDataSetObject.getPropertySafelyAsString("CountryId", "");
			}
		}

		String imgUrl;
		imgUrl = Contant.PIC_URL + newDataSetObject.getPropertySafelyAsString("PhotoUrl", "");

		imgProfilePic.setTag(imgUrl);
		imgDownloader.displayImage(imgUrl, new WeakReference<ImageView>(imgProfilePic));

		UserPreferences.setProfilePicture(getActivity(), newDataSetObject.getPropertySafelyAsString("PhotoUrl", ""));

	}

	private void parsePrivacySettingResponse(SoapObject res) {

		SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

		SoapObject newDataSetObject;

		newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		newDataSetObject = (SoapObject) newDataSetObject.getProperty(0);

		if (newDataSetObject.getPropertySafelyAsString("IsFileDownload", "").equals("0")) {
			edtAllowDownload.setText("Allow");
		} else {

			edtAllowDownload.setText("Do not allow");
		}

		if (newDataSetObject.getPropertySafelyAsString("IsAccess", "").equals("false")) {
			edtProfileSetting.setText("Public");
		} else {
			edtProfileSetting.setText("Private");
		}
	}

	private int getImageWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.00));

		int imageWidth = (width / 3) - plus;

		return imageWidth;
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
				photo = Bitmap.createScaledBitmap(photo, imgProfilePic.getWidth(), imgProfilePic.getWidth(), true);
				bitmap = photo;
				imgProfilePic.setImageBitmap(photo);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imgArr = baos.toByteArray();
			}

			else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				// String selectedImagePath = getPath(selectedImageUri);

				Bitmap photo = decodeUri(selectedImageUri);

				photo = Bitmap.createScaledBitmap(photo, imgProfilePic.getWidth(), imgProfilePic.getWidth(), true);
				bitmap = photo;
				imgProfilePic.setImageBitmap(photo);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imgArr = baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

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

	private void checkValidation() {

		// if (imgArr == null) {
		// showMessage("Please select display image");
		// return;
		// }

		boolean flag = false;

		if (!btnEditBiography.isSelected()) {

			if (edtName.getText().toString().isEmpty()) {
				showMessage("Please enter name");
				return;
			}

			if (edtGender.getText().toString().isEmpty()) {
				showMessage("Please select gender");
				return;
			}

			if (edtCountry.getText().toString().isEmpty()) {
				showMessage("Please select gender");
				return;
			}
			flag = true;
		}

		if (!btnEditAccount.isSelected()) {

			String cnfPwd = edtPassword.getText().toString();
			String cnfRePwd = edtPassword.getText().toString();

			if (edtEmail.getText().toString().isEmpty()) {
				showMessage("Please enter email");
				return;
			}

			if (cnfPwd.equals("")) {
				showMessage("Please Enter Passoword");
				return;
			}

			if (cnfRePwd.equals("")) {
				showMessage("Please Enter Confirm Passoword");
				return;
			}

			if (!cnfPwd.equals(cnfRePwd)) {
				showMessage("Passwords are not matching");
				return;
			}
			flag = true;
		}

		if (!btnEditLocation.isSelected()) {
			if (edtLocation.getText().toString().isEmpty()) {
				showMessage("Please enter location");
				return;
			}
		}

		if (!btnEditProfileUrl.isSelected()) {
			if (edtLocation.getText().toString().isEmpty()) {
				showMessage("Please enter profile url");
				return;
			}
			flag = true;
		}

		if (!btnEditPrivacySetting.isSelected()) {

			flag = true;
		}

		if (imgArr != null) {
			flag = true;
		}

		if (flag == true) {
			new AsyncUpdateProfile(getActivity(), "Updating").execute();
		}
	}

}
