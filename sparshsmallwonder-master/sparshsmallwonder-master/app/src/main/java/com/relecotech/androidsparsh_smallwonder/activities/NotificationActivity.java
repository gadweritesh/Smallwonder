package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.NotificationListAdapter;
import com.relecotech.androidsparsh_smallwonder.models.NotificationListData;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class NotificationActivity extends AppCompatActivity {

    private ArrayList<NotificationListData> noticeList;
    private NotificationListData noticeListData;
    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SessionManager sessionManager = new SessionManager(getApplicationContext() , sharedPrefValue);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noticeList = new ArrayList<>();
        DatabaseHandler databaseHandler = new DatabaseHandler(this);

        TextView noNotificationTextView = (TextView) findViewById(R.id.noNotificationTextView);
        ListView notificationListView = (ListView) findViewById(R.id.notificationListView);
//        noticeList.add(0, noticeListData);
//        noticeList.add(0, new NotificationListData("2", "5 May 2018", "Assignment", "English Assignment Added", "30 Apr 2018", "Ramesh Bhagwat"));
//        noticeList.add(0, new NotificationListData("3", "10 May 2018", "Alert", "Pending Fees Alert", "16 Mar 2018", "Ramesh Bhagwat"));
//        noticeList.add(0, new NotificationListData("4", "10 May 2018", "Gallery", "Cultural Event Added in Gallery ", "3 Feb 2018", "Ramesh Bhagwat"));
//        noticeList.add(0, new NotificationListData("5", "10 May 2018", "Achievement", "New Academic Achievements Added", "20 Dec 2018", "Ramesh Bhagwat"));

        Cursor cursor = databaseHandler.getAllNotificationDataByCursor();
        cursor.moveToFirst();
        int cursorCount = cursor.getCount();
        System.out.println(" cursorCount " + cursorCount);
        if (cursorCount == 0) {
            noNotificationTextView.setText("No Notifications");
        } else {
            for (int i = 0; i < cursorCount; i++) {
                String notification_Main_Tag = cursor.getString(cursor.getColumnIndex("notificationCategory"));
                String notifiaction_Main_Message_Body = cursor.getString(cursor.getColumnIndex("notificationMessage"));
                String notifcation_Main_Submitted_By = cursor.getString(cursor.getColumnIndex("notificationSubmittedBy"));
                String notifaction_Main_Post_Date = cursor.getString(cursor.getColumnIndex("notificationPostDate"));
                System.out.println("notifaction_Main_Post_Date " + notifaction_Main_Post_Date);
                String notification_Main_Assignment_Due_Date = cursor.getString(cursor.getColumnIndex("notificationAssmntDueDate"));
                String notifaction_Main_Assignm_Id = cursor.getString(cursor.getColumnIndex("notificationAssignmentId"));
                noticeListData = new NotificationListData(notifaction_Main_Assignm_Id, notification_Main_Assignment_Due_Date, notification_Main_Tag, notifiaction_Main_Message_Body, notifaction_Main_Post_Date, notifcation_Main_Submitted_By);
                noticeList.add(0, noticeListData);
                cursor.moveToNext();
                int count = 0;
            }
        }


        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationListData noticeListData = noticeList.get(position);
                Intent intent;
                switch (noticeListData.getNotifaction_Tag()) {
                    case "Leave":
                        intent = new Intent(NotificationActivity.this, ParentZoneActivity.class);
                        startActivity(intent);
                        break;
                    case "Notes":
                        intent = new Intent(NotificationActivity.this, ParentZoneActivity.class);
                        startActivity(intent);
                        break;
                    case "Assignment":
                        if (userRole.equals("Administrator")) {
                            intent = new Intent(NotificationActivity.this, AdminAssignmentListActivity.class);
                        } else {
                            intent = new Intent(NotificationActivity.this, AssignmentListActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case "Alert":
                        if (userRole.equals("Administrator")) {
                            intent = new Intent(NotificationActivity.this, AdminAlertListActivity.class);
                        } else {
                            intent = new Intent(NotificationActivity.this, AlertListActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case "Gallery":
                        intent = new Intent(NotificationActivity.this, SchoolGallery.class);
                        startActivity(intent);
                        break;
                    case "Achievement":
                        intent = new Intent(NotificationActivity.this, AchievementActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        NotificationListAdapter notificationListAdapter = new NotificationListAdapter(this, noticeList);
        notificationListView.setAdapter(notificationListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
