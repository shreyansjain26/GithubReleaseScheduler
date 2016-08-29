package com.practo.githubreleasescheduler.Databases;

import android.database.sqlite.SQLiteDatabase;


public class LabelsTable {
    public static final String TABLE_LABELS = "labels";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_PRID = "FK_pull_request_ID";

    public static final String DATABASE_CREATE = "create table "+
            TABLE_LABELS +
            "(" +
            COLUMN_NAME + " text not null, " +
            COLUMN_COLOR + " text not null, " +
            COLUMN_PRID + " text not null, " +
            "PRIMARY KEY (" + COLUMN_NAME + ", " + COLUMN_PRID +
            ") );";


    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXIST " + TABLE_LABELS);
        onCreate(database);
    }
}
