package com.practo.githubreleasescheduler.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Databases.LabelsTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;
import com.practo.githubreleasescheduler.R;

public class PrDescriptionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String oAuthToken;
    private int mId = 126;
    private String prId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr_description);


        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            this.startActivity(loginPage);
        }

        Bundle extras = getIntent().getExtras();
        prId = extras.getString("prId");
        String prTitle = extras.getString("title");
        String prAssignee = extras.getString("assignee");

        ((TextView) findViewById(R.id.pullRequest)).setText(prTitle);
        ((TextView) findViewById(R.id.assignee)).setText(prAssignee);

        getSupportLoaderManager().initLoader(mId,null,this);
    }


    private void setoAuthToken() {
        SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken", null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                GitContentProvider.LABELS_URI,
                null,
                LabelsTable.COLUMN_PRID + " = ?",
                new String[] {prId},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();
        TableLayout table = (TableLayout) findViewById(R.id.labelTable);
        while (!cursor.isAfterLast()) {
            String labelAName = cursor.getString(cursor.getColumnIndexOrThrow(LabelsTable.COLUMN_NAME));
            String labelAColor = cursor.getString(cursor.getColumnIndexOrThrow(LabelsTable.COLUMN_COLOR));
            String labelAPrId = cursor.getString(cursor.getColumnIndexOrThrow(LabelsTable.COLUMN_PRID));
            Log.d("LabelA",labelAPrId);
            Log.d(labelAName,labelAColor);

            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.rows_label, null);
            TextView labelA = (TextView) row.findViewById(R.id.labelA);
            labelA.setText(labelAName);
            labelA.setBackgroundColor(Color.parseColor("#"+labelAColor));

            cursor.moveToNext();

            if (!cursor.isAfterLast()) {
                String labelBName = cursor.getString(cursor.getColumnIndexOrThrow(LabelsTable.COLUMN_NAME));
                String labelBColor = cursor.getString(cursor.getColumnIndexOrThrow(LabelsTable.COLUMN_COLOR));
                String labelBPrId = cursor.getString(cursor.getColumnIndexOrThrow(LabelsTable.COLUMN_PRID));
                Log.d("labelB",labelBPrId);
                Log.d(labelAName, labelBColor);

                TextView labelB = (TextView) row.findViewById(R.id.labelB);
                labelB.setText(labelBName);
                labelB.setBackgroundColor(Color.parseColor("#"+labelBColor));

                cursor.moveToNext();
            }
            table.addView(row);
        }
        table.requestLayout();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}