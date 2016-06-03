/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.wdipl.bloodbank.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import com.google.android.gcm.GCMBaseIntentService;
import com.wdipl.bloodbank.R;
import com.wdipl.bloodbank.SplashScreen;
import com.wdipl.json.Database;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
public class GCMIntentService extends GCMBaseIntentService {
	public GCMIntentService() {
		super("694951481226");
	}

	@Override
	protected void onRegistered(Context ctxt, String regId) {
		Log.d(getClass().getSimpleName(), "onRegistered: " + regId);
		// Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onUnregistered(Context ctxt, String regId) {
		Log.d(getClass().getSimpleName(), "onUnregistered: " + regId);
	}

	@Override
	protected void onMessage(Context ctx, Intent message) {
		Bundle extras = message.getExtras();
		showNotification(ctx, extras);
		// for (String key : extras.keySet()) {
		// Log.d(getClass().getSimpleName(),
		// String.format("onMessage: %s=%s", key, extras.getString(key)));
		// }
	}

	@Override
	protected void onError(Context ctxt, String errorMsg) {
		Log.d(getClass().getSimpleName(), "onError: " + errorMsg);
	}

	@Override
	protected boolean onRecoverableError(Context ctxt, String errorMsg) {
		Log.d(getClass().getSimpleName(), "onRecoverableError: " + errorMsg);

		return (true);
	}

	private void showNotification(Context ctx, Bundle data) {
		int id = (int) new Date().getTime();

		for (String key : data.keySet()) {
			
			try {
				Log.d(getClass().getSimpleName(), String.format("onMessage: %s=%s", key, URLDecoder.decode(data.getString(key), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
			}
			
			
		}

		String[] ids = data.getString("ids").split(",");
		String userId = getUserId() + "";

		for (String str : ids) {
			if (str.equalsIgnoreCase(userId)) {
				
				Intent notificationIntent = new Intent(ctx, SplashScreen.class);
				PendingIntent contentIntent = PendingIntent.getActivity(ctx, 1100, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
				Resources res = ctx.getResources();
				Notification.Builder builder = new Notification.Builder(ctx);
				builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.logo).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
						.setTicker("BloodBank Notification").setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle("BloodBank")
						.setContentText(data.getString("message"));
				Notification n = builder.build();
				nm.notify(id, n);
				break;
			}
		}
		try {
			
			
			
			Database db = new Database(getApplicationContext());
			ContentValues cv = new ContentValues();
			cv.put("name", data.getString("message"));
			cv.put("blood_group", data.getString("bldgrp"));
			cv.put("phone_no", data.getString("contactno"));
			cv.put("city",data.getString("city"));
			cv.put("sent_time",data.getString("time"));
			db.addNotification(cv);

		} catch (Exception e) {

		}

	}

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

}
