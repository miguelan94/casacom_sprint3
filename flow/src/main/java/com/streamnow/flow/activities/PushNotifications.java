package com.streamnow.flow.activities;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.streamnow.flow.R;

/**
 * Created by Miguel Angel on 27/07/2016.
 */
public class PushNotifications extends GcmListenerService {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("test");
        String message1 = data.getString("Message");

        Log.d("From", "From: " + from);
        Log.d("Msg", "Message: " + message);
        Log.d("Msg", "Message1: " + message1);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification")
                        .setContentText(message);
        Intent resultIntent = new Intent(this, MenuActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MenuActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}
