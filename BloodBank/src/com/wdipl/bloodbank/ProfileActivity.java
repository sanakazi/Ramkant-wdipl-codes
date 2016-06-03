package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.view.Menu;
import android.view.MenuItem;
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
public class ProfileActivity extends Activity {
	private TextView txtDob;
	private EditText edtFirstname, edtLastname, edtMobile, edtEmail, edtCity, edtStreet, edtCountry, edtPassword, edtPincode;
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
		setContentView(R.layout.profile_setting);
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

		ArrayAdapter<CharSequence> mArrayAdapter = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.blood_groups, android.R.layout.simple_list_item_1);
		autoBloodGroup.setAdapter(mArrayAdapter);
		ArrayAdapter<CharSequence> mArrayAdapter1 = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.gender, android.R.layout.simple_list_item_1);
		autoGender.setAdapter(mArrayAdapter1);

		new AsyncGetProfile().execute();

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

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (edtFirstname.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Firstname", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtLastname.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Lastname", Toast.LENGTH_LONG).show();
					return;
				}

				if (autoBloodGroup.getText().toString().equals("") || autoBloodGroup.getText().toString().equals("Blood Group")) {
					Toast.makeText(getApplicationContext(), "Please Select the BloodGroup", Toast.LENGTH_LONG).show();
					return;
				}
				if (autoGender.getText().toString().equals("") || autoGender.getText().toString().equals("Gender")) {
					Toast.makeText(getApplicationContext(), "Please Select the Gender", Toast.LENGTH_LONG).show();
					return;
				}

				if (txtDob.getText().toString().trim().equals("mm/dd/yyyy")) {
					Toast.makeText(getApplicationContext(), "Please Set the Date Of Birth", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtMobile.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Contact No.", Toast.LENGTH_LONG).show();
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
					Toast.makeText(getApplicationContext(), "Passwords should be atleast 5 characters", Toast.LENGTH_LONG).show();
					return;
				}

				if (getAge(txtDob.getText().toString().trim()) <= 18) {
					Toast.makeText(getApplicationContext(), "Your age must be atleast 18 years old", Toast.LENGTH_LONG).show();
					return;
				}

				if (!isValidEmail(edtEmail.getText().toString().trim())) {
					Toast.makeText(getApplicationContext(), "Email id not valid!", Toast.LENGTH_LONG).show();
					return;
				}

				if (!isValidMobile(edtMobile.getText().toString().trim())) {
					Toast.makeText(getApplicationContext(), "Mobile number not valid!", Toast.LENGTH_LONG).show();
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
					Toast.makeText(getApplicationContext(), "Contact no already registered !", Toast.LENGTH_LONG).show();
				} else if (result == -2) {
					Toast.makeText(getApplicationContext(), "UnSuccessfull !", Toast.LENGTH_LONG).show();
				} else {
					savePreferences(result);
					Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_LONG).show();
					finish();
				}
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ProfileActivity.this);
			dialog.setIndeterminate(true);
			dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");

			dialog.show();

		}

		@Override
		protected Integer doInBackground(String... params) {
			RegisterSoap rs = new RegisterSoap();
			try {
				FROM = FROM_REGISTER;
				int res = (Integer) rs.updateProfile(getUserId() + "", params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8],
						params[9], params[10], params[11], params[12]);
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

	private class AsyncGetProfile extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						JSONObject jData = json.getJSONArray("Table").getJSONObject(0);
						String[] name = jData.getString("DonorName").split(" ");
						if (name.length == 1) {
							edtFirstname.setText(name[0]);
						} else if (name.length == 2) {
							edtFirstname.setText(name[0]);
							edtLastname.setText(name[1]);
						} else if (name.length == 2) {
							edtFirstname.setText(name[0] + " " + name[1]);
							edtLastname.setText(name[2]);
						}

						autoBloodGroup.setText(jData.getString("BloodGroup"));
						txtDob.setText(jData.getString("DateofBirth"));

						autoGender.setText(jData.getString("Gender"));

						edtMobile.setText(jData.getString("ContactNo"));
						edtStreet.setText(jData.getString("Street"));
						edtCity.setText(jData.getString("City"));
						autoDistrict.setText(jData.getString("District1"));
						autoState.setText(jData.getString("StateRegion1"));
						edtCountry.setText(jData.getString("Country"));
						edtPassword.setText(jData.getString("Password"));

						districtId = jData.getString("District");
						stateId = jData.getString("StateRegion");

						edtEmail.setText(jData.getString("Email"));
						edtPincode.setText(jData.getString("Pincode"));

						ArrayAdapter<CharSequence> mArrayAdapter = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.blood_groups, android.R.layout.simple_list_item_1);
						autoBloodGroup.setAdapter(mArrayAdapter);
						ArrayAdapter<CharSequence> mArrayAdapter1 = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.gender, android.R.layout.simple_list_item_1);
						autoGender.setAdapter(mArrayAdapter1);

					} catch (JSONException e) {

						e.printStackTrace();
					}

				}
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ProfileActivity.this);
			dialog.setIndeterminate(true);
			dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");

			dialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			RegisterSoap rs = new RegisterSoap();
			try {

				String res = rs.getProfile(getUserId() + "");
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

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
			sp.edit().clear().commit();
			finish();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		case R.id.notifications:
			Intent intentNotification = new Intent(this, NotificationsActivity.class);
			startActivity(intentNotification);
			return true;

		case R.id.profile:
			Intent intentProfile = new Intent(this, ProfileActivity.class);
			startActivity(intentProfile);
			return true;

		case R.id.bloodbank_list:

			Intent intentBloodbank = new Intent(this, BloodBankListActivity.class);
			startActivity(intentBloodbank);
			return true;

		case R.id.favourites:

			Intent i = new Intent(this, FavouritesActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);

		return true;
	}

	private class AsyncGetStates extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {

				autoState.setAdapter(new AutocompleteAdapter(ProfileActivity.this, stateList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ProfileActivity.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
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

				autoDistrict.setAdapter(new DistrictAdapter(ProfileActivity.this, districtList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ProfileActivity.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
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

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		int id = Integer.parseInt(sp.getString("user_id", "-1"));
		if (id == -1) {
			finish();
		}
	}
}
