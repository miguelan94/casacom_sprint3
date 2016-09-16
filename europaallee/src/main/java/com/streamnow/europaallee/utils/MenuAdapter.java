package com.streamnow.europaallee.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streamnow.europaallee.R;
import com.streamnow.europaallee.datamodel.DMCategory;
import com.streamnow.europaallee.datamodel.DMDocument;
import com.streamnow.europaallee.datamodel.LDCategory;
import com.streamnow.europaallee.datamodel.LDService;
import com.streamnow.europaallee.interfaces.IMenuPrintable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 2/2/16.
 */
public class MenuAdapter extends BaseAdapter
{
    private ArrayList<? extends  IMenuPrintable> items;
    private Context context;

    public MenuAdapter(Context context, ArrayList<? extends  IMenuPrintable> items)
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
        IMenuPrintable menuPrintable = items.get(position);

        ImageView imageArrow = (ImageView)convertView.findViewById(R.id.row_arrow);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.row_icon);


        ImageView bgnd_circle = (ImageView)convertView.findViewById(R.id.bgnd_circle);
        FrameLayout layout_bgnd = (FrameLayout)convertView.findViewById(R.id.layout_bgnd);







        //imageView.setBackground();
        //imageView.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.backgroundColorIconSmartphone);
        TextView textView = (TextView) convertView.findViewById(R.id.row_text);
        textView.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        Picasso.with(context)
                .load(menuPrintable.getIconUrlString())
                .into(imageView);
        textView.setText(menuPrintable.getRowTitleText());

        createBitMap(bgnd_circle);


        if(items.get(position) instanceof LDService){
            LDService service = (LDService) items.get(position);
            if(service.id.equals("3")){
                imageArrow.setVisibility(View.VISIBLE);
            }
            else{
                imageArrow.setVisibility(View.INVISIBLE);
            }
        }
        if(items.get(position) instanceof  LDCategory){
            LDCategory category = (LDCategory)items.get(position);
            if(Lindau.getInstance().getCurrentSessionUser().getAvailableServicesForCategoryId(category.id).size()>1 || category.id.equals("20")){
                imageArrow.setVisibility(View.VISIBLE);
            }
            else{
                imageArrow.setVisibility(View.INVISIBLE);
            }

            //ArrayList<LDService> services = Lindau.getInstance().getCurrentSessionUser().getAvailableServicesForCategoryId(category.id);
            //System.out.println("position " + position + "name of category " + category.name  +" id " + category.id + "size "+  services.size());

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
        //paint.setStrokeWidth(0.5f);
        paint.setAntiAlias(true);
        bgnd.setImageBitmap(bitMap);
        canvas.drawCircle(75,75,55,paint);

        bgnd.invalidate();
    }
}
