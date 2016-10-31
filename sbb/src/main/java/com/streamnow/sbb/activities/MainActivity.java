package com.streamnow.sbb.activities;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.sbb.datamodel.LDSessionUser;
import com.streamnow.sbb.lib.LDConnection;
import com.streamnow.sbb.utils.Lindau;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    private String[] arrayString = null;
    private SharedPreferences preferences;
    private KeyStore keyStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("keepSession", false) && !preferences.getString("AppId", "").equalsIgnoreCase("")) {
            if (!preferences.getString("access_token", "").equals("")) {
                try {
                    keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                Lindau.getInstance().appId = preferences.getString("AppId", "");
                System.out.println("AppId----------->" + Lindau.getInstance().appId);
                if (!LDConnection.isSetCurrentUrl()) {
                    getURL();
                } else {
                    checkTime();
                }
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void getURL() {
        RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
        LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String url = response.getString("url");
                    System.out.println("URL: " + url);
                    LDConnection.setCurrentUrlString(url);
                    checkTime();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("onFailure json");
            }
        });


    }

    private void checkTime() {

        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat dateFormatLocal1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatLocal.setTimeZone(TimeZone.getTimeZone("CET"));
        String time = dateFormatLocal.format(new Date());
        Date date_current = null;
        Date date_server = null;

        try {
            date_current = dateFormatLocal.parse(time);
            date_server = dateFormatLocal.parse(preferences.getString("valid_until", ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = 0;
        if (date_server != null && date_current != null) {
            difference = (date_server.getTime() - date_current.getTime()) / 1000;
        }

        if (difference > 0 && difference <= 100) {
            //refresh

            RequestParams requestParams = new RequestParams();
            requestParams.add("access_token", preferences.getString("access_token", ""));
            requestParams.add("refresh_token", preferences.getString("refresh_token", ""));
            System.out.println("Params " + preferences.getString("access_token", "") + " " + preferences.getString("refresh_token", ""));
            LDConnection.get("refreshToken", requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();

                    try {
                        prefEditor.putString("valid_until", response.getString("validUntil"));
                        prefEditor.putString("access_token", response.getString("access_token"));
                        prefEditor.putString("refresh_token", response.getString("refresh_token"));
                        prefEditor.apply();

                        JSONObject json = new JSONObject(preferences.getString("session_user", ""));

                        json.put("access_token", response.getString("access_token"));
                        json.put("refresh_token", response.getString("refresh_token"));
                        json.put("validUntil", response.getString("validUntil"));
                        prefEditor.putString("session_user", json.toString());
                        prefEditor.apply();
                        Log.i("JSON", json.toString());
                        continueCheckLogin();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("onFailure json" + errorResponse.toString());

                }
            });


        } else if (difference <= 0) {
            //login

            System.out.println("login");
            final String username = preferences.getString("user", "");
            //String cipherPassword = preferences.getString("pass","");
            String cipherPassword = preferences.getString("pass", "");
            // final String password = decryptString("livingservices",cipherPassword);
            final String password = decryptString(cipherPassword);
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", username);
            requestParams.add("password", password);
            requestParams.add("source", "Mobile");

            LDConnection.post("auth/login", requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println("session: " + response.toString());
                    LDSessionUser sessionUser;

                    try {
                        sessionUser = new LDSessionUser(response);
                    } catch (Exception e) {
                        sessionUser = null;
                        e.printStackTrace();
                    }

                    if (sessionUser != null && sessionUser.accessToken != null) {
                        Lindau.getInstance().setCurrentSessionUser(sessionUser);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        prefEditor.putString("valid_until", sessionUser.validUntil);
                        prefEditor.putString("session_user", response.toString());
                        prefEditor.putString("access_token", sessionUser.accessToken);
                        prefEditor.putString("refresh_token", sessionUser.refreshToken);
                        prefEditor.putBoolean("keepSession", true);
                        prefEditor.putString("AppId", Lindau.getInstance().appId);

                        // createNewKeys("livingservices");
                        String cipherPass = encryptString("livingservices",password); //livingservices
                        prefEditor.putString("user", username);
                        prefEditor.putString("pass", cipherPass);
                        prefEditor.apply();
                        //Intent i = new Intent(MainActivity.this, RegistrationIntentService.class);
                        //startService(i);
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        System.out.println("Incorrect username or password");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("login onFailure json" + errorResponse.toString());
                }
            });
        } else {
            continueCheckLogin();
        }
    }

    private void continueCheckLogin() {
        String session = preferences.getString("session_user", "");
        if (!session.equalsIgnoreCase("")) {
            LDSessionUser LDsessionUser;
            try {
                JSONObject json = new JSONObject(session);
                LDsessionUser = new LDSessionUser(json);
            } catch (JSONException e) {
                LDsessionUser = null;
                e.printStackTrace();
            }


            if (LDsessionUser != null && LDsessionUser.accessToken != null) {
                Lindau.getInstance().setCurrentSessionUser(LDsessionUser);
                Intent intent = new Intent(this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }


    private String decryptString(String cipherText) {
        if (!preferences.getString("Secret_Key", "").equals("")) {

            byte[] decodeBytes;

            try {
               // keyStore.getEntry("livingservices",null);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                String key = preferences.getString("Secret_Key", "");
                byte [] IV = Base64.decode(preferences.getString("IV",""),Base64.DEFAULT);
                byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
                SecretKey originalKey = new SecretKeySpec(encodedKey, 0,
                        encodedKey.length, "AES");
                cipher.init(Cipher.DECRYPT_MODE, originalKey,new IvParameterSpec(IV));
                byte[] cipherTextInBytes = Base64.decode(cipherText, Base64.DEFAULT);
                decodeBytes = cipher.doFinal(cipherTextInBytes);
                return new String(decodeBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }










      /*  String decryptedText = null;
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKey);



            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            decryptedText = new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            Log.e("Exception", Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return decryptedText;
        */
    }

    private void refreshKeys() {
        List<String> keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                System.out.println("KEYSTORE: " + aliases.nextElement());
                keyAliases.add(aliases.nextElement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void createNewKeys(String alias) {
        try {

            if (!keyStore.containsAlias(alias)) {
                //keyStore.setKeyEntry("livingservices",key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshKeys();
    }

    private String encryptString(String alias,String text) {


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("Secret_Key", "").equals("")) {
            byte[] randomByte = new byte[8];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomByte);


            try {

                KeyGenerator key = KeyGenerator.getInstance("HmacSHA256");
                key.init(256);
                SecretKey secret = key.generateKey();


                try {

                    if (!keyStore.containsAlias(alias)) {
                        keyStore.setKeyEntry(alias,secret,null,null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshKeys();
                String stringSecretKey = Base64.encodeToString(secret.getEncoded(), Base64.DEFAULT);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putString("Secret_Key", stringSecretKey);
                prefEditor.apply();


            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();


            }
        }
            if (!preferences.getString("Secret_Key", "").equals("")) {
                byte[] encodedBytes;
                try {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                    String key = preferences.getString("Secret_Key", "");
                    byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);

                    SecretKey originalkey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

                    byte[] IV = new byte[cipher.getBlockSize()];

                    SecureRandom secureRandom = new SecureRandom();
                    secureRandom.nextBytes(IV);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("IV", Base64.encodeToString(IV, Base64.DEFAULT));
                    prefEditor.apply();

                    IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);

                    cipher.init(Cipher.ENCRYPT_MODE, originalkey, ivParameterSpec);
                    encodedBytes = cipher.doFinal(text.getBytes());

                    return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";

                }
            } else {
                return "";
            }


    }
}
