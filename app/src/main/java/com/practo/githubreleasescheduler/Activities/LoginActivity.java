package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        Button loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = String.valueOf(((EditText) findViewById(R.id.username)).getText());
                String password = String.valueOf(((EditText) findViewById(R.id.password)).getText());
                String keyString = username + ":" + password;
                final String encodedJava8 = Base64.encodeToString(keyString.getBytes(),Base64.DEFAULT);

                String url = "https://api.github.com/authorizations";
                final JSONObject json = new JSONObject();
                JSONArray scopes = new JSONArray();
                scopes.put("user");
                scopes.put("public_repo");

                try {
                    json.put("note","git release claendar");
                    json.put("client_id","37492183482f42649a5a");
                    json.put("client_secret","3bea637dcc835e30040c33893060bf585474ce76");
                    json.put("scopes",scopes);
                } catch (Exception e) {
                }
                RequestQueue queue = Volley.newRequestQueue(mContext);

                JsonObjectRequest req = null;
                req = new JsonObjectRequest(Request.Method.POST,url, json,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                saveAuthToken(response.getString("token"));
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
                            if(error.networkResponse.statusCode == 401 &&
                                    (error.networkResponse.headers.get("X-GitHub-OTP") != null )) {
                                Intent OTPIntent = new Intent(mContext, OTPActivity.class);
                                OTPIntent.putExtra("username",username);
                                OTPIntent.putExtra("AuthEncoded",encodedJava8);
                                OTPIntent.putExtra("authBody", json.toString());
                                OTPIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(OTPIntent);
                            }
                            else {
                                ((TextView) findViewById(R.id.here)).setText("wrong username or password");
                            }
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("Authorization", "Basic "+encodedJava8);
                            return params;
                        }
                    };

                queue.add(req);

            }
        });
    }

    private void saveAuthToken(String token){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences("AUTHTOKEN",Context.MODE_PRIVATE); //1

        editor = settings.edit(); //2
        editor.putString("authtoken", token); //3
        editor.commit(); //4


    }
}
