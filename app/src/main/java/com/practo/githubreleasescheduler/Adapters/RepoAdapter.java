package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Objects.Repository;
import com.practo.githubreleasescheduler.R;

import java.util.List;

/**
 * Created by shreyans on 22/08/16.
 */
public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView repoName;
        public TextView repoOwner;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            repoName = (TextView) itemView.findViewById(R.id.name);
            repoOwner = (TextView) itemView.findViewById(R.id.owner);
        }
    }

    private List<Repository> mRepositories;
    private Context mContext;

    public RepoAdapter(Context context, List<Repository> repos) {
        mContext = context;
        mRepositories = repos;
    }

    private Context getContext() {
        return mContext;
    }



    public RepoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_repository, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RepoAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Repository repo = mRepositories.get(position);

        // Set item views based on your views and data model
        TextView nameView = viewHolder.repoName;
        nameView.setText(repo.getName());
        TextView ownerView = viewHolder.repoOwner;
        ownerView.setText(repo.getOwner());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mRepositories.size();
    }



}
