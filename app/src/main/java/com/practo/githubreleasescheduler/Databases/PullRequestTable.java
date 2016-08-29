package com.practo.githubreleasescheduler.Databases;

import android.database.sqlite.SQLiteDatabase;


public class PullRequestTable {
    public static final String TABLE_PULLREQUEST = "pull_requests";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_ASSIGNEE = "assignee";
    public static final String COLUMN_MILSTONEID = "FK_milestoneID";
    public static final String COLUMN_MILENUMBER = "FK_milestoneNumber";

    public static final String DATABASE_CREATE = "create table "+
            TABLE_PULLREQUEST +
            "(" +
            COLUMN_ID + " integer primary key, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_NUMBER + " text not null, " +
            COLUMN_ASSIGNEE + " text not null, " +
            COLUMN_MILSTONEID + " text not null, " +
            COLUMN_MILENUMBER + " text not null " +
            ");";


    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXIST " + TABLE_PULLREQUEST);
        onCreate(database);
    }
}
