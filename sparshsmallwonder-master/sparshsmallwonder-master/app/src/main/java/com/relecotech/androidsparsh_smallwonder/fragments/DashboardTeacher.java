package com.relecotech.androidsparsh_smallwonder.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.MainActivity;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.activities.AchievementActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AlertListActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AssignmentListActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AttendanceTeacher;
import com.relecotech.androidsparsh_smallwonder.activities.CalendarActivity;
import com.relecotech.androidsparsh_smallwonder.activities.ClassScheduleActivity;
import com.relecotech.androidsparsh_smallwonder.activities.NotificationActivity;
import com.relecotech.androidsparsh_smallwonder.activities.ParentZoneActivity;
import com.relecotech.androidsparsh_smallwonder.activities.SchoolGallery;
import com.relecotech.androidsparsh_smallwonder.activities.UserProfile;
import com.relecotech.androidsparsh_smallwonder.azureControllers.SchoolClassSubjectView;
import com.relecotech.androidsparsh_smallwonder.azureControllers.School_Class;
import com.relecotech.androidsparsh_smallwonder.azureControllers.School_Subject;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.Converter;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler.KEY_HEADLINE_END_DATE;
import static com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler.KEY_HEADLINE_TITLE;

/**
 * Created by Relecotech on 23-02-2018.
 */
//Dash
public class DashboardTeacher extends Fragment {

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO};
    private TextView calendarTextFirst, calendarTextSecond, calendarTextThird;
    private TextView alertTextFirst, alertTextSecond, alertTextThird;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private String teacherId;

    private View viewCalendar, viewAlert;
    private LinearLayout viewAssignment, viewAttendance, viewGallery, viewTimeTable,viewprofile ,viewparntzone;
    private String schoolClassString, division;
    private TextView nameTextView, classTextView;
    private String headline;
    private Date headlineEndDate;
    private int notificationCount = 0;
    private TextView notification_count_textView;
    private MenuItem menuItem;
    //    IntentFilter filter = new IntentFilter("com.relecotech.androidsparsh.USER_ACTION");
    IntentFilter filter;
    Boolean backPressRefresh = true;
    private long back_pressed;
    private CircleImageView userProfileImageView;
    private ProgressDialog progressDialog;
    private MobileServiceClient mClient;
    private TextView calendarNoDataTextView, alertNoDataTextView;
    private MobileServiceTable<School_Class> mSchoolClassTable;
    private MobileServiceTable<School_Subject> mSchoolSubjectTable;
    private MobileServiceTable<SchoolClassSubjectView> mSchoolSubjectTable1;
    private HashMap<String, String> userDetails;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = Singleton.Instance().mClientMethod(getActivity());
        mSchoolClassTable = mClient.getTable(School_Class.class);
        mSchoolSubjectTable = mClient.getTable(School_Subject.class);
        mSchoolSubjectTable1 = mClient.getTable(SchoolClassSubjectView.class);


        filter = new IntentFilter(getActivity().getPackageName() + ".USER_ACTION");
        System.out.println("getActivity().getPackageName() " + getActivity().getPackageName());
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMyBroadcastReceiver, filter);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_teacher, container, false);

        // TextView headlineTextView = (TextView) rootView.findViewById(R.id.marqueHeadlineTextView);
        userProfileImageView = (CircleImageView) rootView.findViewById(R.id.dashTeacherImageView);

        connectionDetector = new ConnectionDetector(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        findViewByIdMethod(rootView);

        String dashboardData = getArguments().getString("dashboardData");
        sessionManager = new SessionManager(getActivity(), dashboardData);
        nameTextView.setText(dashboardData);
        try {
            if (sessionManager.getUserDetails().get(SessionManager.KEY_DESIGNATION) == null ||
                    sessionManager.getUserDetails().get(SessionManager.KEY_DESIGNATION).equals("null") ||
                    sessionManager.getUserDetails().get(SessionManager.KEY_DESIGNATION).isEmpty()) {
            } else {
                classTextView.setText("Designation : " + sessionManager.getUserDetails().get(SessionManager.KEY_DESIGNATION));
            }

        } catch (Exception e) {
            System.out.println(" Exception " + e.getMessage());
        }

        try {
            databaseHandler = new DatabaseHandler(getActivity());
            Cursor resultSet = databaseHandler.getHeadlineTitle();
            System.out.println("resultSet " + resultSet.getCount());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            resultSet.moveToFirst();
            for (int i = 0; i < resultSet.getCount(); i++) {
                headline = resultSet.getString(resultSet.getColumnIndex(KEY_HEADLINE_TITLE));
                String headlineDateInString = resultSet.getString(resultSet.getColumnIndex(KEY_HEADLINE_END_DATE));
                System.out.println(" headline " + headline);
                System.out.println(" headlineDateInString " + headlineDateInString);
                headlineEndDate = null;
                try {
                    headlineEndDate = dateFormat.parse(headlineDateInString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                resultSet.moveToNext();
            }

            Date currentDate = Calendar.getInstance().getTime();
            String formattedDate = dateFormat.format(currentDate);
            try {
                currentDate = dateFormat.parse(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(" headlineEndDate " + headlineEndDate);
            System.out.println(" currentDate " + currentDate);
            if (currentDate.before(headlineEndDate) || headlineEndDate.equals(currentDate)) {
               /* headlineTextView.setText(headline);
            } else {
                headlineTextView.setText(" *** Welcome to " + getString(R.string.app_name) + " Mobile App ***  ");*/
            }
        } catch (Exception e) {
           /* headlineTextView.setText(" *** Welcome to " + getString(R.string.app_name) + " Mobile App ***  ");
            System.out.println(" Exception " + e.getMessage());*/
        }
        //headlineTextView.setSelected(true);

        new AsyncTaskRunner().execute();


        Cursor resultSet = databaseHandler.getClassDataByCursor();
        System.out.println(" resultSet " + resultSet.getCount());

        viewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calendarIntent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(calendarIntent);
            }
        });

        viewAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alertIntent = new Intent(getActivity(), AlertListActivity.class);
                startActivity(alertIntent);
            }
        });
        viewAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent assignmentIntent = new Intent(getActivity(), AssignmentListActivity.class);
