package com.streamnow.lsmobile.activities;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Miguel Angel on 27/07/2016.
 */
public class PushNotifications extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("From", "From: " + from);
        Log.d("Msg", "Message: " + message);

    }
}
