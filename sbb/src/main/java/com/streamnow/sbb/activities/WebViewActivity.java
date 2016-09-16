package com.streamnow.sbb.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.streamnow.sbb.R;
import com.streamnow.sbb.utils.Lindau;

import java.util.Locale;


/** !
 * Created by Miguel Estévez on 31/1/16.
 */
public class WebViewActivity extends BaseActivity
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private WebView webView;
    private static final String TAG = "WebViewActivity";
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        // Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        String webUrlString = getIntent().getStringExtra("web_url");
        String serviceId = getIntent().getStringExtra("service_id");

        if( webUrlString == null || webUrlString.equals("") )
        {
            finish();
        }

        setContentView(R.layout.activity_web_view);

        LinearLayout bgnd = (LinearLayout)findViewById(R.id.bar_bgnd);
        ImageView imageView = (ImageView) findViewById(R.id.bgnd_image);
        ImageView leftArrow = (ImageView)findViewById(R.id.image_left_arrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.webView = (WebView)findViewById(R.id.webView);

        if( serviceId != null && (serviceId.equals("29") || serviceId.equals("57")) )
        {
            FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.webview_top_frame);
            topFrameLayout.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)this.webView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            this.webView.setLayoutParams(params);
        }
        else
        {
            int colorTop = Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop;

            bgnd.setBackgroundColor(colorTop);
            imageView.setColorFilter(colorTop, PorterDuff.Mode.SRC_ATOP);
            imageView.invalidate();

            final GestureDetector gdt = new GestureDetector(new GestureListener());

            imageView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(final View view, final MotionEvent event)
                {
                    gdt.onTouchEvent(event);
                    return true;
                }
            });
        }

        String token = getIntent().getStringExtra("token");
        String version = android.os.Build.VERSION.RELEASE;
        String  ID =   android.os.Build.ID;
        String model = android.os.Build.MODEL;
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo("com.android.chrome",0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        //this.webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android "+version+"; "+model+" Build/"+ID+") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+packageInfo.versionName+" Mobile Safari/537.36");
        if(webUrlString.contains("youtube"))
        {
            this.webView.getSettings().setUseWideViewPort(true);
            this.webView.getSettings().setLoadWithOverviewMode(true);
            this.webView.canGoBack();
            this.webView.setWebChromeClient(new WebChromeClient()
            {
            });
        }

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        // final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.i(TAG, "Processing webview url click...");

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.i(TAG, "Finished loading URL: " + url);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }


        });
        if(getIntent().getStringExtra("token")!=null && (serviceId.equals("29") || serviceId.equals("57") || serviceId.equals("59") || serviceId.equals("60") || serviceId.equals("27"))){
           /* String token = getIntent().getStringExtra("token");
            String version = android.os.Build.VERSION.RELEASE;
            String  ID =   android.os.Build.ID;
            String model = android.os.Build.MODEL;
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo("com.android.chrome",0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            */
            webView.loadUrl(webUrlString+"token="+token);

            //webView.loadUrl("file:///android_asset/fullscreen.html");


        }else{

            webView.loadUrl(webUrlString);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                return true;
        }

    }
    return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView!=null && webView.canGoBack() && getIntent().getStringExtra("token")!=null ) {
            webView.goBack();
            RequestParams requestParams = new RequestParams();
            requestParams.add("token",getIntent().getStringExtra("token"));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setUserAgent("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
            httpClient.setEnableRedirects(true);
            httpClient.post("http://streamnow.interoud.tv/vodkatv/external/client/core/RevokeToken.do?",requestParams,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    System.out.println("success json" + response.toString());



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
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    public void onPause()
    {
        super.onPause();
        this.webView.onPause();
    }
    public void onResume(){
        super.onResume();
        this.webView.onResume();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                WebViewActivity.this.finish();
                return false;
            }
            return false;
        }
    }

}


