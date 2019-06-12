package com.relecotech.androidsparsh_smallwonder;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import com.relecotech.androidsparsh_smallwonder.activities.AchievementActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AdminAlertListActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AdminAssignmentListActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AlertListActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AlertPost;
import com.relecotech.androidsparsh_smallwonder.activities.AssignmentListActivity;
import com.relecotech.androidsparsh_smallwonder.activities.AssignmentPost;
import com.relecotech.androidsparsh_smallwonder.activities.AttendanceStudentIndividual;
import com.relecotech.androidsparsh_smallwonder.activities.AttendanceTeacher;
import com.relecotech.androidsparsh_smallwonder.activities.BusRouteActivity;
import com.relecotech.androidsparsh_smallwonder.activities.ExamReportActivity;
import com.relecotech.androidsparsh_smallwonder.activities.ExamTimeTableActivity;
import com.relecotech.androidsparsh_smallwonder.activities.FeesActivity;
import com.relecotech.androidsparsh_smallwonder.activities.LoginActivity;
import com.relecotech.androidsparsh_smallwonder.activities.SettingActivity;
import com.relecotech.androidsparsh_smallwonder.activities.SyllabusActivity;
import com.relecotech.androidsparsh_smallwonder.activities.VideoGallery;
import com.relecotech.androidsparsh_smallwonder.fragments.DashboardAdmin;
import com.relecotech.androidsparsh_smallwonder.fragments.DashboardStudent;
import com.relecotech.androidsparsh_smallwonder.fragments.DashboardTeacher;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import static com.relecotech.androidsparsh_smallwonder.utils.SessionManager.KEY_NAME_LIST;

