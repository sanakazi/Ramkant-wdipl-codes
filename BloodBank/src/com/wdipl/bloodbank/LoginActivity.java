package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.wdipl.json.HttpRequest;
import com.wdipl.json.JsonParse;
import com.wdipl.json.RegisterSoap;
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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private ImageButton btnLogin, btnRegister, btnCallEmergency;
	private EditText edtUsername, edtPassword;
	private final int REQUEST_FROM_LOGIN = 1, REQUEST_FROM_FORGOT_PASSWORD = 2;
	private int FROM = 0;
	private String strEmailId;
	private TextView txtForgotPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_new);
		btnLogin = (ImageButton) findViewById(R.id.btnLogin);
		btnRegister = (ImageButton) findViewById(R.id.btnRegister);
		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
		init();

		edtPassword.setTypeface(Typeface.DEFAULT);
		edtPassword.setTransformationMethod(new PasswordTransformationMethod());

		SharedPreferences sp = getSharedPreferences("search", Context.MODE_PRIVATE);
		sp.edit().clear().commit();

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edtUsername.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Please enter valid mobile no", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtUsername.getText().toString().length() < 10) {
					Toast.makeText(getApplicationContext(), "Please enter valid mobile no", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtPassword.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
					return;
				}

				new AsyncApp().execute(edtUsername.getText().toString(), edtPassword.getText().toString());

			}
		});
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(i);
			}
		});

		txtForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				forgotPassword();
			}
		});
	}

	private class AsyncApp extends AsyncTask<String, Void, Integer> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess) {
				if (result != -2) {
					// AlertDialog ad = AskOption();
					// ad.show();
					savePreferences(result);
					Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
					Intent i = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(i);
					finish();

				} else
					Toast.makeText(getApplicationContext(), "Invalid credentials, please try again", Toast.LENGTH_LONG).show();
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setIndeterminate(true);
			//dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");

			dialog.show();

		}

		@Override
		protected Integer doInBackground(String... params) {
			RegisterSoap rs = new RegisterSoap();
			try {
				FROM = REQUEST_FROM_LOGIN;
				SoapObject res = rs.donorLogin(params[0], params[1]);
				int resId = Integer.parseInt(res.getPropertySafelyAsString("Result", ""));
				savePreferences(res);

				isSuccess = true;

				return resId;
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
				isSuccess = false;
				e.printStackTrace();
			} catch (NullPointerException e) {
				isSuccess = false;
				e.printStackTrace();
			}
			return 0;
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

	private void savePreferences(int userId) {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("user_id", userId + "");
		editor.commit();
	}

	private void savePreferences(SoapObject data) {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("donor_name", data.getPropertySafelyAsString("DonorName", ""));
		editor.putString("donor_bloodgroup", data.getPropertySafelyAsString("BloodGroup", ""));
		editor.putString("city", data.getPropertySafelyAsString("City", ""));
		editor.putString("mobile", data.getPropertySafelyAsString("ContactNo", ""));
		editor.commit();
	}

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);
		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(LoginActivity.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:" + getString(R.string.bloodbank_phone_no)));
								callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(callIntent);
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
			}
		});

		ImageButton btnHelp = (ImageButton) findViewById(R.id.imageButton3);

		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new JsonParse().showHelp(LoginActivity.this);
			}
		});
	}

	private void forgotPassword() {
		final Dialog dialog = new Dialog(this);
		dialog.setTitle("Forgot Password");
		LayoutInflater inflater = getLayoutInflater();
		// this is what I did to added the layout to the alert dialog
		View layout = inflater.inflate(R.layout.forgot_password, null);

		final EditText edtEmailId = (EditText) layout.findViewById(R.id.edtEmail);
		final Button btnSend = (Button) layout.findViewById(R.id.btnSend);
		final Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
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
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
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
				try {
					//JSONObject jObj = new JSONObject(result);
					if (result.equalsIgnoreCase("EMail Id Not Found")) {
						//if (jObj.getJSONArray("Table").getJSONObject(0).getString("Error").equals("-1")) {
							Toast.makeText(getApplicationContext(), "Email does not exists!", Toast.LENGTH_LONG).show();
						//}
					} else {
						Toast.makeText(getApplicationContext(), "Please check your Email Id!", Toast.LENGTH_LONG).show();

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
			//dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
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
