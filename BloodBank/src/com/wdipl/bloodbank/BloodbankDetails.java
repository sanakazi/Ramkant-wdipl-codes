package com.wdipl.bloodbank;

import com.wdipl.json.JsonParse;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BloodbankDetails extends Activity {
	private ImageButton btnCall, btnCall1, btnCall2;
	private TextView txtContactNo, txtContactNo1, txtContactNo2, txtBankName, txtContactName, txtMobileNo, txtState, txtDistrict;
	private String bankName, contactPerson, contactNo, mobileNo;
	private BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
	private ImageButton btnCallEmergency;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.act_in, R.anim.act_out);
		setContentView(R.layout.bloodbank_details);
		Intent i = getIntent();
		Bundle temp = i.getExtras();
		bankName = temp.getString("BankName");
		mobileNo = temp.getString("mobile_no");
		contactNo = temp.getString("contact_no");
		contactPerson = temp.getString("contact_person");
		init();
		txtContactNo = (TextView) findViewById(R.id.txtContactNo);
		btnCall = (ImageButton) findViewById(R.id.btnCall);
		btnCall1 = (ImageButton) findViewById(R.id.btnCall1);
		btnCall2 = (ImageButton) findViewById(R.id.btnCall2);
		txtBankName = (TextView) findViewById(R.id.txtName);
		txtContactNo = (TextView) findViewById(R.id.txtContactNo);
		txtContactNo1 = (TextView) findViewById(R.id.txtContactNo1);
		txtContactNo2 = (TextView) findViewById(R.id.txtContactNo2);
		txtContactName = (TextView) findViewById(R.id.txtContactName);
		txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);
		txtState = (TextView) findViewById(R.id.txtStateName);
		txtDistrict = (TextView) findViewById(R.id.txtDistrictName);
		txtBankName.setText(bankName);
		txtContactNo.setText(contactNo);
		txtContactName.setText(contactPerson);
		txtState.setText(temp.getString("state"));
		txtDistrict.setText(temp.getString("district"));
		txtMobileNo.setText(mobileNo);

		final String[] contacts = contactNo.split(",");

		if (contacts.length == 0) {
			txtContactNo1.setVisibility(View.GONE);
			txtContactNo2.setVisibility(View.GONE);
			btnCall.setVisibility(View.GONE);
			btnCall1.setVisibility(View.GONE);
			btnCall2.setVisibility(View.GONE);

		} else if (contacts.length == 1 && contacts[0].length() < 3) {
			txtContactNo.setText(contacts[0].trim());
			txtContactNo1.setVisibility(View.GONE);
			txtContactNo2.setVisibility(View.GONE);
			btnCall.setVisibility(View.GONE);
			btnCall1.setVisibility(View.GONE);
			btnCall2.setVisibility(View.GONE);
		} else if (contacts.length == 1) {
			txtContactNo.setText(contacts[0]);
			txtContactNo1.setVisibility(View.GONE);
			txtContactNo2.setVisibility(View.GONE);
			btnCall1.setVisibility(View.GONE);
			btnCall2.setVisibility(View.GONE);
		} else if (contacts.length == 2) {
			txtContactNo.setText(contacts[0]);
			txtContactNo1.setText(contacts[1]);

			txtContactNo2.setVisibility(View.GONE);

			btnCall2.setVisibility(View.GONE);

		} else if (contacts.length == 3) {
			txtContactNo.setText(contacts[0]);
			txtContactNo1.setText(contacts[1]);
			txtContactNo2.setText(contacts[2]);

		}

		btnCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(BloodbankDetails.this).setTitle("Alert").setMessage("Are you sure you want to call " + contacts[0] + "?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:" + contacts[0]));
								callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(callIntent);
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();

			}
		});
		btnCall1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(BloodbankDetails.this).setTitle("Alert").setMessage("Are you sure you want to call " + contacts[1] + "?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:" + contacts[1]));
								callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(callIntent);
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();

			}
		});
		btnCall2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(BloodbankDetails.this).setTitle("Alert").setMessage("Are you sure you want to call " + contacts[2] + "?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:" + contacts[2]));
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

	private void init() {
		btnCallEmergency = (ImageButton) findViewById(R.id.imageButton1);
		btnCallEmergency.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(BloodbankDetails.this).setTitle("Alert").setMessage("Are you sure you want to call 104?")
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
				new JsonParse().showHelp(BloodbankDetails.this);
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
