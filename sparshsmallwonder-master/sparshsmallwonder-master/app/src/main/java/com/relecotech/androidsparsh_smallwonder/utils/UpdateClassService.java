package com.relecotech.androidsparsh_smallwonder.utils;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.relecotech.androidsparsh_smallwonder.MainActivity;

public class UpdateClassService extends IntentService {

    public UpdateClassService() {
        super("update-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String fullName = intent.getStringExtra("name");
        String newClassId = intent.getStringExtra("schoolClassId");
        String schoolClass = intent.getStringExtra("class");
        String division = intent.getStringExtra("division");
        String branchId = intent.getStringExtra("branchId");

        SessionManager sessionManager = new SessionManager(getApplicationContext(), fullName);
        System.out.println(" fullName " + fullName);
        System.out.println(" newClassId " + newClassId);
        System.out.println(" schoolClass " + schoolClass + " division " + division);
        System.out.println(" branchId " + branchId);
        sessionManager.updateLoginSession(fullName, newClassId, schoolClass, division, branchId);
//        Toast.makeText(getApplicationContext(), "UpdateClassService  newClassId " + newClassId, Toast.LENGTH_LONG).show();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }
}
