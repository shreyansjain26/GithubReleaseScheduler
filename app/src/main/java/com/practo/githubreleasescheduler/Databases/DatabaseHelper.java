package com.practo.githubreleasescheduler.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shreyans on 24/08/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABSE_NAME = "gitMiles.db";
    public static final int DATABSE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RepositoryTable.onCreate(db);
        MilestoneTable.onCreate(db);
        PullRequestTable.onCreate(db);
        LabelsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RepositoryTable.onUpgrade(db, oldVersion, newVersion);
        MilestoneTable.onUpgrade(db,oldVersion,newVersion);
        PullRequestTable.onUpgrade(db,oldVersion,newVersion);
        LabelsTable.onUpgrade(db,oldVersion,newVersion);
    }
}
