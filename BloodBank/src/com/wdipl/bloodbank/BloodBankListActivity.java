package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.json.JSONException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.wdipl.adapter.AutocompleteAdapter;
import com.wdipl.adapter.BloodbankListAdapter;
import com.wdipl.adapter.DistrictAdapter;
import com.wdipl.json.BloodBankModel;
import com.wdipl.json.DistrictModel;
import com.wdipl.json.HttpRequest;
import com.wdipl.json.JsonParse;
import com.wdipl.json.StateModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BloodBankListActivity extends Activity {
	private AutoCompleteTextView autoState, autoDistrict;
	private ArrayList<StateModel> stateList;
	private ArrayList<DistrictModel> districtList;
	private String stateId, districtId;
	private ArrayList<BloodBankModel> bloodbankList;
	private ListView lvBloodbankList;
	private ImageButton btnCallEmergency;
	private int FROM, FROM_GET_STATE = 0, FROM_GET_DISTRICT = 1, FROM_GET_LIST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.bloodbank_list);
		init();
		autoState = (AutoCompleteTextView) findViewById(R.id.autoState);
		autoDistrict = (AutoCompleteTextView) findViewById(R.id.autoDistrict);
		lvBloodbankList = (ListView) findViewById(R.id.listView1);
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
					new AsyncGetBloodbankList().execute();
				}
			}
		});

		new AsyncGetStates().execute();
	}

	private class AsyncGetStates extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {

				autoState.setAdapter(new AutocompleteAdapter(BloodBankListActivity.this, stateList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(BloodBankListActivity.this);
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
				autoDistrict.setText("");
				autoDistrict.setAdapter(new DistrictAdapter(BloodBankListActivity.this, districtList));
			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(BloodBankListActivity.this);
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

	private class AsyncGetBloodbankList extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (isSuccess) {

				lvBloodbankList.setAdapter(new BloodbankListAdapter(BloodBankListActivity.this, R.layout.bloodbank_list_row, bloodbankList, autoState.getText().toString(),
						autoDistrict.getText().toString()));

			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(BloodBankListActivity.this);
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
				FROM = FROM_GET_LIST;
				ContentValues cv = new ContentValues();
				cv.put("districtId", districtId);
				SoapObject res = hr.getSoapObjectFromServer(cv, "SelectBloodBankList");
				bloodbankList = hr.parseBloodBankList(res);
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

	private void showRetryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Problem getting data");
		builder.setCancelable(true);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

				if (FROM == FROM_GET_LIST) {
					new AsyncGetBloodbankList().execute();
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

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);
		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(BloodBankListActivity.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
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
				new JsonParse().showHelp(BloodBankListActivity.this);
			}
		});

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

}
