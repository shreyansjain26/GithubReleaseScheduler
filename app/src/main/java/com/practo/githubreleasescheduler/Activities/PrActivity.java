package com.practo.githubreleasescheduler.Activities;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Adapters.PrAdapter;
import com.practo.githubreleasescheduler.Databases.PullRequestTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Services.GetPrService;
import com.practo.githubreleasescheduler.Services.GetRepoService;


public class PrActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String oAuthToken;
    private int mId = 125;
    private String mMileId;
    private PrAdapter adapter;
    private String mileNumber;
    private String mOwner;
    private String mRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr);
        getSupportActionBar().setTitle("Pull Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            this.startActivity(loginPage);
            finish();
        }

        Bundle extras = getIntent().getExtras();
        String milestone = extras.get("mile").toString();
        mRepo = extras.get("repo").toString();
        mOwner = extras.get("owner").toString();
        mileNumber = extras.get("number").toString();
        mMileId = extras.getString("mileID");
        String open = extras.getString("open");
        String closed = extras.getString("closed");
        String due = extras.getString("due");
        String lastUpdate = extras.getString("lastUpdate");

        Intent getDataService = new Intent(this, GetPrService.class);
        getDataService.putExtra("owner", mOwner);
        getDataService.putExtra("repo", mRepo);
        getDataService.putExtra("mileNumber", mileNumber);
        this.startService(getDataService);

        ((TextView) findViewById(R.id.milestone)).setText(milestone);
        ((TextView) findViewById(R.id.dueDate)).setText(due);
        int completion = 0;
        int openI = Integer.parseInt(open);
        int closedI = Integer.parseInt(closed);
        if (openI + closedI != 0) {
            completion = (closedI * 100) / (openI + closedI);
        }
        ((ProgressBar) findViewById(R.id.progressBar)).setProgress(completion);
        ((TextView) findViewById(R.id.completion))
                .setText(String.valueOf(completion) + "% Complete");
        ((TextView) findViewById(R.id.open)).setText(open + " Open");
        ((TextView) findViewById(R.id.closed)).setText(closed + " Closed");
        ((TextView) findViewById(R.id.lastUpdated)).setText(lastUpdate);

        getSupportLoaderManager().initLoader(mId, null, this);

        showList();

    }

    public void showList() {
        RecyclerView rvPr = (RecyclerView) findViewById(R.id.rvPr);
        adapter = new PrAdapter(null);
        rvPr.setAdapter(adapter);
        rvPr.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        oAuthToken = settings.getString("authtoken", null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(this,
                GitContentProvider.PR_URI,
                null,
                PullRequestTable.COLUMN_MILSTONEID +
                        " = ? and " +
                        PullRequestTable.COLUMN_MILENUMBER + " = ?",
                new String[]{mMileId, mileNumber},
                PullRequestTable.COLUMN_NAME + " ASC"
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Intent getDataService = new Intent(this, GetPrService.class);
                getDataService.putExtra("owner", mOwner);
                getDataService.putExtra("repo", mRepo);
                getDataService.putExtra("mileNumber", mileNumber);
                this.startService(getDataService);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_others, menu);
        return true;
    }
}
