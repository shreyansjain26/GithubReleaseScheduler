package com.practo.githubreleasescheduler.Recievers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.practo.githubreleasescheduler.R;

public class MilestoneAlarmReceiver extends BroadcastReceiver {
    public MilestoneAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String title = extras.getString("title");
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(context);
        mBuilder.setSmallIcon(R.drawable.github_img);
        mBuilder.setContentTitle(title + " is due");
        mBuilder.setContentText("Milestone " + title +
                " is due. Please check if all issues are closed.");

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }
}
