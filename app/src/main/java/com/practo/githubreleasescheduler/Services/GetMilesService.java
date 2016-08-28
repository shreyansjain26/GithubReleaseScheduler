package com.practo.githubreleasescheduler.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GetMilesService extends IntentService {

    private String mOAuthToken;

    public GetMilesService() {
        super("GetMilesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            setoAuthToken();
            Bundle extras = intent.getExtras();
            String repo = extras.getString("repo");
            String owner = extras.getString("owner");
            String repoId = extras.getString("repoId");
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/milestones";

            getMiles(url, repoId);
        }
    }

    private void getMiles(String url, final String repoId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = null;

        req = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int length = response.length();
                ContentValues[] value = new ContentValues[length];
                for (int i = 0; i < length; i++) {
                    try {
                        JSONObject mile = response.getJSONObject(i);
                        value[i] = new ContentValues();
                        value[i].put(MilestoneTable.COLUMN_ID, Integer.toString(mile.getInt("id")));
                        value[i].put(MilestoneTable.COLUMN_NUMBER, Integer.toString(mile.getInt("number")));
                        value[i].put(MilestoneTable.COLUMN_NAME, mile.getString("title"));
                        value[i].put(MilestoneTable.COLUMN_DUEON, mile.getString("due_on"));
                        value[i].put(MilestoneTable.COLUMN_DESCRIPTION, mile.getString("description"));
                        value[i].put(MilestoneTable.COLUMN_OPENISSUE, Integer.toString(mile.getInt("open_issues")));
                        value[i].put(MilestoneTable.COLUMN_CLOSEDISSUE, Integer.toString(mile.getInt("closed_issues")));
                        value[i].put(MilestoneTable.COLUMN_REPOID, repoId);

                        if (!mile.getString("due_on").equals(null)) {
                            setAlarm(mile.getInt("id"), mile.getString("title"), mile.getString("due_on"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getApplicationContext().getContentResolver().bulkInsert(GitContentProvider.MILES_URI, value);
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

    private void setAlarm(int id, String title, String date) {
        Intent intent = new Intent("ALARM");
        if (PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_NO_CREATE) != null) {
            if (!date.equals(getAlarmTimeById(id))) {
                PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
                setAlarmTimeById(id, null);
            } else {
                return;
            }
        }

        Date dueDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newSdf = new SimpleDateFormat("MMM dd,yyyy HH:mm a");
        String dueDateTemp = (date.replace("T", " ")).replace("Z", "");
        try {
            dueDate = newSdf.parse(newSdf.format(sdf.parse(dueDateTemp)));
            intent.putExtra("title", title);
            PendingIntent sender = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarm1.set(AlarmManager.RTC_WAKEUP, dueDate.getTime(), sender);
            setAlarmTimeById(id, date);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setoAuthToken() {
        SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        mOAuthToken = settings.getString("authtoken", null);
    }

    private void setAlarmTimeById(int id, String date) {

        SharedPreferences alarmLog;
        SharedPreferences.Editor editor;
        alarmLog = this.getSharedPreferences("ALARMLOG", Context.MODE_PRIVATE);
        editor = alarmLog.edit();
        editor.putString(Integer.toString(id), date);
        editor.apply();
    }

    private String getAlarmTimeById(int id) {
        SharedPreferences alarmLog;
        alarmLog = this.getSharedPreferences("ALARMLOG", Context.MODE_PRIVATE);
        return alarmLog.getString(Integer.toString(id), null);
    }

}
