package com.practo.githubreleasescheduler.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practo.githubreleasescheduler.Classes.PullRequest;
import com.practo.githubreleasescheduler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by shreyans on 22/08/16.
 */
public class PrAdapter extends RecyclerView.Adapter<PrAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView assignee;
        public TextView label;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            assignee = (TextView) itemView.findViewById(R.id.assignee);
            label = (TextView) itemView.findViewById(R.id.label);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }


    private Context mContext;
    private List<PullRequest> mPr;

    public PrAdapter(Context context, List<PullRequest> pr) {
        mContext = context;
        mPr = pr;
    }

    private Context getContext() {
        return mContext;
    }



    @Override
    public PrAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the custom layout
        View prView = inflater.inflate(R.layout.list_pr, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(prView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PrAdapter.ViewHolder holder, int position) {
        PullRequest pr = mPr.get(position);
        TextView title = holder.title;
        TextView assignee = holder.assignee;
        TextView label = holder.label;

        title.setText(pr.getTitle());
        assignee.setText(pr.getAssignee());
        JSONArray labels = pr.getLabels();
        if (labels != null) {
            try {
                JSONObject lbl = labels.getJSONObject(0);
                label.setText(lbl.getString("name"));
                label.setBackgroundColor(Integer.parseInt(lbl.getString("color"), 16));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPr.size();
    }


}
