package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.relecotech.androidsparsh_smallwonder.MainActivity;
import com.relecotech.androidsparsh_smallwonder.R;

/**
 * Created by Amey on 24-04-2018.
 */

public class LocalsNotification extends ContextWrapper {
    private static final CharSequence ANDROID_CHANNEL_NAME = "Sparsh_Notification";
    private final String ANDROID_CHANNEL_ID = getApplicationContext().getPackageName();
    public static int NOTIFICATION_ID = 1;
    private NotificationManager mManager;
    private String notification_description, notification_Title;

    public LocalsNotification(Context base) {
        super(base);
    }

    public void sendNotification(Bundle bundle) {

        String getNotificationTag = bundle.getString("Notification_Tag");

        switch (getNotificationTag) {
            case "Calendar":
                System.out.println("Calendar Notification Tag Recive");
                bundle.getString("Notification_Title");
                bundle.getString("Notification_Description");
                popUpNotification(bundle.getString("Notification_Title"), bundle.getString("Notification_Description"));
                break;
            case "Assignment":
                System.out.println("Assignment Notification Tag Recive");
//                popUpNotification(notification_Title, notification_description);
                popUpNotification("Assignment", notification_description);
                break;
            case "Birthday":
                System.out.println("Birthday Notification Tag Recive");
                popUpNotification(bundle.getString("Notification_Title"), bundle.getString("Notification_Description"));
//                popUpNotification(notification_Title, notification_description);
                break;
        }

    }

    private void popUpNotification(String notification_Title, String notification_description) {

        System.out.println("notification_Title----------------   " + notification_Title);
        System.out.println("notification_description----------------   " + notification_description);

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent NotifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, NotifyIntent, 0);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.RED);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);


            Notification.Builder mNotificationBuilder = new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                    .setContentTitle(notification_Title)
                    .setContentText(notification_description)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
            mNotificationBuilder.setContentIntent(contentIntent);
            mManager.notify(NOTIFICATION_ID++, mNotificationBuilder.build());

        } else {
            NotificationCompat.Builder mNotificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(notification_Title)
                            .setContentText(notification_description)
                            .setAutoCancel(true);

            mNotificationBuilder.setContentIntent(contentIntent);
            mManager.notify(NOTIFICATION_ID++, mNotificationBuilder.build());
        }
    }

}
