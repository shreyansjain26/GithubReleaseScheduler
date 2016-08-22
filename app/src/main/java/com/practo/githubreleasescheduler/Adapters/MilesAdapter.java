package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Activities.PrActivity;
import com.practo.githubreleasescheduler.Objects.Milestone;
import com.practo.githubreleasescheduler.R;

import java.util.List;

/**
 * Created by shreyans on 22/08/16.
 */
public class MilesAdapter extends RecyclerView.Adapter<MilesAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView mileName;
        public TextView mileDate;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mileName = (TextView) itemView.findViewById(R.id.name);
            mileDate = (TextView) itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String mile = mileName.getText().toString();
            String date = mileDate.getText().toString();
            Intent prPage = new Intent(view.getContext(), PrActivity.class);
            prPage.putExtra("mile", mile);
            prPage.putExtra("repo", mRepo);
            prPage.putExtra("owner",mOwner);
            view.getContext().startActivity(prPage);
        }
    }

    private List<Milestone> mMilestones;
    private Context mContext;
    private String mRepo;
    private String mOwner;

    public MilesAdapter(Context context, List<Milestone> miles, String repo, String owner) {
        mContext = context;
        mMilestones = miles;
        mRepo = repo;
        mOwner = owner;
    }

    private Context getContext() {
        return mContext;
    }



    public MilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the custom layout
        View milesView = inflater.inflate(R.layout.list_milestone, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(milesView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MilesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Milestone mile = mMilestones.get(position);

        // Set item views based on your views and data model
        TextView nameView = viewHolder.mileName;
        nameView.setText(mile.getName());
        TextView ownerView = viewHolder.mileDate;
        ownerView.setText(mile.getDate());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mMilestones.size();
    }

}