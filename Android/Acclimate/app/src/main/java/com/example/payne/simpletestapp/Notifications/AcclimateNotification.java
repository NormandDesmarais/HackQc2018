package com.example.payne.simpletestapp.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.payne.simpletestapp.MainActivities.MainActivity;
import com.example.payne.simpletestapp.R;

public abstract class AcclimateNotification {

    private Context context;
    protected NotificationCompat.Builder builder;

    public AcclimateNotification(Context context) {

        this.context = context;

        this.builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }


    public void sendNotification() {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void setContentText(String text){
        builder.setContentText(text);
    }

    public void setTitleText(String text){
        builder.setContentTitle(text);
    }

}
