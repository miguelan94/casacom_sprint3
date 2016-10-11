package com.streamnow.flow.datamodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * !
 * Created by Miguel Estévez on 2/2/16.
 */
public class LDLanguage {
    public String id;
    public String iso;
    public String name;

    public LDLanguage(JSONObject o) {
        try {
            if (!o.isNull("id")) this.id = o.getString("id");
            if (!o.isNull("iso")) this.iso = o.getString("iso");
            if (!o.isNull("name")) this.name = o.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<LDLanguage> languagesFromArray(JSONArray a) {
        ArrayList<LDLanguage> categories = new ArrayList<>();

        for (int i = 0; i < a.length(); i++) {
            JSONObject o;
            try {
                o = a.getJSONObject(i);
                categories.add(new LDLanguage(o));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }
}
