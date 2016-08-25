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

//        ContentValues value = new ContentValues();
//        value.put(RepositoryTable.COLUMN_ID,"2");
//        value.put(RepositoryTable.COLUMN_NAME,"Zoweqozo");
//        value.put(RepositoryTable.COLUMN_OWNER,"Abduqwel");
//
//        mContext.getContentResolver().insert(GitContentProvider.REPO_URI,value);
//        Cursor c = mContext.getContentResolver().query(GitContentProvider.REPO_URI,null,
//                null,null,null);
//        c.moveToFirst();
//        Log.d("Database Test",
//                c.getString(c.getColumnIndexOrThrow(RepositoryTable.COLUMN_NAME)));

        SharedPreferences settings;
        //SharedPreferences.Editor editor;
        //settings = this.getSharedPreferences("AUTHTOKEN",Context.MODE_PRIVATE); //1
        //editor = settings.edit();
        //editor.clear();
        //editor.commit();


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
