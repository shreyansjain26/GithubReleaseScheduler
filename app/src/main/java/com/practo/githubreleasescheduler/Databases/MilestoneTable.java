package com.practo.githubreleasescheduler.Databases;

import android.database.sqlite.SQLiteDatabase;


public class MilestoneTable {
    public static final String TABLE_MILESTONES = "milestones";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_OPENISSUE = "open_issue";
    public static final String COLUMN_CLOSEDISSUE = "closed_issue";
    public static final String COLUMN_DUEON = "due_on";
    public static final String COLUMN_REPOID = "FK_repoID";
    public static final String COLUMN_LASTUPDATE = "last_update";

    public static final String DATABASE_CREATE = "create table " +
            TABLE_MILESTONES +
            "(" +
            COLUMN_ID + " integer primary key, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_NUMBER + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " +
            COLUMN_OPENISSUE + " text not null, " +
            COLUMN_CLOSEDISSUE + " text not null, " +
            COLUMN_DUEON + " text not null, " +
            COLUMN_LASTUPDATE + " text not null, " +
            COLUMN_REPOID + " text not null " +
            ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXIST " + TABLE_MILESTONES);
        onCreate(database);
    }
}
