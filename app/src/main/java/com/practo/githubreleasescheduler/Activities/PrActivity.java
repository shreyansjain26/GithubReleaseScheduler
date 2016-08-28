package com.practo.githubreleasescheduler.Activities;

import android.os.StrictMode;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Adapters.PrAdapter;
import com.practo.githubreleasescheduler.Classes.PullRequest;
import com.practo.githubreleasescheduler.Databases.PullRequestTable;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Services.GetPrService;


public class PrActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private String oAuthToken;
    private Cursor mCursor;
    private int mId = 125;
    private String mMileId;
    private PrAdapter adapter;
    private String mileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr);

        mContext = getApplicationContext();
        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            mContext.startActivity(loginPage);
        }

        Bundle extras = getIntent().getExtras();
        String milestone = extras.get("mile").toString();
        String repo = extras.get("repo").toString();
        String owner = extras.get("owner").toString();
        mileNumber = extras.get("number").toString();
        mMileId = extras.getString("mileID");
        String open = extras.getString("open");
        String closed = extras.getString("closed");
        String due = extras.getString("due");

        Intent getDataService = new Intent(mContext, GetPrService.class);
        getDataService.putExtra("owner", owner);
        getDataService.putExtra("repo", repo);
        getDataService.putExtra("mileNumber", mileNumber);
        mContext.startService(getDataService);

        ((TextView) findViewById(R.id.milestone)).setText(milestone);
        ((TextView) findViewById(R.id.dueDate)).setText(due);
        int completion = 0;
        int openI = Integer.parseInt(open);
        int closedI = Integer.parseInt(closed);
        if (openI + closedI != 0) {
            completion = (closedI * 100) / (openI + closedI);
        }
        ((ProgressBar) findViewById(R.id.progressBar)).setProgress(completion);
        ((TextView) findViewById(R.id.completion)).setText(String.valueOf(completion) + "% Complete");
        ((TextView) findViewById(R.id.open)).setText(open + " Open");
        ((TextView) findViewById(R.id.closed)).setText(closed + " Closed");
        ((TextView) findViewById(R.id.lastUpdated)).setText("Last updated 11 days ago");

        getSupportLoaderManager().initLoader(mId, null, this);

        showList();

    }

    public void showList() {
        RecyclerView rvPr = (RecyclerView) findViewById(R.id.rvPr);
        adapter = new PrAdapter(mContext, mCursor);
        rvPr.setAdapter(adapter);
        rvPr.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        oAuthToken = settings.getString("authtoken", null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(this,
                GitContentProvider.PR_URI,
                null,
                PullRequestTable.COLUMN_MILSTONEID + " = ? and " + PullRequestTable.COLUMN_MILENUMBER + " = ?",
                new String[]{mMileId, mileNumber},
                PullRequestTable.COLUMN_ID + " DESC"
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }
}
