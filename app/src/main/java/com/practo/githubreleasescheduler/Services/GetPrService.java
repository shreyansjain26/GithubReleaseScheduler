package com.practo.githubreleasescheduler.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.Databases.LabelsTable;
import com.practo.githubreleasescheduler.Databases.PullRequestTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetPrService extends IntentService {

    private String mOAuthToken;
    private String url;

    public GetPrService() {
        super("GetPrService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            setoAuthToken();
            Bundle extras = intent.getExtras();
            String owner = extras.getString("owner");
            String repo = extras.getString("repo");
            String mileNumber = extras.getString("mileNumber");
            url = "https://api.github.com/repos/" + owner + "/" + repo +
                    "/issues?milestone=" + mileNumber + "&per_page=100&page=";
            getPrs(mileNumber, 1);
        }
    }

    private void getPrs(final String mileNumber, final int page) {

        String pageUrl = url + Integer.toString(page);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req;

        req = new JsonArrayRequest(Request.Method.GET, pageUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int length = response.length();
                        ContentValues[] value = new ContentValues[length];
                        for (int i = 0; i < length; i++) {
                            try {
                                JSONObject pr = response.getJSONObject(i);
                                value[i] = new ContentValues();
                                value[i].put(PullRequestTable.COLUMN_ID,
                                        Integer.toString(pr.getInt("id")));

                                value[i].put(PullRequestTable.COLUMN_NUMBER,
                                        Integer.toString(pr.getInt("number")));

                                value[i].put(PullRequestTable.COLUMN_NAME,
                                        pr.getString("title"));

                                if ((pr.get("assignee")).equals(null)) {
                                    value[i].put(PullRequestTable.COLUMN_ASSIGNEE,
                                            "No Assignee");
                                } else {
                                    value[i].put(PullRequestTable.COLUMN_ASSIGNEE,
                                            (pr.getJSONObject("assignee")).
                                                    getString("login"));
                                }

                                value[i].put(PullRequestTable.COLUMN_MILSTONEID,
                                        Integer.toString((pr
                                                .getJSONObject("milestone"))
                                                .getInt("id")));

                                value[i].put(PullRequestTable.COLUMN_MILENUMBER,
                                        mileNumber);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        getApplicationContext().getContentResolver().
                                bulkInsert(GitContentProvider.PR_URI, value);

                        try {
                            getApplicationContext().getContentResolver().
                                    bulkInsert(GitContentProvider.LABELS_URI,
                                            getLabels(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (length != 0 && length == 100) {
                            getPrs(mileNumber, page + 1);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + mOAuthToken);
                return params;
            }
        };

        queue.add(req);
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        mOAuthToken = settings.getString("authtoken", null);
    }

    public ContentValues[] getLabels(JSONArray response) throws JSONException {
        List<ContentValues> value = new ArrayList<ContentValues>();

        for (int i = 0; i < response.length(); i++) {
            JSONArray labels = (response.getJSONObject(i)).
                    getJSONArray("labels");

            for (int j = 0; j < labels.length(); j++) {
                JSONObject label = labels.getJSONObject(j);
                ContentValues lbl = new ContentValues();

                lbl.put(LabelsTable.COLUMN_NAME, label.getString("name"));

                lbl.put(LabelsTable.COLUMN_COLOR, label.getString("color"));

                lbl.put(LabelsTable.COLUMN_PRID, Integer.toString(
                        response.getJSONObject(i).getInt("id")));

                value.add(lbl);
            }
        }

        return value.toArray(new ContentValues[value.size()]);
    }
}
