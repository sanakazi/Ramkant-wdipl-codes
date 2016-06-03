package com.debalink.services;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver{
	public static int notificationCount,messageCount,subscribersCount;
	@Override
	public void onReceive(Context context, Intent intent) {
		String userId=UserPreferences.getUserId(context);
		
		if(userId!=null&!userId.equals("-1")){
			new Async().execute(userId);
		}
		
	}
	private class Async extends AsyncTask<String, Void,Void>{
		SoapObject res;
		@Override
		protected Void doInBackground(String... params) {
			HttpRequest hr=new HttpRequest();
			ContentValues cv=new ContentValues();
			cv.put("UserId", params[0]);
			
			try {
				res=hr.getSoapObjectFromServer(cv, "FillNotificationCount");
				
				SoapObject diffgramObject = (SoapObject) res.getProperty("diffgram");

				SoapObject newDataSetObject=null;
				

				if (diffgramObject.hasProperty("NewDataSet")) {
					newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
				} else {
				
				}
				//for(int i=0;i<newDataSetObject.getPropertyCount();i++){
					SoapObject data1 = (SoapObject) newDataSetObject.getProperty(0);
					SoapObject data2 = (SoapObject) newDataSetObject.getProperty(1);
					SoapObject data3 = (SoapObject) newDataSetObject.getProperty(2);
					
					String strNotificationCount=data1.getPropertySafelyAsString("NotificationCount", "0");
					String strMessageCount=data2.getPropertySafelyAsString("MessageCount", "0");
					String strSubcriberRequestcnt=data3.getPropertySafelyAsString("SubcriberRequestcnt", "0");
					notificationCount=Integer.parseInt(strNotificationCount);
					messageCount=Integer.parseInt(strMessageCount);
					subscribersCount=Integer.parseInt(strSubcriberRequestcnt);
				//}
					
					Log.i("OnAlarmReceiver",strNotificationCount+" "+strMessageCount+" " +strSubcriberRequestcnt);
				
			} catch (SocketTimeoutException e) {
				
				e.printStackTrace();
			} catch (SocketException e) {
				
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			return null;
		}
	}
}
