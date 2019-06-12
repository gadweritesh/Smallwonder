package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.relecotech.androidsparsh_smallwonder.MainActivity;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.NotificationListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;


/**
 * Created by Relecotech on 01-02-2018.
 */

public class AppNotificationHandler extends NotificationsHandler {

    public static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_id_x";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    private SessionManager sessionManager;
    private int count;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;

        sessionManager = new SessionManager(context,sharedPrefValue);
        DatabaseHandler dataBaseHelper = new DatabaseHandler(context);

        createNotificationChannel();

        String title_Payload = bundle.getString("notification_title");
        String message_Payload = bundle.getString("notification_message");
        String tag_Payload = bundle.getString("tag");
        String postDate_Payload = bundle.getString("posted_date");
        String submitted_by_Payload = bundle.getString("submitted_by");
        String id_Payload = bundle.getString("assignment_id");
        String dueDate_Payload = bundle.getString("assignment_due_date");


        if (sessionManager.getBooleanSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH)) {

            if (tag_Payload.equals("Alert")) {
                System.out.println("alert_priority" + bundle.getString("alert_priority"));
                String alertPriority = bundle.getString("alert_priority");
                if (alertPriority.contains("Urgent")) {
                    System.out.println("Emergency Alert Received----------");
                    sendEmergencyNotification(title_Payload, message_Payload);
                } else {
                    sendNotification(title_Payload, message_Payload, tag_Payload);
                }
            } else if (tag_Payload.equals("Update")) {
                sendNotification(title_Payload, message_Payload, tag_Payload);
            } else if (tag_Payload.equals("Updates")) {
                sendNotification(title_Payload, message_Payload, tag_Payload);
            } else if (tag_Payload.equals("Headline")) {
                String titleEndDate = bundle.getString("titleEndDate");
                String titleActive = bundle.getString("titleActive");
                dataBaseHelper.addHeadlineToDatabase(message_Payload, titleEndDate, 1);
                sendNotification(title_Payload, message_Payload, tag_Payload);
            } else if (tag_Payload.equals("Change")) {

                Intent i = new Intent(ctx, UpdateClassService.class);
                i.putExtra("name", bundle.getString("name"));
                i.putExtra("schoolClassId", title_Payload);
                i.putExtra("class", bundle.getString("class"));
                i.putExtra("division", bundle.getString("division"));
                i.putExtra("branchId", bundle.getString("branchId"));
                ctx.startService(i);
            } else {
                sendNotification(title_Payload, message_Payload, tag_Payload);
            }


            try {
                if (!postDate_Payload.equals(null)) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date datePostBy = null;
                    try {
                        datePostBy = simpleDateFormat.parse(postDate_Payload);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                    targetDateFormat.setTimeZone(TimeZone.getDefault());
                    postDate_Payload = targetDateFormat.format(datePostBy);
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ Date time" + postDate_Payload);
                }

            } catch (Exception e) {
                System.out.println("Exception on GCM INTENT SERVICE NOTIFICATION PAYLOAD IS GIVING NULL VALUE");
                e.printStackTrace();
            }

        }else {
            System.out.println(" NOTIFICATION SWITCH OFF.");
        }

        //Storing Notification data  into SqlLite database
        NotificationListData noticeListData = new NotificationListData(id_Payload, dueDate_Payload, tag_Payload, message_Payload, postDate_Payload, submitted_by_Payload);
        dataBaseHelper.addNotificationToDatabase(noticeListData);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        System.out.println(" context.getPackageName() " + context.getPackageName() + ".USER_ACTION");
        Intent localIntent = new Intent(context.getPackageName() + ".USER_ACTION");
        localBroadcastManager.sendBroadcast(localIntent);


        try {
            count = Integer.parseInt(sessionManager.getUserDetails().get(SessionManager.KEY_NOTIFICATION_COUNT));
        } catch (Exception e) {
            System.out.println(" Count " + e.getMessage());
            count = 0;
        }
        count++;

        sessionManager.setSharedPrefItem(SessionManager.KEY_NOTIFICATION_COUNT, String.valueOf(count));
        System.out.println("sessionManager.getUserDetails().get(SessionManager.KEY_NOTIFICATION_COUNT) " + sessionManager.getUserDetails().get(SessionManager.KEY_NOTIFICATION_COUNT));

    }

    private void sendNotification(String title, String msg, String tag) {
        Intent intent;
        if (tag.equalsIgnoreCase("Update")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ctx.getPackageName() + "&hl=en"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(ctx, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        if (tag.equals("Leave") || tag.equals("Notes") || tag.equals("Achievement")) {
            title = ctx.getString(R.string.notification);
        }

        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx , CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
//                        .setContentInfo(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

        //channel ID is hardcoded. it works as there is not other channel ID
//        Intent intent = new Intent(ctx, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
//                intent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//                .setAutoCancel(true)
//                .setContentText(msg);
//        mBuilder.setContentIntent(contentIntent);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
//        notificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }


    private void sendEmergencyNotification(String title, String msg) {
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent NotifyIntent = new Intent(ctx, MainActivity.class);
        NotifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, NotifyIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        mBuilder.setSound(Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.open_your_eyes_and_see));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void setNotification(String title, String msg) {

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  ctx.getString(R.string.channel_name);
            //  String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
