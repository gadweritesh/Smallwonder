package com.relecotech.androidsparsh_smallwonder.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.activities.AttendanceMark;
import com.relecotech.androidsparsh_smallwonder.activities.AttendanceReview;
import com.relecotech.androidsparsh_smallwonder.adapters.AttendanceDraftAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceDraftListData;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AttendanceClassFragment extends Fragment {

    private Spinner classSpinner, divisionSpinner, studentNameSpinner;
    private DatabaseHandler databaseHandler;
    private ArrayList<String> classList, divisionList;
    private HashMap<String, String> conditionHashMap;
    private Cursor resultSet;
    private static String attendanceSchoolClassId;
    private static String classString;
    private static String divisionString;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private TextView todayAttendanceTitleText, todayAttendanceStatusText;
    private ListView draftListView;
    private TextView draftTitleTextView;
    List<AttendanceDraftListData> draftList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_teacher_class_fragment, container, false);

        classSpinner = (Spinner) rootView.findViewById(R.id.attendanceTeacherClassSpinner);
        divisionSpinner = (Spinner) rootView.findViewById(R.id.attendanceTeacherDivisionSpinner);

        draftListView = (ListView) rootView.findViewById(R.id.attendanceDraftListView);
        draftTitleTextView = (TextView) rootView.findViewById(R.id.draftTitleTextView);
        draftTitleTextView.setVisibility(View.INVISIBLE);

        todayAttendanceTitleText = (TextView) rootView.findViewById(R.id.todayAttendanceTitleTextView);
        todayAttendanceStatusText = (TextView) rootView.findViewById(R.id.todayAttendanceStatusTextView);
        todayAttendanceTitleText.setVisibility(View.INVISIBLE);
        todayAttendanceStatusText.setVisibility(View.INVISIBLE);

        Button attendanceReviewButton = (Button) rootView.findViewById(R.id.attendanceReviewButton);
        Button attendanceMarkButton = (Button) rootView.findViewById(R.id.attendanceMarkButton);

        sessionManager = new SessionManager(getActivity(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        databaseHandler = new DatabaseHandler(getActivity());
        draftList = new ArrayList<>();
        setDraftMetaData();

//        checkAttendanceStatus();

        classList = new ArrayList<>();
        divisionList = new ArrayList<>();
        conditionHashMap = new HashMap<>();


        resultSet = databaseHandler.getClassDataByCursor();

        resultSet.moveToFirst();
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!classList.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                classList.add(resultSet.getString(resultSet.getColumnIndex("class")));
            }
            resultSet.moveToNext();
        }

        classList.add("[ Class ]");
        ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, classList);
        adapterClass.setDropDownViewResource(R.layout.spinner_item);
        classSpinner.setAdapter(adapterClass);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classString = classSpinner.getSelectedItem().toString();
                divisionList.clear();
                if (!classString.contains("[ Class ]")) {
                    conditionHashMap.put("class", classString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        if (!divisionList.contains(resultSet.getString(resultSet.getColumnIndex("division")))) {
                            divisionList.add(resultSet.getString(resultSet.getColumnIndex("division")));
                        }
                        resultSet.moveToNext();
                    }
                } else {
                    divisionList = new ArrayList<>();
                }
                divisionList.add("[ Division ]");
                ArrayAdapter<String> adapterDivision = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, divisionList);
                adapterDivision.setDropDownViewResource(R.layout.spinner_item);
                divisionSpinner.setAdapter(adapterDivision);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                divisionString = divisionSpinner.getSelectedItem().toString();
                try {
                    resultSet.moveToFirst();
                    do {
                        if (resultSet.getString(resultSet.getColumnIndex("class")).equals(classSpinner.getSelectedItem().toString()) && resultSet.getString(resultSet.getColumnIndex("division")).equals(divisionString)) {
                            attendanceSchoolClassId = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));
                        }
                    } while (resultSet.moveToNext());
                } catch (Exception e) {
                    System.out.println("Exception in class spinner of attendanceClassFrag");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        attendanceReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!classSpinner.getSelectedItem().toString().equals("[ Class ]")) {
                    if (!divisionSpinner.getSelectedItem().toString().equals("[ Division ]")) {

                        DialogFragment newFragment = new SelectDateFragment();
                        newFragment.show(getFragmentManager(), "DatePicker");
                    } else {
                        FancyToast.makeText(getActivity(), "Select Division", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    }
                } else {
                    FancyToast.makeText(getActivity(), "Select Class", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }
            }
        });

        attendanceMarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!classSpinner.getSelectedItem().toString().equals("[ Class ]")) {
                    if (!divisionSpinner.getSelectedItem().toString().equals("[ Division ]")) {
//                        if (!checkAttendanceStatus()) {
                        Intent markIntent = new Intent(getActivity(), AttendanceMark.class);
//                        markIntent.putExtra("Task", "MarkAttendance");
                        markIntent.putExtra("schoolClassId", attendanceSchoolClassId);
                        markIntent.putExtra("class", classString);
                        markIntent.putExtra("division", divisionString);
                        startActivity(markIntent);
//                        } else {
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("Attendance marked.")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    }).show();
//                        }
                    } else {
                        FancyToast.makeText(getActivity(), "Select Division", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    }
                } else {
                    FancyToast.makeText(getActivity(), "Select Class", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }
            }
        });


        draftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                System.out.println(" draftListView position " + position);
                System.out.println(" draftList size " + draftList.size());
                AttendanceDraftListData selectedDraftItem = draftList.get(position);

                // Calling new attendance activity
                // along with passing necessary data to fetch student list from azure
                Intent markAttendanceIntent = new Intent(getActivity(), AttendanceMark.class);
                markAttendanceIntent.putExtra("schoolClassId", selectedDraftItem.getSchoolClassId());
                markAttendanceIntent.putExtra("class", selectedDraftItem.getAttendanceClass());
                markAttendanceIntent.putExtra("division", selectedDraftItem.getAttendanceDivision());
                markAttendanceIntent.putExtra("Date", selectedDraftItem.getAttendanceDate());
                startActivity(markAttendanceIntent);
            }
        });

        return rootView;
    }

    private boolean checkAttendanceStatus() {

        //getting current date and comparing with saved date
        String todaysDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        System.out.println("today's Date : " + todaysDate);
        System.out.println("sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE) " + sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));

        //condition to check weather tab is class tab and attendance is marked or not
        if (todaysDate.equals(sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE))) {
            System.out.println("Attendance completed ");
//            todayAttendanceTitleText.setVisibility(View.VISIBLE);
//            todayAttendanceTitleText.setText("Attendance for " + sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));
//            todayAttendanceStatusText.setVisibility(View.VISIBLE);
            return true;
        } else {
            System.out.println("Attendance left OR in student tab");
            todayAttendanceTitleText.setVisibility(View.INVISIBLE);
            todayAttendanceStatusText.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    private void setDraftMetaData() {
        try {

            //setting draft metadata to draft list
            Cursor draftCursor = databaseHandler.geAttendanceDraftMetaData();
            draftList = new ArrayList<>();
            int draftCount = draftCursor.getCount();

            if (draftCursor.moveToFirst()) {
                do {
                    //getting class division from schoolclass id
                    System.out.println("draftCursor.getString(1) " + draftCursor.getString(1));
                    Cursor classDivisionCursor = databaseHandler.getClassDivisionByClassId(draftCursor.getString(1));
                    classDivisionCursor.moveToFirst();

                    System.out.println("draftCursor.getString(0) " + draftCursor.getString(0));
                    draftList.add(new AttendanceDraftListData(draftCursor.getString(1),
                            classDivisionCursor.getString(0), classDivisionCursor.getString(1), draftCursor.getString(0)));
                } while (draftCursor.moveToNext());
            }

            draftTitleTextView.setVisibility(View.VISIBLE);
            System.out.println(" draftList.size() " + draftList.size());
            if (draftList.size() == 0) {
                draftTitleTextView.setVisibility(View.INVISIBLE);
            }
            AttendanceDraftAdapter draftAdapter = new AttendanceDraftAdapter(getActivity(), draftList);
            draftListView.setAdapter(draftAdapter);

        } catch (Exception e) {
            System.out.println("Exception attendanceClassFragment " + e.getMessage());
        }

    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            final int yy = calendar.get(java.util.Calendar.YEAR);
            final int mm = calendar.get(java.util.Calendar.MONTH);
            final int dd = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, yy, mm, dd) {
                @Override
                public void onDateChanged(DatePicker view, int year, int month, int day) {
                    super.onDateChanged(view, year, month, day);
                    if (year > yy) {
                        view.updateDate(yy, mm, dd);

                    }
                    if (month > mm && year == yy) {
                        view.updateDate(yy, mm, dd);
                    }
                    if (day > dd && month == mm && year == yy) {
                        view.updateDate(yy, mm, dd);
                    }
                }

                @Override
                public void onClick(DialogInterface dialog, int doneBtn) {
                    if (doneBtn == BUTTON_POSITIVE) {

                        int year = getDatePicker().getYear();
                        int month = getDatePicker().getMonth();
                        int day = getDatePicker().getDayOfMonth();

                        Intent reviewAttendanceIntent = new Intent(getActivity(), AttendanceReview.class);
                        reviewAttendanceIntent.putExtra("reviewDate", day + "-" + (month + 1) + "-" + year);
                        reviewAttendanceIntent.putExtra("reviewClassId", attendanceSchoolClassId);
                        reviewAttendanceIntent.putExtra("class", classString);
                        reviewAttendanceIntent.putExtra("division", divisionString);
                        startActivity(reviewAttendanceIntent);
                    }
                    super.onClick(dialog, doneBtn);
                }
            };
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAttendanceStatus();
    }
}
