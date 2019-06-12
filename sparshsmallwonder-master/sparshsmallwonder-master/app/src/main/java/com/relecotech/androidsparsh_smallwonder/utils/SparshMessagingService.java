package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.relecotech.androidsparsh_smallwonder.MainActivity;
import com.relecotech.androidsparsh_smallwonder.R;

/**
 * Created by Relecotech on 01-02-2018.
 **/
public class SparshMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = remoteMessage.getData().get("message");
        if (message != null) {
            sendNotification("Notification Hub Demo", message);
        }
    }

    private void sendNotification(String title, String messageBody) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