import static com.relecotech.androidsparsh_smallwonder.utils.SessionManager.KEY_NAME_LIST2;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private HashMap<String, String> user;
    private String deviceInformation;
    public static String sharedPrefValue;
    String userRole, studentId, schoolClass, branchId, teacherId;
    private ConnectionDetector connectionDetector;
    private Spinner titleNav;
    public MobileServiceClient mClient;
    private DatabaseHandler databaseHandler;
    private int notification_id = 1;
    public static final String NOTIFY_ALERT = "com.relecotech.androidsparsh.alert";
    public static final String NOTIFY_ASSIGNMENT = "com.relecotech.androidsparsh.assignment";
    public static final String NOTIFY_ATTENDANCE = "com.relecotech.androidsparsh.attendance";
    public static final String NOTIFY_CLOSE = "com.relecotech.androidsparsh.close";
    String ADMOB_APP_ID = "ca-app-pub-4696884248109648~1839716272";
    private RemoteViews remoteViews;
    ArrayList<String> studentList,studentList2;
    String name,name2;

    ArrayList<String> userNameList,copydata;
    private HashMap<String, String> userDetails;
    String activity = null;
    SessionManager sessionManager1 = null;
    String lastName,lastName2;
    String Temp1,Temp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initializeSSLContext(this);
        mClient = Singleton.Instance().mClientMethod(this);
        MobileAds.initialize(this , ADMOB_APP_ID);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        databaseHandler = new DatabaseHandler(this);
        Intent getNewUserIntent = getIntent();
        activity = getNewUserIntent.getStringExtra("activity");
        lastName = getNewUserIntent.getStringExtra("LastName");
        lastName2 = getNewUserIntent.getStringExtra("LastName2");
        System.out.println("lastName "+ lastName);
        System.out.println("lastName2 "+ lastName2);
        name = sessionManager.getUserDetails().get(KEY_NAME_LIST);
        System.out.println("name " + name);

        //////////////////////////////////////////////////////////////
        Temp1 = name.substring(name.indexOf(" ")+1,name.length()-2);
        System.out.println("Temp1 " + Temp1);

        name2 = sessionManager.getUserDetails().get(KEY_NAME_LIST2);
        System.out.println("name2 " + name2);

        if(name2 != null) {
            Temp2 = name2.substring(name2.indexOf(" ") + 1, name2.length() - 2);
            System.out.println("Temp2 " + Temp2);
        }
       /* String name = sessionManager.getUserDetails().get(SessionManager.KEY_NAME_LIST);
        System.out.println("name " + name);*/

        Gson googleJson = new Gson();
        studentList = new ArrayList<String>();
        studentList2 = new ArrayList<String>();
        userNameList = new ArrayList<String>();
        copydata = new ArrayList<String>();
        studentList = googleJson.fromJson(name, ArrayList.class);
        copydata.addAll(studentList);
        if(!name.equals(name2)) {
            studentList2 = googleJson.fromJson(name2,ArrayList.class);
        }

        System.out.println("javaArrayListFromGSON " + studentList);
        System.out.println("javaArrayListFromGSON " + studentList2);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); // <----- HERE
        View header = navigationView.getHeaderView(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        titleNav = (Spinner) header.findViewById(R.id.nav_Title);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0f);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(true);
        toolbar.setNavigationIcon(R.drawable.menuiconfill);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.purplelight));

        ArrayAdapter<String> studentSelector = new ArrayAdapter<String>(getApplicationContext(), R.layout.name_spinner_listitem, studentList);
        studentSelector.setDropDownViewResource(R.layout.name_spinner_dropdown_listitem);
        titleNav.setAdapter(studentSelector);
        titleNav.setSelection(0);

        System.out.println("titleNav.getSelectedItem().toString() " + titleNav.getSelectedItem().toString());
        sharedPrefValue = titleNav.getSelectedItem().toString();
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        String stuName = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);

        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);
        System.out.println("USERROLE " + userRole);
        if(userRole.equals("Student") && activity != null)
        {

            System.out.println("ACTIVITY "+activity);

            if(activity.equals("User")) {
                System.out.println("lastName "+ lastName);
                System.out.println("lastName2 "+ lastName2);
                System.out.println("STUDENTLIST " + studentList);
                userNameList = (ArrayList<String>) getIntent().getSerializableExtra("userNameList");
                System.out.println("USERLIST " + userNameList);

                if (studentList.containsAll(userNameList))
                {
                    FancyToast.makeText(MainActivity.this, "Student Already Login", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    // FancyToast.setGravity(Gravity.CENTER, 0, 0);
                    //FancyToast.show();
                    studentList.add("Add Account");
                }
                else {
                    studentList.addAll(userNameList);
                }

                System.out.println("Last Name are not same ");
                System.out.println("lastName "+ lastName);
                System.out.println("lastName2 "+ lastName2);
                //FancyToast.makeText(MainActivity.this, "! Sibling can not be added..Please check username", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                // studentList.add("Add Account");
            }
            else if(activity.equals("Launch"))
            {
                System.out.println("Student2list Size:  ");
                if(studentList2 == null)
                {
                    studentList.add("Add Account");
                }
                else
                {
                    studentList.addAll(studentList2);
                }

            }
            else if(activity.equals("Login") && studentList2 == null)
            {
                studentList.add("Add Account");
            }
            System.out.println("STUDENTLIST " + studentList);
            studentSelector = new ArrayAdapter<String>(getApplicationContext(), R.layout.name_spinner_listitem, studentList);
            studentSelector.setDropDownViewResource(R.layout.name_spinner_dropdown_listitem);
            titleNav.setAdapter(studentSelector);

            int count = studentList.size();
            System.out.println(" : "+count);
            if(activity.equals("User") && lastName.equals(lastName2)) {
                count = count-1;
                titleNav.setSelection(count);
            }
            else {
                titleNav.setSelection(0);
            }
        }

        if (!(userRole == null)) {
//            displayFragment(R.id.nav_dashboard);
//            DashBoardDataCall();
            MenuItem item1 = navigationView.getMenu().findItem(R.id.nav_portal_link);
            MenuItem item2 = navigationView.getMenu().findItem(R.id.nav_fees);
            MenuItem item3 = navigationView.getMenu().findItem(R.id.nav_exam);
            MenuItem item4 = navigationView.getMenu().findItem(R.id.nav_report);
            MenuItem item5 = navigationView.getMenu().findItem(R.id.nav_bus);
            MenuItem item8 = navigationView.getMenu().findItem(R.id.navigation_achievement);

//            item5.setVisible(false);
//            MenuItem item8 = navigationView.getMenu().findItem(R.id.nav_website);
//            item8.setVisible(false);
//            MenuItem item9 = navigationView.getMenu().findItem(R.id.nav_video);
//            item9.setVisible(false);
            if (userRole.equals("Teacher")) {
                item1.setVisible(false);
                item2.setVisible(false);
                item3.setVisible(false);
                item4.setVisible(false);
            } else if (userRole.equals("Student")) {
                item1.setVisible(false);
            } else {
                item2.setVisible(false);
                item3.setVisible(false);
                item4.setVisible(false);
                MenuItem item6 = navigationView.getMenu().findItem(R.id.nav_syllabus);
                item6.setVisible(false);
                MenuItem item7 = navigationView.getMenu().findItem(R.id.nav_feedback);
                item7.setVisible(false);
                MenuItem item9= navigationView.getMenu().findItem(R.id.nav_achievement);
                item6.setVisible(false);
            }
        }


        if (connectionDetector.isConnectingToInternet()) {
            new ActiveLoginAsyncTask().execute();
//            ActiveLogin();
//            DashBoardDataCall();
        } else {
            FancyToast.makeText(MainActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);

        titleNav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                System.out.println("Selected Item: "+selectedItem);
                if(selectedItem.equals("Add Account"))
                {
                    Intent newAccount= new Intent(getApplicationContext(), LoginActivity.class);
                    newAccount.putExtra("Name","NewUser");
                    newAccount.putExtra("LastName",Temp1);
                    startActivity(newAccount);
                    titleNav.setSelection(0);
                }
                else {
                    sharedPrefValue = adapterView.getItemAtPosition(i).toString();
                    System.out.println("sharedPrefValue Item: "+sharedPrefValue);
                    sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
//                 String stuName = sessionManager.getUserDetails().get(SessionManager.KEY_USERDATA);
//                System.out.println(":stuName 2" + stuName);
                    displayFragment(true, R.id.nav_dashboard, sharedPrefValue);
                    click();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        if (getIntent().hasExtra("action")) {
            System.out.println(" INSIDE hasExtra ");
            String action = getIntent().getStringExtra("action");
            System.out.println(" action action " + action);
            Intent intentSend;
            if (action.equals(NOTIFY_ALERT)) {
                if (userRole.equals("Student")) {
                    intentSend = new Intent(getApplicationContext(), AlertListActivity.class);
                } else if (userRole.equals("Administrator")) {
                    intentSend = new Intent(getApplicationContext(), AdminAlertListActivity.class);
                } else {
                    intentSend = new Intent(getApplicationContext(), AlertPost.class);
                }
                startActivity(intentSend);
            } else if (action.equals(NOTIFY_ASSIGNMENT)) {
                if (userRole.equals("Student")) {
                    intentSend = new Intent(getApplicationContext(), AssignmentListActivity.class);
                } else if (userRole.equals("Administrator")) {
                    intentSend = new Intent(getApplicationContext(), AdminAssignmentListActivity.class);
                } else {
                    intentSend = new Intent(getApplicationContext(), AssignmentPost.class);
                }
                startActivity(intentSend);
            } else if (action.equals(NOTIFY_ATTENDANCE)) {
                if (userRole.equals("Student")) {
                    intentSend = new Intent(getApplicationContext(), AttendanceStudentIndividual.class);
                } else {
                    intentSend = new Intent(getApplicationContext(), AttendanceTeacher.class);
                }
                startActivity(intentSend);
            }
        }


        Intent alertIntent = new Intent(NOTIFY_ALERT);
        PendingIntent pendingAlertIntent = PendingIntent.getBroadcast(getApplicationContext(), notification_id, alertIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.notify_alert, pendingAlertIntent);

        Intent assignmentIntent = new Intent(NOTIFY_ASSIGNMENT);
        PendingIntent pendingAssignmentIntent = PendingIntent.getBroadcast(getApplicationContext(), notification_id, assignmentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notify_assignment, pendingAssignmentIntent);

        Intent attendanceIntent = new Intent(NOTIFY_ATTENDANCE);
        PendingIntent pendingAttendanceIntent = PendingIntent.getBroadcast(getApplicationContext(), notification_id, attendanceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notify_attendance, pendingAttendanceIntent);

        Intent closeIntent = new Intent(NOTIFY_CLOSE);
        closeIntent.putExtra("id", notification_id);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(getApplicationContext(), notification_id, closeIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.notify_close, pendingCloseIntent);

    }


    private void click() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent notification_intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notification_intent, 0);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        notificationManager.notify(notification_id, builder.build());
    }


    public void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

