package com.practo.githubreleasescheduler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.practo.githubreleasescheduler.Activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginPage = new Intent(this, LoginActivity.class);
        this.startActivity(loginPage);
    }
}
