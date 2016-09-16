package com.streamnow.lsmobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lsmobile.R;
import com.streamnow.lsmobile.datamodel.LDEvents;
import com.streamnow.lsmobile.lib.LDConnection;
import com.streamnow.lsmobile.utils.Lindau;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class EventActivity extends BaseActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private ArrayList<LDEvents> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(R.layout.activity_event);

        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

        LinearLayout bgnd = (LinearLayout) findViewById(R.id.bar_bgnd);
        bgnd.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop);
        ImageView bgnd_image = (ImageView) findViewById(R.id.event_bgnd_image);
        bgnd_image.setColorFilter(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop, PorterDuff.Mode.SRC_ATOP);
        ImageView leftArrow = (ImageView) findViewById(R.id.left_arrow_event);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RequestParams requestParams = new RequestParams();
        requestParams.add("access_token", Lindau.getInstance().getCurrentSessionUser().accessToken);
        LDConnection.post("getNotifications", requestParams, new ResponseHandlerJson());
    }

    private void init() {

        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_main);

        TextView textView0 = (TextView) findViewById(R.id.headDate);
        TextView textView1 = (TextView) findViewById(R.id.headTime);
        TextView textView2 = (TextView) findViewById(R.id.headOccasion);
        TextView textView3 = (TextView) findViewById(R.id.headWhere);
        textView0.setText(R.string.date_event);
        textView0.setTextColor(Color.BLACK);
        textView1.setText(R.string.time_event);
        textView1.setTextColor(Color.BLACK);
        textView2.setText(R.string.title_event);
        textView2.setTextColor(Color.BLACK);
        textView3.setText(R.string.place_event);
        textView3.setTextColor(Color.BLACK);
        for (int i = 0; i < events.size(); i++) {
            TableRow tbrow = new TableRow(this);
            tbrow.setTag(i);
            tbrow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbrow.setPadding(0, 35, 0, 35);
            TextView t1v = new TextView(this);
            t1v.setPadding(3, 3, 3, 3);
            t1v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            t1v.setText(events.get(i).date);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setPadding(3, 3, 3, 3);
            t2v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            t2v.setText(events.get(i).time);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setPadding(3, 3, 3, 3);
            t3v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            t3v.setText(events.get(i).title);
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setPadding(3, 3, 3, 3);
            t4v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            t4v.setText(events.get(i).place);
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            View line = new View(this);
            line.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
            line.setBackgroundColor(Color.rgb(213, 212, 212));
            line.setPadding(5, 0, 5, 0);
            tableLayout.addView(tbrow);
            tableLayout.addView(line);
            tbrow.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();
        showPopUp(position);

    }

    private void showPopUp(int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(getResources().getString(R.string.create_date_event) + ": " + events.get(position).create_date + "\n"
                        + "\n" + getResources().getString(R.string.date_event) + ": " + events.get(position).date + "\n"
                        + "\n" + getResources().getString(R.string.time_event) + ": " + events.get(position).time + "\n"
                        + "\n" + getResources().getString(R.string.title_event) + ": " + events.get(position).title + "\n"
                        + "\n" + getResources().getString(R.string.description_event) + ": " + events.get(position).description + "\n"
                        + "\n" + getResources().getString(R.string.place_event) + ": " + events.get(position).place)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    private class ResponseHandlerJson extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                events = LDEvents.eventsFromArray(response.getJSONArray("notifications"));
                init();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
            System.out.println("getevent onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            System.out.println("getEvent onFailure json" + errorResponse.toString());
            progressDialog.dismiss();
        }
    }
}

