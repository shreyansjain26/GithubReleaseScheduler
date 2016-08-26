package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Activities.MilestoneActivity;
import com.practo.githubreleasescheduler.Classes.Repository;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Utils.Utils;

import java.util.List;

/**
 * Created by shreyans on 22/08/16.
 */
public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView repoName;
        public TextView repoOwner;


        public ViewHolder(View itemView) {
            super(itemView);

            repoName = (TextView) itemView.findViewById(R.id.name);
            repoOwner = (TextView) itemView.findViewById(R.id.owner);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String repo = repoName.getText().toString();
            String owner = repoOwner.getText().toString();
            String id = repoName.getTag().toString();
            Intent milPage = new Intent(view.getContext(), MilestoneActivity.class);
            milPage.putExtra("repo", repo);
            milPage.putExtra("owner", owner);
            milPage.putExtra("repoId", id);
            view.getContext().startActivity(milPage);
        }
    }

    private Cursor mCursor;

    public RepoAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    public RepoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View repoView = inflater.inflate(R.layout.list_repository, parent, false);
        return new ViewHolder(repoView);
    }


    @Override
    public void onBindViewHolder(RepoAdapter.ViewHolder viewHolder, int position) {

        String id, name, owner;

        if (!Utils.isCursorEmpty(mCursor) && mCursor.moveToPosition(position)) {
            id = mCursor.getString(mCursor.getColumnIndexOrThrow(
                    RepositoryTable.COLUMN_ID));
            name = mCursor.getString(mCursor.getColumnIndexOrThrow(
                    RepositoryTable.COLUMN_NAME));
            owner = mCursor.getString(mCursor.getColumnIndexOrThrow(
                    RepositoryTable.COLUMN_OWNER));

            viewHolder.repoName.setText(name);
            viewHolder.repoName.setTag(id);
            viewHolder.repoOwner.setText(owner);
        }

    }


    @Override
    public int getItemCount() {
        if (!Utils.isCursorEmpty(mCursor)) {
            return mCursor.getCount();
        } else {
            return 0;
        }

    }

    public Cursor swapCursor(Cursor c) {
        if (this.mCursor == c) {
            return null;
        }
        Cursor oldCursor = this.mCursor;
        int count = getItemCount();
        this.mCursor = c;
        if (!Utils.isCursorEmpty(mCursor)) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, count);
        }
        return oldCursor;
    }

}
