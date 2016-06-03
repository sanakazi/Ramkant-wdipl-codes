package com.wdipl.bloodbank;

import com.google.android.gcm.GCMBroadcastReceiver;
import com.google.android.gcm.GCMRegistrar;
import com.wdipl.bloodbank.notification.CommonUtilities;
import com.wdipl.bloodbank.notification.GCMIntentService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;

public class SplashScreen extends Activity {

	CountSplash cs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen_new);
		cs = new CountSplash(2 * 1000, 1 * 1000);
		cs.start();

		
		try {
			if (com.wdipl.bloodbank.BuildConfig.DEBUG) {
				GCMRegistrar.checkManifest(this);
			}

			GCMRegistrar.register(this, getString(R.string.gcm_sender_id));
			registerReceiver(mHandleMessageReceiver, new IntentFilter("DISPLAY_MESSAGE_ACTION"));

			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

			new GCMIntentService();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// mDisplay.append(newMessage + "\n");
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mHandleMessageReceiver);
	};

	public class GCMReceiver extends GCMBroadcastReceiver {
		@Override
		protected String getGCMIntentServiceClassName(Context context) {
			return "com.wdipl.bloodbank.notification.GCMIntentService";
		}
	}

	private class CountSplash extends CountDownTimer {

		public CountSplash(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			finish();
			if (getUserId() == -1) {
				Intent i = new Intent(SplashScreen.this, LoginActivity.class);
				startActivity(i);
			} else {
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);
			}

		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

	}

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}
	
	
}
