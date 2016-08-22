package com.practo.githubreleasescheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.practo.githubreleasescheduler.Activities.LoginActivity;
import com.practo.githubreleasescheduler.Activities.RepoActivity;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private String oAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();



        SharedPreferences settings;
        SharedPreferences.Editor editor;
        //settings = this.getSharedPreferences("AUTHTOKEN",Context.MODE_PRIVATE); //1
        //editor = settings.edit(); //2
        //editor.putString("authtoken", null); //3
        //editor.commit(); //4


        //SharedPreferences settings;
        //SharedPreferences.Editor editor;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken",null);

        if (oAuthToken == null) {
            Intent loginPage = new Intent(mContext, LoginActivity.class);
            loginPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(loginPage);
        }
        else {
            Intent repoPage = new Intent(mContext, RepoActivity.class);
            repoPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(repoPage);
        }

    }
}
