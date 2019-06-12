package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.AttendanceMarkAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.School_Config;
import com.relecotech.androidsparsh_smallwonder.models.AbsentStudentNameNumber;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceMarkListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SendSmsManager;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;


/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceMark extends AppCompatActivity {
    int presentCount = 0;
    int j = 0;
    long postingDate;

    ListView attendanceListView;
    ArrayList<AttendanceMarkListData> attendancelist;
    CheckBox checkAll;
    TextView presentCounterTextView;
    AttendanceMarkAdapter markStudentadapter;
    AttendanceMarkListData selectedAttendanceMarkListData;
    private ConnectionDetector connectionDetector;
    private String schoolClassIdToFetchStudentList;
    private String student_Id;
    private String student_firstName;
    private String student_lastName;
    private String student_rollNo;
    private JsonArray getJsonListResponse;
    private Calendar calendar;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    private JsonArray jsonArrayForNewAttendanceMark;
    private boolean dataReceivedStatus;
    private String markedDate;
    private JsonObject jsonObjectForStudentList;
    private TextView noDataTextView;
    private Button draftBtn;
    private DatabaseHandler dbHandler;
    private String attendanceDateString;
    private long attendanceDate;
    private Cursor studentNamesDraftCursor;
    private Cursor studentNamesFreshCursor;
    private String student_phoneNo;
    private ProgressDialog progressDialog;
    private ProgressBar attendanceMarkProgressBar;
    private MobileServiceTable<School_Config> mSchoolConfigTable;
    private String smsUrl;
    private String onOrOffValue;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_mark);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        mSchoolConfigTable = mClient.getTable(School_Config.class);

        //flag to check weather data is received or not
        dataReceivedStatus = false;

        //get data from attendance teacher fragment through intent
        schoolClassIdToFetchStudentList = getIntent().getStringExtra("schoolClassId");
        String task = getIntent().getStringExtra("Task");
        String attendanceClass = getIntent().getStringExtra("class");
        String attendanceDivision = getIntent().getStringExtra("division");

        attendanceMarkProgressBar = (ProgressBar) findViewById(R.id.attendanceMarkProgressBar);
        noDataTextView = (TextView) findViewById(R.id.noDataAttendanceMarkTextView);
