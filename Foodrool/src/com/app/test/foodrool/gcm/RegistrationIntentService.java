package com.app.test.foodrool.gcm;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.xmlpull.v1.XmlPullParserException;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.app.test.foodrool.webservices.HttpRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static SharedPreferences sharedPreferences;

	public RegistrationIntentService() {
		super(TAG);
	
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

		try {

			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken("533963746016",
					GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			// [END get_token]
			Log.i(TAG, "GCM Registration Token: " + token);
			sharedPreferences.edit().putString("device_id", token).commit();
			sharedPreferences.edit().putBoolean("device_id_sent_to_server", false).commit();

			sendRegistrationToServer(this,token, getUserId());

		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh", e);

		}

	}

	public static void sendRegistrationToServer(Context ctx,String deviceId, int userId) {
		new SendTokenToServer(ctx,deviceId, userId).execute();
	}

	private static class SendTokenToServer extends AsyncTask<String, Void, String> {
		private boolean isSuccess = false;
		private String deviceId;
		private Context mCtx;
		int userId;

		public SendTokenToServer(Context ctx,String deviceId, int userId) {
			this.deviceId = deviceId;
			this.userId = userId;
			mCtx=ctx;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (!isSuccess) {

				new SendTokenToServer(mCtx,deviceId, userId).execute();
			} else {
				if (mCtx != null) {
					sharedPreferences = mCtx.getSharedPreferences("data", Context.MODE_PRIVATE);
					sharedPreferences.edit().putBoolean("device_id_sent_to_server", true).commit();
				}
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

				String res = null;
				if (userId >= 0) {
					ContentValues cv = new ContentValues();
					cv.put("userid", userId + "");
					cv.put("DeviceId", deviceId);

					res = hr.getDataFromServer(cv, "AdminUserMasterInsertDeviceId");
					Log.e("Register","Register res-"+ res+" toeken-"+deviceId);
				}

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

				e.printStackTrace();
			}
			return null;
		}
	}

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

}