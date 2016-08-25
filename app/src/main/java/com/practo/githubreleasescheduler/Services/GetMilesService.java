package com.practo.githubreleasescheduler.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.Classes.Milestone;
import com.practo.githubreleasescheduler.Databases.MilestoneTable;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GetMilesService extends IntentService {

    private String mOAuthToken;

    public GetMilesService()
    {
        super("GetMilesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            setoAuthToken();
            Bundle extras = intent.getExtras();
            String repo = extras.getString("repo");
            String owner = extras.getString("owner");
            String url = "https://api.github.com/repos/"+owner+"/"+repo+"/milestones";

            getMiles(url);
        }
    }

    private void getMiles(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = null;

        req = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int length = response.length();
                ContentValues[] value = new ContentValues[length];
                for(int i = 0; i < length; i++) {
                    try {
                        JSONObject mile = response.getJSONObject(i);
                        value[i] = new ContentValues();
                        value[i].put(MilestoneTable.COLUMN_ID, Integer.toString(mile.getInt("id")));
                        value[i].put(MilestoneTable.COLUMN_NUMBER,Integer.toString(mile.getInt("number")));
                        value[i].put(MilestoneTable.COLUMN_NAME,mile.getString("title"));
                        value[i].put(MilestoneTable.COLUMN_DUEON,mile.getString("due_on"));
                        value[i].put(MilestoneTable.COLUMN_DESCRIPTION,mile.getString("description"));
                        value[i].put(MilestoneTable.COLUMN_OPENISSUE,Integer.toString(mile.getInt("open_issues")));
                        value[i].put(MilestoneTable.COLUMN_CLOSEDISSUE,Integer.toString(mile.getInt("closed_issues")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getApplicationContext().getContentResolver().bulkInsert(GitContentProvider.MILES_URI,value);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization","Bearer " + mOAuthToken);
                return params;
            }
        };

        queue.add(req);
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        mOAuthToken = settings.getString("authtoken", null);
    }
}
