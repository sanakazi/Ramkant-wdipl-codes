package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.services.OnAlarmReceiver;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import com.janrain.android.engage.JREngage;
import com.janrain.android.engage.JREngageDelegate;
import com.janrain.android.engage.JREngageError;
import com.janrain.android.engage.net.async.HttpResponseHeaders;
import com.janrain.android.engage.types.JRActivityObject;
import com.janrain.android.engage.types.JRDictionary;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity implements OnClickListener, JREngageDelegate {

	private ImageButton btnSignUp, btnSignIn, btnFbSignUp;
	private EditText edtUsername, edtPassword;
	private JREngage mEngage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_new);

		btnSignUp = (ImageButton) findViewById(R.id.btnSignUp);
		btnSignIn = (ImageButton) findViewById(R.id.btnSignIn);
		btnFbSignUp = (ImageButton) findViewById(R.id.btnFbSignUp);

		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);

		edtPassword.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		edtUsername.setTypeface(Fonts.getRobotoMedium(getBaseContext()));

		btnSignIn.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);
		btnFbSignUp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.btnSignIn:

			if (edtUsername.getText().toString().isEmpty()) {
				showMessage("Enter Username/Email Id");
				return;
			}
			if (edtPassword.getText().toString().isEmpty()) {
				showMessage("Enter password");
				return;
			}

			new Async(LoginActivity.this, "Logging In").execute();

			break;
		case R.id.btnSignUp:
			Intent signupIntent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(signupIntent);
		case R.id.btnFbSignUp:
			showSocialLoginDialog("facebook");
			break;
		}
	}

	// Invaliduser=2
	// Invalid password=3
	private class Async extends AsynDownloader {
		public Async(Context ctx, String message) {
			super(ctx, message);
		}

		String resFromServer = "-999", message = "";
		private SoapObject res;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				String username, pwd;

				username = edtUsername.getText().toString();

				pwd = edtPassword.getText().toString();

				ContentValues cv = new ContentValues();
				cv.put("UserName", username);
				cv.put("Password", pwd);

				res = hr.getSoapObjectFromServer(cv, "Validate_LogIn");

				// if (res != null) {
				res = hr.parseLoginResponse(res);
				resFromServer = res.getPropertySafelyAsString("Invaliduser", "");
				if (resFromServer.equals("")) {
					UserPreferences.saveUserPreferences(getApplicationContext(), res);
					resFromServer = "-100";
				}
				// }

				Log.i("insertPrfilePicRes", resFromServer);

			} catch (SocketTimeoutException e) {
				message = "Connection timeout";
				e.printStackTrace();
			} catch (SocketException e) {
				message = "Connection timeout";
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				message = "Parser Error";
				e.printStackTrace();
			} catch (IOException e) {
				message = "Connection timeout";
				e.printStackTrace();
			} catch (Exception e) {
				message = "Unknown Error";
				e.printStackTrace();
			}

			try {

				ContentValues cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(getBaseContext()));
				res = hr.getSoapObjectFromServer(cv, "FillNotificationCount");
				SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");
				SoapObject newDataSetObject = null;
				if (diffgramObject.hasProperty("NewDataSet")) {
					newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
				} else {

				}

				SoapObject data1 = (SoapObject) newDataSetObject.getProperty(0);
				SoapObject data2 = (SoapObject) newDataSetObject.getProperty(1);
				SoapObject data3 = (SoapObject) newDataSetObject.getProperty(2);

				String strNotificationCount = data1.getPropertySafelyAsString("NotificationCount", "0");
				String strMessageCount = data2.getPropertySafelyAsString("MessageCount", "0");
				String strSubcriberRequestcnt = data3.getPropertySafelyAsString("SubcriberRequestcnt", "0");
				OnAlarmReceiver.notificationCount = Integer.parseInt(strNotificationCount);
				OnAlarmReceiver.messageCount = Integer.parseInt(strMessageCount);
				OnAlarmReceiver.subscribersCount = Integer.parseInt(strSubcriberRequestcnt);

				Log.i("OnAlarmReceiver", strNotificationCount + " " + strMessageCount + " " + strSubcriberRequestcnt);

			} catch (SocketTimeoutException e) {

				e.printStackTrace();
			} catch (SocketException e) {

				e.printStackTrace();
			} catch (XmlPullParserException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			return resFromServer;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			int id = Integer.parseInt(result);
			if (id == -999) {
				showMessage(message);
			} else if (id == 2) {
				showMessage("Invalid Username/Email Id");
			} else if (id == 3) {
				showMessage("Invalid Password");
			} else {
				finish();
				Intent signupIntent = new Intent(LoginActivity.this,

				HomeActivity.class);
				startActivity(signupIntent);
			}

		}

	}

	private void showMessage(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
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

			new CheckEmailIdExist(LoginActivity.this, "Loading", profile).execute();
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
					Intent home = new Intent(LoginActivity.this, HomeActivity.class);
					startActivity(home);
				} else if (id == 0) {
					//setData(profile);
					Intent signupIntent = new Intent(LoginActivity.this, RegisterActivity.class);
					signupIntent.putExtra("profile", profile.toString());
					startActivity(signupIntent);
				}
			}
		}

	}
}
