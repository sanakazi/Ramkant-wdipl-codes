package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.wdipl.adapter.AutocompleteAdapter;
import com.wdipl.adapter.DistrictAdapter;
import com.wdipl.json.Common;
import com.wdipl.json.DistrictModel;
import com.wdipl.json.HttpRequest;
import com.wdipl.json.RegisterSoap;
import com.wdipl.json.StateModel;

@SuppressLint("NewApi")
public class RegisterActivity extends Activity {
	private TextView txtDob;
	private EditText edtFirstname, edtLastname, edtMobile, edtEmail, edtCity, edtStreet, edtCountry, edtPassword, edtPincode, edtRetypePassword;
	private AutoCompleteTextView autoBloodGroup, autoGender, autoState, autoDistrict;
	private ImageButton btnRegister, btnCancel;
	private String name, bloodGroup, gender, dob, mobile, state, district, city, street, country, password, pincode, email;
	private CheckBox chkText;
	private ArrayList<StateModel> stateList;
	private ArrayList<DistrictModel> districtList;
	private String stateId, districtId;
	private int FROM, FROM_GET_STATE = 0, FROM_GET_DISTRICT = 1, FROM_REGISTER = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.register_new);
		txtDob = (TextView) findViewById(R.id.txtDate);
		edtFirstname = (EditText) findViewById(R.id.edtFirstName);
		edtLastname = (EditText) findViewById(R.id.edtLastName);
		edtMobile = (EditText) findViewById(R.id.edtMobile);
		autoState = (AutoCompleteTextView) findViewById(R.id.edtState);
		autoDistrict = (AutoCompleteTextView) findViewById(R.id.edtDistrict);
		edtCity = (EditText) findViewById(R.id.edtCity);
		edtStreet = (EditText) findViewById(R.id.edtStreet);
		edtCountry = (EditText) findViewById(R.id.edtCountry);
		edtPincode = (EditText) findViewById(R.id.edtPincode);
		edtEmail = (EditText) findViewById(R.id.edtEmail);
		edtRetypePassword = (EditText) findViewById(R.id.edtRetypePassword);
		chkText = (CheckBox) findViewById(R.id.chkText);
		
		
		
		

		autoBloodGroup = (AutoCompleteTextView) findViewById(R.id.spnBloodGroup);
		autoGender = (AutoCompleteTextView) findViewById(R.id.spnGender);
		btnRegister = (ImageButton) findViewById(R.id.btnRegister);
		btnCancel = (ImageButton) findViewById(R.id.btnCancel);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		edtCountry.setFocusable(false);
		edtCountry.setFocusableInTouchMode(false);
		
		
		edtPassword.setTypeface(Typeface.DEFAULT);
		edtPassword.setTransformationMethod(new PasswordTransformationMethod());
		
		edtRetypePassword.setTypeface(Typeface.DEFAULT);
		edtRetypePassword.setTransformationMethod(new PasswordTransformationMethod());

		ArrayAdapter<CharSequence> mArrayAdapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.blood_groups, android.R.layout.simple_list_item_1);

		// mArrayAdapter.setDropDownViewResource(R.layout.spinner);
		autoBloodGroup.setAdapter(mArrayAdapter);

		ArrayAdapter<CharSequence> mArrayAdapter1 = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.gender, android.R.layout.simple_list_item_1);

		// mArrayAdapter1.setDropDownViewResource(R.layout.spinner);
		autoGender.setAdapter(mArrayAdapter1);

		autoBloodGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoBloodGroup.showDropDown();
			}
		});
		autoGender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoGender.showDropDown();
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (edtFirstname.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter First Name", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtLastname.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Last Name", Toast.LENGTH_LONG).show();
					return;
				}

				if (autoBloodGroup.getText().toString().equals("") || autoBloodGroup.getText().toString().equals("Blood Group")) {
					Toast.makeText(getApplicationContext(), "Please Select the Blood Group", Toast.LENGTH_LONG).show();
					return;
				}
				if (autoGender.getText().toString().equals("") || autoGender.getText().toString().equals("Gender")) {
					Toast.makeText(getApplicationContext(), "Please Select the Gender", Toast.LENGTH_LONG).show();
					return;
				}

				if (txtDob.getText().toString().trim().equals("mm/dd/yyyy") || txtDob.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Set the Date Of Birth", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtMobile.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Mobile No", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtEmail.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Email Id", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtPassword.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtRetypePassword.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Retype Password", Toast.LENGTH_LONG).show();
					return;
				}

				if (autoState.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter State", Toast.LENGTH_LONG).show();
					return;
				}

				if (autoDistrict.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter District", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtCity.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter City", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtStreet.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Street", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtPincode.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Pincode", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtCountry.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Country", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtPassword.getText().toString().length() < 5) {
					Toast.makeText(getApplicationContext(), "Password should be atleast 6 characters", Toast.LENGTH_LONG).show();
					return;
				}

				if (getAge(txtDob.getText().toString().trim()) <= 18) {
					Toast.makeText(getApplicationContext(), "Your age must be atleast 18 years old", Toast.LENGTH_LONG).show();
					return;
				}

				if (!isValidEmail(edtEmail.getText().toString().trim())) {
					Toast.makeText(getApplicationContext(), "Email Id not valid!", Toast.LENGTH_LONG).show();
					return;
				}

				if (!isValidMobile(edtMobile.getText().toString().trim())) {
					Toast.makeText(getApplicationContext(), "Mobile number not valid", Toast.LENGTH_LONG).show();
					return;
				}

				if (!edtRetypePassword.getText().toString().equals(edtPassword.getText().toString())) {
					Toast.makeText(getApplicationContext(), "Password and Retype password are not matching", Toast.LENGTH_LONG).show();
					return;
				}

				if (!chkText.isChecked()) {
					Toast.makeText(getApplicationContext(), "Please accept terms & conditions", Toast.LENGTH_LONG).show();
					return;
				}

				name = edtFirstname.getText().toString().trim() + " " + edtLastname.getText().toString().trim();
				bloodGroup = autoBloodGroup.getText().toString();
				gender = autoGender.getText().toString();
				dob = txtDob.getText().toString().trim();
				mobile = edtMobile.getText().toString().trim();
				state = autoState.getText().toString().trim();
				district = autoDistrict.getText().toString().trim();
				city = edtCity.getText().toString().trim();
				street = edtStreet.getText().toString().trim();
				country = edtCountry.getText().toString().trim();
				password = edtPassword.getText().toString();
				pincode = edtPincode.getText().toString();
				email = edtEmail.getText().toString();

				if (!Common.hasConnection(getApplicationContext())) {

					Toast.makeText(getApplicationContext(), "No internet connection found!", Toast.LENGTH_LONG).show();
					return;
				}

				new AsyncApp().execute(name, bloodGroup, gender, dob, mobile, street, city, districtId, stateId, country, password, pincode, email);

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		txtDob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DatePickerFrag dpf = new DatePickerFrag();
				dpf.show(getFragmentManager(), "Date Picker");

			}
		});

		autoState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoState.showDropDown();
			}
		});

		autoDistrict.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoDistrict.showDropDown();
			}
		});
		autoState.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (stateList != null) {
					autoState.setText(stateList.get(position).getStateName());
					stateId = stateList.get(position).getStateId();

					new AsyncGetDistrict().execute();
				}
			}
		});

		autoDistrict.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (districtList != null) {
					autoDistrict.setText(districtList.get(position).getDistrictName());
					districtId = districtList.get(position).getDistrictId();
				}
			}
		});

		new AsyncGetStates().execute();
	}

	private class DatePickerFrag extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

			String mnth = String.format(Locale.getDefault(), "%02d", (monthOfYear + 1));
			String day = String.format(Locale.getDefault(), "%02d", dayOfMonth);

			txtDob.setText(new StringBuilder().append(mnth).append("/").append(day).append("/").append(year));

			// dob.setText(new StringBuilder().append(monthOfYear +
			// 1).append("/").append(dayOfMonth).append("/").append(year));

		}

	}

	private class AsyncApp extends AsyncTask<String, Void, Integer> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {
				if (result == -1) {
					Toast.makeText(getApplicationContext(), "Contact no already registered", Toast.LENGTH_LONG).show();
				} else if (result == -2) {
					Toast.makeText(getApplicationContext(), "UnSuccessfull ", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Registration is Successful", Toast.LENGTH_LONG).show();

					savePreferences(result);
					Intent i = new Intent(RegisterActivity.this, MainActivity.class);
					startActivity(i);
				}

			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(RegisterActivity.this);
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
				FROM = FROM_REGISTER;
				int res = (Integer) rs.donorRegister(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10],
						params[11], params[12]);
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
			return 0;
		}
	}

	/*
	 * private AlertDialog AskOption() { AlertDialog myQuittingDialogBox = new
	 * AlertDialog.Builder(this) // set message, title, and icon //
	 * .setTitle("Confirm") .setMessage("Registration is Successful!") //
	 * .setIcon(R.drawable.a)
	 * 
	 * .setPositiveButton("Exit Apps", new DialogInterface.OnClickListener() {
	 * 
	 * public void onClick(DialogInterface dialog, int whichButton) { // your
	 * deleting code dialog.dismiss(); }
	 * 
	 * }).setNegativeButton("Ok", new DialogInterface.OnClickListener() { public
	 * void onClick(DialogInterface dialog, int which) { dialog.dismiss();
	 * finish(); Intent i = new Intent(RegisterActivity.this,
	 * LoginActivity.class); startActivity(i); } }).create(); return
	 * myQuittingDialogBox; }
	 */

	@Override
	protected void onStart() {
		super.onStart();
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
	}

	private void showRetryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Problem getting data");
		builder.setCancelable(true);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if (FROM == FROM_REGISTER) {
					new AsyncApp().execute(name, bloodGroup, gender, dob, mobile, street, city, districtId, stateId, country, password, pincode, email);
				} else if (FROM == FROM_GET_STATE) {
					new AsyncGetStates().execute();
				} else if (FROM == FROM_GET_DISTRICT) {
					new AsyncGetDistrict().execute();
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
		editor.putString("donor_name", name);
		editor.putString("donor_bloodgroup", bloodGroup);
		editor.putString("city", city);
		editor.putString("mobile", mobile);
		editor.putString("state", state);
		editor.putString("district", district);
		editor.commit();
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

	private boolean isValidMobile(String phone) {
		boolean check;
		if (phone.length() < 10 || phone.length() > 13) {
			check = false;
		} else {
			check = true;
		}
		return check;
	}

	private boolean isValidEmail(String target) {
		if (target == null) {
			return false;
		} else if (target.startsWith(".") || target.endsWith(".")) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	private class AsyncGetStates extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {

				autoState.setAdapter(new AutocompleteAdapter(RegisterActivity.this, stateList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setIndeterminate(true);
			//dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpRequest hr = new HttpRequest();
			try {
				FROM = FROM_GET_STATE;
				SoapObject res = hr.getSoapObjectFromServer(null, "SelectState");
				stateList = hr.parseStateList(res);
				isSuccess = true;
				return "";
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
			} catch (JSONException e) {

				e.printStackTrace();
			}
			return null;
		}
	}

	private class AsyncGetDistrict extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {

				autoDistrict.setAdapter(new DistrictAdapter(RegisterActivity.this, districtList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setIndeterminate(true);
			//dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpRequest hr = new HttpRequest();
			try {
				FROM = FROM_GET_DISTRICT;
				ContentValues cv = new ContentValues();
				cv.put("StateID", stateId);
				SoapObject res = hr.getSoapObjectFromServer(cv, "SelectDistrict");
				districtList = hr.parseDistrictList(res);
				isSuccess = true;
				return "";
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
