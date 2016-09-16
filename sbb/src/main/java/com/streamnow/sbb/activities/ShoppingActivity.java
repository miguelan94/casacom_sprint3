package com.streamnow.sbb.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.squareup.picasso.Picasso;
import com.streamnow.sbb.R;
import com.streamnow.sbb.datamodel.LDService;
import com.streamnow.sbb.datamodel.LDSessionUser;
import com.streamnow.sbb.utils.Lindau;

import java.util.ArrayList;
import java.util.Locale;

public class ShoppingActivity extends BaseActivity implements View.OnClickListener {

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    private LinearLayout linearLayout;
    private int size = 0;
    private int numCols = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        // Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        init();



    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_shopping);
            Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
            // Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init(){
        RelativeLayout background = (RelativeLayout)findViewById(R.id.background_shopping);
        background.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);
        TableLayout tableLayout = (TableLayout)findViewById(R.id.table);
        ImageView left_arrow = (ImageView)findViewById(R.id.left_arrow_shopping);
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp_w = 171 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dp_h = 186 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        System.out.println("dp_w : " + dp_w + " dp_h: " + dp_h);
        ArrayList<LDService> services = sessionUser.availableServices;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            numCols=4;
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            numCols=6;
        }

        int cont=0;
        int numRows = 0;
        if(services.size()%numCols==0){
            numRows = services.size()/numCols;
        }
        else{
            numRows = services.size()/numCols + 1;
        }
        for(int i=0; i<numRows; i++){

            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));


            if(i==numRows-1){
                if(services.size()%numCols!=0){
                    numCols = services.size()%numCols;
                }
            }

            for(int j=0; j<numCols; j++){

                linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                linearLayout.setLayoutParams(new TableRow.LayoutParams(width, height));
                int paddingLayout = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                linearLayout.setPadding(paddingLayout,paddingLayout,paddingLayout,paddingLayout);
                linearLayout.setGravity(Gravity.CENTER);

                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams( (int) (getResources().getDimension(R.dimen.box_icons_width)), (int) (getResources().getDimension(R.dimen.box_icons_height))));


                ImageView image = new ImageView(this);
                ImageView image_bgnd = new ImageView(this);
                int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                image.setPadding(padding,padding,padding,padding);
                //image.setBackgroundColor(Color.GREEN);
                //image.setAdjustViewBounds(true);

                image.setTag(services.get(cont).name);
                image.setOnClickListener(this);
                //image.setLayoutParams();


                if(!services.get(cont).usable){
                    image.setAlpha(100);
                }else{
                    image.setAlpha(255);
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
                // linearLayout.addView(image);

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
        //paint.setStrokeWidth(0.5f);
        paint.setAntiAlias(true);
        bgnd.setImageBitmap(bitMap);
        canvas.drawCircle(75,75,60,paint);

        bgnd.invalidate();
    }

    @Override
    public void onClick(View v) {
        if(v.getTag()!=null && !v.getTag().toString().equals("")){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(v.getTag().toString())
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
        }




    }
}

