package com.practo.githubreleasescheduler.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shreyans on 22/08/16.
 */
public class PullRequest {

    private String mTitle;
    private String mAssignee;
    private JSONArray mLabels;

    public PullRequest(String title, String assignee, JSONArray labels) {
        mTitle = title;
        mAssignee = assignee;
        mLabels = labels;
    }

    public String getTitle() { return mTitle; }
    public String getAssignee() { return mAssignee; }
    public JSONArray getLabels() { return mLabels; }

    public static ArrayList<PullRequest> createPrList(JSONArray data) throws JSONException {
        ArrayList<PullRequest> prs = new ArrayList<PullRequest>();
        for(int i = 0;i < data.length(); i++) {
            JSONObject issue = data.getJSONObject(i);
            if (issue.has("pull_request")) {

                String title = issue.getString("title");
                String assignee = "No Assignee";
                JSONArray labels = issue.getJSONArray("labels");
                //JSONObject asign =issue.getJSONObject("assignee");
                try {
                    assignee = issue.getJSONObject("assignee").getString("login");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (labels.length() > 0) {
                    prs.add(new PullRequest(title, assignee, labels));
                }
                else {
                    prs.add(new PullRequest(title,assignee,null));
                }

            }

        }
        return prs;
    }
}
