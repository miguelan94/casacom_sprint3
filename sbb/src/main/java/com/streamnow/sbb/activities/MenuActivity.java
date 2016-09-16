package com.streamnow.sbb.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.streamnow.sbb.R;
import com.streamnow.sbb.datamodel.LDService;
import com.streamnow.sbb.datamodel.LDSessionUser;
import com.streamnow.sbb.interfaces.IMenuPrintable;
import com.streamnow.sbb.lib.LDConnection;
import com.streamnow.sbb.utils.Lindau;
import com.streamnow.sbb.utils.MenuAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MenuActivity extends BaseActivity
{
    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    String categoryId;
    CustomTabsClient mCustomTabsClient;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        // Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main_menu);

        Intent i = new Intent(this, RegistrationIntentService.class);
        startService(i);


        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        List<Cookie> cookies = myCookieStore.getCookies();
        for (int c = 0; c < cookies.size(); c++) {
            System.out.println("COOKIES---" + cookies.get(c).getName() + " value" + cookies.get(c).getValue() );
        }


        categoryId = this.getIntent().getStringExtra("category_id");
        ArrayList<? extends IMenuPrintable> adapterArray;

        if( categoryId == null )
        {
            adapterArray = sessionUser.getAvailableServicesForSession();
        }
        else
        {
            adapterArray = sessionUser.getAvailableServicesForCategoryId(categoryId);
        }


        /*Locale locale = new Locale(sessionUser.userInfo.language);
        //Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
*/





        RelativeLayout mainBackground = (RelativeLayout) findViewById(R.id.main_menu_background);
        //mainBackground.setBackgroundColor(sessionUser.userInfo.partner.colorTop);
        mainBackground.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);


        TextView textView = (TextView)findViewById(R.id.text_app_name);
        if(sessionUser.userInfo.partner.smartphoneAppName!=null && sessionUser.userInfo.partner.smartphoneAppName.isEmpty()){
            textView.setText(sessionUser.userInfo.partner.name);
        }
        else{
            textView.setText(sessionUser.userInfo.partner.smartphoneAppName);
        }




        RelativeLayout bgnd_image = (RelativeLayout)findViewById(R.id.bgnd_image);
        ImageView smart_image = (ImageView)findViewById(R.id.smartphone_image);
        ImageView left_arrow = (ImageView)findViewById(R.id.left_arrow);
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View dividerTop = findViewById(R.id.divider);
        ImageView imageView = (ImageView)findViewById(R.id.settings_ico); //settings
        if(!getIntent().getBooleanExtra("sub_menu",false)){
            //smart_image.setImageResource(sessionUser.userInfo.partner.backgroundSmartphoneImage);

            System.out.println("Image: " + sessionUser.userInfo.partner.backgroundSmartphoneImage);
            dividerTop.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(sessionUser.userInfo.partner.backgroundSmartphoneImage)
                    .into(smart_image);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MenuActivity.this,SettingsActivity.class);
                    i.putExtra("main_menu",true);
                    startActivity(i);

                }
            });
        }
        else{
            dividerTop.setBackgroundColor(sessionUser.userInfo.partner.lineColorSmartphone);
            dividerTop.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            smart_image.setVisibility(View.GONE);
            //bgnd_image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            left_arrow.setVisibility(View.VISIBLE);
            //bgnd_image.setVisibility(View.GONE);
            //mainBackground.setPadding(0,(int)getResources().getDimension(R.dimen.activity_vertical_margin),0,0);
        }

        final ListView listView = (ListView) findViewById(R.id.main_menu_list_view);
        listView.setDivider(new ColorDrawable(sessionUser.userInfo.partner.lineColorSmartphone));
        listView.setDividerHeight(1);
        listView.setAdapter(new MenuAdapter(this, adapterArray));



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                menuItemClicked(position);
            }
        });
    }

    private void menuItemClicked(int position)
    {
        ArrayList<? extends IMenuPrintable> services;

        if( getIntent().getBooleanExtra("sub_menu", false) ) //si true
        {

            services = sessionUser.getAvailableServicesForCategoryId(categoryId);
            final LDService service = (LDService) services.get(position);
            if (service.type.equals("2"))
            {
                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", service.webviewUrl);
                intent.putExtra("service_id", service.id);
                if(categoryId.equals("5")){
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("appId",service.secretId);
                    requestParams.add("userId",getIntent().getStringExtra("user_vodka"));
                    requestParams.add("password",getIntent().getStringExtra("pass_vodka"));
                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    String version = android.os.Build.VERSION.RELEASE;
                    String  ID =   android.os.Build.ID;
                    String model = android.os.Build.MODEL;
                    System.out.println("version: " + version + " id: " + ID);
                    PackageInfo packageInfo = null;
                    PackageInfo packageInfo1 = null;
                    try {
                         packageInfo = getPackageManager().getPackageInfo("com.android.chrome",0);
                        packageInfo1 = getPackageManager().getPackageInfo("com.google.android.webview",0);
                        System.out.println("Info webview: " + packageInfo1.packageName + " " + packageInfo1.versionName + " " + packageInfo1.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                    //System.out.println("name: " + packageInfo.packageName + " version: "+ packageInfo.versionName + " codigo: " + packageInfo.versionCode + " info: " + packageInfo.applicationInfo);

                    //System.out.println("user agent: " + "Mozilla/5.0 (Linux; Android "+version+"; "+model+" Build/"+ID+"; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/"+packageInfo.versionName+" Mobile Safari/537.36");
                    httpClient.setUserAgent("Mozilla/5.0 (Linux; Android "+version+"; "+model+" Build/"+ID+"; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/"+packageInfo.versionName+" Mobile Safari/537.36");
                    httpClient.setEnableRedirects(true);
                    httpClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                    httpClient.post(service.apiUrl,requestParams,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                        {
                            try {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){



                                    /*CustomTabsServiceConnection mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
                                        @Override
                                        public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                                           mCustomTabsClient = customTabsClient;
                                            mCustomTabsClient.warmup(0L);
                                        }

                                        @Override
                                        public void onServiceDisconnected(ComponentName name) {
                                            mCustomTabsClient = null;
                                        }
                                    };*/

/*
                                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.left_arrow);
                                int height_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
                                int width_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                                    Bitmap ic = Bitmap.createScaledBitmap(icon,width_dp,height_dp,false);
                                System.out.println("width: " + icon.getWidth() + " height: " + icon.getHeight());
                                System.out.println("new width: " + ic.getWidth() + " new heihgt: " + ic.getHeight());
                                    Intent i = new Intent(MenuActivity.this,MenuActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MenuActivity.this,0,i,0);

                                    CustomTabsClient.bindCustomTabsService(MenuActivity.this, "com.android.chrome",new CustomTabsServiceConnection() {
                                        @Override
                                        public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                                            mCustomTabsClient = customTabsClient;
                                            mCustomTabsClient.warmup(0);

                                        }

                                        @Override
                                        public void onServiceDisconnected(ComponentName name) {
                                            mCustomTabsClient = null;
                                        }
                                    });

                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                   // builder.setCloseButtonIcon(icon);
                                    //builder.enableUrlBarHiding();
                                    //builder.setActionButton(ic,"back",pendingIntent);
                                   // builder.addDefaultShareMenuItem().setCloseButtonIcon(ic);

                                    builder.setToolbarColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop);

                                   // builder.setExitAnimations(MenuActivity.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    //builder.enableUrlBarHiding ();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    customTabsIntent.launchUrl(MenuActivity.this, Uri.parse(service.apiUrl+"token="+response.getString("token")));


                                //response.getString("token");
*/


                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(service.webviewUrl+"token="+response.getString("token")));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setPackage("com.android.chrome");
                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        // Chrome is probably not installed
                                        intent.setPackage(null);
                                        startActivity(intent);
                                    }


                                   // intent.putExtra("token",response.getString("token") );
                                    //startActivity(intent);

                                }
                                else{
                                    intent.putExtra("token",response.getString("token") );
                                    startActivity(intent);
                                }
                            }
                            catch (JSONException e){
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
                            System.out.println("get token KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                        }
                    });
                }else{
                    startActivity(intent);
                }

            }
            else if (service.type.equals("3"))
            {
                // TODO Open youtube video here
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", "https://m.youtube.com/watch?v=" + service.webviewUrl);
                startActivity(intent);
            }
            else if(service.type.equals("1")){
                if(service.id.equals("22")){//events
                    Intent i = new Intent(this,EventActivity.class);
                    startActivity(i);

                }
                else if( service.id.equals("53") || service.id.equals("20") )
                {
                    Intent intent = new Intent(this, ContactActivity.class);
                    System.out.println("Type 1" + service.apiUrl);
                    intent.putExtra("api_url", service.apiUrl);
                    startActivity(intent);
                }
                else if(service.id.equals("3"))
                {
                    Intent intent = new Intent(this, DocmanMenuActivity.class);
                    intent.putExtra("root_menu", true);
                    intent.putExtras( new Bundle());
                    startActivity(intent);
                }
            }
            else if(service.type.equals("8")){
                Intent intent = getPackageManager().getLaunchIntentForPackage(service.webviewUrl);
                if(intent!=null){
                    startActivity(intent);
                }else{

                }
            }
        }
        else
        {
            services = sessionUser.getAvailableServicesForCategoryId(sessionUser.categories.get(position).id);

             System.out.println("clicked on item with title " + sessionUser.categories.get(position).name + " it has " + services.size() + " services available");

            if (services.size() == 1)
            {

                LDService service = (LDService) services.get(0);
                    //check service type
                if (service.type.equals("1"))
                {
                    if( service.id.equals("53") )
                    {
                        Intent intent = new Intent(this, ContactActivity.class);
                        System.out.println("API---> " + service.apiUrl);
                        intent.putExtra("api_url", service.apiUrl);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(this, DocmanMenuActivity.class);
                        intent.putExtra("root_menu", true);
                        intent.putExtras( new Bundle());
                        startActivity(intent);
                    }
                }
                else if (service.type.equals("2"))
                {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("web_url", service.webviewUrl);
                    startActivity(intent);
                }
                else if (service.type.equals("3"))
                {
                    // TODO Open youtube video here
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("web_url", "https://m.youtube.com/watch?v=" + service.webviewUrl);
                    startActivity(intent);
                }
                else if(service.type.equals("8")){
                    Intent intent = getPackageManager().getLaunchIntentForPackage(service.webviewUrl);
                    if(intent!=null){
                        startActivity(intent);
                    }else{

                    }
                }
            }
            else if (services.size() > 1)
            {
                final Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra("category_id", sessionUser.categories.get(position).id);
                intent.putExtra("sub_menu", true);
                if(sessionUser.categories.get(position).id.equals("5")){//entertainment
                    //if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1){ //API 21-22
                     //   showAlertDialog(getResources().getString(R.string.no_entertainment_avaliable));
                   // }
                   // else{
                        final RequestParams requestParams = new RequestParams("access_token",sessionUser.accessToken);
                        LDConnection.get("myentertainment/getCredentials",requestParams,new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                            {
                                try{
                                    if(response.getJSONObject("status").getString("status").equals("ok")){
                                        intent.putExtra("user_vodka",response.getJSONObject("status").getJSONObject("credentials").getString("username"));
                                        intent.putExtra("pass_vodka",response.getJSONObject("status").getJSONObject("credentials").getString("password"));
                                    }
                                    else{
                                    }
                                    startActivity(intent);

                                }
                                catch (JSONException e){
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
                                System.out.println("getCredentials KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                            }
                        });
                   // }


                }
                else{
                    startActivity(intent);
                }

            }
        }
    }
    private void showAlertDialog(String msg){
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        finish();

    }
}
