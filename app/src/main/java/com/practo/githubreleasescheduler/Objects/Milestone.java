package com.practo.githubreleasescheduler.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shreyans on 22/08/16.
 */
public class Milestone {
    private String mName;
    private String mDate;

    public Milestone(String name, String date) {
        mName = name;
        mDate = date;
    }

    public String getName() { return mName; }

    public String getDate() { return mDate; }

    public static ArrayList<Milestone> createMilestonesList(JSONArray data) throws JSONException {
        ArrayList<Milestone> miles = new ArrayList<Milestone>();

        for(int i = 0; i < data.length(); i++) {
            JSONObject repo = data.getJSONObject(i);
            miles.add(new Milestone(repo.getString("title"),repo.getString("due_on")));
        }
        return miles;
    }
}
