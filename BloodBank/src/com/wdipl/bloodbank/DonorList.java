package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import com.wdipl.adapter.CustomAdapter;
import com.wdipl.json.DonorDetail;
import com.wdipl.json.JsonParse;
import com.wdipl.json.RegisterSoap;

@SuppressLint("InflateParams")
public class DonorList extends ListActivity {
	private ListView lstDonor;
	private String bg, state, district, city, stateId;
	private ImageButton btnSendNotification, btnCallEmergency;
	private ArrayList<DonorDetail> list;
	private TextView txtSearchedHeader;
	private int FROM, FROM_GET_PINCODE = 0, FROM_GET_CITY = 1, FROM_GET_STATE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.donor_list_new);

		Intent i = getIntent();
		init();
		lstDonor = getListView();
		bg = i.getStringExtra("BloodGroup");
		state = i.getStringExtra("State");
		district = i.getStringExtra("District");
		city = i.getStringExtra("City");
		stateId = i.getStringExtra("stateId");
		txtSearchedHeader = (TextView) findViewById(R.id.txtSearchedHeader);
		btnSendNotification = (ImageButton) findViewById(R.id.btnNotification);

		if (i.getIntExtra("from", 0) == 1) {
			txtSearchedHeader.setText(bg + " Blood Group donors");
			new AsyncSearchByPincode().execute(bg, i.getStringExtra("Pincode"));
		} else if (i.getIntExtra("from", 0) == 2) {
			new AsyncSearchByCity().execute(bg, i.getStringExtra("City"));
			txtSearchedHeader.setText(bg + " Blood Group donors from " + i.getStringExtra("City"));
		} else if (i.getIntExtra("from", 0) == 3) {
			txtSearchedHeader.setText(bg + " Blood Group donors from " + state);
			new AsyncApp().execute(bg, stateId);
		}

		btnSendNotification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (list == null || list.size() == 0) {
					Toast toast = Toast.makeText(DonorList.this, "No donor found to send alert", Toast.LENGTH_LONG);
					toast.show();
				} else {
					btnSendNotification.setEnabled(false);
					new AsyncSendNotification().execute();
				}
			}
		});

	}

	public void setUI(ArrayList<DonorDetail> list) {
		if (list != null) {

			View view = getLayoutInflater().inflate(R.layout.empty_dir_view, null);
			ViewGroup viewGroup = (ViewGroup) lstDonor.getParent();
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			viewGroup.addView(view, params);
			lstDonor.setEmptyView(view);

			CustomAdapter adapter = new CustomAdapter(DonorList.this, R.layout.custom_view, list);
			lstDonor.setAdapter(adapter);
		} else {
			Toast toast = Toast.makeText(DonorList.this, "Data Not Found!", Toast.LENGTH_LONG);
			toast.setGravity(1, 0, 0);
			toast.show();

			finish();
		}
	}

	private class AsyncApp extends AsyncTask<String, Void, ArrayList<DonorDetail>> {

		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(DonorList.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();

		}

		@Override
		protected ArrayList<DonorDetail> doInBackground(String... params) {
			JsonParse js = new JsonParse();
			try {
				FROM = FROM_GET_STATE;
				list = js.getDonorDetail(params[0], params[1], getUserId() + "");
			//	ArrayList<DonorDetail> favouritesList = js.getFavouriteList(getUserId() + "");

				//for (int i = 0; i < list.size(); i++) {
					//for (int j = 0; j < favouritesList.size(); j++) {
						//if (list.get(i).getId().equals(favouritesList.get(j).getId())) {
							//list.get(i).setAddedToFavourite(true);
							//continue;
						//}
					//}
				//}

				isSuccess = true;
				final String regId = GCMRegistrar.getRegistrationId(DonorList.this);

				if (regId.length() == 0) {
					GCMRegistrar.register(DonorList.this, getString(R.string.gcm_sender_id));
				} else {
					RegisterSoap rs = new RegisterSoap();
					try {
						rs.updateDeviceId(getUserId() + "", regId);
					} catch (XmlPullParserException e) {

						e.printStackTrace();
					}
				}

				return list;
			} catch (SocketTimeoutException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (SocketException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (IOException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (XmlPullParserException e1) {
				isSuccess = false;
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<DonorDetail> result) {
			dialog.dismiss();
			if (isSuccess) {
				setUI(result);
			} else {
				showRetryDialog();
			}
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
				if (FROM == FROM_GET_STATE) {
					new AsyncApp().execute(bg, city, district, state);
				} else if (FROM == FROM_GET_PINCODE) {
					new AsyncSearchByPincode().execute(bg, getIntent().getStringExtra("Pincode"));
				} else if (FROM == FROM_GET_CITY) {
					new AsyncSearchByCity().execute(bg, getIntent().getStringExtra("City"));
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

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

	private class AsyncSearchByPincode extends AsyncTask<String, Void, ArrayList<DonorDetail>> {

		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(DonorList.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();

		}

		@Override
		protected ArrayList<DonorDetail> doInBackground(String... params) {
			JsonParse js = new JsonParse();
			try {
				FROM = FROM_GET_PINCODE;
				list = js.getDonorListByPincode(params[0], params[1],getUserId()+"");
				/*ArrayList<DonorDetail> favouritesList = js.getFavouriteList(getUserId() + "");

				for (int i = 0; i < list.size(); i++) {
					for (int j = 0; j < favouritesList.size(); j++) {
						if (list.get(i).getId().equals(favouritesList.get(j).getId())) {
							list.get(i).setAddedToFavourite(true);
							continue;
						}
					}
				}*/

				isSuccess = true;
				final String regId = GCMRegistrar.getRegistrationId(DonorList.this);

				if (regId.length() == 0) {
					GCMRegistrar.register(DonorList.this, getString(R.string.gcm_sender_id));
				} else {
					RegisterSoap rs = new RegisterSoap();
					rs.updateDeviceId(getUserId() + "", regId);
				}

				return list;
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

		@Override
		protected void onPostExecute(ArrayList<DonorDetail> result) {
			dialog.dismiss();
			if (isSuccess) {
				setUI(result);
			} else {
				showRetryDialog();
			}
		}

	}

	private class AsyncSearchByCity extends AsyncTask<String, Void, ArrayList<DonorDetail>> {

		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(DonorList.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait..."); 
			dialog.show();

		}

		@Override
		protected ArrayList<DonorDetail> doInBackground(String... params) {
			JsonParse js = new JsonParse();
			try {
				FROM = FROM_GET_CITY;
				list = js.getDonorListByCity(params[0], params[1],getUserId()+"");
				/*ArrayList<DonorDetail> favouritesList = js.getFavouriteList(getUserId() + "");

				for (int i = 0; i < list.size(); i++) {
					for (int j = 0; j < favouritesList.size(); j++) {
						if (list.get(i).getId().equals(favouritesList.get(j).getId())) {
							list.get(i).setAddedToFavourite(true);
							continue;
						}
					}
				}*/

				isSuccess = true;

				final String regId = GCMRegistrar.getRegistrationId(DonorList.this);

				if (regId.length() == 0) {
					GCMRegistrar.register(DonorList.this, getString(R.string.gcm_sender_id));
				} else {
					RegisterSoap rs = new RegisterSoap();
					try {
						rs.updateDeviceId(getUserId() + "", regId);
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					}
				}

				return list;
			} catch (SocketTimeoutException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (SocketException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (IOException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (XmlPullParserException e1) {
				isSuccess = false;
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<DonorDetail> result) {
			dialog.dismiss();
			if (isSuccess) {
				setUI(result);
			} else {
				showRetryDialog();
			}
		}

	}

	private class AsyncSendNotification extends AsyncTask<Void, Void, Void> {

		private boolean isSuccess = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			JsonParse js = new JsonParse();
			try {
				String ids = "";
				for (int i = 0; i < list.size(); i++) {
					if (i == 0) {
						ids = ids + list.get(i).getId();
					} else {
						ids = ids + "," + list.get(i).getId();
					}
				}
				String message = getDonorName() + " from " + getCityName() + " need " + getBloodgroup() + " blood urgently";
				message = URLEncoder.encode(message, "UTF-8");
				js.sendNotification(ids, message, getCityName(), URLEncoder.encode(getBloodgroup(), "UTF-8"), getMobile());
				isSuccess = true;

				return null;
			} catch (SocketTimeoutException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (SocketException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (IOException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (XmlPullParserException e1) {
				isSuccess = false;
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (isSuccess) {
				Toast toast = Toast.makeText(DonorList.this, "Alert sent successfully to all searched donors", Toast.LENGTH_LONG);
				toast.setGravity(1, 0, 0);
				toast.show();
			} else {
				Toast toast = Toast.makeText(DonorList.this, "Error sending alert", Toast.LENGTH_LONG);
				toast.setGravity(1, 0, 0);
				toast.show();
			}
			btnSendNotification.setEnabled(true);
		}
	}

	private String getDonorName() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		return sp.getString("donor_name", "-1");
	}

	private String getCityName() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		return sp.getString("city", "-1");
	}

	private String getMobile() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		return sp.getString("mobile", "-1");
	}

	private String getBloodgroup() {
		// SharedPreferences sp = getSharedPreferences("data",
		// Context.MODE_PRIVATE);
		return bg; // sp.getString("donor_bloodgroup", "-1");
	}

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);
		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DonorList.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
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
				new JsonParse().showHelp(DonorList.this);
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
