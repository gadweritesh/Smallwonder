package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.TeacherAttendancePagerAdapter;
import com.relecotech.androidsparsh_smallwonder.fragments.AttendanceClassFragment;
import com.relecotech.androidsparsh_smallwonder.fragments.AttendanceStudentFragment;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AttendanceTeacher extends AppCompatActivity {

    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private TabLayout attendanceTabLayout;
    private ViewPager attendanceViewPager;

    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_teacher_view);

        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        attendanceTabLayout = (TabLayout) findViewById(R.id.attendance_tab_layout);
        attendanceViewPager = (ViewPager) findViewById(R.id.attendance_view_pager);
        setUpViewPager(attendanceViewPager);

        attendanceTabLayout.setupWithViewPager(attendanceViewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        TeacherAttendancePagerAdapter adapter = new TeacherAttendancePagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new AttendanceClassFragment(), "Class");
        adapter.addFragment(new AttendanceStudentFragment(), "Student");

        viewPager.setAdapter(adapter);
    }
}
