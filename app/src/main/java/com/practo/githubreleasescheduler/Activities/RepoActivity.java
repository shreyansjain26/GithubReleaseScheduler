package com.practo.githubreleasescheduler.Activities;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.practo.githubreleasescheduler.Adapters.RepoAdapter;
import com.practo.githubreleasescheduler.BuildConfig;
import com.practo.githubreleasescheduler.Databases.DatabaseHelper;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.Providers.GitContentProvider;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Services.GetRepoService;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RepoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String oAuthToken;
    private RepoAdapter adapter;
    private Cursor mCursor;
    private int mId = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        setoAuthToken();

        if (oAuthToken == null) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            this.startActivity(loginPage);
        }

        Intent getDataService = new Intent(this, GetRepoService.class);
        this.startService(getDataService);

        getSupportLoaderManager().initLoader(mId, null, this);


        showList();

    }


    public void showList() {

        RecyclerView rvRepos = (RecyclerView) findViewById(R.id.rvRepos);
        adapter = new RepoAdapter(mCursor);
        rvRepos.setAdapter(adapter);
        rvRepos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setoAuthToken() {
        SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE); //1
        oAuthToken = settings.getString("authtoken", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        final SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        final String oAuthToken = settings.getString("authtoken", null);
        final String authId = settings.getString("authID", null);

        String keyString = BuildConfig.GITHUB_CLIENT_ID + ":" + BuildConfig.GITHUB_CLIENT_SECRET;

        String userPass = null;
        try {
            userPass = "Basic " + Base64.encodeToString(keyString.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest req;
        String url = "https://api.github.com/applications/" + BuildConfig.GITHUB_CLIENT_ID + "/tokens/" + oAuthToken;
        Log.d("LOGOUT_url", url);
        Log.d("userpass", userPass);
        final String finalUserPass = userPass;
        req = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                clearStoredData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                clearStoredData();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", finalUserPass);
                params.put("Accept", "application/json");
                return params;
            }
        };

        queue.add(req);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(this,
                GitContentProvider.REPO_URI,
                null,
                null,
                null,
                RepositoryTable.COLUMN_ID + " DESC"
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

    public void clearStoredData() {
        final SharedPreferences settings;
        settings = this.getSharedPreferences("AUTHTOKEN", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.clear();
        editor.apply();
        ContentResolver resolver = getApplicationContext().getContentResolver();
        resolver.delete(GitContentProvider.REPO_URI, null, null);
        resolver.delete(GitContentProvider.MILES_URI, null, null);
        resolver.delete(GitContentProvider.PR_URI, null, null);
        resolver.delete(GitContentProvider.LABELS_URI, null, null);

        Intent intent = new Intent(RepoActivity.this, LoginActivity.class);
        RepoActivity.this.startActivity(intent);
        finish();
    }

}