package com.streamnow.lsmobile.utils;

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

import com.streamnow.lsmobile.interfaces.IMenuPrintable;
import com.streamnow.lsmobile.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 12/2/16.
 */
public class DocMenuAdapter extends BaseAdapter
{
    private ArrayList<? extends IMenuPrintable> items;
    private Context context;

    public DocMenuAdapter(Context context, ArrayList<? extends IMenuPrintable> items)
    {
        this.context = context;
        this.items = items;

        if(this.items == null)
        {
            this.items = new ArrayList<>();
        }
    }

    @Override
    public int getCount()
    {
        return this.items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if( convertView == null )
        {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.main_menu_row, parent, false);
        }

        LinearLayout row_bgnd = (LinearLayout)convertView.findViewById(R.id.row_bgnd);
        row_bgnd.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.backgroundColorSmartphone);
        IMenuPrintable dmElement = items.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.row_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.row_text);
        textView.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        textView.setText(dmElement.getRowTitleText());
        ImageView bgnd_circle = (ImageView)convertView.findViewById(R.id.bgnd_circle);
        if( dmElement.getIconUrlString() == null )
        {
            createBitMap(bgnd_circle);
            if( dmElement.getRowTitleText().equals(context.getString(R.string.general_docs)) )
            {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.general_docs_icon));
            }
            else if( dmElement.getRowTitleText().equals(context.getString(R.string.personal_docs)) )
            {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.personal_docs_icon));
            }
        }
        else
        {
            Picasso.with(context)
                    .load(dmElement.getIconUrlString())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageView);
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
        canvas.drawCircle(75,75,55,paint);

        bgnd.invalidate();
    }
}
