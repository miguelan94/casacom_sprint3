package com.streamnow.europaallee.datamodel;

import org.json.JSONObject;

/** !
 * Created by Miguel Estévez on 2/2/16.
 */
public class LDUserInfo
{
    public String id;
    public String mobil;
    public String status;
    public String lastname;
    public LDPartner partner;
    public String fax;
    public String image;
    public String demo;
    public String defaultAddressId;
    public String username;
    public String paymentAllowed;
    public String language;
    public String telephone;
    public String email;
    public String name;
    public String gender;

    public LDUserInfo(JSONObject o)
    {
        try
        {
            if(!o.isNull("id")) this.id = o.getString("id");
            if(!o.isNull("mobil")) this.mobil = o.getString("mobil");
            if(!o.isNull("status")) this.status = o.getString("status");
            if(!o.isNull("lastname")) this.lastname = o.getString("lastname");
            if(!o.isNull("partner")) this.partner = new LDPartner(o.getJSONObject("partner"));
            if(!o.isNull("fax")) this.fax = o.getString("fax");
            if(!o.isNull("image")) this.image = o.getString("image");
            if(!o.isNull("demo")) this.demo = o.getString("demo");
            if(!o.isNull("default_address_id")) this.defaultAddressId = o.getString("default_address_id");
            if(!o.isNull("username")) this.username = o.getString("username");
            if(!o.isNull("payment_allowed")) this.paymentAllowed = o.getString("payment_allowed");
            if(!o.isNull("language")) this.language = o.getString("language");
            if(!o.isNull("telephone")) this.telephone = o.getString("telephone");
            if(!o.isNull("email")) this.email = o.getString("email");
            if(!o.isNull("name")) this.name = o.getString("name");
            if(!o.isNull("gender")) this.gender = o.getString("gender");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
