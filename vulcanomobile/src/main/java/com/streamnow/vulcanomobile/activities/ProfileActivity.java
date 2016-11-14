package com.streamnow.vulcanomobile.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.streamnow.vulcanomobile.R;
import com.streamnow.vulcanomobile.datamodel.LDSessionUser;
import com.streamnow.vulcanomobile.utils.Lindau;
import com.streamnow.vulcanomobile.activities.BaseActivity;

import java.util.Locale;

public class ProfileActivity extends BaseActivity {

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(R.layout.activity_profile);
        ImageView bgnd_image = (ImageView) findViewById(R.id.profile_bgnd_image);
        bgnd_image.setColorFilter(sessionUser.userInfo.partner.backgroundColorSmartphone, PorterDuff.Mode.SRC_ATOP);
        ImageView leftArrow = (ImageView) findViewById(R.id.left_arrow_profile);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View backView = findViewById(R.id.profile_view_bgnd);
        backView.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.backgroundColorSmartphone);
        TextView profile_title = (TextView) findViewById(R.id.profile_title);
        profile_title.setText(R.string.profile);
        TextView name_title = (TextView) findViewById(R.id.name_title);
        name_title.setText(R.string.profile_name);
        TextView lastname_title = (TextView) findViewById(R.id.lastname_title);
        lastname_title.setText(R.string.profile_lastname);
        TextView mobile_title = (TextView) findViewById(R.id.mobile_title);
        mobile_title.setText(R.string.profile_mobile);
        TextView email_title = (TextView) findViewById(R.id.email_title);
        email_title.setText(R.string.profile_email);
        TextView telephone_title = (TextView) findViewById(R.id.telephone_title);
        telephone_title.setText(R.string.profile_telephone);

        ImageView imageView = (ImageView) findViewById(R.id.profile_avatar);
        EditText name_data = (EditText) findViewById(R.id.name_data);
        EditText lastname_data = (EditText) findViewById(R.id.lastname_data);
        EditText mobile_data = (EditText) findViewById(R.id.mobile_data);
        EditText email_data = (EditText) findViewById(R.id.email_data);
        EditText telephone_data = (EditText) findViewById(R.id.telephone_data);
        name_data.setKeyListener(null);
        lastname_data.setKeyListener(null);
        mobile_data.setKeyListener(null);
        telephone_data.setKeyListener(null);
        email_data.setKeyListener(null);

        Picasso.with(this)
                .load(sessionUser.userInfo.image)
                .placeholder(R.drawable.contact_placeholder)
                .into(imageView);
        name_data.setText(sessionUser.userInfo.name);
        lastname_data.setText(sessionUser.userInfo.lastname);
        mobile_data.setText(sessionUser.userInfo.mobil);
        email_data.setText(sessionUser.userInfo.email);
        telephone_data.setText(sessionUser.userInfo.telephone);
    }
}
