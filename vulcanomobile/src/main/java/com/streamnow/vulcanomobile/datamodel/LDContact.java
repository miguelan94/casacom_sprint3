package com.streamnow.vulcanomobile.datamodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * !
 * Created by Miguel Estévez on 09/2/16.
 */
public class LDContact {
    public String schedule;
    public String city;
    public String id;
    public String role;
    public String company;
    public String email;
    public String zip;
    public String telephone;
    public String fullname;
    public String address;
    public String url;

    public LDContact(JSONObject o) {
        try {
            if (!o.isNull("schedule")) this.schedule = o.getString("schedule");
            if (!o.isNull("city")) this.city = o.getString("city");
            if (!o.isNull("id")) this.id = o.getString("id");
            if (!o.isNull("role")) this.role = o.getString("role");
            if (!o.isNull("company")) this.company = o.getString("company");
            if (!o.isNull("email")) this.email = o.getString("email");
            if (!o.isNull("zip")) this.zip = o.getString("zip");
            if (!o.isNull("telephone")) this.telephone = o.getString("telephone");
            if (!o.isNull("fullname")) this.fullname = o.getString("fullname");
            if (!o.isNull("address")) this.address = o.getString("address");
            if (!o.isNull("url")) this.url = o.getString("url");
            System.out.println("ID contact: " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<LDContact> contactsFromArray(JSONArray a) {
        ArrayList<LDContact> contacts = new ArrayList<>();

        for (int i = 0; i < a.length(); i++) {
            JSONObject o;
            try {
                o = a.getJSONObject(i);
                contacts.add(new LDContact(o));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return contacts;
    }
}
