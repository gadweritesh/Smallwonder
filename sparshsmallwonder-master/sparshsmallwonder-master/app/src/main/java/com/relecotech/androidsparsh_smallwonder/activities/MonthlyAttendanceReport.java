package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.AttendanceReportAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceListData;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amey on 12-04-2018.
 */

public class MonthlyAttendanceReport extends AppCompatActivity {

    private Intent getMonthlyAttendanceIntent;
    private String getStudentId;
    private Spinner attendanceReportMonthselectorSpiner;
    private ArrayAdapter<String> monthlyAttendanceReportAdapter;
    private ListView attendanceReportListView;
    private TextView attendanceReportTitleTextView, attendanceReportSummaryTextView;
    private HashMap<Integer, List<AttendanceListData>> getAttendanceReportMap;
    private HashMap<String, Integer> monthNameHashMap;
    private int presnetStatusCount, totalNoOfDays;
    private String getSelectedMonth;
    private AdView mAdView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_attendance_report);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attendanceReportMonthselectorSpiner = (Spinner) findViewById(R.id.individualStudentReviewMonthselectorSpiner);
        attendanceReportListView = (ListView) findViewById(R.id.attendanceReportListView);
        attendanceReportTitleTextView = (TextView) findViewById(R.id.attendanceReportTitleTextView);
        attendanceReportSummaryTextView = (TextView) findViewById(R.id.attendanceReportSummaryTextView);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        monthNameHashMap = new HashMap<>();

        //Receive intent data from Student Attendance Fragment
        getMonthlyAttendanceIntent = getIntent();


        ArrayList<String> monthList = getMonthlyAttendanceIntent.getStringArrayListExtra("MonthList");
        getStudentId = getMonthlyAttendanceIntent.getStringExtra("Student_id");
        getSelectedMonth = getMonthlyAttendanceIntent.getStringExtra("Selected_month");
        System.out.println("Check Selected Month---- " + getSelectedMonth);

        getAttendanceReportMap = (HashMap<Integer, List<AttendanceListData>>) getMonthlyAttendanceIntent.getSerializableExtra("AttendanceReportMap");
        monthlyAttendanceReportAdapter = new ArrayAdapter<String>(MonthlyAttendanceReport.this, R.layout.monthly_spinner_item, monthList);
        monthlyAttendanceReportAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        System.out.println("get Index of Month List-----  " + monthList.indexOf(getSelectedMonth));
        attendanceReportMonthselectorSpiner.setAdapter(monthlyAttendanceReportAdapter);
        attendanceReportMonthselectorSpiner.setSelection(monthList.indexOf(getSelectedMonth));


        //Month Name Map
        monthNameHashMap.put("January", 1);
        monthNameHashMap.put("February", 2);
        monthNameHashMap.put("March", 3);
        monthNameHashMap.put("April", 4);
        monthNameHashMap.put("May", 5);
        monthNameHashMap.put("June", 6);
        monthNameHashMap.put("July", 7);
        monthNameHashMap.put("August", 8);
        monthNameHashMap.put("September", 9);
        monthNameHashMap.put("October", 10);
        monthNameHashMap.put("November", 11);
        monthNameHashMap.put("December", 12);

        attendanceReportMonthselectorSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) attendanceReportMonthselectorSpiner.getItemAtPosition(position);
                List<AttendanceListData> attendanceReportList;
                Log.d("selected Item Position", "" + s);
                if (!s.equalsIgnoreCase("Select Month")) {
                    attendanceReportList = getAttendanceReportMap.get(monthNameHashMap.get(attendanceReportMonthselectorSpiner.getSelectedItem()));
                    SetListView(attendanceReportList);
                    attendanceReportTitleTextView.setText("Attendance Report : ");
                } else {
                    FancyToast.makeText(MonthlyAttendanceReport.this, "Select Month", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void SetListView(List<AttendanceListData> attendanceReportList) {
        presnetStatusCount = 0;
        totalNoOfDays = attendanceReportList.size();
        System.out.println(" attendanceReportList 2" + attendanceReportList.size());
        for (int attloop = 0; attloop < attendanceReportList.size(); attloop++) {

            //Monthly report Sort
            Collections.sort(attendanceReportList, new Comparator<AttendanceListData>() {
                @Override
                public int compare(AttendanceListData obj1, AttendanceListData obj2) {
                    // ## Ascending order
                    System.out.println(obj1.getAttendanceDate().compareToIgnoreCase(obj2.getAttendanceDate()));
                    return obj1.getAttendanceDate().compareToIgnoreCase(obj2.getAttendanceDate());
                }
            });

            if (attendanceReportList.get(attloop).getAttendanceStatus().contains("P")) {
                presnetStatusCount++;
            }
        }


        attendanceReportSummaryTextView.setText(presnetStatusCount + " / " + totalNoOfDays);
        System.out.println(" attendanceReportList 3" + attendanceReportList.size());
        AttendanceReportAdapter attendanceReportAdapter = new AttendanceReportAdapter(MonthlyAttendanceReport.this, attendanceReportList);
        attendanceReportAdapter.notifyDataSetChanged();
        attendanceReportListView.setAdapter(attendanceReportAdapter);

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
