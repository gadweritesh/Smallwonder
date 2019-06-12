package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.relecotech.androidsparsh_smallwonder.R;

/**
 * Created by Amey on 29-05-2018.
 */

public class AdminAttendance extends AppCompatActivity {

    private LinearLayout hostelAttendanceView, absentStudentReportAttendanceView, attendanceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_attendance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attendanceView = (LinearLayout) findViewById(R.id.adminAttendance_linear_layout);
        absentStudentReportAttendanceView = (LinearLayout) findViewById(R.id.adminAbsentAttendance_linear_layout);
        hostelAttendanceView = (LinearLayout) findViewById(R.id.adminHostelAttendance_linear_layout);


        attendanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendanceReviewIntent = new Intent(AdminAttendance.this, AttendanceTeacher.class);
                startActivity(attendanceReviewIntent);
            }
        });

        absentStudentReportAttendanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendanceReviewIntent = new Intent(AdminAttendance.this, AbsentAttendanceReview.class);
                startActivity(attendanceReviewIntent);
            }
        });


        hostelAttendanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendanceReviewIntent = new Intent(AdminAttendance.this, AbsentAttendanceReview.class);
                startActivity(attendanceReviewIntent);
            }
        });
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
