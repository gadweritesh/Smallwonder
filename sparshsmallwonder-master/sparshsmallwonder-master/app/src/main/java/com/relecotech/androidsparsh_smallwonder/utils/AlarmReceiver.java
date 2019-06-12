package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

/**
 * Created by Relecotech on 23-03-2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Context ctx;
    private NotificationManager mNotificationManager;
    public static int NOTIFICATION_ID = 1;
    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Bundle bundle;
    private LocalsNotification mNotificationUtils;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        System.out.println("INSIDE ALARM Manager");
        bundle = intent.getExtras();

        mNotificationUtils = new LocalsNotification(ctx);
        mNotificationUtils.sendNotification(bundle);

    }
}