//                assignmentIntent.putExtra("classIdDivObjHashMap", (Serializable) classIdDivObjHashMap);
                startActivity(assignmentIntent);
            }
        });
        viewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent attendanceIntent = new Intent(getActivity(), AttendanceTeacher.class);
                startActivity(attendanceIntent);
            }
        });
        viewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getActivity(), SchoolGallery.class);
                startActivity(galleryIntent);
            }
        });
        viewTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timeTableIntent = new Intent(getActivity(), ClassScheduleActivity.class);
                startActivity(timeTableIntent);
            }
        });
        viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timeTableIntent = new Intent(getActivity(), UserProfile.class);
                startActivity(timeTableIntent);
            }

        });
        viewparntzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getActivity(), ParentZoneActivity.class);
                startActivity(galleryIntent);

            }
        });
/*

        BottomNavigationView navigationBottom = (BottomNavigationView) rootView.findViewById(R.id.navigation);
        navigationBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectPage(item);
                return false;
            }
        });
*/

        backPressRefresh = false;
        DashBoardConnectionCall();
//        spiltDashboardJSON(new JsonParser().parse(dashboardData));

        return rootView;
    }

    private void findViewByIdMethod(View rootView) {
        nameTextView = (TextView) rootView.findViewById(R.id.name_textView);
        classTextView = (TextView) rootView.findViewById(R.id.class_textView);
        calendarTextFirst = (TextView) rootView.findViewById(R.id.calendarTextFirst);
        calendarTextSecond = (TextView) rootView.findViewById(R.id.calendarTextSecond);
        calendarTextThird = (TextView) rootView.findViewById(R.id.calendarTextThird);

        alertTextFirst = (TextView) rootView.findViewById(R.id.alertTextFirst);
        alertTextSecond = (TextView) rootView.findViewById(R.id.alertTextSecond);
        alertTextThird = (TextView) rootView.findViewById(R.id.alertTextThird);

        calendarNoDataTextView = (TextView) rootView.findViewById(R.id.calendarNoData);
        alertNoDataTextView = (TextView) rootView.findViewById(R.id.alertNoData);

        viewCalendar = (View) rootView.findViewById(R.id.viewCalendarBg);
        viewAlert = (View) rootView.findViewById(R.id.viewAlertBg);

        viewAssignment = (LinearLayout) rootView.findViewById(R.id.assignment_linear_layout);
        viewAttendance = (LinearLayout) rootView.findViewById(R.id.attendance_linear_layout);
        viewGallery = (LinearLayout) rootView.findViewById(R.id.gallery_linear_layout);
        viewTimeTable = (LinearLayout) rootView.findViewById(R.id.schedule_linear_layout);
        viewprofile = (LinearLayout) rootView.findViewById(R.id.profile_linear_layout);
        viewparntzone = (LinearLayout) rootView.findViewById(R.id.parentzone_linear_layout);
    }

    private void DashBoardDataCall() {

        JsonObject jsonDashBoardCall = new JsonObject();
        jsonDashBoardCall.addProperty("userRole", sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE));
        jsonDashBoardCall.addProperty("teacherId", sessionManager.getUserDetails().get(SessionManager.KEY_TEACHER_ID));
        jsonDashBoardCall.addProperty("branchId", sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture;
        serviceFilterFuture = mClient.invokeApi("dashboardDataApi", jsonDashBoardCall);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception.getMessage());
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DashBoardConnectionCall();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 5000);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                spiltDashboardJSON(response);
            }
        });
    }

    private void spiltDashboardJSON(JsonElement response) {

        JsonObject responseToJsonObject = response.getAsJsonObject();

        JsonElement dataFromJsonObjectToJsonElement = responseToJsonObject.get("message");
        if (dataFromJsonObjectToJsonElement.toString().contains("inactive")) {
            progressDialog.dismiss();
            new AlertDialog.Builder(getActivity())
                    .setTitle("User Inactive.")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            sessionManager = new SessionManager(getActivity());

                            HashMap<String, String> user = new HashMap<String, String>();

                            sessionManager = new SessionManager(getActivity());
                            SharedPreferences pref = getActivity().getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.commit();
                            user.clear();
                            databaseHandler.deleteAllTables();

                            getActivity().finish();
                        }
                    }).show();

            FancyToast.makeText(getActivity(), "USER INACTIVE", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        } else {
            JsonObject dataFromJsonElementToJsonObject = dataFromJsonObjectToJsonElement.getAsJsonObject();

            JsonElement alertResult = dataFromJsonElementToJsonObject.get("alertResult");
            JsonElement calendarResult = dataFromJsonElementToJsonObject.get("calendarResult");

            System.out.println("alertResult - ----------------" + alertResult);
            System.out.println("calendarResult --------------- " + calendarResult);
            setDataOnDashboardPanels(alertResult, calendarResult);
        }
    }
    private void setDataOnDashboardPanels(JsonElement alertFetchResponse, JsonElement calendarFetchResponse) {
        //logic to set data on ALERT panel
        try {
            JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
            System.out.println("alertJsonArray.size() " + alertJsonArray.size());
            if (alertJsonArray.size() == 0) {
                alertNoDataTextView.setText(R.string.noDataAvailable);
                alertNoDataTextView.setVisibility(View.VISIBLE);
                alertTextFirst.setText(null);
                alertTextSecond.setText(null);
                alertTextThird.setText(null);
            } else {
                alertNoDataTextView.setVisibility(View.INVISIBLE);
                int loopCount = 0;
                for (int i = 0; i <= alertJsonArray.size() - 1; i++) {
                    JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();
                    String alertTitle = jsonObjectForIteration.get("alertTitle").toString().replace("\"", "").replace("\\n", "\n").replace("\\", "");
                    String postDateString = jsonObjectForIteration.get("alertPostDate").toString().replace("\"", "");
                    System.out.println("alertTitle.size() " + alertTitle);
                    String alertType = jsonObjectForIteration.get("alertCategory").toString().replace("\"", "").replace("\\n", "\n");
                    System.out.println("alertCategory"+alertType);
                    DateFormat alertDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    Date alertDate = null;

                    try{
                        alertDate = alertDateFormat.parse(postDateString);

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    DateFormat targetAlertDateFormat = new SimpleDateFormat("dd MMMM ", Locale.getDefault());
                    postDateString = targetAlertDateFormat.format(alertDate);

                    alertType = "" + alertType.substring(0, 1) + "";
                    System.out.println("alertTYPE  "+alertType);

                    switch (loopCount) {
                        case 0:
                            alertTextFirst.setText(Html.fromHtml("<b>"+"<font color=#FF0000>"+'('+"</font>"+"<font color=#FF0000>"+alertType+"</font>" +"<font color=#FF0000>"+')'+"</font>&nbsp;"+"<font color=#808080>"+ postDateString+"</font>"+"<font color=#333333>"+ " : </b></font>"+ "<b>"+alertTitle+"</b>"  ));
                            break;
                        case 1:
                            alertTextSecond.setText(Html.fromHtml("<b>"+"<font color=#FF0000>"+'('+"</font>"+"<font color=#FF0000>"+alertType+"</font>" +"<font color=#FF0000>"+')'+"</font>&nbsp;"+"<font color=#808080>"+ postDateString+"</font>"+"<font color=#333333>"+ " : </b></font>"+ "<b>"+alertTitle+"</b>"));
                            break;
                        case 2:
                            alertTextThird.setText(Html.fromHtml("<b>"+"<font color=#FF0000>"+'('+"</font>"+"<font color=#FF0000>"+alertType+"</font>" +"<font color=#FF0000>"+')'+"</font>&nbsp;"+"<font color=#808080>"+ postDateString+"</font>"+"<font color=#333333>"+ " : </b></font>"+ "<b>"+alertTitle+"</b>"));
                            break;
                    }
                    if (loopCount == 2) {
                        break;
                    }
                    loopCount++;
                }
            }
        } catch (Exception e) {
            System.out.println("Catch of Alert Logic" + e.getMessage());
        }

        try {
            JsonArray calendarJsonArray = calendarFetchResponse.getAsJsonArray();
            //logic to set data on CALENDAR panel
            if (calendarJsonArray.size() == 0) {
                calendarNoDataTextView.setText(R.string.noDataAvailable);
                calendarNoDataTextView.setVisibility(View.VISIBLE);
                calendarTextFirst.setText(null);
                calendarTextSecond.setText(null);
                calendarTextThird.setText(null);
            } else {
                calendarNoDataTextView.setVisibility(View.INVISIBLE);
                for (int i = calendarJsonArray.size() - 1; i >= 0; i--) {
                    JsonObject jsonObjectForIteration = calendarJsonArray.get(i).getAsJsonObject();

                    String eventTitle = jsonObjectForIteration.get("calendarTitle").toString().replace("\"", "").replace("\\n", "\n");
                    String eventType = jsonObjectForIteration.get("calendarType").toString().replace("\"", "").replace("\\n", "\n");
                    String eventDate = jsonObjectForIteration.get("startDate").toString().replace("\"", "").replace("\\n", "\n");

                    DateFormat calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    Date calDate = null;
                    try {
                        calDate = calendarDateFormat.parse(eventDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DateFormat targetDateFormat = new SimpleDateFormat("dd MMMM ", Locale.getDefault());
                    eventDate = targetDateFormat.format(calDate);


                    String typeColor = "#F06103";
                    if (eventType.equals("Holiday")) {
                        typeColor = "#FF0000";
                    }
                    eventType = "(" + eventType.substring(0, 1) + ")";
                    switch (i) {
                        case 0:
                            calendarTextFirst.setText(Html.fromHtml("<b><font color=\"" + typeColor + "\">" + "<font color=#FF0000>" +eventType +"</font>"+ "</font></i>" +"&nbsp"+"<font color=#808080>"+ eventDate+"</font>" +"<font color=#333333> : </b>" +"</font>"+" " +"<b><font color=#333333>"+ eventTitle +"</b></font>"));
                            break;
                        case 1:
                            calendarTextSecond.setText(Html.fromHtml("<b><font color=\"" + typeColor + "\">" + "<font color=#FF0000>" +eventType +"</font>"+ "</font></i>" +"&nbsp"+"<font color=#808080>"+ eventDate+"</font>" +"<font color=#333333> : </b>" +"</font>"+" " +"<b><font color=#333333>"+ eventTitle +"</b></font>"));
                            break;
                        case 2:
                            calendarTextThird.setText(Html.fromHtml("<b><font color=\"" + typeColor + "\">" + "<font color=#FF0000>" +eventType +"</font>"+ "</font></i>" +"&nbsp"+"<font color=#808080>"+ eventDate+"</font>" +"<font color=#333333> : </b>" +"</font>"+" " +"<b><font color=#333333>"+ eventTitle +"</b></font>"));
                            break;
                    }
                }
            }
            progressDialog.dismiss();
        } catch (Exception e) {
            System.out.println("Catch of CALENDAR Logic" + e.getMessage());
        }
    }


    private void selectPage(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.navigation_parent:
                Intent parentIntent = new Intent(getActivity(), ParentZoneActivity.class);
                startActivity(parentIntent);
                break;
            case R.id.navigation_achievement:
                Intent achievementIntent = new Intent(getActivity(), AchievementActivity.class);
                startActivity(achievementIntent);
                break;
            case R.id.navigation_profile:
                Intent profileIntent = new Intent(getActivity(), UserProfile.class);
                startActivity(profileIntent);
//                Intent videoIntent = new Intent(getActivity(), VideoGallery.class);
//                startActivity(videoIntent);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private boolean isPermissionGranted() {
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                || getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            return false;
        }
    }

//    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                classResult = schoolClass.where().field("Branch_id").eq(branchId).execute().get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            for (int i = 0; i < classResult.size(); i++) {
//                String schoolClass = classResult.get(i).getSchoolClass();
//                String schoolDivision = classResult.get(i).getDivision();
//                String schoolClassId = classResult.get(i).getId();
//
//                System.out.println("schoolClass " + schoolClass + "  schoolDivision " + schoolDivision);
//                if (classIdDivObjHashMap.containsKey(schoolClass)) {
//                    classDivisionModelList = classIdDivObjHashMap.get(schoolClass);
//                    classDivisionModelList.add(0, new SchoolClassDivisionModel(schoolClassId, schoolClass, schoolDivision));
//                } else {
//                    classDivisionModelList = new ArrayList<>();
//                    classDivisionModelList.add(0, new SchoolClassDivisionModel(schoolClassId, schoolClass, schoolDivision));
//                    classIdDivObjHashMap.put(schoolClass, classDivisionModelList);
//                }
//            }
//
//        }
//    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

//            JsonObject jsonFetchSubject = new JsonObject();
//            jsonFetchSubject.addProperty("branchId", sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID));
//
//            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
//            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchTeachingData", jsonFetchSubject);
////        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("subjectFetchApi", jsonFetchSubject);
//
//            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
//                @Override
//                public void onFailure(Throwable exception) {
//                    resultFuture.setException(exception);
//                    System.out.println(" fetch teachingData Exception    " + exception);
//                }
//
//                @Override
//                public void onSuccess(JsonElement response) {
//                    resultFuture.set(response);
//                    System.out.println("fetch teachingData response    " + response);
//                    JsonArray jsonArray = response.getAsJsonArray();
//
//                    for (int loop = 0; loop < jsonArray.size(); loop++) {
//
//                        JsonObject object = jsonArray.get(loop).getAsJsonObject();
//                        String sqlLiteSchoolClassId = object.get("id").toString().replace("\"", "");
//                        String sqlLiteClass = object.get("class").toString().replace("\"", "");
//                        String sqlLiteDivision = object.get("division").toString().replace("\"", "");
//                        String sqlLiteSubject = object.get("subject").toString().replace("\"", "");
//
//
//                        databaseHandler.addClassToDatabase(sqlLiteSchoolClassId, sqlLiteClass, sqlLiteDivision, sqlLiteSubject);
//                    }
//
//                }
//            });

            try {
//                userDetails = new HashMap<>();
//                userDetails = sessionManager.getUserDetails();
//                List<School_Class> resultClass = mSchoolClassTable.where().field("Branch_id").eq(userDetails.get(SessionManager.KEY_BRANCH_ID)).select().execute().get();
//                //List<SchoolClassSubjectView> schoolClassSubjectViews = mSchoolSubjectTable1.select().execute().get();
//
//                System.out.println("resultClass size -------  " + resultClass.size());
                classSubjectData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    private void classSubjectData() {
        JsonObject jsonFetchSubject = new JsonObject();
        jsonFetchSubject.addProperty("branchId", sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchTeachingData", jsonFetchSubject);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" fetch teachingData Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("fetch teachingData response    " + response);
                JsonArray jsonArray = response.getAsJsonArray();

                for (int loop = 0; loop < jsonArray.size(); loop++) {

                    JsonObject object = jsonArray.get(loop).getAsJsonObject();
                    String sqlLiteSchoolClassId = object.get("id").toString().replace("\"", "");
                    String sqlLiteClass = object.get("class").toString().replace("\"", "");
                    String sqlLiteDivision = object.get("division").toString().replace("\"", "");
                    String sqlLiteSubject = object.get("subject").toString().replace("\"", "");

                    System.out.println("sqlLiteSchoolClassId " + sqlLiteSchoolClassId + " sqlLiteClass " + sqlLiteClass);
                    System.out.println("sqlLiteDivision " + sqlLiteDivision + " sqlLiteSubject " + sqlLiteSubject);

                    databaseHandler.addClassToDatabase(sqlLiteSchoolClassId, sqlLiteClass, sqlLiteDivision, sqlLiteSubject);
                }

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        menuItem = menu.findItem(R.id.action_notification);
        menuItem.setIcon(Converter.convertLayoutToImage(getActivity(), notificationCount, R.drawable.notification_purpeliconsecond));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                DashBoardConnectionCall();
                return true;

            case R.id.action_notification:
                Intent notificationIntent = new Intent(getActivity(), NotificationActivity.class);
                notificationCount = 0;
                startActivity(notificationIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DashBoardConnectionCall() {
        if (connectionDetector.isConnectingToInternet()) {
            progressDialog.show();
            DashBoardDataCall();
        } else {
            calendarNoDataTextView.setText(R.string.noDataAvailable);
            alertNoDataTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(getActivity(), getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            File myDir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Profile_Pic");
            String directory = myDir.getPath();
            System.out.println(" directory " + directory);
            if (sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME) != null) {
                String profile_file_name = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
                String string_file_path = directory + "/" + profile_file_name;
                System.out.println(" string_file_path " + string_file_path);
                File file_path = new File(string_file_path);
                Bitmap myBitmap = BitmapFactory.decodeFile(file_path.getAbsolutePath());
                userProfileImageView.setImageBitmap(myBitmap);
            } else {
                System.out.println("Set image-----------------else");
                userProfileImageView.setImageResource(R.drawable.ic_user);
                ;
            }
        } catch (NullPointerException e) {
            System.out.println(" NullPointerException " + e.getMessage());
            userProfileImageView.setImageResource(R.drawable.ic_user);
            ;
        }

        if (backPressRefresh) {
            DashBoardConnectionCall();
        }

        filter = new IntentFilter(getActivity().getPackageName() + ".USER_ACTION");
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMyBroadcastReceiver, filter);
        } catch (Exception e) {
            System.out.println(" registerReceiver " + e.getMessage());
        }
        try {
            int notificationCount = Integer.parseInt(sessionManager.getUserDetails().get(SessionManager.KEY_NOTIFICATION_COUNT));
            if (notificationCount == 0) {
                menuItem.setIcon(Converter.convertLayoutToImage(getActivity(), notificationCount, R.drawable.notification_purpeliconsecond));
            }
        } catch (Exception e) {
            System.out.println("Exception onStart " + e.getMessage());
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        backPressRefresh = true;
    }


    // Define the callback for what to do when data is received
    private BroadcastReceiver mMyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String task = intent.getStringExtra("UpdateCount");
            int notificationCount = Integer.parseInt(sessionManager.getUserDetails().get(SessionManager.KEY_NOTIFICATION_COUNT));
            menuItem.setIcon(Converter.convertLayoutToImage(getActivity(), notificationCount, R.drawable.notification_purpeliconsecond));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (back_pressed + 2000 > System.currentTimeMillis()) {
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Press Once again to exit", Toast.LENGTH_SHORT).show();
                        back_pressed = System.currentTimeMillis();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMyBroadcastReceiver);
    }

}