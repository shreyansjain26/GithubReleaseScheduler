package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.BuildConfig;
import com.practo.githubreleasescheduler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        String oAuthToken = settings.getString("authtoken", null);

        if (oAuthToken != null) {
            Intent repoPage = new Intent(this, RepoActivity.class);
            this.startActivity(repoPage);
            finish();
        }


        mLoginButton = (Button) findViewById(R.id.login);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void saveAuthToken(String token, String id, String encodedUserPass) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);

        editor = settings.edit();
        editor.putString("authtoken", token);
        editor.putString("authID", id);
        editor.putString("encodedUserPass", encodedUserPass);
        editor.apply();

    }

    private void login() {
        mLoginButton.setEnabled(false);
        final String username = String.valueOf(((EditText) findViewById(R.id.username)).getText());
        String password = String.valueOf(((EditText) findViewById(R.id.password)).getText());
        String keyString = username + ":" + password;
        final String encodedJava8 = Base64.encodeToString(keyString.getBytes(), Base64.DEFAULT);

        String url = "https://api.github.com/authorizations";
        final JSONObject json = new JSONObject();
        JSONArray scopes = new JSONArray();
        scopes.put("user");
        scopes.put("public_repo");

        try {
            json.put("note", "git release claendar");
            json.put("client_id", BuildConfig.GITHUB_CLIENT_ID);
            json.put("client_secret", BuildConfig.GITHUB_CLIENT_SECRET);
            json.put("scopes", scopes);
        } catch (Exception e) {
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req;

        req = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    saveAuthToken(response.getString("token"), Integer.toString(response.getInt("id")), "Basic " + encodedJava8);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent repoIntent = new Intent(LoginActivity.this, RepoActivity.class);
                LoginActivity.this.startActivity(repoIntent);
                finish();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoginButton.setEnabled(true);
                if (error.networkResponse.statusCode == 401 &&
                        (error.networkResponse.headers.get("X-GitHub-OTP") != null)) {

                    Intent OTPIntent = new Intent(LoginActivity.this, OTPActivity.class);

                    OTPIntent.putExtra("username", username);
                    OTPIntent.putExtra("AuthEncoded", encodedJava8);
                    OTPIntent.putExtra("authBody", json.toString());
                    LoginActivity.this.startActivity(OTPIntent);
                } else {
                    ((TextView) findViewById(R.id.here)).setText("wrong username or password");
                }

            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic " + encodedJava8);
                return params;

            }

        };

        queue.add(req);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
