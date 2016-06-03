package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.adapters.GenderAdapter;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class EditPrivacySettingsFragment extends Fragment implements OnClickListener {
	private ImageButton btnSave, btnCancel, btnEditPrivacySetting, btnFeedback, btnRateTheApp, btnPrivacy, btnAboutUs, btnFAQ, btnTermOfServices, btnLogout;
	private TextView txtPrivacySetting;
	private AutoCompleteTextView edtProfileSetting, edtAllowDownload;

	// private CustomEditText edtCountry
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.privacy_settings, null);

		btnSave = (ImageButton) view.findViewById(R.id.btnSave);
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnEditPrivacySetting = (ImageButton) view.findViewById(R.id.btnEditPrivacySetting);
		btnFeedback = (ImageButton) view.findViewById(R.id.btnFeedback);
		btnRateTheApp = (ImageButton) view.findViewById(R.id.btnRateTheApp);
		btnPrivacy = (ImageButton) view.findViewById(R.id.btnPrivacy);
		btnAboutUs = (ImageButton) view.findViewById(R.id.btnAboutUs);
		btnFAQ = (ImageButton) view.findViewById(R.id.btnFaq);
		btnTermOfServices = (ImageButton) view.findViewById(R.id.btnTermsOfService);
		btnLogout = (ImageButton) view.findViewById(R.id.btnLogout);

		txtPrivacySetting = (TextView) view.findViewById(R.id.txtPrivacySetting);
		edtProfileSetting = (AutoCompleteTextView) view.findViewById(R.id.edtProfileSetting);
		edtAllowDownload = (AutoCompleteTextView) view.findViewById(R.id.edtDownloadSetting);

		edtProfileSetting.setAdapter(new GenderAdapter(getActivity(), new String[] { "Private", "Public" }));

		edtAllowDownload.setAdapter(new GenderAdapter(getActivity(), new String[] { "Allow", "Do not allow" }));

		edtAllowDownload.setOnClickListener(this);
		edtProfileSetting.setOnClickListener(this);

		btnFeedback.setOnClickListener(new OnBrowseClickListener());
		btnRateTheApp.setOnClickListener(new OnBrowseClickListener());
		btnPrivacy.setOnClickListener(new OnBrowseClickListener());
		btnAboutUs.setOnClickListener(new OnBrowseClickListener());
		btnFAQ.setOnClickListener(new OnBrowseClickListener());
		btnTermOfServices.setOnClickListener(new OnBrowseClickListener());
		btnLogout.setOnClickListener(new OnBrowseClickListener());

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		edtProfileSetting.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtAllowDownload.setTypeface(Fonts.getRobotoMedium(getActivity()));

		txtPrivacySetting.setTypeface(Fonts.getRobotoMedium(getActivity()));

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

				showMessage("Profile updated successfully");
				getFragmentManager().beginTransaction().remove(EditPrivacySettingsFragment.this).commit();
				((HomeActivity) getActivity()).showLastFramgent();
			}
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

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

			getFragmentManager().beginTransaction().remove(EditPrivacySettingsFragment.this).commit();
			((HomeActivity) getActivity()).showLastFramgent();
			break;

		}

	}

	private void parseViewDiscussionResponse(SoapObject res) {

		SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

		SoapObject newDataSetObject;

		newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		newDataSetObject = (SoapObject) newDataSetObject.getProperty(0);
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
		boolean flag = false;

		if (!btnEditPrivacySetting.isSelected()) {

			flag = true;
		}

		if (flag == true) {
			new AsyncUpdateProfile(getActivity(), "Updating").execute();
		}
	}

	private class OnBrowseClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String url = "";
			switch (v.getId()) {
			case R.id.btnFeedback:
				url = "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
				break;

			case R.id.btnRateTheApp:
				url = "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
				break;
			case R.id.btnPrivacy:
				url = "http://www.debalink.com/Privacy";
				break;
			case R.id.btnAboutUs:
				url = "http://www.debalink.com/About";
				break;
			case R.id.btnFaq:
				url = "http://www.debalink.com/FAQ";
				break;
			case R.id.btnTermsOfService:
				url = "http://www.debalink.com/TermsofService";
				break;
			case R.id.btnLogout:
				break;

			}

			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);

		}

	}

}
