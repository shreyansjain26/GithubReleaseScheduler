package com.practo.githubreleasescheduler.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.Classes.Repository;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GetRepoService extends IntentService {

    private String mOAuthToken;


    public GetRepoService() { super("GetRepoService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            setoAuthToken();

            String url = "https://api.github.com/user/repos";

            getRepo(url);
        }
    }
    private void getRepo(String url) {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = null;

        req = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int length = response.length();
                ContentValues[] value = new ContentValues[length];
                for(int i = 0; i < length; i++) {
                    try {
                        JSONObject repo = response.getJSONObject(i);
                        value[i] = new ContentValues();
                        value[i].put(RepositoryTable.COLUMN_ID, Integer.toString(repo.getInt("id")));
                        value[i].put(RepositoryTable.COLUMN_NAME, repo.getString("name"));
                        value[i].put(RepositoryTable.COLUMN_OWNER, (repo.getJSONObject("owner")).getString("login"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                getApplicationContext().getContentResolver().bulkInsert(GitContentProvider.REPO_URI,value);
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
