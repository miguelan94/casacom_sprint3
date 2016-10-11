package com.streamnow.flow.datamodel;

import com.streamnow.flow.lib.LDConnection;
import com.streamnow.flow.utils.Lindau;
import com.streamnow.flow.interfaces.IMenuPrintable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * !
 * Created by Miguel Estévez on 09/2/16.
 */
public class DMCategory extends DMElement implements IMenuPrintable {
    public String id;
    public String categoryName;
    public String shortDesc;
    public ArrayList<DMDocument> docs;
    public ArrayList<DMCategory> categories;

    public DMCategory(JSONObject o) {
        this.elementType = DMElementType.DMElementTypeCategory;

        try {
            if (!o.isNull("id")) this.id = o.getString("id");
            if (!o.isNull("category_name")) this.categoryName = o.getString("category_name");
            if (!o.isNull("short_desc")) this.shortDesc = o.getString("short_desc");
            if (!o.isNull("docs"))
                this.docs = DMDocument.documentsWithArray(o.getJSONArray("docs"));
            if (!o.isNull("categories"))
                this.categories = DMCategory.categoriesWithArray(o.getJSONArray("categories"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DMCategory(String name) {
        this.elementType = DMElementType.DMElementTypeCategory;
        this.categoryName = name;
    }

    public static ArrayList<DMCategory> categoriesWithArray(JSONArray array) {
        ArrayList<DMCategory> categories = new ArrayList<>();

        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new DMCategory(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public String getIconUrlString() {
        String ret;
        if (this.id == null || this.id.equals("")) {
            ret = null;
        } else {
            ret = LDConnection.getAbsoluteUrl("getImage") + "?access_token=" + Lindau.getInstance().getCurrentSessionUser().accessToken + "&item_id=" + this.id;
        }
        return ret;
    }

    @Override
    public String getRowTitleText() {
        return this.categoryName;
    }
}
