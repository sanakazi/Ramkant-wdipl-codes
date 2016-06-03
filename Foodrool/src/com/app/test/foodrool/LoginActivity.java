package com.app.test.foodrool;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.app.test.foodrool.gcm.RegistrationIntentService;
import com.app.test.foodrool.utils.Fonts;
import com.app.test.foodrool.webservices.HttpRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button btnLogin;
	private EditText edtUsername, edtPassword;
	private TextView txtForgotPassword;
	private final int REQUEST_FROM_LOGIN = 1, REQUEST_FROM_FORGOT_PASSWORD = 2;
	private int FROM = 0;
	private String strEmailId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);

		btnLogin.setTypeface(Fonts.getSansBold(getApplicationContext()));
		edtUsername.setTypeface(Fonts.getSansLight(getApplicationContext()));
		edtPassword.setTypeface(Fonts.getSansLight(getApplicationContext()));

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edtUsername.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Please enter a valid email id or mobile no",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (edtUsername.getText().toString().length() < 10) {
					// Toast.makeText(getApplicationContext(),
					// "Please enter valid mobile no !",
					// Toast.LENGTH_LONG).show();
					// return;
				}
				if (edtPassword.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
					return;
				}

				new AsyncApp().execute(edtUsername.getText().toString(), edtPassword.getText().toString());

			}
		});

		txtForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				forgotPassword();
			}
		});
	}

	private class AsyncApp extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess) {
				// if (result != -2) {

				try {
					JSONArray jObj = new JSONArray(result);
					if (jObj.getJSONObject(0).has("Error") && jObj.getJSONObject(0).getString("Error").equals("3")) {
						Toast.makeText(getApplicationContext(), "Invalid credentials, Please try again",
								Toast.LENGTH_LONG).show();
					} else if (jObj.getJSONObject(0).has("Error")
							&& jObj.getJSONObject(0).getString("Error").equals("2")) {
						Toast.makeText(getApplicationContext(), "User is blocked.Please contact to administrator",
								Toast.LENGTH_LONG).show();
					} else {
						savePreferences(result);
						finish();
						Intent i = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(i);

					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Invalid credentials, Please try again", Toast.LENGTH_LONG)
							.show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Something went wrong, Please try again", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {

				FROM = REQUEST_FROM_LOGIN;
				ContentValues cv = new ContentValues();
				cv.put("UserName", edtUsername.getText().toString());
				cv.put("Password", edtPassword.getText().toString());

				String res = hr.getDataFromServer(cv, "ValidateUser");
				isSuccess = true;
				return res;
			} catch (SocketTimeoutException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (SocketException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (IOException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private void showRetryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Problem getting data");
		builder.setCancelable(true);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

				if (FROM == REQUEST_FROM_LOGIN) {
					new AsyncApp().execute(edtUsername.getText().toString(), edtPassword.getText().toString());
				} else if (FROM == REQUEST_FROM_FORGOT_PASSWORD) {

					new AsyncForgotPassword().execute(strEmailId);
				}

			}
		});
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		});

		AlertDialog alert11 = builder.create();
		alert11.show();
	}

	private void savePreferences(String response) throws JSONException {
		JSONArray jArr = new JSONArray(response);
		JSONObject jPreferences = jArr.getJSONObject(0);

		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		Editor editor = sp.edit();

		editor.putString("user_id", jPreferences.getString("AdminUserId"));
		editor.putString("Role_id", jPreferences.getString("RoleId"));
		// editor.putString("gender", jPreferences.getString("Gender"));
		editor.putString("email", jPreferences.getString("AdminUserEmail"));
		// editor.putString("ph_no", jPreferences.getString("phNo"));
		editor.commit();

		int userId = Integer.parseInt(sp.getString("user_id", "-1"));
		RegistrationIntentService.sendRegistrationToServer(this, sp.getString("device_id", "-1"), userId);
	}

	private void forgotPassword() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// dialog.setTitle("Forgot Password");
		LayoutInflater inflater = getLayoutInflater();
		// this is what I did to added the layout to the alert dialog
		View layout = inflater.inflate(R.layout.forgot_password, null);

		final EditText edtEmailId = (EditText) layout.findViewById(R.id.edtEmail);
		final Button btnSend = (Button) layout.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isValidEmail(edtEmailId.getText().toString())) {

					strEmailId = edtEmailId.getText().toString();
					new AsyncForgotPassword().execute(strEmailId);
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "Please enter valid Email Id!", Toast.LENGTH_LONG).show();
				}

			}
		});

		dialog.setContentView(layout);
		dialog.show();
	}

	private boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	private class AsyncForgotPassword extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess) {
				try {// User is blocked.Please contact to administrator
					if (result.contains("Email id not present or invalid")) {
						Toast.makeText(getApplicationContext(), "Email Id does not exists", Toast.LENGTH_LONG).show();
					} else if (result.contains("User is blocked.Please contact to administrator")) {
						Toast.makeText(getApplicationContext(), "User is blocked.Please contact to administrator",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "Please check your Email Id", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {

				FROM = REQUEST_FROM_FORGOT_PASSWORD;
				ContentValues cv = new ContentValues();
				cv.put("EmailId", params[0]);

				String res = hr.getDataFromServer(cv, "ForgotPassword");

				isSuccess = true;
				return res;
			} catch (SocketTimeoutException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (SocketException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (IOException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (XmlPullParserException e) {

				e.printStackTrace();
			}
			return null;
		}
	}
}
