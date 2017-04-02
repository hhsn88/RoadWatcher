package com.jlabs.accident_listener.Networking;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jlabs.accident_listener.R;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        try {
            // TODO(developer): Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.d(TAG, "gagag from: " + remoteMessage.getFrom());

            String lat = null;
            String lon = null;

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0)
            {
                lat = remoteMessage.getData().get("lat");
                lon = remoteMessage.getData().get("lon");

                if (lat == null || lon == null)
                {
                    Map<String,String> m = remoteMessage.getData();
                    lat = m.get("lat");
                }

                //Calling method to generate notification
                if (remoteMessage.getNotification() != null)
                {
                    String click_action = remoteMessage.getNotification().getClickAction();
                    sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), lat, lon, click_action/*"com.google.firebase.INSTANCE_ID_EVENT"*/);
                }
                else
                {
                    Log.wtf(TAG, "Seriously, WTF?");
                }
            }
            else if (remoteMessage.getNotification() != null)
            {
                // Check if message contains a notification payload.
                Log.d(TAG, "gagag Notification Body: " + remoteMessage.getNotification().getBody());
                if (lat == null || lon == null)
                {
                    String s = remoteMessage.getNotification().getBody();
                    Map<String, String> params = remoteMessage.getData();


                    lat = remoteMessage.getData().toString();
                }
                sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), lat, lon, "com.google.firebase.INSTANCE_ID_EVENT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String messageBody, String messageTitle, String lat, String lon, String click_action)
    {
        try {
            Intent intent = new Intent(click_action);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
