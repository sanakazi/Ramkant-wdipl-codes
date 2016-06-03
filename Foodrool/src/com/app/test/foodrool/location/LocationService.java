package com.app.test.foodrool.location;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.app.test.foodrool.webservices.HttpRequest;

public class LocationService extends Service {
	private static final String TAG = "LocationService";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 1f;
	public static final int NOTIFY_INTERVAL_MINUTE = 1 * 60 * 1000;
	private boolean isSending = false;
	private AlarmManager alarmMgr;
	private PendingIntent pendingIntent;
	LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.GPS_PROVIDER), new LocationListener(LocationManager.NETWORK_PROVIDER) };

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		startAlarmManager();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		isSending = false;
		initializeLocationManager();
		try {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[1]);
		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		}catch (Exception ex) {
			Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		}
		try {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					mLocationListeners[0]);
		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}catch (Exception ex) {
			Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		}

		
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.i(TAG, "fail to remove location listners, ignore", ex);
				}
			}
		}
	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}

	private class LocationListener implements android.location.LocationListener {
		Location mLastLocation;

		public LocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "onLocationChanged: " + location);
			try {
				mLastLocation.set(location);
				if (!isSending)
					new AsyncUpdateLocation(mLastLocation.getLatitude() + "", mLastLocation.getLongitude() + "").execute();

				isSending = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}

	private class AsyncUpdateLocation extends AsyncTask<String, Void, String> {

		private boolean isSuccess = false;
		private String lat, lng;

		public AsyncUpdateLocation(String lat, String lng) {
			this.lat = lat;
			this.lng = lng;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			isSending = false;
			if (isSuccess && result != null) {
				//if (result.equalsIgnoreCase("0")) {
					//Toast.makeText(getApplicationContext(), "Location updated successfully", Toast.LENGTH_LONG).show();
					stopSelf();
				//}

			} else {

			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				String res;

			//	cv.put("AdminUserId", getUserId());
				cv.put("AdminUserId", "6");
				cv.put("Latitude", lat);
				cv.put("Longitude", lng);
				res = hr.getDataFromServer(cv, "UpdateUserLocation");
				Log.e("LocationService", "loc res-"+res);
				isSuccess = true;
				return res;
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
				isSuccess = false;
				e.printStackTrace();
			}catch (Exception e) {
				isSuccess = false;
				e.printStackTrace();
			}
			return null;
		}
	}

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

	public void startAlarmManager() {
		Intent dialogIntent = new Intent(getBaseContext(), AlarmService.class);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, NOTIFY_INTERVAL_MINUTE);
		alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		pendingIntent = PendingIntent.getBroadcast(this, 100002, dialogIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		//alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);

	}
}