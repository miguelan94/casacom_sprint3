package com.streamnow.sentmatt.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streamnow.sentmatt.R;

import java.util.ArrayList;

/**
 * Created by Miguel Angel on 29/06/2016.
 */

public class SettingsAdapter extends BaseAdapter {

    private ArrayList<String> items;
    private Context context;

    public SettingsAdapter(Context context, ArrayList<String> items) {

        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.settings_menu_row, parent, false);
        }

        LinearLayout row_settings_bgnd = (LinearLayout) convertView.findViewById(R.id.row_settings_bgnd);
        // row_settings_bgnd.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorService);
        row_settings_bgnd.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.backgroundColorSmartphone);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.row_settings_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.row_settings_text);
        textView.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        ImageView bgnd_circle = (ImageView) convertView.findViewById(R.id.bgnd_circle_settings);
        createBitMap(bgnd_circle);


        if (position == 0) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.profile));
            textView.setText(items.get(position));
        } else if (position == 1) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.logout));
            textView.setText(items.get(position));
        } else if (position == 2) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.contacts));
            textView.setText(items.get(position));
        } else if (position == 3) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.buy));
            textView.setText(items.get(position));
        }

        return convertView;
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
        canvas.drawCircle(75, 75, 55, paint);

        bgnd.invalidate();

    }
}
