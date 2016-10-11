package com.streamnow.vulcanomobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.vulcanomobile.R;
import com.streamnow.vulcanomobile.datamodel.LDSessionUser;
import com.streamnow.vulcanomobile.lib.LDConnection;
import com.streamnow.vulcanomobile.utils.Lindau;
import com.streamnow.vulcanomobile.utils.SettingsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends BaseActivity {

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    protected ArrayList<String> items;
    private static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(R.layout.activity_settings);
        RelativeLayout settings_menu = (RelativeLayout) findViewById(R.id.settings_menu_background);
        settings_menu.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);
        TextView textVersion = (TextView) findViewById(R.id.text_version);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyyMMdd");
        dateFormatLocal.setTimeZone(TimeZone.getDefault());
        textVersion.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        textVersion.setText(getString(R.string.app_name) + " " + pInfo.versionName + " - " + getString(R.string.versionDate));
        if (getIntent().getBooleanExtra("main_menu", true)) {
            this.items = new ArrayList<>();
            String[] list = {getResources().getString(R.string.profile), getResources().getString(R.string.contacts), getResources().getString(R.string.logout), getResources().getString(R.string.shopping)};
            //items.addAll(Arrays.asList(list)); //all
            items.add(0, list[0]);
            items.add(1, list[2]);
        }
        View dividerTop = findViewById(R.id.divider);
        View dividerBottom = findViewById(R.id.dividerBottom);
        dividerTop.setBackgroundColor(sessionUser.userInfo.partner.lineColorSmartphone);
        dividerBottom.setBackgroundColor(sessionUser.userInfo.partner.lineColorSmartphone);
        ListView listView = (ListView) findViewById(R.id.settings_menu_list_view);
        listView.setDivider(new ColorDrawable(sessionUser.userInfo.partner.lineColorSmartphone));
        listView.setDividerHeight(1);
        listView.setAdapter(new SettingsAdapter(this, items));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuItemClicked(position);
            }
        });
        ImageView left_arrow = (ImageView) findViewById(R.id.left_arrow_settings);
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void menuItemClicked(int position) {
        if (position == 0) { //profile clicked
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);

        } else if (position == 1) {//logout

            RequestParams requestParams = new RequestParams();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            requestParams.add("access_token", sharedPref.getString("access_token", ""));
            LDConnection.get("logout", requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("msg").equals("Logout OK")) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putBoolean("keepSession", false);
                            prefEditor.putString("access_token", "");
                            prefEditor.apply();
                            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putBoolean("keepSession", false);
                    prefEditor.putString("access_token", "");
                    prefEditor.apply();
                    Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("login onFailure json");

                }
            });

        } else if (position == 2) {//contacts

            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT_REQUEST);
        } else if (position == 3) {//shopping

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {

                Uri contactData = data.getData();
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(contactData, null, null, null, null);
                    int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                    int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    cursor.moveToFirst();
                    do {
                        String idContact = cursor.getString(contactIdIdx);
                        String name = cursor.getString(nameIdx);
                        String phoneNumber = cursor.getString(phoneNumberIdx);
                    } while (cursor.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }
}
