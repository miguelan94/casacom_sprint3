package com.streamnow.sbb.datamodel;

import com.streamnow.sbb.interfaces.IMenuPrintable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 2/2/16.
 */
public class LDCategory implements IMenuPrintable
{
    public String parentId;
    public String id;
    public String active;
    public String name;
    public String image;
    public String smartImage;

    public LDCategory(JSONObject o)
    {
        try
        {
            if(!o.isNull("parent_id")) this.parentId = o.getString("parent_id");
            if(!o.isNull("id")) this.id = o.getString("id");
            if(!o.isNull("active")) this.active = o.getString("active");
            if(!o.isNull("name")) this.name = o.getString("name");
            if(!o.isNull("image")) this.image = o.getString("image");
            if(!o.isNull("smart_image")) this.smartImage = o.getString("smart_image");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<LDCategory> categoriesFromArray(JSONArray a)
    {
        ArrayList<LDCategory> categories = new ArrayList<>();

        for( int i = 0; i < a.length(); i++ )
        {
            JSONObject o;
            try
            {
                o = a.getJSONObject(i);
                categories.add(new LDCategory(o));
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
        return categories;
    }

    // IMenuPrintable methods
    @Override
    public String getIconUrlString()
    {
        String ret = "";

        if( this.smartImage != null )
        {
            ret = this.smartImage;
        }
        return ret;
    }

    @Override
    public String getRowTitleText()
    {
        String ret = "";

        if( this.name != null )
        {
            ret = this.name;
        }
        return ret;
    }
}
