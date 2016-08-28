package com.practo.githubreleasescheduler.Providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.practo.githubreleasescheduler.Classes.Milestone;
import com.practo.githubreleasescheduler.Databases.DatabaseHelper;
import com.practo.githubreleasescheduler.Databases.LabelsTable;
import com.practo.githubreleasescheduler.Databases.MilestoneTable;
import com.practo.githubreleasescheduler.Databases.PullRequestTable;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;

public class GitContentProvider extends ContentProvider {

    public static final int REPO = 100;
    public static final int MILES = 200;
    public static final int PR = 300;
    public static final int LABELS = 400;

    private DatabaseHelper databaseHelper;

    public static final String authorities = "com.practo.grs.provider";

    private static final String REPO_PATH = "repositories";
    private static final String MILES_PATH = "milestones";
    private static final String PR_PATH = "pull_requests";
    private static final String LABELS_PATH = "labels";

    private static final UriMatcher mUriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);



    static{
        mUriMatcher.addURI(authorities,REPO_PATH,REPO);
        mUriMatcher.addURI(authorities,MILES_PATH,MILES);
        mUriMatcher.addURI(authorities,PR_PATH,PR);
        mUriMatcher.addURI(authorities,LABELS_PATH,LABELS);
    }



    public static final Uri REPO_URI = Uri.parse("content://"+
            authorities + "/" + REPO_PATH);
    public static final Uri MILES_URI = Uri.parse("content://"+
            authorities + "/" + MILES_PATH);
    public static final Uri PR_URI = Uri.parse("content://"+
            authorities + "/" + PR_PATH);
    public static final Uri LABELS_URI = Uri.parse("content://"+
            authorities + "/" + LABELS_PATH);


    public GitContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table =selectTable(uri);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowDeleted =  db.delete(table,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);

        return rowDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = selectTable(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long id = db.insert(table,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        if (table == RepositoryTable.TABLE_REPOSITORIES) {
            return Uri.parse(REPO_PATH + "/" + id);
        }
        else if (table == MilestoneTable.TABLE_MILESTONES) {
            return Uri.parse(MILES_PATH + "/" + id);
        }
        else if (table == PullRequestTable.TABLE_PULLREQUEST) {
            return Uri.parse(PR_PATH + "/" + id);
        }
        else {
            return Uri.parse(LABELS_PATH + "/" + id);
        }

    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = selectTable(uri);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(table);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor  = builder.query(db,projection,selection,selectionArgs,
                null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String table = selectTable(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsUpdated = db.update(table,values,selection,
                selectionArgs);

        getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }

    public String selectTable(Uri uri) {
        int uriType = mUriMatcher.match(uri);
        String table;
        if (uriType == REPO) {
            table = RepositoryTable.TABLE_REPOSITORIES;
        }
        else if (uriType == MILES) {
            table = MilestoneTable.TABLE_MILESTONES;
        }
        else if (uriType == PR) {
            table = PullRequestTable.TABLE_PULLREQUEST;
        }
        else if (uriType == LABELS) {
            table = LabelsTable.TABLE_LABELS;
        }
        else {
            throw new IllegalStateException("Unkown URI");
        }
        return table;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        String table = selectTable(uri);
        int numberOfInserts = 0;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(ContentValues value : values){
                long id = db.insertWithOnConflict(table,null,
                        value,SQLiteDatabase.CONFLICT_REPLACE);
                if(id>0){
                    numberOfInserts++;
                }

            }
            db.setTransactionSuccessful();

        } catch(Exception e){
            Log.e("Provider", "Error Happened during bulkInsert");
            e.printStackTrace();
        } finally{
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return numberOfInserts;
    }

}
