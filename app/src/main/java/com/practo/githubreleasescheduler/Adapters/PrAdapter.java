package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Activities.PrDescriptionActivity;
import com.practo.githubreleasescheduler.Classes.PullRequest;
import com.practo.githubreleasescheduler.Databases.PullRequestTable;
import com.practo.githubreleasescheduler.R;
import com.practo.githubreleasescheduler.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by shreyans on 22/08/16.
 */

public class PrAdapter extends RecyclerView.Adapter<PrAdapter.ViewHolder> {

    private int mExpandedPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView assignee;
        public TextView label;

        public ViewHolder(View itemView) {

            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            assignee = (TextView) itemView.findViewById(R.id.assignee);
            label = (TextView) itemView.findViewById(R.id.label);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent prdesc = new Intent(view.getContext(), PrDescriptionActivity.class);
            prdesc.putExtra("title", title.getText());
            prdesc.putExtra("prId", String.valueOf(title.getTag()));
            prdesc.putExtra("assignee", assignee.getText());
            view.getContext().startActivity(prdesc);
        }
    }


    private Cursor mCursor;


    public PrAdapter(Cursor cursor) {
        mCursor = cursor;
    }


    @Override
    public PrAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View prView = inflater.inflate(R.layout.list_pr, parent, false);
        return new ViewHolder(prView);
    }

    @Override
    public void onBindViewHolder(PrAdapter.ViewHolder holder, final int position) {

        String id, number, title, assignee, milestoneId;

        if (!Utils.isCursorEmpty(mCursor) && mCursor.moveToPosition(position)) {
            id = mCursor.getString(mCursor.getColumnIndexOrThrow(PullRequestTable.COLUMN_ID));
            number = mCursor.getString(mCursor.getColumnIndexOrThrow(PullRequestTable.COLUMN_NUMBER));
            title = mCursor.getString(mCursor.getColumnIndexOrThrow(PullRequestTable.COLUMN_NAME));
            assignee = mCursor.getString(mCursor.getColumnIndexOrThrow(PullRequestTable.COLUMN_ASSIGNEE));
            milestoneId = mCursor.getString(mCursor.getColumnIndexOrThrow(PullRequestTable.COLUMN_MILSTONEID));

            holder.title.setText(title);
            holder.title.setTag(id);
            holder.assignee.setText(assignee);
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

    public void changeCursor(Cursor c) {
        Cursor oldCursor = swapCursor(c);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }


}
