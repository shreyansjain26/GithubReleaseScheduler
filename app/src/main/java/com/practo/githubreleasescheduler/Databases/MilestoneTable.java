package com.practo.githubreleasescheduler.Databases;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by shreyans on 24/08/16.
 */
public class MilestoneTable {
    //Database JOB table
    public static final String TABLE_MILESTONES = "milestones";

    //Columns for JOB table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_OPENISSUE = "open_issue";
    public static final String COLUMN_CLOSEDISSUE = "closed_issue";
    public static final String COLUMN_DUEON = "due_on";

    //Database Creation statement
    public static final String DATABASE_CREATE = "create table "+
            TABLE_MILESTONES +
            "(" +
            COLUMN_ID + " integer primary key, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_NUMBER + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " +
            COLUMN_OPENISSUE + " text not null, " +
            COLUMN_CLOSEDISSUE + " text not null, " +
            COLUMN_DUEON + " text not null " +
            ");";


    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database,
                                 int olderVersion, int newVersion){
        database.execSQL("DROP TABLE IF EXIST " + TABLE_MILESTONES);
        onCreate(database);
    }
}
