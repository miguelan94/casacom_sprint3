package com.streamnow.lsmobile.datamodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Miguel Angel on 05/07/2016.
 */
public class LDEvents {

    public String id;
    public String create_date;
    public String date;
    public String time;
    public String title;
    public String place;
    public String description;

    public LDEvents(JSONObject o){
        try{
            if(o.getString("id")!=null) this.id = o.getString("id");
            if(o.getString("create_date")!=null) this.create_date = o.getString("create_date");
            if(o.getString("date")!=null) this.date = o.getString("date");
            if(o.getString("time")!=null) this.time = o.getString("time");
            if(o.getString("title")!=null) this.title = o.getString("title");
            if(o.getString("description")!=null) this.description = o.getString("description");
            if(o.getString("place")!=null) this.place = o.getString("place");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<LDEvents> eventsFromArray(JSONArray array){

        ArrayList<LDEvents> events = new ArrayList<>();

        for( int i = 0; i < array.length(); i++ )
        {
            JSONObject o;
            try
            {
                o = array.getJSONObject(i);
                events.add(new LDEvents(o));
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
        return events;
    }
}
