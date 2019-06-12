package com.relecotech.androidsparsh_smallwonder.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.activities.ClassScheduleActivity;
import com.relecotech.androidsparsh_smallwonder.adapters.ClassScheduleSlotAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ClassScheduleListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by amey on 6/10/2016.
 */
public class ClassSchedule_Saturday extends Fragment {

    private Map<Integer, ClassScheduleListData> slotWiseMap;
    private Date startTime;
    private Date endTime;
    private String subject;
    private String subjectTeacherName;
    private String slotType;
    ArrayList<ClassScheduleListData> studentTimeTableList;
    private String userRole;
    private ClassScheduleSlotAdapter slotAdapter;
    private ListView timeTableListView;
    private TextView noDataAvailableTextView;
    private String teacherClassDivision;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentTimeTableList = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(getActivity(), sharedPrefValue);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        slotWiseMap = ClassScheduleActivity.studentTimeTableHashMap.get("Saturday");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_schedule_timetable_fragment, container, false);

        timeTableListView = (ListView) rootView.findViewById(R.id.time_table_listView);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.no_data_available_text_view);

        if (slotWiseMap != null) {
            noDataAvailableTextView.setVisibility(View.INVISIBLE);
            if (userRole.equals("Student")) {
                try {
                    System.out.println("userRole       :: " + "Student");
                    for (Map.Entry<Integer, ClassScheduleListData> entry : slotWiseMap.entrySet()) {
                        startTime = entry.getValue().getSlotStartTime();
                        endTime = entry.getValue().getSlotEndTime();
                        subject = entry.getValue().getSlotSubject();
                        subjectTeacherName = entry.getValue().getSubjectTeacherName();
                        slotType = entry.getValue().getSlotType();

                        studentTimeTableList.add(new ClassScheduleListData(startTime, endTime, subject, subjectTeacherName, slotType, "", false));
                    }
                } catch (Exception e) {
                    System.out.println("Exception---------" + e.getMessage());
                }
            }
            if (userRole.equals("Teacher")) {
                System.out.println("userRole       :: " + "Teacher");
                try {
//                    Map.Entry<Integer, ClassScheduleListData> entry = slotWiseMap.entrySet().iterator().next();
//                    Integer key = entry.getKey();
                    for (Map.Entry<Integer, ClassScheduleListData> entry : slotWiseMap.entrySet()) {

                        startTime = entry.getValue().getSlotStartTime();
                        endTime = entry.getValue().getSlotEndTime();
                        subject = entry.getValue().getSlotSubject();
                        System.out.println("ClassSchedule_Monday subject " + subject);
                        subjectTeacherName = entry.getValue().getSubjectTeacherName();
                        slotType = entry.getValue().getSlotType();
                        teacherClassDivision = entry.getValue().getTeacherClassDivision();

                        long timeDifference = dateDifference(startTime, endTime);
                        System.out.println("timeDifference  ######################### " + timeDifference);
                        if (timeDifference > 10) {
                            studentTimeTableList.add(new ClassScheduleListData(null, null, null, null, "Free", null, false));
                        }
                        studentTimeTableList.add(new ClassScheduleListData(startTime, endTime, subject, subjectTeacherName, slotType, teacherClassDivision, false));

                    }
                } catch (Exception e) {
                    System.out.println("Exception---------" + e.getMessage());
                }
            }
        } else {
            noDataAvailableTextView.setVisibility(View.VISIBLE);
        }

        slotAdapter = new ClassScheduleSlotAdapter(getActivity(), studentTimeTableList);
        timeTableListView.setAdapter(slotAdapter);

        return rootView;
    }

    private static long dateDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        return different / 60000;
    }
}
