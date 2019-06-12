package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class SettingActivity extends AppCompatActivity {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String appVersion;
    private TextView setting_RemainderTextView;
    private SimpleDateFormat displayFormat;
    private SimpleDateFormat parseFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(SettingActivity.this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setting_RemainderTextView = (TextView) findViewById(R.id.textViewAssignmentTime);
        TextView setting_FeedbackTextView = (TextView) findViewById(R.id.textfeedback);
        TextView setting_AboutTextView = (TextView) findViewById(R.id.aboutTextView);
        Switch notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);

        if (sessionManager.getBooleanSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH)) {
            notificationSwitch.setChecked(true);
        } else {
            notificationSwitch.setChecked(false);
        }

        displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        parseFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        if (!sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER).isEmpty()) {
            String time = sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER);
            setTimeMethod(time);
        }

        setting_RemainderTextView.setOnClickListener(new View.OnClickListener() {
            public String AM_PM;
            public int mMinute;
            public int mHour;

            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 12 && hourOfDay >= 0) {
                            AM_PM = "AM";
                        } else {
                            hourOfDay -= 12;
                            if (hourOfDay == 0) {
                                hourOfDay = 12;
                            }
                            AM_PM = "PM";
                        }

                        Date date = null;
                        try {
                            date = parseFormat.parse(hourOfDay + ":" + minute + " " + AM_PM);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
                        String time = displayFormat.format(date);
                        sessionManager.setSharedPrefItem(SessionManager.KEY_REMINDER, time);
                        setting_RemainderTextView.setText(parseFormat.format(date));


                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println(" Switch " + true);
                    sessionManager.setBooleanSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH, true);
                } else {
                    sessionManager.setBooleanSharedPrefItem(SessionManager.KEY_NOTIFICATION_SWITCH, false);
                }
            }
        });

        setting_FeedbackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"help@relecotech.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                i.putExtra(Intent.EXTRA_TEXT, "Student Name : " + userDetails.get(SessionManager.KEY_NAME) + " \n" + "\n" + "Android Version : " + android.os.Build.VERSION.RELEASE + " \n" + " SDK version : " + android.os.Build.VERSION.SDK_INT + " \n " + "Brand : " + android.os.Build.BRAND + "\n App Version : " + appVersion + "\n" + getString(R.string.app_name));
                startActivity(Intent.createChooser(i, "Send mail..."));
            }
        });

        setting_AboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent(SettingActivity.this, AboutPage.class);
                startActivity(aboutIntent);
            }
        });

    }

    private void setTimeMethod(String time) {
        Date timeDateFrmSession;
        String timeStringFrmSession = "";
        try {
            timeDateFrmSession = displayFormat.parse(time);
            timeStringFrmSession = parseFormat.format(timeDateFrmSession);
            System.out.println("timeDateFrmSession)   " + timeDateFrmSession);
            System.out.println("timeStringFrmSession   " + timeStringFrmSession);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setting_RemainderTextView.setText(timeStringFrmSession);
    }

}
