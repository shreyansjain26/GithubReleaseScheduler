package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.practo.githubreleasescheduler.Objects.Repository;
import com.practo.githubreleasescheduler.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RepoActivity extends AppCompatActivity {

    private Context mContext;
    private String oAuthToken;
    ArrayList<Repository> repos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);
        mContext = getApplicationContext();

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken",null);
        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            loginPage.addFlags("Intent.FLAG_ACTIVITY_NEW_TASK");
            mContext.startActivity(loginPage);
        }
        RecyclerView rvRepos = (RecyclerView) findViewById(R.id.rvRepos);
        JSONArray data;
        repos = Repository.createRepositoriesList(data);



    }
}