//    private void DashBoardDataCall() {
//
////        name = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
//        branchId = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID);
//        studentId = sessionManager.getUserDetails().get(SessionManager.KEY_STUDENT_ID);
//        teacherId = sessionManager.getUserDetails().get(SessionManager.KEY_TEACHER_ID);
//        schoolClass = sessionManager.getUserDetails().get(SessionManager.KEY_SCHOOL_CLASS_ID);
//        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);
//
//        JsonObject jsonDashBoardCall = new JsonObject();
//        jsonDashBoardCall.addProperty("userRole", userRole);
//        jsonDashBoardCall.addProperty("studentId", studentId);
//        jsonDashBoardCall.addProperty("schoolClassId", schoolClass);
//        jsonDashBoardCall.addProperty("branchId", branchId);
//        jsonDashBoardCall.addProperty("teacherId", teacherId);
//
//        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
//        ListenableFuture<JsonElement> serviceFilterFuture;
//        if (userRole.equals("Student")) {
//            serviceFilterFuture = LauncherActivity.mobileServiceClient.invokeApi("fetchStudentDashboardData", jsonDashBoardCall);
//        } else if (userRole.equals("Teacher")) {
//            serviceFilterFuture = LauncherActivity.mobileServiceClient.invokeApi("fetchTeacherDashboardData", jsonDashBoardCall);
//        } else {
//            serviceFilterFuture = LauncherActivity.mobileServiceClient.invokeApi("fetchAdminDashboardData", jsonDashBoardCall);
//        }
//
//        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
//            @Override
//            public void onFailure(Throwable exception) {
//                resultFuture.setException(exception);
//                System.out.println("exception    " + exception.getMessage());
//            }
//
//            @Override
//            public void onSuccess(JsonElement response) {
//                resultFuture.set(response);
//                System.out.println("response    " + response.toString());
//                displayFragment(R.id.nav_dashboard, response.toString());
//            }
//        });
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        displayFragment(false, id, null);
        return true;
    }


    private void displayFragment(boolean value, int viewId, String name) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        if (!(userRole == null)) {

            switch (viewId) {
                case R.id.nav_dashboard:
                    Bundle bundle = new Bundle();
                    if (value) {
                        bundle.putString("dashboardData", name);
                    } else {
                        bundle.putString("dashboardData", titleNav.getSelectedItem().toString());
                    }

                    if (userRole.equals("Teacher")) {
                        fragment = new DashboardTeacher();
                    } else if (userRole.equals("Student")) {
                        fragment = new DashboardStudent();
                    } else {
                        fragment = new DashboardAdmin();
                    }
                    fragment.setArguments(bundle);
                    title = "Dashboard";
                    break;

                case R.id.nav_exam:
                    title = "Dashboard";
                    Intent examIntent = new Intent(MainActivity.this, ExamTimeTableActivity.class);
                    startActivity(examIntent);
                    break;

                case R.id.nav_report:
                    Intent reportIntent = new Intent(MainActivity.this, ExamReportActivity.class);
                    startActivity(reportIntent);
                    break;
                case R.id.nav_achievement:
                    Intent navachivement = new Intent(MainActivity.this, AchievementActivity.class);
                    startActivity(navachivement);
                    break;

                case R.id.nav_syllabus:
                    title = "Dashboard";
                    Intent syllabusIntent = new Intent(MainActivity.this, SyllabusActivity.class);
                    startActivity(syllabusIntent);
                    break;

                case R.id.nav_bus:
                    title = "Dashboard";
                    Intent parentIntent = new Intent(MainActivity.this, BusRouteActivity.class);
                    startActivity(parentIntent);
                    break;

                case R.id.nav_video:
                    Intent videoIntent = new Intent(MainActivity.this, VideoGallery.class);
                    startActivity(videoIntent);
                    break;

                case R.id.nav_fees:
                    title = "Dashboard";
                    Intent feesIntent = new Intent(MainActivity.this, FeesActivity.class);
                    startActivity(feesIntent);
                    break;

                case R.id.nav_rate:
                    title = "Dashboard";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    break;

                case R.id.nav_share:
                    title = "Dashboard";
                    try {
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Mobile App");
                        String sAux = "Digital the way of communication between Parent and Teachers.\n And helps you to track your child progress.\n\n";
                        sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName();
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sAux);
//                        startActivity(Intent.createChooser(sendIntent, "choose one"));
                        startActivity(sendIntent);
                    } catch (Exception e) {
                        System.out.println(" Share App exception " + e.getMessage());
                    }
                    break;

                case R.id.nav_feedback:
                    title = "Dashboard";
                    break;

                case R.id.nav_website:
                    title = "Dashboard";
                    try {
                        //load link from database
                        Uri uri = Uri.parse("http://rukminschoolnagpur.com/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        System.out.println("uri Exception");
                    }
                    break;

                case R.id.nav_portal_link:
                    title = "Dashboard";
                    try {
                        //load link from database
                        Uri uri = Uri.parse("http://sp.relecotech.com/sparsh/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        System.out.println("uri Exception");
                    }
                    break;

                case R.id.nav_setting:
                    title = "Dashboard";
                    Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(settingIntent);
                    break;

                case R.id.nav_exit:
                   /* title = "Dashboard";
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.openDrawer(Gravity.LEFT);
                        }
                    });
                    alertDialogBuilder.setMessage("Confirm Logout ?");
                    alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user = new HashMap<String, String>();
                            sessionManager = new SessionManager(getApplicationContext());
                            SharedPreferences pref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.commit();
                            user.clear();

                            databaseHandler.deleteAllTables();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    });
                    alertDialogBuilder.create().show();

                    break;
                    */
                    title = "Dashboard";
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.openDrawer(Gravity.LEFT);
                        }
                    });
                    alertDialogBuilder.setMessage("Confirm Logout ?");
                    alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user = new HashMap<String, String>();
                            sessionManager = new SessionManager(getApplicationContext());
                            SharedPreferences pref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
                            final SharedPreferences.Editor editor = pref.edit();

                            /////////////////////////////////////////////////////
                            if(userRole.equals("Student")) {
                                if(studentList.size() > 1 && !studentList.contains("Add Account"))
                                {
                                    String selectedItem = String.valueOf(titleNav.getSelectedItem());
                                    System.out.println("SELECTED ITEM : "+selectedItem);
                                    editor.clear();
                                    editor.commit();
                                    user.clear();

                                    databaseHandler.deleteAllTables();
                                    sessionManager1 = new SessionManager(getApplicationContext());

                                    if (selectedItem.equals(studentList.get(1))) {
                                        sessionManager1.createList(copydata);
                                        studentList.clear();
                                        studentList2.clear();
                                    }
                                    else if(selectedItem.equals(studentList.get(0))) {
                                        sessionManager1.createList(studentList2);
                                        studentList.clear();
                                        studentList2.clear();
                                    }
                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    i.putExtra("activity","Launch");
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                                else
                                {
                                    editor.clear();
                                    editor.commit();
                                    user.clear();

                                    databaseHandler.deleteAllTables();
                                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                    i.putExtra("Name","Login");
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            }
                            else {
                                editor.clear();
                                editor.commit();
                                user.clear();

                                databaseHandler.deleteAllTables();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.putExtra("Name","Login");
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }

                        }
                    });
                    alertDialogBuilder.create().show();

                    break;
            }
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @SuppressLint("StaticFieldLeak")
    private class ActiveLoginAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String getAppVersionName = packageInfo.versionName;
                deviceInformation = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName() + " " + "v" + getAppVersionName;
            } catch (Exception e) {
                System.out.println("Device Information Exception " + e.getMessage());
            }

            Calendar calender = Calendar.getInstance();
            long lastLoginTime = calender.getTimeInMillis();

            JsonObject activeStatusJsonObjectParam = new JsonObject();
            activeStatusJsonObjectParam.addProperty("Reply", sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID));
            activeStatusJsonObjectParam.addProperty("lastLoginTime", lastLoginTime);
            activeStatusJsonObjectParam.addProperty("deviceInformation", deviceInformation);

            System.out.println("activeStatusJsonObjectParam---------------- " + activeStatusJsonObjectParam);
            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("activeUserApi", activeStatusJsonObjectParam);
            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println("Api Call --------- unSuccessFul" + exception);
                }

                @Override
                public void onSuccess(JsonElement response) {
                    resultFuture.set(response);
                    System.out.println("Api Call --------- SuccessFul");
                }
            });

            return null;

        }
    }

