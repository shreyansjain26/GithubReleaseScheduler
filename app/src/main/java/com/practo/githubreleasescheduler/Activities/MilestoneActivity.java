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


import com.practo.githubreleasescheduler.Adapters.MilesAdapter;
import com.practo.githubreleasescheduler.Classes.Milestone;
import com.practo.githubreleasescheduler.Databases.MilestoneTable;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Services.GetMilesService;
import java.util.ArrayList;


public class MilestoneActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private String oAuthToken;
    private String repo;
    private String owner;
    private String repoId;
    private Cursor mCursor;
    private MilesAdapter adapter;
    private int mId = 124;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone);

        mContext = getApplicationContext();
        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            mContext.startActivity(loginPage);
        }
        Bundle extras = getIntent().getExtras();
        owner = extras.get("owner").toString();
        repo =  extras.get("repo").toString();
        repoId = extras.get("repoId").toString();

        Intent getDataService = new Intent(mContext, GetMilesService.class);
        getDataService.putExtra("repo", repo);
        getDataService.putExtra("owner", owner);
        getDataService.putExtra("repoId", repoId);
        mContext.startService(getDataService);

        getSupportLoaderManager().initLoader(mId,null,this);

        showList();

    }

    public void showList(){
        RecyclerView rvMiles = (RecyclerView) findViewById(R.id.rvMiles);
        //miles = Milestone.createMilestonesList(data);
        adapter = new MilesAdapter(mContext, repo, owner, mCursor);
        rvMiles.setAdapter(adapter);
        rvMiles.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = mContext.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken", null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(this,
                GitContentProvider.MILES_URI,
                null,
                MilestoneTable.COLUMN_REPOID + " = ?",
                new String[]{repoId},
                MilestoneTable.COLUMN_ID + " DESC"
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
