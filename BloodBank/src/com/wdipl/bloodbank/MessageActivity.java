package com.wdipl.bloodbank;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import org.xmlpull.v1.XmlPullParserException;
import com.wdipl.json.JsonParse;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends Activity implements OnClickListener {
	private ImageButton btnSend, btnCall;
	private EditText edtMsg;
	private TextView txtMobile, txtName, txtAdd;
	private String mobile, msg, id, bloodGroup;
	// ImageButton imgBack;
	private BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
	private ImageButton btnSendNotification, btnCallEmergency;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.activity_message);
		Intent i = getIntent();
		Bundle temp = i.getExtras();
		String name = temp.getString("Name");
		mobile = temp.getString("Mobile");
		String add = temp.getString("Address");

		init();

		id = temp.getString("id");
		bloodGroup = temp.getString("blood_group");

		btnSend = (ImageButton) findViewById(R.id.btnSend);
		edtMsg = (EditText) findViewById(R.id.edtMsg);
		txtMobile = (TextView) findViewById(R.id.txtMobileNo);
		btnCall = (ImageButton) findViewById(R.id.btnCall);
		btnSendNotification = (ImageButton) findViewById(R.id.btnNotification);
		txtName = (TextView) findViewById(R.id.txtName);
		txtMobile = (TextView) findViewById(R.id.txtMobileNo);
		txtAdd = (TextView) findViewById(R.id.txtAdd);

		txtName.setText(name);
		txtMobile.setText(mobile);
		txtAdd.setText(add);

		btnSend.setOnClickListener(this);

		btnCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(MessageActivity.this).setTitle("Alert").setMessage("Are you sure you want to call " + mobile + "?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:" + mobile));
								callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(callIntent);
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();

			}
		});

		btnSendNotification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnSendNotification.setEnabled(false);
				new AsyncSendNotification().execute();
			}
		});

	}
	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnSend:

			if (edtMsg.getText().toString().equals("")) {
				return;
			}

			msg = edtMsg.getText().toString().trim();

			SmsManager mSmsManager = SmsManager.getDefault();

			PendingIntent piSent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
			PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

			mSmsManager.sendTextMessage(mobile, null, msg, piSent, piDelivered);

			edtMsg.setText("");
			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		int id = Integer.parseInt(sp.getString("user_id", "-1"));
		if (id == -1) {
			finish();
		}
		
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		smsSentReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS has been sent", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic Failure", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No Service", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio Off", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}
		};
		smsDeliveredReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
		registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED"));
	}

	public void onPause() {
		super.onPause();
		unregisterReceiver(smsSentReceiver);
		unregisterReceiver(smsDeliveredReceiver);
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
				String ids = id;

				String message = getDonorName() + " from " + getCityName() + " need " + bloodGroup + " blood urgently";

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
				Toast toast = Toast.makeText(MessageActivity.this, "Alert sent successfully to donor", Toast.LENGTH_LONG);
				toast.setGravity(1, 0, 0);
				toast.show();
			} else {
				Toast toast = Toast.makeText(MessageActivity.this, "Error sending alert", Toast.LENGTH_LONG);
				toast.setGravity(1, 0, 0);
				toast.show();
			}
			btnSendNotification.setEnabled(false);
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
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		return sp.getString("donor_bloodgroup", "-1");
	}

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);
		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MessageActivity.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
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
				new JsonParse().showHelp(MessageActivity.this);
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
