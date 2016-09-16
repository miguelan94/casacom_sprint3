package com.streamnow.europaallee.datamodel;

import com.streamnow.europaallee.interfaces.IMenuPrintable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 2/2/16.
 */
public class LDService implements IMenuPrintable
{
    public String apiUrl;
    public String active;
    public String params;
    public String adminUrl;
    public String image;
    public String updatedAt;
    public String userId;
    public String managername;
    public String currency;
    public boolean usable;
    public String vat;
    public String admin;
    public String name;
    public String availableInApp;
    public String type;
    public String docUrl;
    public String id;
    public String adminPassword;
    public String secretId;
    public String smartImage;
    public String internalUse;
    public String createdAt;
    public int categoryId;
    public String deviceType;
    public String description;
    public String webviewUrl;

    public LDService(JSONObject o)
    {
        try
        {
            if(!o.isNull("api_url")) this.apiUrl = o.getString("api_url");
            if(!o.isNull("active")) this.active = o.getString("active");
            if(!o.isNull("params")) this.params = o.getString("params");
            if(!o.isNull("admin_url")) this.adminUrl = o.getString("admin_url");
            if(!o.isNull("image")) this.image = o.getString("image");
            if(!o.isNull("updated_at")) this.updatedAt = o.getString("updated_at");
            if(!o.isNull("user_id")) this.userId = o.getString("user_id");
            if(!o.isNull("managername")) this.managername = o.getString("managername");
            if(!o.isNull("currency")) this.currency = o.getString("currency");
            if(!o.isNull("usable")) this.usable = o.getBoolean("usable");
            if(!o.isNull("vat")) this.vat = o.getString("vat");
            if(!o.isNull("admin")) this.admin = o.getString("admin");
            if(!o.isNull("name")) this.name = o.getString("name");
            if(!o.isNull("available_in_app")) this.availableInApp = o.getString("available_in_app");
            if(!o.isNull("type")) this.type = o.getString("type");
            if(!o.isNull("doc_url")) this.docUrl = o.getString("doc_url");
            if(!o.isNull("id")) this.id = o.getString("id");
            if(!o.isNull("admin_password")) this.adminPassword = o.getString("admin_password");
            if(!o.isNull("secret_id")) this.secretId = o.getString("secret_id");
            if(!o.isNull("smart_image")) this.smartImage = o.getString("smart_image");
            if(!o.isNull("internal_use")) this.internalUse = o.getString("internal_use");
            if(!o.isNull("created_at")) this.createdAt = o.getString("created_at");
            if(!o.isNull("category_id")) this.categoryId = o.getInt("category_id");
            if(!o.isNull("device_type")) this.deviceType = o.getString("device_type");
            if(!o.isNull("description")) this.description = o.getString("description");
            if(!o.isNull("webview_url")) this.webviewUrl = o.getString("webview_url");


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<LDService> servicesFromArray(JSONArray a)
    {
        ArrayList<LDService> categories = new ArrayList<>();

        for( int i = 0; i < a.length(); i++ )
        {
            JSONObject o;
            try
            {
                o = a.getJSONObject(i);
                categories.add(new LDService(o));
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
        return categories;
    }

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
