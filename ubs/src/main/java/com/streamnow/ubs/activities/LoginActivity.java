package com.streamnow.ubs.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.streamnow.ubs.R;
import com.streamnow.ubs.datamodel.LDSessionUser;
import com.streamnow.ubs.lib.LDConnection;
import com.streamnow.ubs.utils.Lindau;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final int LOGIN_BUTTON_TAG = 21;
    private final int RESET_BUTTON_TAG = 22;
    private ProgressDialog progressDialog;

    private Button loginButton;
    private Button resetButton;
    private EditText userEditText;
    private EditText passwdEditText;
    private ImageView main_logo;
    private Switch switch_logged;
    private KeyStore keyStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_login_land);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_login);
        }
        init();
    }
  /*  @Override
    protected  void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
       // state.putString("userSaved",userEditText.getText().toString());
        //state.putString("passSaved",passwdEditText.getText().toString());
    }*/

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        main_logo = (ImageView) findViewById(R.id.main_logo);
        loginButton = (Button) this.findViewById(R.id.loginButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        final int colorBP = getResources().getColor(R.color.colorBP);
        loginButton.setBackgroundColor(colorBP);
        resetButton.setBackgroundColor(colorBP);
        switch_logged = (Switch) findViewById(R.id.switch_loggged);
        switch_logged.getThumbDrawable().setColorFilter(getResources().getColor(R.color.switchLogged), PorterDuff.Mode.MULTIPLY);
        switch_logged.getTrackDrawable().setColorFilter(colorBP, PorterDuff.Mode.MULTIPLY);
        switch_logged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch_logged.getThumbDrawable().setColorFilter(colorBP, PorterDuff.Mode.MULTIPLY);


                } else {
                    switch_logged.getThumbDrawable().setColorFilter(getResources().getColor(R.color.switchLogged), PorterDuff.Mode.MULTIPLY);
                }
            }
        });
        userEditText = (EditText) this.findViewById(R.id.userEditText);
        passwdEditText = (EditText) this.findViewById(R.id.passwdEditText);

        loginButton.setOnClickListener(this);
        loginButton.setTag(LOGIN_BUTTON_TAG);
        resetButton.setOnClickListener(this);
        resetButton.setTag(RESET_BUTTON_TAG);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (userEditText.getText().toString().isEmpty() && passwdEditText.getText().toString().isEmpty()) {
                    loginButton.setText(R.string.login_button_title1);
                } else {
                    loginButton.setText(R.string.login_button_title2);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };

        userEditText.addTextChangedListener(textWatcher);
        passwdEditText.addTextChangedListener(textWatcher);

        userEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    passwdEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        passwdEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loginButtonClicked(null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if ((int) v.getTag() == LOGIN_BUTTON_TAG) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                this.loginButtonClicked(v);
            } else {
                showAlertDialog(getResources().getString(R.string.network_error));
            }
        }
        if ((int) v.getTag() == RESET_BUTTON_TAG) {
            resetButtonClicked(v);
        }
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_login_land);
            init();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_login);
            init();
        }

    }*/

    public void loginButtonClicked(View sender) {
        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);
        LDConnection.setCurrentUrlString(null);
        RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
        LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String url = response.getString("url");
                    //'http://esp-testing.streamnow.ch/'
                    LDConnection.setCurrentUrlString(url);
                   // LDConnection.setCurrentUrlString("https://esp-testing.streamnow.ch/");
                    //LDConnection.getHttpClient().setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                    System.out.println("endpoint = " + LDConnection.getAbsoluteUrl("auth/login"));
                    System.out.println("Response.url = '" + url + "'");
                    continueLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("onFailure json");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                System.out.println("onFailure array");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                System.out.println("getURL KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                progressDialog.dismiss();
            }
        });

    }

    public void resetButtonClicked(View v) {
        if (LDConnection.isSetCurrentUrl()) {
            showAlertDialogReset(R.string.no_mail_reset);
        } else {
            RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
            LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String url = response.getString("url");
                        LDConnection.setCurrentUrlString(url);
                        showAlertDialogReset(R.string.no_mail_reset);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("onFailure json");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    System.out.println("onFailure array");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                }
            });
        }
    }


    private void continueLogin() {
        final String username, password;

        if (userEditText.getText().toString().isEmpty() && passwdEditText.getText().toString().isEmpty()) {
            username = Lindau.getInstance().appDemoAccount;
            password = Lindau.getInstance().appDemoAccount;
        } else {
            username = userEditText.getText().toString();
            password = passwdEditText.getText().toString();
        }

        RequestParams requestParams = new RequestParams();
        requestParams.add("email", username);
        requestParams.add("password", password);
        requestParams.add("source", "Mobile");

        LDConnection.post("auth/login", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                LDSessionUser sessionUser;

                try {
                    sessionUser = new LDSessionUser(response);
                } catch (Exception e) {
                    sessionUser = null;
                    e.printStackTrace();
                }


                if (sessionUser != null && sessionUser.accessToken != null) {
                    System.out.println("token: " + sessionUser.accessToken);
                    Lindau.getInstance().setCurrentSessionUser(sessionUser);
                    if (switch_logged.isChecked()) {
                        prefEditor.putString("valid_until", sessionUser.validUntil);
                        prefEditor.putString("session_user", response.toString());
                        prefEditor.putString("access_token", sessionUser.accessToken);
                        prefEditor.putString("refresh_token", sessionUser.refreshToken);
                        prefEditor.putBoolean("keepSession", true);
                        prefEditor.putString("AppId", Lindau.getInstance().appId);

                        createNewKeys("livingservices");
                        String cipherPass = encryptString("livingservices", password);
                        prefEditor.putString("user", username);
                        prefEditor.putString("pass", cipherPass);
                        prefEditor.apply();
                    } else {
                        prefEditor.putBoolean("keepSession", false);
                        prefEditor.apply();
                    }

                    progressDialog.dismiss();
                    //Intent i = new Intent(LoginActivity.this, RegistrationIntentService.class);
                    //startService(i);
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.dismiss();
                    showAlertDialog(getString(R.string.login_error));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode + " response: " + response.toString());
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("login onFailure json " + throwable.toString());
                progressDialog.dismiss();
            }
        });
    }

    private void showAlertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void showAlertDialogReset(int msg) {
        final EditText inputEmail = new EditText(this);
        inputEmail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        layout.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0);
        layout.addView(inputEmail);
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_button_title)
                .setMessage(msg)
                .setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String email = inputEmail.getText().toString();
                        RequestParams requestParams = new RequestParams("email", email);
                        LDConnection.post("auth/reset", requestParams, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                System.out.println("Response: " + response.toString());
                                try {
                                    if (response.get("msg").equals("Ok")) {
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle(getString(R.string.tittle_password_reset))
                                                .setMessage(getString(R.string.password_reseted))
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode + " response: " + response.toString());

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                System.out.println(" onFailure json: " + errorResponse.toString() + " status code = " + statusCode);
                                try {
                                    if (statusCode == 403 && errorResponse.getString("msg").equals("KO")) {
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle(getString(R.string.reset_error_title))
                                                .setMessage(getString(R.string.reset_error_content))
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    private void refreshKeys() {
        List<String> keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
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
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshKeys();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String encryptString(String alias, String text) {
        String encryptedText = null;
        try {

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(text.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            encryptedText = Base64.encodeToString(vals, Base64.DEFAULT);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

}