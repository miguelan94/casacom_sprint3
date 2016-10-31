package com.streamnow.lsmobile.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.streamnow.lsmobile.R;
import com.streamnow.lsmobile.datamodel.DMCategory;
import com.streamnow.lsmobile.datamodel.LDService;
import com.streamnow.lsmobile.datamodel.LDSessionUser;
import com.streamnow.lsmobile.interfaces.IMenuPrintable;
import com.streamnow.lsmobile.lib.LDConnection;
import com.streamnow.lsmobile.utils.DocMenuAdapter;
import com.streamnow.lsmobile.utils.Lindau;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ShoppingActivity extends BaseActivity {

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    private LinearLayout linearLayout;
    private int size = 0;
    private int numCols = 0;
    int serviceClicked = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_shopping);
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init() {
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        RelativeLayout background = (RelativeLayout) findViewById(R.id.background_shopping);
        background.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        ImageView left_arrow = (ImageView) findViewById(R.id.left_arrow_shopping);
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp_w = 171 / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dp_h = 186 / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        System.out.println("dp_w : " + dp_w + " dp_h: " + dp_h);
        final ArrayList<LDService> services = sessionUser.availableServices;


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            numCols = 4;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 6;
        }

        int cont = 0;
        int numRows;
        if (services.size() % numCols == 0) {
            numRows = services.size() / numCols;
        } else {
            numRows = services.size() / numCols + 1;
        }
        for (int i = 0; i < numRows; i++) {

            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));


            if (i == numRows - 1) {
                if (services.size() % numCols != 0) {
                    numCols = services.size() % numCols;
                }
            }

            for (int j = 0; j < numCols; j++) {

                linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                linearLayout.setLayoutParams(new TableRow.LayoutParams(width, height));
                int paddingLayout = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                linearLayout.setPadding(paddingLayout, paddingLayout, paddingLayout, paddingLayout);
                linearLayout.setGravity(Gravity.CENTER);

                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getResources().getDimension(R.dimen.box_icons_width)), (int) (getResources().getDimension(R.dimen.box_icons_height))));


                ImageView image = new ImageView(this);
                ImageView image_bgnd = new ImageView(this);
                final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                image.setPadding(padding, padding, padding, padding);
                image.setTag(R.string.TAGService,cont);
                //image.setOnClickListener(this);

                if (!services.get(cont).usable) {
                    image.setAlpha(100);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getTag(R.string.TAGService)!=null) {
                                final int position = Integer.parseInt(v.getTag(R.string.TAGService).toString());
                                new AlertDialog.Builder(ShoppingActivity.this)
                                        .setTitle(services.get(position).name)
                                        .setMessage(services.get(position).description)
                                        .setPositiveButton(getString(R.string.buy), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                new AlertDialog.Builder(ShoppingActivity.this)
                                                        .setMessage(getString(R.string.shopping_confirm))
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                serviceClicked = position;
                                                                RequestParams requestParams = new RequestParams();
                                                                requestParams.add("access_token",sessionUser.accessToken);
                                                                requestParams.add("service_id",services.get(position).id);
                                                                LDConnection.put("services/enable",requestParams,new ResponseHandlerJson());

                                                            }
                                                        })
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    });



                } else {
                    image.setAlpha(255);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getTag(R.string.TAGService) != null) {
                                new AlertDialog.Builder(ShoppingActivity.this)
                                        .setTitle(services.get(Integer.parseInt(v.getTag(R.string.TAGService).toString())).name)
                                        .setMessage(services.get(Integer.parseInt(v.getTag(R.string.TAGService).toString())).description)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        }
                    });


                }
                createBitMap(image_bgnd);
                Picasso.with(this)
                        .load(services.get(cont).smartImage)
                        .into(image);

                cont++;
                frameLayout.addView(image_bgnd);
                frameLayout.addView(image);
                linearLayout.addView(frameLayout);
                row.addView(linearLayout);

            }
            tableLayout.addView(row);

        }
    }

    private void createBitMap(ImageView bgnd) {

        Bitmap bitMap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        bitMap = bitMap.copy(bitMap.getConfig(), true);
        Canvas canvas = new Canvas(bitMap);

        Paint paint = new Paint();
        paint.setColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.backgroundColorIconSmartphone);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        bgnd.setImageBitmap(bitMap);
        canvas.drawCircle(75, 75, 60, paint);

        bgnd.invalidate();
    }

    private class ResponseHandlerJson extends JsonHttpResponseHandler{
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if(response.getString("msg").equalsIgnoreCase("OK")){
                    new AlertDialog.Builder(ShoppingActivity.this)
                            .setMessage(getString(R.string.service_activated))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //ArrayList<LDService> services = sessionUser.availableServices;
                                    //services.get(serviceClicked).usable = true;


                                    RequestParams requestParams = new RequestParams();
                                    requestParams.add("access_token", sessionUser.accessToken);
                                    LDConnection.get("getUserInfo", requestParams, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                            ArrayList<LDService> services;
                                            try {
                                                services = LDService.servicesFromArray(response.getJSONArray("available_services"));
                                            } catch (JSONException e) {
                                                 services = null;
                                                e.printStackTrace();
                                            }
                                            if(services!=null){
                                                sessionUser.availableServices = services;
                                                Lindau.getInstance().setCurrentSessionUser(sessionUser);
                                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ShoppingActivity.this);
                                                SharedPreferences.Editor prefEditor = sharedPref.edit();

                                                if(sharedPref.getBoolean("keepSession",false)){
                                                    JSONObject json = null;
                                                    try {
                                                        json = new JSONObject(sharedPref.getString("session_user",""));
                                                        json.put("available_services",response.getJSONArray("available_services"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if(json!=null){
                                                         prefEditor.putString("session_user",json.toString());
                                                    }
                                                }
                                                prefEditor.putBoolean("buy",true);
                                                prefEditor.apply();
                                                recreate();




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
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(ShoppingActivity.this)
                            .setMessage(getString(R.string.shopping_error))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
            System.out.println("JSON services onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            new AlertDialog.Builder(ShoppingActivity.this)
                    .setMessage(getString(R.string.shopping_error))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            System.out.println("JSON service onFailure json" + errorResponse.toString());
            new AlertDialog.Builder(ShoppingActivity.this)
                    .setMessage(getString(R.string.shopping_error))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }


}
