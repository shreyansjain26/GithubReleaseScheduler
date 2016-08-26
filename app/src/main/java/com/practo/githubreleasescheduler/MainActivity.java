package com.practo.githubreleasescheduler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.practo.githubreleasescheduler.Activities.LoginActivity;
import com.practo.githubreleasescheduler.Activities.RepoActivity;
import com.practo.githubreleasescheduler.Classes.Repository;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private String oAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        SharedPreferences settings;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken",null);

        if (oAuthToken == null) {
            Intent loginPage = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(loginPage);
        }
        else {
            Intent repoPage = new Intent(mContext, RepoActivity.class);
            mContext.startActivity(repoPage);
        }
        finish();

    }
}
