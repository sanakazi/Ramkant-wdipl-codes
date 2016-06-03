package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import com.wdipl.adapter.AutocompleteAdapter;
import com.wdipl.json.Common;
import com.wdipl.json.HttpRequest;
import com.wdipl.json.JsonParse;
import com.wdipl.json.StateModel;

public class MainActivity extends Activity {

	private AutoCompleteTextView spnBloodGroup, autoState;// ,
	private String pattern = "[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*"; // spnCity,spnDistrict;
	private ImageButton btnSearch, btnFavourites, btnCallEmergency;
	private RadioGroup rgSearchType;
	private EditText edtSearchCriateria;
	private ArrayList<StateModel> stateList;
	private String stateId;
	private int radio = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.search_activity);
		spnBloodGroup = (AutoCompleteTextView) findViewById(R.id.autoBloodGroup);
		// spnDistrict = (Spinner) findViewById(R.id.spnDistrict);
		autoState = (AutoCompleteTextView) findViewById(R.id.autoState);
		edtSearchCriateria = (EditText) findViewById(R.id.edtSearchCriateria);
		// spnCity = (Spinner) findViewById(R.id.spnCity);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnFavourites = (ImageButton) findViewById(R.id.btnFavourites);
		init();
		rgSearchType = (RadioGroup) findViewById(R.id.radioGroup1);

		ArrayAdapter<CharSequence> mArrayAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.blood_groups, android.R.layout.simple_list_item_1);
		mArrayAdapter.setDropDownViewResource(R.layout.spinner);
		ArrayAdapter<CharSequence> mArrayAdapter1 = ArrayAdapter.createFromResource(MainActivity.this, R.array.state, android.R.layout.simple_list_item_1);
		mArrayAdapter1.setDropDownViewResource(R.layout.spinner);

		spnBloodGroup.setAdapter(mArrayAdapter);

		;

		autoState.setAdapter(mArrayAdapter1);

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/* new AsyncApp().execute(); */
				if (!Common.hasConnection(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), "No internet connection found!", Toast.LENGTH_LONG).show();
					return;
				}

				String message = "Please Select Blood Group";

				if (spnBloodGroup.getText().toString().equals("") || spnBloodGroup.getText().toString().equals("Blood Group")) {

					Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}

				int radioButtonID = rgSearchType.getCheckedRadioButtonId();
				Intent i = new Intent(MainActivity.this, DonorList.class);
				i.putExtra("BloodGroup", spnBloodGroup.getText().toString().trim());

				switch (radioButtonID) {

				case R.id.rbPincode:
					i.putExtra("Pincode", edtSearchCriateria.getText().toString());
					i.putExtra("from", 1);
					message = "Please Enter Pincode";

					if (edtSearchCriateria.getText().toString().equals("")) {
						Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}

					break;
				case R.id.rbCity:
					i.putExtra("City", edtSearchCriateria.getText().toString());
					i.putExtra("from", 2);
					message = "Please Enter City";

					if (edtSearchCriateria.getText().toString().equals("")) {
						Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}

					break;
				case R.id.rbState:

					i.putExtra("State", autoState.getText().toString().trim());
					i.putExtra("City", "All");
					i.putExtra("District", "All");
					i.putExtra("from", 3);
					i.putExtra("stateId", stateId);
					message = "Please Select State";

					if (autoState.getText().toString().equals("")) {
						Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}

					break;
				}
				saveSearchPreferences();
				startActivity(i);

			}

		});

		btnFavourites.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainActivity.this, FavouritesActivity.class);
				startActivity(i);
			}

		});

		spnBloodGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spnBloodGroup.showDropDown();
			}
		});
		autoState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoState.showDropDown();
			}
		});
		autoState.setVisibility(View.GONE);
		// edtSearchCriateria.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
		rgSearchType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				autoState.setVisibility(View.GONE);
				edtSearchCriateria.setVisibility(View.GONE);
				switch (checkedId) {

				case R.id.rbPincode:
					edtSearchCriateria.setHint("Please Enter Pincode");
					edtSearchCriateria.setVisibility(View.VISIBLE);
					edtSearchCriateria.setText("");
					// edtSearchCriateria.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
					pattern = "[1234567890]*";
					edtSearchCriateria.setFilters(new InputFilter[] { filter, new InputFilter.LengthFilter(8) });
					radio = 0;
					break;

				case R.id.rbCity:
					edtSearchCriateria.setHint("Please Enter City");
					edtSearchCriateria.setVisibility(View.VISIBLE);
					edtSearchCriateria.setText("");
					// edtSearchCriateria.setKeyListener(TextKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz1234567890 ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
					pattern = "[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ]*";
					edtSearchCriateria.setFilters(new InputFilter[] { filter, new InputFilter.LengthFilter(50) });
					radio = 1;
					break;
				case R.id.rbState:
					edtSearchCriateria.setHint("Please Enter State");
					autoState.setVisibility(View.VISIBLE);
					radio = 2;

					break;
				}

			}
		});

		autoState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoState.showDropDown();
			}
		});

		autoState.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (stateList != null) {
					autoState.setText(stateList.get(position).getStateName());
					stateId = stateList.get(position).getStateId();

				}
			}
		});
		edtSearchCriateria.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						if (!Common.hasConnection(getApplicationContext())) {
							Toast.makeText(getApplicationContext(), "No internet connection found!", Toast.LENGTH_LONG).show();
							return true;
							
						}

						String message = "Please Select Blood Group";

						if (spnBloodGroup.getText().toString().equals("") || spnBloodGroup.getText().toString().equals("Blood Group")) {

							Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							return true;
							
						}

						int radioButtonID = rgSearchType.getCheckedRadioButtonId();
						Intent i = new Intent(MainActivity.this, DonorList.class);
						i.putExtra("BloodGroup", spnBloodGroup.getText().toString().trim());

						switch (radioButtonID) {

						case R.id.rbPincode:
							i.putExtra("Pincode", edtSearchCriateria.getText().toString());
							i.putExtra("from", 1);
							message = "Please Enter Pincode";

							if (edtSearchCriateria.getText().toString().equals("")) {
								Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return true;
							}

							break;
						case R.id.rbCity:
							i.putExtra("City", edtSearchCriateria.getText().toString());
							i.putExtra("from", 2);
							message = "Please Enter City";

							if (edtSearchCriateria.getText().toString().equals("")) {
								Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return true;
							}

							break;
						case R.id.rbState:

							i.putExtra("State", autoState.getText().toString().trim());
							i.putExtra("City", "All");
							i.putExtra("District", "All");
							i.putExtra("from", 3);
							i.putExtra("stateId", stateId);
							message = "Please Select State";

							if (autoState.getText().toString().equals("")) {
								Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return true;
							}

							break;
						}
						saveSearchPreferences();
						startActivity(i);
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});

		setLastSearchedData();

		new AsyncGetStates().execute();

	}

	InputFilter filter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; ++i) {
				if (!Pattern.compile(pattern).matcher(String.valueOf(source.charAt(i))).matches()) {
					return "";
				}
			}

			return null;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		int id = Integer.parseInt(sp.getString("user_id", "-1"));
		if (id == -1) {
			finish();
		}
	}

	protected void onStart() {
		super.onStart();
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
			sp.edit().clear().commit();
			finish();
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);

		ImageButton btnHelp = (ImageButton) findViewById(R.id.imageButton3);

		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new JsonParse().showHelp(MainActivity.this);
			}
		});

		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MainActivity.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
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
	}

	@Override
	public void onBackPressed() {

		new AlertDialog.Builder(MainActivity.this).setTitle("Alert").setMessage("Are you sure you want to exit?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				exit();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		}).setIcon(android.R.drawable.ic_dialog_alert).show();

	}

	private void exit() {
		super.onBackPressed();
	}

	private class AsyncGetStates extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {

				autoState.setAdapter(new AutocompleteAdapter(MainActivity.this, stateList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MainActivity.this);
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

	private void showRetryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Problem getting data");
		builder.setCancelable(true);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				new AsyncGetStates().execute();

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

	private void saveSearchPreferences() {
		SharedPreferences sp = getSharedPreferences("search", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("state_id", stateId);
		editor.putString("state_name", autoState.getText().toString().trim());
		editor.putString("edt_text", edtSearchCriateria.getText().toString().trim());
		editor.putString("blood_group", spnBloodGroup.getText().toString().trim());

		editor.putInt("radio", radio).commit();
	}

	private void setLastSearchedData() {
		SharedPreferences sp = getSharedPreferences("search", Context.MODE_PRIVATE);
		int radio = sp.getInt("radio", 0);
		spnBloodGroup.setText(sp.getString("blood_group", ""));
		if (radio == 2) {
			stateId = sp.getString("state_id", "");
			autoState.setText(sp.getString("state_name", ""));
			((RadioButton) rgSearchType.getChildAt(2)).setChecked(true);
		} else if (radio == 1) {

			((RadioButton) rgSearchType.getChildAt(1)).setChecked(true);
			edtSearchCriateria.setText(sp.getString("edt_text", ""));
		} else if (radio == 0) {

			((RadioButton) rgSearchType.getChildAt(0)).setChecked(true);
			edtSearchCriateria.setText(sp.getString("edt_text", ""));
		}

		ArrayAdapter<CharSequence> mArrayAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.blood_groups, android.R.layout.simple_list_item_1);
		mArrayAdapter.setDropDownViewResource(R.layout.spinner);
		spnBloodGroup.setAdapter(mArrayAdapter);
	}
}
