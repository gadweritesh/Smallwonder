package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.ArrayList;

/**
 * Created by Relecotech on 01-02-2018.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    private NotificationHub hub;
    private ArrayList<String> list;
    private String tagList;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String resultString = null;
        String regID = null;
        String storedToken = null;

        try {
            list = intent.getStringArrayListExtra("notificationTagList");

            tagList = list.toString().trim().replace(" ", "").replace("[", "").replace("]", "");
            System.out.println(" tagList " + tagList);
            System.out.println(" tagList.length " + tagList.length());
        } catch (Exception e) {
            System.out.println(" Exception " + e.getMessage());
        }

        try {
            String FCM_token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "FCM Registration Token: " + FCM_token);

            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if (((regID = sharedPreferences.getString("registrationID", null)) == null)) {

                NotificationHub hub = new NotificationHub(AzureConfiguration.HubName, AzureConfiguration.HubListenConnectionString, this);
                Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);

                if (tagList != null) {
                    regID = hub.register(FCM_token, tagList).getRegistrationId();
                    resultString = "New NH Registration Successfully - INSIDE if RegId : " + regID;
                    Log.d(TAG, resultString);
                } else {
                    System.out.println(" TAG LIST NULL ");
                }

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply();
            }

            // Check if the token may have been compromised and needs refreshing.
            else if ((storedToken = sharedPreferences.getString("FCMtoken", "")) != FCM_token) {
                NotificationHub hub = new NotificationHub(AzureConfiguration.HubName, AzureConfiguration.HubListenConnectionString, this);
                Log.d(TAG, "NH Registration refreshing with token : " + FCM_token);
                if (tagList != null) {
                    regID = hub.register(FCM_token, tagList).getRegistrationId();
                    resultString = "New NH Registration Successfully - INSIDE ELSE RegId : " + regID;
                    Log.d(TAG, resultString);
                } else {
                    System.out.println("ELSE  TAG LIST NULL ");
                }

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply();
            } else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
                Log.d(TAG, resultString);
            }
        } catch (Exception e) {
            Log.e(TAG, resultString = "Failed to complete registration", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
//        if (MainActivity.isVisible) {
//            MainActivity.mainActivity.ToastNotify(resultString);
//        }
    }
}