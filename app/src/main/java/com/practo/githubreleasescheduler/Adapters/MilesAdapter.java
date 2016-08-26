package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Activities.PrActivity;
import com.practo.githubreleasescheduler.Classes.Milestone;
import com.practo.githubreleasescheduler.Databases.MilestoneTable;
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
            String number = mileName.getTag().toString();
            //String date = mileDate.getText().toString();
            Intent prPage = new Intent(view.getContext(), PrActivity.class);
            prPage.putExtra("mile", mile);
            prPage.putExtra("repo", mRepo);
            prPage.putExtra("owner",mOwner);
            prPage.putExtra("number", number);
            prPage.putExtra("mileID", mMileId);
            view.getContext().startActivity(prPage);
        }
    }

    private Context mContext;
    private String mRepo;
    private String mOwner;
    private Cursor mCursor;
    private boolean dataValid;
    private int idColumn;
    private String mMileId;

    public MilesAdapter(Context context, String repo, String owner, Cursor cursor) {
        mContext = context;
        mRepo = repo;
        mOwner = owner;
        mCursor = cursor;
        if(cursor != null){
            dataValid = true;
            idColumn = cursor.getColumnIndex("_id");
        } else{
            dataValid = false;
            idColumn = -1;
        }
    }

    private Context getContext() {
        return mContext;
    }



    public MilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View milesView = inflater.inflate(R.layout.list_milestone, parent, false);
        return new ViewHolder(milesView);
    }

    @Override
    public void onBindViewHolder(MilesAdapter.ViewHolder viewHolder, int position) {

        String id, number, title, description, dueOn;
        int openIssue, closedIssue;
        float completion;

        if (dataValid && mCursor.moveToPosition(position)) {
                id = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_ID));
                number = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_NUMBER));
                title = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_NAME));
                description = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_DESCRIPTION));
                openIssue = Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_OPENISSUE)));
                closedIssue = Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_CLOSEDISSUE)));
                dueOn = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_DUEON));

                completion = (float) ((closedIssue * 1.0) / (openIssue + closedIssue));

                mMileId = id;
                viewHolder.mileName.setText(title);
                viewHolder.mileDate.setText(dueOn);
                viewHolder.mileName.setTag(number);

        }

    }

    @Override
    public int getItemCount() {
        if(dataValid){
            return mCursor.getCount();
        }
        else{
            return 0;
        }
    }

    public Cursor swapCursor(Cursor c) {
        if(this.mCursor == c){
            return null;
        }
        Cursor oldCursor = this.mCursor;
        int count = getItemCount();
        this.mCursor = c;
        if(c!=null){
            dataValid = true;
            idColumn = mCursor.getColumnIndex("_id");
            notifyDataSetChanged();
        } else{
            dataValid = false;
            idColumn = -1;
            notifyItemRangeRemoved(0,count);
        }
        return oldCursor;
    }

    public void changeCursor(Cursor c){
        Cursor oldCursor = swapCursor(c);
        if(oldCursor!=null){
            oldCursor.close();
        }
    }


}