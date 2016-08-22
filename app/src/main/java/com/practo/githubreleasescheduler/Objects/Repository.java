package com.practo.githubreleasescheduler.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shreyans on 22/08/16.
 */
public class Repository {

    private String mName;
    private String mOwner;

    public Repository(String name, String owner) {
        mName = name;
        mOwner = owner;
    }

    public String getName() { return mName; }

    public String getOwner() { return mOwner; }

    public static ArrayList<Repository> createRepositoriesList(JSONArray data) throws JSONException {
        ArrayList<Repository> repos = new ArrayList<Repository>();

        for(int i = 0; i < data.length(); i++) {
            JSONObject repo = data.get(i);
            repos.add(new Repository(repo.getString("name"),(repo.getJSONObject("owner")).getString("login")));
        }
        return repos;
    }

}
