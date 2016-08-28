package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTPActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mContext = getApplicationContext();
        final String url = "https://api.github.com/authorizations";
        Bundle extras = getIntent().getExtras();
        JSONObject authBody = null;
        String username;
        String authEncoded = null;
        if (extras !=null) {
            username = extras.getString("username");
            authEncoded = extras.getString("AuthEncoded");
            try {
                authBody = new JSONObject(extras.getString("authBody"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final String finalAuthEncoded = authEncoded;
        final JSONObject finalAuthBody = authBody;

        Button loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String otp = String.valueOf(((TextView) findViewById(R.id.otp)).getText());
                RequestQueue queue = Volley.newRequestQueue(mContext);
                JsonObjectRequest req = null;

                req = new JsonObjectRequest(Request.Method.POST, url, finalAuthBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            saveAuthToken(response.getString("token"),
                                    Integer.toString(response.getInt("id")),"Basic " + finalAuthEncoded );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent repoIntent = new Intent(mContext,RepoActivity.class);
                        repoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(repoIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((TextView) findViewById(R.id.here)).setText("wrong OTP");
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "Basic " + finalAuthEncoded);
                        params.put("X-GitHub-OTP", otp);
                        return params;
                    }
                };

                queue.add(req);

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
}
