package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.relecotech.androidsparsh_smallwonder.MainActivity;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.NOTIFY_CLOSE;

public class Button_listener extends BroadcastReceiver {

    private Intent intentSend;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Button_listener  ");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (intent.getAction().equals(NOTIFY_CLOSE)) {
            manager.cancel(intent.getExtras().getInt("id"));
        } else {
            intentSend = new Intent(context.getApplicationContext(), MainActivity.class);
            intentSend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intentSend.putExtra("action", intent.getAction());
            context.startActivity(intentSend);
        }


//        if (intent.getAction().equals(NOTIFY_ALERT)) {
//            Toast.makeText(context, "NOTIFY_ALERT", Toast.LENGTH_LONG).show();
//            intentSend = new Intent(context.getApplicationContext(), AlertPost.class);
////            intentSend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intentSend);
//        } else if (intent.getAction().equals(NOTIFY_ASSIGNMENT)) {
//            Toast.makeText(context, "NOTIFY_ASSIGNMENT", Toast.LENGTH_LONG).show();
//            intentSend = new Intent(context.getApplicationContext(), AssignmentPost.class);
//            context.startActivity(intentSend);
//        } else if (intent.getAction().equals(NOTIFY_ATTENDANCE)) {
//            Toast.makeText(context, "NOTIFY_ATTENDANCE", Toast.LENGTH_LONG).show();
//            intentSend = new Intent(context.getApplicationContext(), AttendanceTeacher.class);
//            context.startActivity(intentSend);
//        }
    }
}