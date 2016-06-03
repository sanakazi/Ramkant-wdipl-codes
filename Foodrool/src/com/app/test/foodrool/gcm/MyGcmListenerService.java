/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.app.test.foodrool.gcm;

import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.app.test.foodrool.MainActivity;
import com.app.test.foodrool.R;
import com.google.android.gms.gcm.GcmListenerService;


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class MyGcmListenerService extends GcmListenerService {

   

    public MyGcmListenerService() {
       
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification(data);
    }

    @Override
    public void onDeletedMessages() {
        //sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        //sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        //sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(data.getString("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getNotificationId() /* ID of notification */, notificationBuilder.build());
    }
    private int getNotificationId(){
    	long time = new Date().getTime();
    	String tmpStr = String.valueOf(time);
    	String last4Str = tmpStr.substring(tmpStr.length() - 5);
    	int notificationId = Integer.valueOf(last4Str);

    	return notificationId;
    }
}