//    private void ActiveLogin() {
//
//        try {
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            String getAppVersionName = packageInfo.versionName;
//            deviceInformation = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName() + " " + "v" + getAppVersionName;
//        } catch (Exception e) {
//            System.out.println("Device Information Exception " + e.getMessage());
//        }
//
//        Calendar calender = Calendar.getInstance();
//        long lastLoginTime = calender.getTimeInMillis();
//
//        JsonObject activeStatusJsonObjectParam = new JsonObject();
//        activeStatusJsonObjectParam.addProperty("Reply", sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID));
//        activeStatusJsonObjectParam.addProperty("lastLoginTime", lastLoginTime);
//        activeStatusJsonObjectParam.addProperty("deviceInformation", deviceInformation);
//
//        System.out.println("activeStatusJsonObjectParam---------------- " + activeStatusJsonObjectParam);
//        System.out.println("user.get(SessionManager.KEY_USER_ID)------------------ " + sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID));
//        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
//        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("activeUserApi", activeStatusJsonObjectParam);
//        System.out.println("AddActiveUserStatus---API CALLED-----------  ");
//        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
//            @Override
//            public void onFailure(Throwable exception) {
//                resultFuture.setException(exception);
//                System.out.println("Api Call --------- unSuccesFul" + exception);
//            }
//
//            @Override
//            public void onSuccess(JsonElement response) {
//                resultFuture.set(response);
//                System.out.println("Api Call --------- SuccesFul");
//            }
//        });
//    }


}
