/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.codesolutions.gms.gcm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

import kr.codesolutions.gms.MainActivity;
import kr.codesolutions.gms.MessageWindow;
import kr.codesolutions.gms.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GCMBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GCMIntentService extends IntentService {
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context context;

    public GCMIntentService() {
        super("GMS GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error!", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted on server!", extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				try {
					Iterator<String> itr = extras.keySet().iterator();
					while(itr.hasNext()){
						String key = itr.next();
						if(key.startsWith("GMS.")){
							Log.d(GCMConstants.TAG," key = " + key);
							extras.putString(key, URLDecoder.decode(extras.getString(key), "UTF-8")); // 한글처리
						}
					}
	                // Post notification of received message.
	                //sendNotification(extras);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Log.i(GCMConstants.TAG, "Received: " + extras.toString());
                displayMessage(context, extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

	static void displayMessage(Context context, Bundle bundle) {
		Intent intent = new Intent(GCMConstants.DISPLAY_MESSAGE_ACTION);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);

		Intent messageWindowIntent = new Intent(context, MessageWindow.class);
		messageWindowIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		messageWindowIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		messageWindowIntent.putExtras(bundle);
        context.startActivity(messageWindowIntent);
	}

	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle bundle) {
		String subject = bundle.getString(GCMConstants.GMS_MESSAGE_SUBJECT);
		String content = bundle.getString(GCMConstants.GMS_MESSAGE_CONTENT);
    	
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle(subject)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(content))
        .setContentText(content)
        .setContentIntent(contentIntent);
        Notification noti = mBuilder.build();
        noti.defaults = Notification.DEFAULT_ALL;
        mNotificationManager.notify(GCMConstants.NOTIFICATION_ID, noti);
    }
}
