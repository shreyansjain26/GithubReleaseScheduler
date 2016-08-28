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

import com.practo.githubreleasescheduler.Activities.PrActivity;
import com.practo.githubreleasescheduler.Databases.MilestoneTable;
import com.practo.githubreleasescheduler.R;

import java.text.SimpleDateFormat;


/**
 * Created by shreyans on 22/08/16.
 */
public class MilesAdapter extends RecyclerView.Adapter<MilesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mileName;
        public TextView mileDate;
        public TextView openClose;

        public ViewHolder(View itemView) {
            super(itemView);

            mileName = (TextView) itemView.findViewById(R.id.name);
            mileDate = (TextView) itemView.findViewById(R.id.date);
            openClose = (TextView) itemView.findViewById(R.id.open_close);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String mile = mileName.getText().toString();
            String number = mileName.getTag().toString();
            String oc = openClose.getText().toString();
            String mileId = openClose.getTag().toString();
            String[] opnclose = oc.split("/");
            String dueDate = mileDate.getText().toString();
            String lastUpdate = mileDate.getTag().toString();

            Intent prPage = new Intent(view.getContext(), PrActivity.class);
            prPage.putExtra("mile", mile);
            prPage.putExtra("repo", mRepo);
            prPage.putExtra("owner", mOwner);
            prPage.putExtra("number", number);
            prPage.putExtra("mileID", mileId);
            prPage.putExtra("open", opnclose[0]);
            prPage.putExtra("closed", opnclose[1]);
            prPage.putExtra("due", dueDate);
            prPage.putExtra("lastUpdate",lastUpdate);
            view.getContext().startActivity(prPage);
        }
    }

    private Context mContext;
    private String mRepo;
    private String mOwner;
    private Cursor mCursor;
    private boolean dataValid;

    public MilesAdapter(Context context, String repo, String owner, Cursor cursor) {
        mContext = context;
        mRepo = repo;
        mOwner = owner;
        mCursor = cursor;
        if (cursor != null) {
            dataValid = true;
        } else {
            dataValid = false;
        }
    }

    public MilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View milesView = inflater.inflate(R.layout.list_milestone, parent, false);
        return new ViewHolder(milesView);
    }

    @Override
    public void onBindViewHolder(MilesAdapter.ViewHolder viewHolder, int position) {

        String id, number, title, dueOn, lastUpdate;
        String openIssue, closedIssue;

        if (dataValid && mCursor.moveToPosition(position)) {
            id = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_ID));
            number = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_NUMBER));
            title = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_NAME));
            openIssue = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_OPENISSUE));
            closedIssue = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_CLOSEDISSUE));
            dueOn = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_DUEON));
            lastUpdate = mCursor.getString(mCursor.getColumnIndexOrThrow(MilestoneTable.COLUMN_LASTUPDATE));

            viewHolder.mileName.setText(title);
            viewHolder.mileName.setTag(number);
            viewHolder.openClose.setText(openIssue + "/" + closedIssue);
            viewHolder.openClose.setTag(id);

            String dueDate = null;
            String lastUpdateFormated = null;
            if (dueOn != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat newSdf = new SimpleDateFormat("MMM dd,yyyy HH:mm a");
                String dueDateTemp = (dueOn.replace("T", " ")).replace("Z", "");
                String updateDateTemp = (lastUpdate.replace("T", " ")).replace("Z", "");
                try {
                    lastUpdateFormated = newSdf.format(sdf.parse(updateDateTemp));
                    dueDate = newSdf.format(sdf.parse(dueDateTemp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (dueDate != null) {
                viewHolder.mileDate.setText("Due by " + dueDate);
            } else {
                viewHolder.mileDate.setText("No due date");
            }
            viewHolder.mileDate.setTag("Last Updated on " + lastUpdateFormated);
        }

    }

    @Override
    public int getItemCount() {
        if (dataValid) {
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
        if (c != null) {
            dataValid = true;
            notifyDataSetChanged();
        } else {
            dataValid = false;
            notifyItemRangeRemoved(0, count);
        }
        return oldCursor;
    }

    public void changeCursor(Cursor c) {
        Cursor oldCursor = swapCursor(c);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }


}