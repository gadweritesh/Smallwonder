package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.ClassSchedulePagerAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ClassScheduleListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ClassScheduleActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SessionManager sessionManager;
    private ConnectionDetector connectionDetector;
    private HashMap<String, String> userDetails;
    private int currentTab = 0;

    private Map<Integer, ClassScheduleListData> slotWiseHashMapData;
    public static HashMap<String, Map<Integer, ClassScheduleListData>> studentTimeTableHashMap;
    private String userRole;
    private String teacherName;
    private String teacherClassDivision;
    private ProgressDialog progressDialog;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);

        mClient = Singleton.Instance().mClientMethod(this);

        studentTimeTableHashMap = new HashMap<>();
        slotWiseHashMapData = new TreeMap<>();

        viewPager = (ViewPager) findViewById(R.id.class_schedule_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.class_schedule_tab_layout);
        TextView teacher_class_name_TextView = (TextView) findViewById(R.id.teacher_class_name_textView);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        if (userRole.equals("Teacher")) {
            teacher_class_name_TextView.setText("Class");
        } else {
            teacher_class_name_TextView.setText("Teacher");
        }

        tabLayout.addTab(tabLayout.newTab().setText("MON"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("TUE"), 1);
        tabLayout.addTab(tabLayout.newTab().setText("WED"), 2);
        tabLayout.addTab(tabLayout.newTab().setText("THU"), 3);
        tabLayout.addTab(tabLayout.newTab().setText("FRI"), 4);
        tabLayout.addTab(tabLayout.newTab().setText("SAT"), 5);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        if (connectionDetector.isConnectingToInternet()) {
            FetchClassScheduleData();
        } else {
            setTab();
        }

    }


    private void FetchClassScheduleData() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
            jsonObject.addProperty("teacher_Id", userDetails.get(SessionManager.KEY_TEACHER_ID));
            jsonObject.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
            jsonObject.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));

            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("classScheduleMainApi", jsonObject);

            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println("class_Schedule exception " + exception);
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ClassScheduleActivity.this)
                                    .setMessage(R.string.check_network)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FetchClassScheduleData();
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //onBackPressed();
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
                    System.out.println(" class_Schedule  response " + response);
                    ClassScheduleJsonParse(response);
                }
            });

        } catch (Exception e) {
            System.out.println("Exception in Class Schedule Fragment " + e.getMessage());
        }
    }

    public void ClassScheduleJsonParse(JsonElement response) {
        try {
            JsonArray classScheduleJsonArray = response.getAsJsonArray();
            if (classScheduleJsonArray.size() == 0) {
                progressDialog.dismiss();
                System.out.println("classScheduleJsonArray not received");
            } else {

                for (int loop_C = 0; loop_C < classScheduleJsonArray.size(); loop_C++) {
                    JsonObject jsonObjectForIteration = classScheduleJsonArray.get(loop_C).getAsJsonObject();
                    String scheduleDay = jsonObjectForIteration.get("scheduleDay").toString().replace("\"", "");
                    String slotStartTime = jsonObjectForIteration.get("slotStartTime").toString().replace("\"", "");
                    String slotEndTime = jsonObjectForIteration.get("slotEndTime").toString().replace("\"", "");
                    String slotType = jsonObjectForIteration.get("slotType").toString().replace("\"", "");
                    int slotNumber = Integer.parseInt(jsonObjectForIteration.get("slotNumber").toString().replace("\"", ""));
                    String slotSubject = jsonObjectForIteration.get("scheduleSubject").toString().replace("\"", "");
                    System.out.println(" slotSubject " + slotSubject);

                    if (userRole.equals("Student")) {
                        teacherName = jsonObjectForIteration.get("teacherName").toString().replace("\"", "");
                    }
                    if (userRole.equals("Teacher")) {
                        String teacher_class = jsonObjectForIteration.get("class").toString().replace("\"", "");
                        String teacher_division = jsonObjectForIteration.get("division").toString().replace("\"", "");
                        teacherClassDivision = teacher_class + "" + teacher_division;
                    }


                    SimpleDateFormat receivedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    SimpleDateFormat targetedDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    receivedDateFormat.setTimeZone(TimeZone.getDefault());
                    Date class_Schedule_StartTime = null;
                    Date class_Schedule_EndTime = null;
                    try {
                        class_Schedule_StartTime = receivedDateFormat.parse(slotStartTime);
                        class_Schedule_EndTime = receivedDateFormat.parse(slotEndTime);
                        slotStartTime = targetedDateFormat.format(class_Schedule_StartTime);
                        slotEndTime = targetedDateFormat.format(class_Schedule_EndTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (studentTimeTableHashMap.containsKey(scheduleDay)) {
                        System.out.println("@@@@@@@@@@@@@@@@@@@ if " + scheduleDay + " " + slotSubject);
                        slotWiseHashMapData = new TreeMap<>();
                        slotWiseHashMapData = studentTimeTableHashMap.get(scheduleDay);
                        slotWiseHashMapData.put(slotNumber, new ClassScheduleListData(class_Schedule_StartTime, class_Schedule_EndTime, slotSubject, teacherName, slotType, teacherClassDivision, false));
                        studentTimeTableHashMap.put(scheduleDay, slotWiseHashMapData);
                    } else {
                        System.out.println("@@@@@@@@@@@@@@@@@@@ else  " + scheduleDay + " " + slotSubject);
                        slotWiseHashMapData = new TreeMap<>();
                        slotWiseHashMapData.put(slotNumber, new ClassScheduleListData(class_Schedule_StartTime, class_Schedule_EndTime, slotSubject, teacherName, slotType, teacherClassDivision, false));
                        studentTimeTableHashMap.put(scheduleDay, slotWiseHashMapData);
                    }
                }
            }
            progressDialog.dismiss();
            setTab();
        } catch (Exception e) {
            System.out.println("Exception in Class Schedule Parsing " + e.getMessage());
        }
    }

    public void setTab() {
        FragmentManager manager = getSupportFragmentManager();
        ClassSchedulePagerAdapter adapter = new ClassSchedulePagerAdapter(manager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date current_date = new Date();
        String dayOfTheWeek = sdf.format(current_date);
        switch (dayOfTheWeek) {
            case "Monday":
                currentTab = 0;
                break;
            case "Tuesday":
                currentTab = 1;
                break;
            case "Wednesday":
                currentTab = 2;
                break;
            case "Thursday":
                currentTab = 3;
                break;
            case "Friday":
                currentTab = 4;
                break;
            case "Saturday":
                currentTab = 5;
                break;
            default:
                break;

        }
        System.out.println("currentTab : " + currentTab);
        viewPager.setCurrentItem(currentTab);
    }
}
