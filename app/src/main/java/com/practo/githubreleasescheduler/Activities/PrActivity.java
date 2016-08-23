package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.Adapters.MilesAdapter;
import com.practo.githubreleasescheduler.Adapters.PrAdapter;
import com.practo.githubreleasescheduler.Objects.Milestone;
import com.practo.githubreleasescheduler.Objects.PullRequest;
import com.practo.githubreleasescheduler.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PrActivity extends AppCompatActivity {

    private Context mContext;
    private String oAuthToken;
    ArrayList<PullRequest> pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr);

        mContext = getApplicationContext();
        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            loginPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(loginPage);
        }

        Bundle extras = getIntent().getExtras();
        String milestone = extras.get("mile").toString();
        String repo =  extras.get("repo").toString();
        String owner = extras.get("owner").toString();
        String mileNumber = extras.get("number").toString();

        ((TextView) findViewById(R.id.milestone)).setText(milestone);
        String url = "https://api.github.com/repos/"+owner+"/"+repo+"/issues?milestone="+mileNumber;

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonArrayRequest req = null;
        req = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("hola","hola");
                    showList(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                params.put("Authorization","Bearer " + oAuthToken);
                return params;
            }
        };
        queue.add(req);



    }

    public void showList(JSONArray data) throws JSONException {
        RecyclerView rvPr = (RecyclerView) findViewById(R.id.rvPr);
        pr = PullRequest.createPrList(data);
        PrAdapter adapter = new PrAdapter(mContext, pr);
        rvPr.setAdapter(adapter);
        rvPr.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken", null);
    }
}
