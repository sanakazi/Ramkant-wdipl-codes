package com.app.test.foodrool.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class AlarmService extends BroadcastReceiver {
	Context context;
	int defaultState;
	boolean FLAG = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Intent service=new Intent(context, LocationService.class);
		context.startService(service);
		Log.e("AlarmService", System.currentTimeMillis()+"");

	}
}
