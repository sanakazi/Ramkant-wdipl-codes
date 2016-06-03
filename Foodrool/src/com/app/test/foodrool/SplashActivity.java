package com.app.test.foodrool;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.cert.CertificateException;

import com.app.test.foodrool.gcm.RegistrationIntentService;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.Window;

public class SplashActivity extends Activity {

	CountSplash cs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		
		 Intent intent = new Intent(this, RegistrationIntentService.class);
         startService(intent);
		cs = new CountSplash(2 * 1000, 1 * 1000);
		cs.start();
		 get();
	}

	private class CountSplash extends CountDownTimer {

		public CountSplash(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			finish();
			if (getUserId() == -1) {
				Intent i = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(i);
			} else {
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
			}

		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

	}

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		//return -1;
		return Integer.parseInt(sp.getString("user_id", "-1"));
	}
	
	private void get(){
		try {
		PackageInfo info=getPackageManager().getPackageInfo("com.app.test.foodrool",PackageManager.GET_SIGNATURES);
		for(Signature signature:info.signatures){
			MessageDigest md=MessageDigest.getInstance("SHA-1");
			md.update(signature.toByteArray());
			Log.i("KeyHash",Base64.encodeToString(md.digest(),Base64.DEFAULT));
		}
	} catch (Exception e) {
		e.printStackTrace();
	}

	}

}
