package com.streamnow.lsmobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.streamnow.lsmobile.R;
import com.streamnow.lsmobile.datamodel.LDContact;
import com.streamnow.lsmobile.lib.LDConnection;
import com.streamnow.lsmobile.utils.Lindau;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ContactActivity extends BaseActivity {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ImageView avatarImageView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView contactInfoTextView;
    private EditText messageEditText;

    private LDContact contact;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(R.layout.activity_contact);

        String apiUrlString = getIntent().getStringExtra("api_url");

        LinearLayout bgnd = (LinearLayout) findViewById(R.id.bar_bgnd);
        ImageView imageView = (ImageView) findViewById(R.id.contact_bgnd_image);

        int colorTop = Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop;

        bgnd.setBackgroundColor(colorTop);
        imageView.setColorFilter(colorTop, PorterDuff.Mode.SRC_ATOP);
        imageView.invalidate();

        final GestureDetector gdt = new GestureDetector(new GestureListener());

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });

        ImageView leftArrow = (ImageView) findViewById(R.id.left_arrow_contact);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.avatarImageView = (ImageView) findViewById(R.id.contact_avatar);
        this.phoneTextView = (TextView) findViewById(R.id.contact_phone);
        this.emailTextView = (TextView) findViewById(R.id.contact_email);
        this.contactInfoTextView = (TextView) findViewById(R.id.contact_info);
        this.messageEditText = (EditText) findViewById(R.id.contact_msg_edittext);

        Button buttonSend = (Button) findViewById(R.id.button_send);
        buttonSend.setBackgroundColor(colorTop);

        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

        RequestParams requestParams = new RequestParams();

        if (apiUrlString == null || apiUrlString.equals("")) {
            requestParams.add("access_token", Lindau.getInstance().getCurrentSessionUser().accessToken);
            LDConnection.get("getContact", requestParams, new ResponseHandlerJson());
        } else {
            String endPoint = apiUrlString + "getContacts";
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            httpClient.setEnableRedirects(true);
            httpClient.get(endPoint, requestParams, new ResponseHandlerJson());
        }


    }

    private void showAlertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void setOutlets() {
        this.phoneTextView.setText(this.contact.telephone);
        this.emailTextView.setText(this.contact.email);

        String spaceChar = "\n";
        String contactInfoString = this.contact.company + spaceChar +
                this.contact.address + spaceChar +
                this.contact.zip + spaceChar +
                this.contact.city + spaceChar +
                getString(R.string.opening_schedule) + ":" + spaceChar +
                this.contact.schedule;
        this.contactInfoTextView.setText(contactInfoString);

        final String avatarUrl = LDConnection.getAbsoluteUrl("getContactAvatar") +
                "?access_token=" + Lindau.getInstance().getCurrentSessionUser().accessToken +
                "&id=" + this.contact.id;

        Picasso.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.contact_placeholder)
                .into(this.avatarImageView);
    }

    public void sendMessage(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{this.contact.email});
        i.putExtra(Intent.EXTRA_SUBJECT, this.contact.company + " " + getString(R.string.support));
        i.putExtra(Intent.EXTRA_TEXT, this.messageEditText.getText());
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            showAlertDialog(getString(R.string.no_mail_account));
        }
    }

    public void resetEditText(View v) {
        this.messageEditText.setText("");
    }

    private class ResponseHandlerJson extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if (response.getString("status").equals("ok")) {
                    ArrayList<LDContact> contacts = LDContact.contactsFromArray(response.getJSONArray("contacts"));

                    if (contacts.size() > 0) {
                        contact = contacts.get(0);
                        setOutlets();
                    } else {
                        showAlertDialog(getString(R.string.network_error));
                    }
                } else {
                    showAlertDialog(getString(R.string.network_error));
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertDialog(getString(R.string.network_error));
            }
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
            showAlertDialog(getString(R.string.network_error));
            System.out.println("getContact onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            showAlertDialog(getString(R.string.network_error));
            System.out.println("getContact onFailure json");
            progressDialog.dismiss();
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false;
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                ContactActivity.this.finish();
                return false;
            }
            return false;
        }
    }
}
