package com.debalink;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.adapters.GenderAdapter;
import com.debalink.customviews.CustomEditText;
import com.debalink.customviews.DrawableClickListener;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.CustomTypefaceSpan;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import com.janrain.android.engage.JREngage;
import com.janrain.android.engage.JREngageDelegate;
import com.janrain.android.engage.JREngageError;
import com.janrain.android.engage.net.async.HttpResponseHeaders;
import com.janrain.android.engage.types.JRActivityObject;
import com.janrain.android.engage.types.JRDictionary;

public class RegisterActivity extends Activity implements OnClickListener, JREngageDelegate {

	private EditText edtName, edtUsername, edtPassword/*
													 * , edtConfirmPassword,
													 * txtProfilePicture
													 */, edtEmailId;
	private CustomEditText edtDOB;
	// private ImageButton btnProfilePicture, btnCamera;
	private ImageButton btnSignUp, btnFbSignUp/* , btnNext, btnPrev */;
	// private ImageView imgProfilePic;
	// private RadioGroup rgGender;
	private static final int DATE_DIALOG_ID = 1;
	// private int CAMERA_REQUEST = 2, GALLERY_REQUEST = 3;
	private int mYear, curYear;
	private int mMonth, curMonth;
	private int mDay, curDay;
	private String gender = "Unspecified";
	private Bitmap profilePic;
	// ViewSwitcher switcher;
	private AutoCompleteTextView autoGender;
	private TextView txtTerms;
	private JREngage mEngage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_new);

		// actionBar = getSupportActionBar();
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setCustomView(R.layout.header);
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		// switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);

		// imgProfilePic = (ImageView) findViewById(R.id.imgProfilePicture);
		autoGender = (AutoCompleteTextView) findViewById(R.id.edtGender);
		// rgGender = (RadioGroup) findViewById(R.id.rgGender);

		edtName = (EditText) findViewById(R.id.edtName);
		edtDOB = (CustomEditText) findViewById(R.id.edtDob);
		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		// edtConfirmPassword = (EditText)
		// findViewById(R.id.edtConfirmPassword);
		edtEmailId = (EditText) findViewById(R.id.edtEmailId);

		// txtProfilePicture = (EditText) findViewById(R.id.txtProfilePicture);
		txtTerms = (TextView) findViewById(R.id.txtTerms);
		autoGender.setAdapter(new GenderAdapter(this, new String[] { "Male", "Female", "Unspecified" }));

		edtName.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		edtDOB.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		edtUsername.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		edtPassword.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		// edtConfirmPassword.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		edtEmailId.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		autoGender.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		// txtProfilePicture.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		btnFbSignUp = (ImageButton) findViewById(R.id.btnFbSignUp);

		btnSignUp = (ImageButton) findViewById(R.id.btnSignUp);

		// btnCamera = (ImageButton) findViewById(R.id.btnCamera);
		// btnNext = (ImageButton) findViewById(R.id.btnNext);
		// btnPrev = (ImageButton) findViewById(R.id.btnPrev);

		// btnNext.setOnClickListener(this);
		edtDOB.setOnClickListener(this);
		btnFbSignUp.setOnClickListener(this);
		// btnCamera.setOnClickListener(this);
		autoGender.setOnClickListener(this);
		// btnPrev.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		c.add(Calendar.DAY_OF_YEAR, 1);

		edtDOB.setDrawableClickListener(new DrawableClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:
					showDialog(DATE_DIALOG_ID);
					break;
				default:
				}
			}

		});

		if (getIntent().hasExtra("profile")) {
			try {
				setData(new JSONObject(getIntent().getStringExtra("profile")));
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

		/*
		 * rgGender.setOnCheckedChangeListener(new
		 * RadioGroup.OnCheckedChangeListener() { public void
		 * onCheckedChanged(RadioGroup rGroup, int checkedId) { switch
		 * (checkedId) { case R.id.rbMale: gender = "Male"; break;
		 * 
		 * case R.id.rbFemale: gender = "Female"; break;
		 * 
		 * case R.id.rbUnspecified: gender = "Unspecified"; break;
		 * 
		 * } } });
		 */
		mDay = c.get(Calendar.DAY_OF_MONTH);
		setTermsText();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnDOB:

			break;

		case R.id.btnProfilePicture:
			// Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			// photoPickerIntent.setType("image/*");
			// startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
			break;

		case R.id.btnCamera:
			// Intent cameraIntent = new
			// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			// startActivityForResult(cameraIntent, CAMERA_REQUEST);
			break;

		// case R.id.btnNext:
		// switcher.showNext();
		// Intent loginIntent = new Intent(RegisterActivity.this,
		// LoginActivity.class);
		// startActivity(loginIntent);

		// break;
		case R.id.btnSignUp:
			if (checkFormDetails()) {
				new Async(RegisterActivity.this, "Registering").execute();
			}
			break;

		case R.id.edtGender:
			autoGender.showDropDown();
			break;

		case R.id.btnFbSignUp:
			showSocialLoginDialog("facebook");
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case DATE_DIALOG_ID:
			// return new DatePickerDialog(this, mDateSetListener, mYear,
			// mMonth, mDay);
			DatePickerDialog _date = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay) {
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					if (year < curYear)
						view.updateDate(mYear, mMonth, mDay);

					if (monthOfYear < curMonth && year == curYear)
						view.updateDate(mYear, mMonth, mDay);

					if (dayOfMonth < (curDay) && year == curYear && monthOfYear == curMonth) {
						view.updateDate(mYear, mMonth, mDay);
						Log.i("DATE", dayOfMonth + "");
					}

				}
			};
			return _date;

		}
		return null;
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {

		case DATE_DIALOG_ID:
			DatePicker dp = new DatePicker(RegisterActivity.this);
			dp.setMinDate(new Date().getTime() - 10000);
			((DatePickerDialog) dialog).onDateChanged(dp, mYear, mMonth, mDay);

			break;
		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();

		}

	};

	private void updateDisplay() {
		edtDOB.setText(new StringBuilder()
		// Month is 0 based so add 1
				.append(mMonth + 1).append("/").append(mDay).append("/").append(mYear).append(" "));

	}

	private boolean checkFormDetails() {
		String name, username, dob, pwd, cnfPwd, emailId;
		name = edtName.getText().toString();
		username = edtUsername.getText().toString();
		dob = edtDOB.getText().toString();
		pwd = edtPassword.getText().toString();
		// cnfPwd = edtConfirmPassword.getText().toString();
		emailId = edtEmailId.getText().toString();
		// cnfEmailId = edtConfirmEmailId.getText().toString();

		if (name.equals("")) {
			showMessage("Please Enter Name");
			return false;
		}

		if (username.equals("")) {
			showMessage("Please Enter Username");
			return false;
		}

		if (pwd.equals("")) {
			showMessage("Please Enter Password");
			return false;
		}

		// if (cnfPwd.equals("")) {
		// showMessage("Please Enter Confirm Passoword");
		// return false;
		// }

		// if (cnfEmailId.equals("")) {
		// showMessage("Fields marked with (*) are required.");
		// return false;
		// }

		if (emailId.equals("")) {
			showMessage("Please Enter Email Id");
			return false;
		}

		// if (!pwd.equals(cnfPwd)) {
		// showMessage("Password Mismatch.");
		// return false;
		// }

		// if (!emailId.equals(cnfEmailId)) {
		// showMessage("Email Id Not Match.");
		// return false;
		// }

		if (getAge(dob) < 16) {
			showMessage("Min required age is 16 years.");
		}

		return true;

	}

	private void showMessage(String message) {
		// Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		// //toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();

		View view = getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView txtMessage = (TextView) view.findViewById(R.id.textView1);
		txtMessage.setTypeface(Fonts.getBold(getBaseContext()));

		txtMessage.setText(message);
		Toast toast = new Toast(this);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		/*
		 * try {
		 * 
		 * if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
		 * Bitmap photo = (Bitmap) data.getExtras().get("data"); profilePic =
		 * photo; imgProfilePic.setImageBitmap(photo); }
		 * 
		 * else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
		 * Uri selectedImageUri = data.getData(); // String selectedImagePath =
		 * getPath(selectedImageUri);
		 * 
		 * Bitmap photo = decodeUri(selectedImageUri);
		 * 
		 * photo = Bitmap.createScaledBitmap(photo, 240, 240, true);
		 * 
		 * profilePic = photo; imgProfilePic.setImageBitmap(photo); } } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
	}

	public String getPath(Uri photoUri) {
		String filePath = null;
		if (photoUri != null) {
			try {
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
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
		BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

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
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
	}

	// validations
	// duplicate email=-3
	// duplicate username=-2
	// transaction fail=-1
	// if get id the succes
	private class Async extends AsynDownloader {
		public Async(Context ctx, String message) {
			super(ctx, message);
		}

		String id = "0", uploadPicResp = "false", insertPrfilePicRes = "";
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				String name, username, dob, pwd, emailId;
				name = edtName.getText().toString();
				username = edtUsername.getText().toString();
				dob = edtDOB.getText().toString();
				pwd = edtPassword.getText().toString();
				emailId = edtEmailId.getText().toString();

				ContentValues cv = new ContentValues();
				cv.put("Name", name);
				cv.put("DOB", dob);
				cv.put("Gender", gender);
				cv.put("UserName", username);
				cv.put("Password", pwd);
				cv.put("EmailId", emailId);
				cv.put("RegisterFrom", "IPAD");//
				cv.put("IPAddress", getLocalIpAddress());
				Locale current = getResources().getConfiguration().locale;
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmsss", current);
				Date dt = new Date();

				String filename = sdf.format(dt);
				filename = "profilepic" + filename + ".jpeg";

				id = hr.getDataFromServer(cv, "InsertUserRegistration");
				int i = Integer.parseInt(id);

				// ContentValues cvPicUpload=new ContentValues();
				// cvPicUpload.put("docname",filename);

				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// profilePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				// byte[] b = baos.toByteArray();

				// if (profilePic != null) {
				// uploadPicResp =
				// hr.uploadFile("SaveProfilePicture",cvPicUpload, b);
				// }

				/*
				 * if (i > 0 && uploadPicResp.equals("true")) { ContentValues c
				 * = new ContentValues(); c.put("UserId", id); c.put("PhotoUrl",
				 * filename); c.put("ProfilePictureFlag", "0");
				 * c.put("CurrentProfilePictureFlag", "1"); c.put("PictureId",
				 * "0"); insertPrfilePicRes = hr.getDataFromServer(c,
				 * "InsertUserPicture"); Log.i("insertPrfilePicRes",
				 * insertPrfilePicRes); }
				 */

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
			return id;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				int id = Integer.parseInt(result);

				if (id == -1) {
					showMessage("Transaction failed");
				} else if (id == -2) {
					showMessage("Username already exist");
					// switcher.showPrevious();
				} else if (id == -3) {
					showMessage("Email Id already exist");
					// switcher.showPrevious();
				} else if (id == -4) {
					showMessage("Error");
					// switcher.showPrevious();
				} else if (id == -5) {
					showMessage("Error");
					// switcher.showPrevious();
				} else if (id > 0) {
					showMessage("Registration successfull.Please Check mail.");
					Intent signupIntent = new Intent(RegisterActivity.this, SplashActivity.class);
					startActivity(signupIntent);
				}
			}

		}

	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						@SuppressWarnings("deprecation")
						String ip = Formatter.formatIpAddress(inetAddress.hashCode());
						Log.i("Debalink", "***** IP=" + ip);
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Debalink", ex.toString());
		}
		return null;
	}

	private int getAge(String strDob) {
		int year, month, day;
		strDob = strDob.replace(" ", "");

		String[] arr = strDob.split("/");
		month = Integer.parseInt(arr[0]);
		day = Integer.parseInt(arr[1]);
		year = Integer.parseInt(arr[2]);

		Calendar dob = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		dob.set(year, month, day);

		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}

		// Integer ageInt = new Integer(age);
		// String ageS = ageInt.toString();

		return age;
	}

	private void setTermsText() {
		// By signing up you accept terms of use and privacy policy
		String firstWord = "By clicking Register, you agree to our", secondWord = " Terms ", thirdWord = " and that you have read our", fourthWord = " Data Use Policy,", fifthWord = "including our Cookie Use.";

		Spannable spannable = new SpannableString(firstWord + secondWord + thirdWord + fourthWord + fifthWord);
		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getNormal(getBaseContext())), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getBold(getBaseContext())), firstWord.length(), firstWord.length() + secondWord.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getNormal(getBaseContext())), firstWord.length() + secondWord.length(),
				firstWord.length() + secondWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getBold(getBaseContext())), firstWord.length() + secondWord.length() + thirdWord.length(), firstWord.length()
				+ secondWord.length() + thirdWord.length() + fourthWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getNormal(getBaseContext())),
				firstWord.length() + secondWord.length() + thirdWord.length() + fourthWord.length(),
				firstWord.length() + secondWord.length() + thirdWord.length() + fourthWord.length() + fifthWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		txtTerms.setText(spannable);
	}

	private void showSocialLoginDialog(String provider) {
		String ENGAGE_APP_ID = "bmpandinhacnolphdehm";
		String ENGAGE_TOKEN_URL = null;
		mEngage = JREngage.initInstance(this.getApplicationContext(), ENGAGE_APP_ID, ENGAGE_TOKEN_URL, this, null);
		mEngage.showAuthenticationDialog(this, provider);

	}

	@Override
	public void jrAuthenticationDidSucceedForUser(JRDictionary auth_info, String provider) {

		try {
			JSONObject jObj = new JSONObject(auth_info.toJSON());

			JSONObject profile = jObj.getJSONObject("profile");
			// String email = profile.getString("verifiedEmail");
			// /String name = profile.getString("displayName");
			// setData(profile);
			// Log.i("email-", email);

			new CheckEmailIdExist(RegisterActivity.this, "Loading", profile).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("auth_info-" + provider, auth_info.toString());
		// jObj.
	}

	@Override
	public void jrAuthenticationDidReachTokenUrl(String tokenUrl, HttpResponseHeaders response, String tokenUrlPayload, String provider) {

	}

	@Override
	public void jrSocialDidPublishJRActivity(JRActivityObject activity, String provider) {

	}

	@Override
	public void jrSocialDidCompletePublishing() {

	}

	@Override
	public void jrEngageDialogDidFailToShowWithError(JREngageError error) {

	}

	@Override
	public void jrAuthenticationDidNotComplete() {

	}

	@Override
	public void jrAuthenticationDidSucceedForLinkAccount(JRDictionary auth_info, String provider) {

	}

	@Override
	public void jrAuthenticationDidFailWithError(JREngageError error, String provider) {

	}

	@Override
	public void jrAuthenticationCallToTokenUrlDidFail(String tokenUrl, JREngageError error, String provider) {

	}

	@Override
	public void jrSocialDidNotCompletePublishing() {

	}

	@Override
	public void jrSocialPublishJRActivityDidFail(JRActivityObject activity, JREngageError error, String provider) {

	}

	private void setData(JSONObject profile) {
		try {
			// JSONObject profile = jObj.getJSONObject("profile");
			String email = profile.getString("verifiedEmail");
			String name = profile.getString("displayName");
			String birthdate = profile.getString("birthday");
			String gender = profile.getString("gender");
			String username = profile.getString("preferredUsername");
			Date dt;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			SimpleDateFormat sdfNewFOrmat = new SimpleDateFormat("MM/dd/yyyy");

			dt = sdf.parse(birthdate);

			birthdate = sdfNewFOrmat.format(dt);

			edtName.setText(name);
			edtEmailId.setText(email);
			edtDOB.setText(birthdate);
			edtUsername.setText(username.toLowerCase());
			autoGender.setText(gender);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class CheckEmailIdExist extends AsynDownloader {
		public CheckEmailIdExist(Context ctx, String message, JSONObject profile) {
			super(ctx, message);
			this.profile = profile;
		}

		JSONObject profile;
		String id = "0";
		private int errorFlag = 0;
		private SoapObject res;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				cv.put("UserEmailID", profile.getString("verifiedEmail"));
				id = hr.getDataFromServer(cv, "IsUserEmailIdExists");
				int idInt = Integer.parseInt(id);

				if (idInt == 1) {

					res = hr.getSoapObjectFromServer(cv, "SelectUserProfilebyEmailID");

					SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

					res = hr.parseLoginResponse(diffgramObject);
					UserPreferences.saveUserPreferencesFromFbRegister(getApplicationContext(), res);
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
			return id;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				int id = Integer.parseInt(result);
				if (id == 1) {
					finish();
					Intent signupIntent = new Intent(RegisterActivity.this, HomeActivity.class);
					startActivity(signupIntent);
				} else if (id == 0) {
					setData(profile);
				}
			}
		}

	}

}