//        markAttendanceLayout = (RelativeLayout) findViewById(R.id.attendanceMarkLayout);
        checkAll = (CheckBox) findViewById(R.id.markAllCheckBox);

        dbHandler = new DatabaseHandler(this);

        calendar = Calendar.getInstance();
        postingDate = calendar.getTimeInMillis();
        attendanceDate = calendar.getTimeInMillis();
        //convert long to string date
        Date attendancedate = new Date(attendanceDate);
        DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        attendanceDateString = format.format(attendancedate);

        presentCounterTextView = (TextView) findViewById(R.id.presentCountTextView);
        draftBtn = (Button) findViewById(R.id.draftButton);
        attendanceListView = (ListView) findViewById(R.id.markAttendanceListView);
        attendancelist = new ArrayList<>();

        new CheckSmsOnOff().execute();

        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataReceivedStatus) {
                    if (!checkAll.isChecked()) {
                        for (j = 0; j < attendanceListView.getCount(); j++) {
                            selectedAttendanceMarkListData = attendancelist.get(j);
                            selectedAttendanceMarkListData.setPresentStatus("A");
                        }
                        presentCount = 0;
                    } else {
                        presentCount = 0;
                        for (j = 0; j < attendanceListView.getCount(); j++) {
                            selectedAttendanceMarkListData = attendancelist.get(j);
                            selectedAttendanceMarkListData.setPresentStatus("P");
                            presentCount++;
                        }
                    }
                    int index = attendanceListView.getFirstVisiblePosition();
                    View v = attendanceListView.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();

                    markStudentadapter.notifyDataSetChanged();
                    presentCounterTextView.setText(Integer.toString(presentCount));
                    attendanceListView.setSelectionFromTop(index, top);
                }
            }

        });

        attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                markIndividualAttendance(i);
            }
        });


        studentNamesDraftCursor = dbHandler.getStudentNames(schoolClassIdToFetchStudentList, attendanceDateString);
        System.out.println("studentNamesDraftCursor.getCount() : " + studentNamesDraftCursor.getCount());
        if (studentNamesDraftCursor.getCount() == 0) {

//            studentNamesFreshCursor = dbHandler.getStudentNames(schoolClassIdToFetchStudentList);
//            System.out.println("studentNamesFreshCursor.getCount() : " + studentNamesFreshCursor.getCount());
//            if (studentNamesFreshCursor.getCount() == 0) {

            if (connectionDetector.isConnectingToInternet()) {

                //Rest calling done here after connection check
//                    if (task.equals("MarkAttendance")) {
                //fetch student list for new attendance mark
                FetchStudentList();
//                    } else {
//                        //fetch student list for review attendance
//                    }
            } else {
                FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
//            } else {
//                //set student list from cursor
//                presentCount = 0;
//
//                studentNamesFreshCursor.moveToFirst();
//                do {
//                    attendancelist.add(new AttendanceMarkListData(studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("attendanceStudentId")),
//                            (studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("attendanceStudentRollNo"))),
//                            (studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("attendanceStudentFullName"))),
//                            "P", (studentNamesFreshCursor.getString(studentNamesFreshCursor.getColumnIndex("attendanceStudentPhone")))));
//
//                    presentCount++;
//                } while (studentNamesFreshCursor.moveToNext());
//
//                presentCounterTextView.setText(Integer.toString(presentCount));
//                markStudentadapter = new AttendanceMarkAdapter(getApplicationContext(), attendancelist);
//                attendanceListView.setAdapter(markStudentadapter);
//                dataReceivedStatus = true;
//            }
        } else {
            //set student list from cursor
            presentCount = 0;

            System.out.println(" studentNamesDraftCursor " + studentNamesDraftCursor);
            studentNamesDraftCursor.moveToFirst();
            do {
                attendancelist.add(new AttendanceMarkListData(studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStudentId")),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStudentRollNo"))),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStudentFirstName"))),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStudentFullName"))),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStatus"))),
                        (studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStudentPhone")))));

                if ((studentNamesDraftCursor.getString(studentNamesDraftCursor.getColumnIndex("attendanceStatus"))).equals("P")) {
                    System.out.println("INSIDE OFFLINE PRESENT");
                    presentCount++;
                }
            } while (studentNamesDraftCursor.moveToNext());
            System.out.println("OFFLINE presentCount : " + presentCount);
            presentCounterTextView.setText(Integer.toString(presentCount));

            markStudentadapter = new AttendanceMarkAdapter(getApplicationContext(), attendancelist);
            attendanceListView.setAdapter(markStudentadapter);
            dataReceivedStatus = true;

        }


        draftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AttendanceMark.this)
                        .setTitle("Save draft")
                        .setMessage("" + presentCount + " Student present")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Save draft", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                System.out.println(" attendancelist  " + attendancelist.size());
                                System.out.println(" schoolClassIdToFetchStudentList  " + schoolClassIdToFetchStudentList);
                                System.out.println(" attendanceDateString  " + attendanceDateString);
                                //deleted older draft and then insert new draft
                                dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                                //add student list to database for 1st time with draft status as 1(true)
                                dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, attendanceDateString, 1);
                                printAllData();
                                finish();
                            }
                        })
                        .create().show();
            }
        });

        attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                markIndividualAttendance(i);
            }
        });
    }

    private void printAllData() {
        Cursor allDataCursor = dbHandler.getAllAttendanceData();
        if (allDataCursor.moveToFirst()) {
            do {
                System.out.println("------- " + allDataCursor.getString(0)
                        + " " + allDataCursor.getString(1)
                        + " " + allDataCursor.getString(2)
                        + " " + allDataCursor.getString(3)
                        + " " + allDataCursor.getString(4)
                        + " " + allDataCursor.getString(5)
                        + " " + allDataCursor.getString(6)
                        + " " + allDataCursor.getString(7));
            } while (allDataCursor.moveToNext());
        }
    }

    private void markAttendanceToAzure() {

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        final List<AbsentStudentNameNumber> getSchoolAbsentStudentList = new ArrayList<>();
        //convert list data to json Array
        AttendanceMarkListData attendanceMarkListDataForIteration;
        JsonObject jsonObjectForIteration;
        jsonArrayForNewAttendanceMark = new JsonArray();

        //loop to convert list data to jsonArray
        for (int i = 0; i < attendancelist.size(); i++) {
            attendanceMarkListDataForIteration = attendancelist.get(i);

            jsonObjectForIteration = new JsonObject();
            jsonObjectForIteration.addProperty("Student_Name", attendanceMarkListDataForIteration.getFirstName());
            jsonObjectForIteration.addProperty("Student_id", attendanceMarkListDataForIteration.getStudentId());
            jsonObjectForIteration.addProperty("AttendanceDate", postingDate);
            jsonObjectForIteration.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_ID));
            jsonObjectForIteration.addProperty("Active", 1);
            jsonObjectForIteration.addProperty("Status", attendanceMarkListDataForIteration.getPresentStatus());
            jsonObjectForIteration.addProperty("AttendanceCategory", "School");
            jsonObjectForIteration.addProperty("SchoolClassId", schoolClassIdToFetchStudentList);
            jsonObjectForIteration.addProperty("Branch_id", userDetails.get(SessionManager.KEY_BRANCH_ID));
            String submittedByTeacherName = userDetails.get(SessionManager.KEY_NAME);
            jsonObjectForIteration.addProperty("SubmittedByTeacherName", submittedByTeacherName);


            if (attendanceMarkListDataForIteration.getPresentStatus() == "A" || attendanceMarkListDataForIteration.getPresentStatus().equals("A")) {

                getSchoolAbsentStudentList.add(new AbsentStudentNameNumber(attendanceMarkListDataForIteration.getFullName(), attendanceMarkListDataForIteration.getStudentPhone()));
                System.out.println("Absent Student added in List-" + attendanceMarkListDataForIteration.getStudentPhone());

            } else {
                System.out.println("Student Present.");
            }

            jsonArrayForNewAttendanceMark.add(jsonObjectForIteration);
        }


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("attendanceNotificationApi", jsonArrayForNewAttendanceMark);

        //creating current date to save into shared pref when attendance is marked
        markedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        System.out.println("markedDate : " + markedDate);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Attendance Mark exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        //assignmentProgressDialog.dismiss();
                        progressDialog.dismiss();
                        new android.app.AlertDialog.Builder(AttendanceMark.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        markAttendanceToAzure();
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
                System.out.println(" ATTENDANCE MARK API  response    " + response);

                progressDialog.dismiss();

                if (response.toString().equals("true")) {
                    // new attendance has ben marked successfully
                    sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, markedDate);
                    try {
                        System.out.println("onOrOffValue " + onOrOffValue);
                        if (onOrOffValue.equalsIgnoreCase("on")) {
                            System.out.println("if SMS");
                            SendSmsManager sendSmsManager = new SendSmsManager(AttendanceMark.this);
                            sendSmsManager.SendSmsToAbsentStudents(smsUrl, getSchoolAbsentStudentList);
                        } else {
                            if (onOrOffValue.equalsIgnoreCase("off")) {
                                System.out.println("else if SMS");
                                FancyToast.makeText(AttendanceMark.this, "Sms Service is Off.", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                            } else {
                                System.out.println("ELSE ELSE SMS");
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println(" onOrOffValue " + e.getMessage());
                    }

                    //Change By Yogesh On 18 July 17
//                    try {
//                        if (sessionManager.getSharedPrefItem(SessionManager.SMS_VALUE).equals("1")) {
////                            Send sms to absent Student.
//                            SendSmsManager sendSmsManager = new SendSmsManager(AttendanceMark.this, getSchoolAbsentStudentList, null, null);
//                            sendSmsManager.SendSmsToAbsentStudents();
//                        }
//                    } catch (Exception e) {
//                        System.out.println("SMS service Not Available");
//                    }

                    //deleted older draft
                    dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                    new AlertDialog.Builder(AttendanceMark.this)
                            .setTitle("Attendance Marked.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    printAllData();
                                    finish();
                                }
                            }).show();

                } else if (response.toString().equals("false")) {

                    //show dialog stating today's attendance already marked
                    System.out.println("attendance exist");
                    //deleted older draft
                    dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);

                    sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, markedDate);
                    new AlertDialog.Builder(AttendanceMark.this)
                            .setTitle("Attendance already marked..")
                            .setCancelable(false)
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    printAllData();
                                    finish();
                                }
                            }).show();
                } else {
                    //something wrong has happened
                    new AlertDialog.Builder(AttendanceMark.this)
                            .setTitle("Error")
                            .setCancelable(false)
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    markAttendanceToAzure();
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setNeutralButton("Save Draft", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //add student list to database for 1st time with draft status as 1(true)
                            dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            printAllData();
                        }
                    }).show();
                }

            }
        });

    }

    private void markIndividualAttendance(int position) {

        selectedAttendanceMarkListData = attendancelist.get(position);
        System.out.println(" selectedAttendanceMarkListData " + selectedAttendanceMarkListData);

        String checkpresentStatus = selectedAttendanceMarkListData.getPresentStatus();
        System.out.println(" checkpresentStatus " + checkpresentStatus);

        if (checkpresentStatus.equals("P")) {
            selectedAttendanceMarkListData.setPresentStatus("A");
            presentCount--;
        } else {
            selectedAttendanceMarkListData.setPresentStatus("P");
            presentCount++;
        }
        int index = attendanceListView.getFirstVisiblePosition();
        View v = attendanceListView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();

        markStudentadapter.notifyDataSetChanged();
        //attendanceListView.setAdapter(attendanceRefreshAdapter);
        attendanceListView.setSelectionFromTop(index, top);
        presentCounterTextView.setText(Integer.toString(presentCount));
    }

    @Override
    public void onBackPressed() {

        if (dataReceivedStatus) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Action")
                    .setCancelable(false)
                    .setMessage("" + presentCount + " Student present")
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // submit attendance on backpress confirmation
                            markAttendanceToAzure();
                        }
                    })
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //deleted older draft
                            dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            printAllData();
                            finish();
                        }
                    })
                    .setNegativeButton("Save draft", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            System.out.println(" attendancelist  " + attendancelist.size());
                            System.out.println(" schoolClassIdToFetchStudentList  " + schoolClassIdToFetchStudentList);
                            System.out.println(" attendanceDateString  " + attendanceDateString);

                            //deleted older draft and then insert new draft
                            dbHandler.deleteAttendanceRecords(schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            //add student list to database for 1st time with draft status as 1(true)
                            dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, attendanceDateString, 1);
                            printAllData();
                            finish();

                        }
                    }).create().show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Action")
                    .setCancelable(false)
                    .setMessage("" + presentCount + " Student present")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
        }
    }

    private void FetchStudentList() {
        attendanceMarkProgressBar.setVisibility(View.VISIBLE);
        jsonObjectForStudentList = new JsonObject();

        // Yogesh changes for invoke api
        jsonObjectForStudentList.addProperty("SchoolClassId", schoolClassIdToFetchStudentList);
        jsonObjectForStudentList.addProperty("UserRole", userDetails.get(SessionManager.KEY_USER_ROLE));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchStudentListApi", jsonObjectForStudentList);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fetch Student List Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Fetch Student List  API   response    " + response);

                StudentListJsonParse(response);
            }
        });
    }

    private void StudentListJsonParse(JsonElement jsonElement) {

        getJsonListResponse = jsonElement.getAsJsonArray();
        try {
            if (getJsonListResponse.size() == 0) {
                System.out.println("data not received");
                attendanceMarkProgressBar.setVisibility(View.INVISIBLE);
                noDataTextView.setText(R.string.noDataAvailable);
                noDataTextView.setGravity(Gravity.CENTER);
                noDataTextView.setVisibility(View.VISIBLE);
            } else {
                noDataTextView.setVisibility(View.INVISIBLE);
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    System.out.println(" INSiDE FOR LOOP");
                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");

                    String fullName = student_firstName + " " + student_lastName;
                    student_rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                    student_phoneNo = jsonObjectForIteration.get("phone").toString().replace("\"", "");
                    System.out.println("student_Id" + student_Id);
                    System.out.println("student_count" + loop);

                    Integer serialNo = loop + 1;

//                    AttendanceMarkListData attendanceMarkdata = new AttendanceMarkListData(student_Id, student_rollNo,
//                            student_firstName, fullName, "P", student_phoneNo);

                    AttendanceMarkListData attendanceMarkdata = new AttendanceMarkListData(student_Id, String.valueOf(serialNo),
                            student_firstName, fullName, "P", student_phoneNo);

                    attendancelist.add(attendanceMarkdata);
                    presentCount++;
                }
                attendanceMarkProgressBar.setVisibility(View.INVISIBLE);
                markStudentadapter = new AttendanceMarkAdapter(getApplicationContext(), attendancelist);
                attendanceListView.setAdapter(markStudentadapter);

                //add student list to database for 1st time with draft status as 0(false)
//                dbHandler.addStudentNamesToDatabase(attendancelist, schoolClassIdToFetchStudentList, null, 0);

                presentCounterTextView.setText(Integer.toString(presentCount));
                dataReceivedStatus = true;

                sessionManager.setSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE, "customDate");

            }

        } catch (Exception e) {
            System.out.println("Exception in attendance mark exception " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attendance_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send:
                if (connectionDetector.isConnectingToInternet()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Attendance ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    markAttendanceToAzure();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
//                    markAttendanceToAzure();
                } else {
                    FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CheckSmsOnOff extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<School_Config> result = mSchoolConfigTable.where().field("Type").eq("smsUrl").and().field("Branch_id").eq(userDetails.get(SessionManager.KEY_BRANCH_ID))
                        .select("Type", "Value", "value_one", "Branch_id").execute().get();
                System.out.println("result " + result);

                smsUrl = result.get(0).getValue();
                onOrOffValue = result.get(0).getValue_one();
                String branchId = result.get(0).getBranch_id();

                System.out.println(" smsUrl " + smsUrl);
                System.out.println(" onOrOffVal " + onOrOffValue);
                System.out.println(" branchId " + branchId);

            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
            return null;
        }
    }
}
