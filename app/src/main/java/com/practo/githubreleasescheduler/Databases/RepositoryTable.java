package com.practo.githubreleasescheduler.Databases;

import android.database.sqlite.SQLiteDatabase;


public class RepositoryTable {
    public static final String TABLE_REPOSITORIES = "repositories";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_OWNER = "owner";

    public static final String DATABASE_CREATE = "create table "+
            TABLE_REPOSITORIES +
            "(" +
            COLUMN_ID + " integer primary key, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_OWNER + " text not null " +
            ");";


    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXIST " + TABLE_REPOSITORIES);
        onCreate(database);
    }
}
