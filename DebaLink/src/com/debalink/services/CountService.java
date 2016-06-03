package com.debalink.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class CountService extends Service{
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "onCreate");
		AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent=new Intent(getBaseContext(), OnAlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(getBaseContext(), 123, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(),1*60*1000, pi);
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		
		return START_STICKY;
	}
}
