package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Activities.MilestoneActivity;
import com.practo.githubreleasescheduler.Activities.RepoActivity;
import com.practo.githubreleasescheduler.Databases.RepositoryTable;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Utils.Utils;

import java.util.HashSet;
import java.util.Set;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        public TextView repoName;
        public TextView repoOwner;
        public ImageButton repoFav;

        public ViewHolder(final View itemView) {
            super(itemView);

            repoName = (TextView) itemView.findViewById(R.id.name);
            repoOwner = (TextView) itemView.findViewById(R.id.owner);
            repoFav = (ImageButton) itemView.findViewById(R.id.favourite);
            repoName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String repo = repoName.getText().toString();
            String owner = repoOwner.getText().toString();
            String id = repoName.getTag().toString();
            Intent milPage = new Intent(view.getContext(),
                    MilestoneActivity.class);
            milPage.putExtra("repo", repo);
            milPage.putExtra("owner", owner);
            milPage.putExtra("repoId", id);
            view.getContext().startActivity(milPage);
        }
    }

    private Context mContext;
    private Cursor mCursor;

    public RepoAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public RepoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View repoView = inflater.inflate(R.layout.list_repository, parent, false);
        return new ViewHolder(repoView);
    }


    @Override
    public void onBindViewHolder(final RepoAdapter.ViewHolder viewHolder, int position) {

        final String id, name, owner;

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

            SharedPreferences pref;
            pref = mContext.getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE);
            Set<String> favList = pref.getStringSet("favList", new HashSet<String>());
            if (favList.contains(id)) {
                viewHolder.repoFav.setBackgroundResource(R.drawable.star_selected);
            }

            viewHolder.repoFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref;
                    pref = mContext.getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor;
                    editor = pref.edit();
                    Set<String> favList = pref.getStringSet("favList", new HashSet<String>());
                    if (!favList.contains(id)){
                        favList.add(id);
                        viewHolder.repoFav.setBackgroundResource(R.drawable.star_selected);
                    }
                    else {
                        favList.remove(id);
                        viewHolder.repoFav.setBackgroundResource(R.drawable.star_unselected);
                    }
                    editor.putStringSet("favList",favList);
                    editor.apply();
                }
            });

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
