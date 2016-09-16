package com.streamnow.sbb.activities;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.sbb.R;
import com.streamnow.sbb.datamodel.LDEvents;
import com.streamnow.sbb.lib.LDConnection;
import com.streamnow.sbb.utils.Lindau;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel Angel on 27/07/2016.
 */
public class RegistrationIntentService extends IntentService {

    private static String SENDER_ID = "583844385806";

    public RegistrationIntentService(){
        super(SENDER_ID);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
/*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            System.out.println( "Sender ID: " + getString((R.string.gcm_defaultSenderId)));

            RequestParams requestParams = new RequestParams();
            requestParams.add("access_token",Lindau.getInstance().getCurrentSessionUser().accessToken);
            requestParams.add("app_id",getString(R.string.gcm_defaultSenderId));
            requestParams.add("platform","android");
            requestParams.add("DeviceToken",token);
            LDConnection.get("/setDeviceToken",requestParams,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    try
                    {

                        if(response.getString("status").equalsIgnoreCase("ok")){
                            //Log.i("Log","response is ok");
                        }
                    }
                    catch( JSONException e )
                    {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable)
                {
                    System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode);


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
                {
                    System.out.println("onFailure json" + errorResponse.toString());

                }
            });

            Log.i("TOKEN", "GCM Registration Token: " + token);
              //sendRegistrationToServer(token);
            //   sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d("Fail token", "Failed to complete token refresh", e);
            //   sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

       // Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
       // LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
*/

    }

    private void sendRegistrationToServer(String token){
        RequestParams requestParams = new RequestParams();
        requestParams.add("access_token",token);
        requestParams.add("user_id", "1021");
        requestParams.add("text","test");
        LDConnection.get("sendPushNotification",requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                try
                {
                   System.out.println("response: " + response.getString("status"));
                }
                catch( JSONException e )
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable)
            {
                System.out.println("push notifications onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
            {
                System.out.println("push notifications onFailure json" + errorResponse.toString());
            }
        });

    }

}
