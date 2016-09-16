package com.streamnow.sbb.activities;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Miguel Angel on 27/07/2016.
 */
public class TokenRefreshListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        Log.i("Log","Token refresh");
        startService(intent);
    }
}
