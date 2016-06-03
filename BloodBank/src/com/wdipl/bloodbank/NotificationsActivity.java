package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.wdipl.adapter.NotificationAdapter;
import com.wdipl.json.Database;
import com.wdipl.json.DonorDetail;
import com.wdipl.json.JsonParse;

public class NotificationsActivity extends ListActivity {
	private ListView lstDonor;
	private ImageButton btnCallEmergency,btnNotification;
	private TextView txtHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.donor_list_new);
		btnNotification = (ImageButton) findViewById(R.id.btnNotification);
		txtHeader=(TextView)findViewById(R.id.txtHeader);
		btnNotification.setVisibility(View.GONE);
		lstDonor = getListView();
		
		txtHeader.setText("Notifications");
		
		init();
		new AsyncApp().execute();

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

	public void setUI(ArrayList<DonorDetail> list) {
		if (list != null) {
			
			View view = getLayoutInflater().inflate(R.layout.empty_dir_view, null);
			ViewGroup viewGroup = (ViewGroup) lstDonor.getParent();
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			viewGroup.addView(view, params);
			lstDonor.setEmptyView(view);
			
			NotificationAdapter adapter = new NotificationAdapter(NotificationsActivity.this, R.layout.notification_row, list);
			lstDonor.setAdapter(adapter);
		} else {
			Toast toast = Toast.makeText(NotificationsActivity.this, "Data Not Found!", Toast.LENGTH_LONG);
			toast.setGravity(1, 0, 0);
			toast.show();

			finish();
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.donor_list, menu);
	// return true;
	// }

	private class AsyncApp extends AsyncTask<String, Void, ArrayList<DonorDetail>> {
		ArrayList<DonorDetail> list;
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(NotificationsActivity.this);
			dialog.setIndeterminate(true);
			//dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();

		}

		@Override
		protected ArrayList<DonorDetail> doInBackground(String... params) {
			
			try {
				Database db = new Database(getApplicationContext());

				list = db.getNotifications();

				isSuccess = true;
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
				new AsyncApp().execute();
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

	//private int getUserId() {
		//SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		//return Integer.parseInt(sp.getString("user_id", "-1"));
	//}

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);
		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(NotificationsActivity.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
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
				new JsonParse().showHelp(NotificationsActivity.this);
			}
		});
		
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

}
