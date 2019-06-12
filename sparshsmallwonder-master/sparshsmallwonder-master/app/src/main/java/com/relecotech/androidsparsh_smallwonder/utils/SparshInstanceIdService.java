package com.relecotech.androidsparsh_smallwonder.utils;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Relecotech on 01-02-2018.
 */

public class SparshInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "SparshInstanceIdService";

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "Refreshing GCM Registration Token");

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}