package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.Adapters.RepoAdapter;
import com.practo.githubreleasescheduler.Classes.Repository;
import com.practo.githubreleasescheduler.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepoActivity extends AppCompatActivity {

    private Context mContext;
    private String oAuthToken;
    ArrayList<Repository> repos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);
        mContext = getApplicationContext();

        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            loginPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(loginPage);
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonArrayRequest req = null;
        String url = "https://api.github.com/user/repos";

        req = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
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

        RecyclerView rvRepos = (RecyclerView) findViewById(R.id.rvRepos);
        repos = Repository.createRepositoriesList(data);
        RepoAdapter adapter = new RepoAdapter(mContext, repos);
        rvRepos.setAdapter(adapter);
        rvRepos.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        final SharedPreferences settings;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        final String authId = settings.getString("authID", null);
        final String userPass = settings.getString("encodedUserPass", null);

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest req = null;
        String url = "https://api.github.com/authorizations/" + authId;
        Log.d("LOGOUT_url",url);
        req = new StringRequest(Request.Method.DELETE,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                final SharedPreferences.Editor editor;
                editor = settings.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

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
                params.put("Authorization",userPass);
                return params;
            }
        };

        queue.add(req);


    }
}