package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.adapters.AssignmentPagerAdapter;
import com.relecotech.androidsparsh_smallwonder.fragments.Assignment_Tabs_Fragment;
import com.relecotech.androidsparsh_smallwonder.fragments.DatePickerFragment;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AssignmentListData;
import com.relecotech.androidsparsh_smallwonder.utils.AlarmReceiver;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Relecotech on 06-03-2018.
 */

public class AssignmentListActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private AssignmentListData assignmentListData;
    private ProgressDialog progressDialog;
    private TextView noDataAvailableTextView;
    private String teacherFirstName, teacherLastName;
    private String assignmentId, assignmentStatus;
    private String assignmentPostDate, assignmentDueDate;
    private String maxCredit, assignmentGrade, assignmentSubject, scoreType;
    private String assignmentDescription, assignmentSubmittedBy, assignment_status_notes;
    private String assignmentAttachmentCount, assignment_status_credit, assignment_status_grades;
    private String assignmentAttachmentIdentifier;
    private JsonObject jsonObjectToFetchAssignment;
    private RelativeLayout assignmentMainLayout;
    private RelativeLayout assignmentFilterLayout;
    private TextView startDateForFilterAssignmentTextView;
    private TextView endDateForFilterAssignmentTextView;
    private Spinner selectedSubjectSpinner;
    private Calendar cal, calen;
    private Date date7daysBefore;
    private String date7dayBefore;
    private Date todaysDate;
    private String todayDate;
    private SimpleDateFormat getParcebleDateFormat;
    private SimpleDateFormat getTargetDateFormat;
    private String getStartDateValue, getEndDateValue;
    private String executionStatus = "Normal";
    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;
    private Map<String, Long> assignmentDueDateNotification_Map;
    private FloatingActionButton addAssignmentBtn;
    private ViewPager pager;
    private TabLayout assignment_tabLayout;
    private View stick;

    public Map<String, List<AssignmentListData>> assignmentList_Map;
    private List<AssignmentListData> assignmentList;

    private Map<String, AssignmentListData> assignmentIdAssignObjHashMap;
    private List<String> studentLikeArrayList;
    private Map<String, List<String>> assignmentIdStudentLikeHashMap;
    private String assignmentClass;
    private String assignmentDivision;
    private int dateClickFlag;
    private Date getParcebleStartDate;
    private Date getParcebleEndDate;
    private AssignmentPagerAdapter adapter;
    private FragmentManager manager;
    private boolean likeCheck = false;
    private AlarmManager alarmManagerForSetNotificaion;

    public static int PENDING_INTENT_REQUEST_CODE = 0;
    private PendingIntent pendingIntentForSetNotification;
    private List<PendingIntent> pendingIntentList;
    private Long timeInMillisecondForAlarm;
    private Cursor resultSet;
    private DatabaseHandler databaseHandler;
    private ArrayList<String> classList, divisionList, subjectList;
    private HashMap<String, String> conditionHashMap;
    private Spinner filterClassSpinner, filterDivisionSpinner;
    Map<AssignmentListData, String> assignmentListDataStringMap;
    private AssignmentListData getAssignmentListData;
    private List<AssignmentListData> classAssignmentList;
    private SimpleDateFormat simpleDateFormat;
    private ImageView classSpinnerImage, divisionSpinnerImage;
    private HashMap<String, String> subjectMap;
    private MobileServiceClient mClient;
    private int IntentId = 1;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_list_activity);

        mClient = Singleton.Instance().mClientMethod(this);

        pendingIntentList = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignmentListDataStringMap = new HashMap<>();
        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        databaseHandler = new DatabaseHandler(this);



        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        assignmentList_Map = new TreeMap<>();
        assignmentDueDateNotification_Map = new HashMap<>();

        assignmentIdStudentLikeHashMap = new HashMap<>();
        assignmentList = new ArrayList<>();
        assignmentIdAssignObjHashMap = new TreeMap<>();


        subjectMap = new HashMap<>();

        classAssignmentList = new ArrayList<>();
        studentLikeArrayList = new ArrayList<>();

        stick = findViewById(R.id.assignmentStickView);
        mAdView  = (AdView)findViewById(R.id.adView);
        noDataAvailableTextView = (TextView) findViewById(R.id.assignmentNoDataAvailable_textView);
        addAssignmentBtn = (FloatingActionButton) findViewById(R.id.addAssignmentButton);

        assignmentMainLayout = (RelativeLayout) findViewById(R.id.assignment_layout);
        assignmentFilterLayout = (RelativeLayout) findViewById(R.id.assignment_filter_layout);
        assignmentFilterLayout.setVisibility(View.INVISIBLE);

        startDateForFilterAssignmentTextView = (TextView) findViewById(R.id.startTextView);
        endDateForFilterAssignmentTextView = (TextView) findViewById(R.id.endTextView);
        filterClassSpinner = (Spinner) findViewById(R.id.filterClassSpinner);
        filterDivisionSpinner = (Spinner) findViewById(R.id.filterDivisionSpinner);
        selectedSubjectSpinner = (Spinner) findViewById(R.id.selectSubjectSpinner);

        classSpinnerImage = (ImageView) findViewById(R.id.classSpinnerImage);
        divisionSpinnerImage = (ImageView) findViewById(R.id.divisionSpinnerImage);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        if (userRole.equalsIgnoreCase("Student")) {
//            if (connectionDetector.isConnectingToInternet()) {
//                FetchSubject();
//            }
//            filterClassSpinner.setVisibility(View.INVISIBLE);
//            filterDivisionSpinner.setVisibility(View.INVISIBLE);
//
//            classSpinnerImage.setVisibility(View.INVISIBLE);
//            divisionSpinnerImage.setVisibility(View.INVISIBLE);
//        }

        Button searchForFilterAssignment = (Button) findViewById(R.id.btnSubmit);
        pager = (ViewPager) findViewById(R.id.assignment_view_pager);
        addAssignmentBtn.setVisibility(View.INVISIBLE);
        assignment_tabLayout = (TabLayout) findViewById(R.id.assignment_tab_layout);

        try {
            Date getParcebleStartDate = getParcebleDateFormat.parse(startDateForFilterAssignmentTextView.getText().toString());
            Date getParcebleEndDate = getParcebleDateFormat.parse(endDateForFilterAssignmentTextView.getText().toString());
            getStartDateValue = getTargetDateFormat.format(getParcebleStartDate);
            getEndDateValue = getTargetDateFormat.format(getParcebleEndDate);

        } catch (Exception e) {
            System.out.println(" INSIDE DATE Exception");
        }

        startDateForFilterAssignmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateClickFlag = 1;
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("dob", todayDate);
                bundle.putString("value", "less");
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                System.out.println("date7daysBefore  " + date7daysBefore);
                try {
                    Date date = getParcebleDateFormat.parse(startDateForFilterAssignmentTextView.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        endDateForFilterAssignmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateClickFlag = 2;
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("dob", todayDate);
                bundle.putString("value", "less");
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                System.out.println("todaysDate  " + todaysDate);
                try {
                    Date date = getParcebleDateFormat.parse(endDateForFilterAssignmentTextView.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

//        getParcebleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        getParcebleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        getTargetDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -7);
        date7daysBefore = cal.getTime();

        date7dayBefore = getParcebleDateFormat.format(date7daysBefore);
        System.out.println("daysBeforeDate " + date7dayBefore);

        startDateForFilterAssignmentTextView.setText(date7dayBefore);

        calen = Calendar.getInstance();
        calen.setTime(new Date());
        calen.add(Calendar.DAY_OF_YEAR, 0);
        todaysDate = calen.getTime();

        todayDate = getParcebleDateFormat.format(todaysDate);
        System.out.println("todayDate " + todayDate);
        endDateForFilterAssignmentTextView.setText(todayDate);

        classList = new ArrayList<>();
        divisionList = new ArrayList<>();
        subjectList = new ArrayList<>();
        conditionHashMap = new HashMap<>();

        if (userRole.equalsIgnoreCase("Teacher")) {
            resultSet = databaseHandler.getClassDataByCursor();
            resultSet.moveToFirst();
            for (int i = 0; i < resultSet.getCount(); i++) {
                if (!classList.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                    classList.add(resultSet.getString(resultSet.getColumnIndex("class")));
                }
                resultSet.moveToNext();
            }

            classList.add("[ Class ]");
            ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, classList);
            adapterClass.setDropDownViewResource(R.layout.spinner_item);
            filterClassSpinner.setAdapter(adapterClass);

            filterClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String assignmentClassString = filterClassSpinner.getSelectedItem().toString();
                    divisionList.clear();
                    if (!assignmentClassString.contains("[ Class ]")) {
                        conditionHashMap.put("class", assignmentClassString);
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
                    ArrayAdapter<String> adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, divisionList);
                    adapterDivision.setDropDownViewResource(R.layout.spinner_item);
                    filterDivisionSpinner.setAdapter(adapterDivision);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            filterDivisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String assignmentDivisionString = filterDivisionSpinner.getSelectedItem().toString();
                    subjectList.clear();
                    if (!assignmentDivisionString.equals("[ Division ]")) {
                        System.out.println(" class " + filterClassSpinner.getSelectedItem().toString());
                        System.out.println(" assignmentDivisionString " + assignmentDivisionString);
                        conditionHashMap.put("class", filterClassSpinner.getSelectedItem().toString());
                        conditionHashMap.put("division", assignmentDivisionString);
                        resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                        resultSet.moveToFirst();
//                    schoolClassId = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));
                        for (int i = 0; i < resultSet.getCount(); i++) {
                            if (!subjectList.contains(resultSet.getString(resultSet.getColumnIndex("subject")))) {
                                subjectList.add(resultSet.getString(resultSet.getColumnIndex("subject")));
                                System.out.println(" resultSet.getString(resultSet.getColumnIndex()) " + resultSet.getString(resultSet.getColumnIndex("subject")));
                            }
                            resultSet.moveToNext();
                        }
                    } else {
                        subjectList = new ArrayList<>();
                    }
                    subjectList.add("[ Subject ]");
                    ArrayAdapter<String> adapterSubject = new ArrayAdapter<String>(AssignmentListActivity.this, R.layout.spinner_item, subjectList);
                    adapterSubject.setDropDownViewResource(R.layout.spinner_item);
                    selectedSubjectSpinner.setAdapter(adapterSubject);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }

        selectedSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        searchForFilterAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    executionStatus = "Filter";
                    getParcebleStartDate = getParcebleDateFormat.parse(startDateForFilterAssignmentTextView.getText().toString());
                    getParcebleEndDate = getParcebleDateFormat.parse(endDateForFilterAssignmentTextView.getText().toString());

                    getStartDateValue = getTargetDateFormat.format(getParcebleStartDate);
                    getEndDateValue = getTargetDateFormat.format(getParcebleEndDate);

                    System.out.println("selectedSubjectSpinner.getSelectedItem().toString()" + selectedSubjectSpinner.getSelectedItem());
                    if (connectionDetector.isConnectingToInternet()) {

                        if (userRole.equalsIgnoreCase("Student")) {
                            if (!selectedSubjectSpinner.getSelectedItem().toString().contains("[ Subject ]")) {
                                if (getParcebleStartDate.equals(getParcebleEndDate) || getParcebleStartDate.before(getParcebleEndDate)) {
                                    if (assignmentList.size() != 0) {
                                        assignmentList.clear();
                                    }
                                    FetchAssignment();
                                } else {
                                    FancyToast.makeText(AssignmentListActivity.this, "Select Proper Date", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                }
                            } else {
                                FancyToast.makeText(AssignmentListActivity.this, "Select Subject", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                            }

                        } else {

                            if (!filterClassSpinner.getSelectedItem().toString().contains("[ Class ]")) {
                                if (!filterDivisionSpinner.getSelectedItem().toString().contains("[ Division ]")) {
                                    if (!selectedSubjectSpinner.getSelectedItem().toString().contains("[ Subject ]")) {
                                        if (getParcebleStartDate.equals(getParcebleEndDate) || getParcebleStartDate.before(getParcebleEndDate)) {
                                            if (assignmentList.size() != 0) {
                                                assignmentList.clear();
                                            }
                                            FetchAssignment();
                                        } else {
                                            FancyToast.makeText(AssignmentListActivity.this, "Select Proper Date", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                        }
                                    } else {
                                        FancyToast.makeText(AssignmentListActivity.this, "Select Subject", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                    }
                                } else {
                                    FancyToast.makeText(AssignmentListActivity.this, "Select Division", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                }
                            } else {
                                FancyToast.makeText(AssignmentListActivity.this, "Select Class", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                            }
                        }
                    } else {
                        FancyToast.makeText(AssignmentListActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        addAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isConnectingToInternet()) {
                    Intent assPostIntent = new Intent(AssignmentListActivity.this, AssignmentPost.class);
                    startActivityForResult(assPostIntent, IntentId);
//                    startActivity(assPostIntent);
                } else {
                    FancyToast.makeText(AssignmentListActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });


        if (connectionDetector.isConnectingToInternet()) {
            FetchAssignment();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

    }

    private void jsonObjectData() {

        String studentId = userDetails.get(SessionManager.KEY_STUDENT_ID);
        String branchId = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID);
        String teacherId = sessionManager.getUserDetails().get(SessionManager.KEY_TEACHER_ID);
        String adminId = sessionManager.getUserDetails().get(SessionManager.KEY_ADMIN_ID);
        String schoolClassId = sessionManager.getUserDetails().get(SessionManager.KEY_SCHOOL_CLASS_ID);

        jsonObjectToFetchAssignment = new JsonObject();

        try {
            if (executionStatus.equals("Filter")) {

                System.out.println("INSIDE FILTER");

                if (userRole.equals("Teacher")) {
                    jsonObjectToFetchAssignment.addProperty("teacherId", teacherId);
                } else if (userRole.equals("Student")) {
                    jsonObjectToFetchAssignment.addProperty("studentId", studentId);
                    jsonObjectToFetchAssignment.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                } else {
                    jsonObjectToFetchAssignment.addProperty("adminId", adminId);
                }
                jsonObjectToFetchAssignment.addProperty("userRole", userRole);
                jsonObjectToFetchAssignment.addProperty("startDate", getStartDateValue);
                jsonObjectToFetchAssignment.addProperty("endDate", getEndDateValue);
                jsonObjectToFetchAssignment.addProperty("subject", selectedSubjectSpinner.getSelectedItem().toString());
                jsonObjectToFetchAssignment.addProperty("status", "Filter");
                jsonObjectToFetchAssignment.addProperty("branchId", branchId);
            } else {
                System.out.println("Else of INSIDE FILTER");
                jsonObjectToFetchAssignment.addProperty("userRole", userRole);
                jsonObjectToFetchAssignment.addProperty("status", "Normal");
                jsonObjectToFetchAssignment.addProperty("branchId", branchId);
                if (userRole.equals("Teacher")) {
                    jsonObjectToFetchAssignment.addProperty("teacherId", teacherId);
                } else if (userRole.equals("Student")) {
                    jsonObjectToFetchAssignment.addProperty("studentId", studentId);
                    jsonObjectToFetchAssignment.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                } else {
                    jsonObjectToFetchAssignment.addProperty("adminId", adminId);
                }
            }
        } catch (Exception e) {
            System.out.println(" Inside Assignment Fragment Catch");
            System.out.println(e.getMessage());
        }

        System.out.println("jsonObjectToFetchAssignment " + jsonObjectToFetchAssignment);
    }

    private void FetchAssignment() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        jsonObjectData();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentFetchApi", jsonObjectToFetchAssignment);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AssignmentListActivity.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FetchAssignment();
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
                System.out.println(" response " + response);
                parseJSON(response);
            }
        });
    }

    private void parseJSON(JsonElement assignmentFetchResponse) {
        String submittedByName = "";
        String assignment_status_status = "";

        fragmentsList.clear();
        fragmentsNameList.clear();
        studentLikeArrayList.clear();
        assignmentList.clear();
        assignmentList_Map.clear();
        assignmentIdStudentLikeHashMap.clear();
        assignmentIdAssignObjHashMap.clear();

        assignmentFilterLayout.setVisibility(View.INVISIBLE);

        JsonArray assignmentJsonArray = assignmentFetchResponse.getAsJsonArray();
        if (assignmentJsonArray.size() == 0) {
            progressDialog.dismiss();
            assignmentMainLayout.setVisibility(View.VISIBLE);

            stick.setVisibility(View.VISIBLE);

            if (userRole.equals("Teacher")) {

                noDataAvailableTextView.setVisibility(View.VISIBLE);
                noDataAvailableTextView.setText(R.string.noDataAvailable);

            } else if (userRole.equals("Student")) {
                stick.setVisibility(View.INVISIBLE);
                /* No Need to set 'No Data Available' text because Tab already have No Data Available text */
            }
        } else {
            noDataAvailableTextView.setVisibility(View.INVISIBLE);
            stick.setVisibility(View.INVISIBLE);
            for (int i = 0; i < assignmentJsonArray.size(); i++) {

                JsonObject jsonObjectForIteration = assignmentJsonArray.get(i).getAsJsonObject();

                assignmentId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                assignmentPostDate = jsonObjectForIteration.get("assignmentPostDate").toString().replace("\"", "");
                assignmentDueDate = jsonObjectForIteration.get("assignmentDueDate").toString().replace("\"", "");
                maxCredit = jsonObjectForIteration.get("assignmentCredit").toString().replace("\"", "");
                assignmentGrade = jsonObjectForIteration.get("assignmentGrade").toString().replace("\"", "");
                assignmentSubject = jsonObjectForIteration.get("assignmentSubject").toString().replace("\"", "");
                assignmentDescription = jsonObjectForIteration.get("assignmentDescription").toString().replace("\\n", "\n").replace("\\r", "").replace("\"", "");
                assignmentSubmittedBy = jsonObjectForIteration.get("assignmentSubmittedBy").toString().replace("\"", "");
                scoreType = jsonObjectForIteration.get("scoreType").toString().replace("\"", "");
                assignmentAttachmentCount = jsonObjectForIteration.get("assignmentAttachmentCount").toString().replace("\"", "");
                assignmentAttachmentIdentifier = jsonObjectForIteration.get("assignmentAttachmentIdentifier").toString().replace("\"", "");
                System.out.println(" assignmentAttachmentIdentifier " + assignmentAttachmentIdentifier);
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
                SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
                Date dateIssue = null;
                Date dateDue = null;
                try {
                    dateIssue = simpleDateFormat.parse(assignmentPostDate);
                    dateDue = simpleDateFormat.parse(assignmentDueDate);
//                    assignmentPostDate = targetDateFormat.format(dateIssue);
//                    assignmentDueDate = targetDateFormat.format(dateDue);

                    assignmentPostDate = targetDateFormat.format(dateIssue);
                    assignmentDueDate = targetDateFormat.format(dateDue);


                    System.out.println("assignmentDueDate after" + assignmentDueDate);
                } catch (ParseException e) {
                }

                assignmentClass = jsonObjectForIteration.get("assignmentClass").toString().replace("\"", "");
                assignmentDivision = jsonObjectForIteration.get("assignmentDivision").toString().replace("\"", "");
                String student_id = jsonObjectForIteration.get("Student_id").toString().replace("\"", "");
                String selectedItemId = jsonObjectForIteration.get("selectedItemId").toString().replace("\"", "");

                if (!selectedItemId.equals("null")) {
                    if (assignmentIdStudentLikeHashMap.containsKey(selectedItemId)) {
                        studentLikeArrayList = assignmentIdStudentLikeHashMap.get(selectedItemId);
                        studentLikeArrayList.add(student_id);
                    } else {
                        studentLikeArrayList = new ArrayList<>();
                        studentLikeArrayList.add(student_id);
                        assignmentIdStudentLikeHashMap.put(selectedItemId, studentLikeArrayList);
                    }
                } else {
                    System.out.println(" INSIDE  IF  ELSE selectedItemId " + selectedItemId + " student_id " + student_id);
                }

                if (userRole.equals("Teacher")) {
                    if (assignmentIdAssignObjHashMap.containsKey(assignmentId)) {
                        getAssignmentListData = assignmentIdAssignObjHashMap.get(assignmentId);
                    } else {
                        assignmentIdAssignObjHashMap.put(assignmentId, new AssignmentListData(R.drawable.ic_assignment, assignmentId, maxCredit, assignmentSubject, dateIssue, dateDue, assignmentClass, assignmentDivision, submittedByName, assignmentDescription, assignment_status_status, assignment_status_credit, assignment_status_grades, assignment_status_notes, scoreType, assignmentAttachmentCount, 0, assignmentAttachmentIdentifier, likeCheck));
                    }
                } else if (userRole.equals("Student")) {

                    assignment_status_status = jsonObjectForIteration.get("assignmentStatus").toString().replace("\"", "");
                    assignment_status_credit = jsonObjectForIteration.get("assignment_status_credit").toString().replace("\"", "");
                    assignment_status_grades = jsonObjectForIteration.get("assignment_status_grades").toString().replace("\"", "");
                    assignment_status_notes = jsonObjectForIteration.get("assignment_status_notes").toString().replace("\"", "");

                    teacherFirstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    teacherLastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    submittedByName = teacherFirstName + " " + teacherLastName;

                    if (assignmentIdAssignObjHashMap.containsKey(assignmentId)) {
                        getAssignmentListData = assignmentIdAssignObjHashMap.get(assignmentId);
                    } else {
                        assignmentIdAssignObjHashMap.put(assignmentId, new AssignmentListData(R.drawable.ic_assignment, assignmentId, maxCredit, assignmentSubject, dateIssue, dateDue, assignmentClass, assignmentDivision, submittedByName, assignmentDescription, assignment_status_status, assignment_status_credit, assignment_status_grades, assignment_status_notes, scoreType, assignmentAttachmentCount, 0, assignmentAttachmentIdentifier, likeCheck));

                    }
                }
            }

            if (userRole.equals("Teacher")) {

                for (String val : assignmentIdAssignObjHashMap.keySet()) {

                    String classDiv = assignmentIdAssignObjHashMap.get(val).getClassStd() + " " + assignmentIdAssignObjHashMap.get(val).getDivision();
                    if (!fragmentsNameList.contains(classDiv)) {
                        fragmentsNameList.add(classDiv);
                    } else {
                        System.out.println("class Division already present..............");
                    }

                    if (assignmentIdStudentLikeHashMap.containsKey(val)) {
                        System.out.println(" val  " + val);
                        System.out.println(" assignmentIdStudentLikeHashMap  " + assignmentIdStudentLikeHashMap.get(val));
                        int countLike = assignmentIdStudentLikeHashMap.get(val).size();
                        System.out.println(" countLike  " + countLike);
                        System.out.println("if assignmentIdAssignObjHashMap.get(val).getAssId() " + assignmentIdAssignObjHashMap.get(val).getAssId());
                        assignmentIdAssignObjHashMap.get(val).setLikeCount(countLike);
                        assignmentIdAssignObjHashMap.get(val).setLikeCheck(true);
                        AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);
                        if (assignmentList_Map.containsKey(classDiv)) {
                            assignmentList = assignmentList_Map.get(classDiv);
                            assignmentList.add(0, listData);
                        } else {
                            assignmentList = new ArrayList<>();
                            assignmentList.add(0, listData);
                            assignmentList_Map.put(classDiv, assignmentList);
                        }
                    } else {
                        System.out.println("else assignmentIdAssignObjHashMap.get(val).getAssId() " + assignmentIdAssignObjHashMap.get(val).getAssId());
                        AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);
                        System.out.println("  listData " + listData.getLikeCount());
                        if (assignmentList_Map.containsKey(classDiv)) {
                            assignmentList = assignmentList_Map.get(classDiv);
                            assignmentList.add(0, listData);
                        } else {
                            assignmentList = new ArrayList<>();
                            assignmentList.add(0, listData);
                            assignmentList_Map.put(classDiv, assignmentList);
                        }
                    }
                }
            } else if (userRole.equals("Student")) {
                for (String val : assignmentIdAssignObjHashMap.keySet()) {


                    AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);
                    String status = assignmentIdAssignObjHashMap.get(val).getAssStatus();
                    try {
                        int countLike = assignmentIdStudentLikeHashMap.get(val).size();
                        listData.setLikeCount(countLike);
                        List<String> likeList = assignmentIdStudentLikeHashMap.get(val);
                        if (likeList.contains(userDetails.get(SessionManager.KEY_STUDENT_ID))) {
                            listData.setLikeCheck(true);
                        }
                    } catch (Exception e) {
                        listData.setLikeCount(0);
                        System.out.println("exception in count " + e.getMessage());
                    }

//                    AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);

                    if (assignmentIdStudentLikeHashMap.containsKey(val)) {

                        if (status.equals("Pending") || status.equals("Re-Submit")) {
                            if (status.equals("Pending")) {
                                if (assignmentList_Map.containsKey("Pending")) {
                                    assignmentList = assignmentList_Map.get(status);
                                    assignmentList.add(0, listData);
                                } else {
                                    assignmentList = new ArrayList<>();
                                    assignmentList.add(0, listData);
                                    assignmentList_Map.put("Pending", assignmentList);
                                }
                            }

                            System.out.println("listData.getDueDate()  " + listData.getDueDate());
                            long dueDateMillSendForNotification = 0;
                            try {
                                // Date dateDueDate = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).(listData.getDueDate());
                                Date dateDueDate = listData.getDueDate();
                                System.out.println("dateDueDate " + dateDueDate);
                                dueDateMillSendForNotification = GetMillisecondMethod(dateDueDate);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(" dueDateMillSendForNotification " + dueDateMillSendForNotification);
                            System.out.println(" System.currentTimeMillis() " + System.currentTimeMillis());
                            if (dueDateMillSendForNotification >= System.currentTimeMillis()) {
                                assignmentDueDateNotification_Map.put(assignmentId, dueDateMillSendForNotification);
                            } else {
                                System.out.println("Due date milliSecond is less than current millisecond........");
                            }

                        } else if (status.equals("Approved")) {
                            if (assignmentList_Map.containsKey("Approved")) {
                                assignmentList = assignmentList_Map.get(status);
                                assignmentList.add(0, listData);
                            } else {
                                assignmentList = new ArrayList<>();
                                assignmentList.add(0, listData);
                                assignmentList_Map.put("Approved", assignmentList);
                            }
                        }
                    } else {
                        if (status.equals("Pending") || status.equals("Re-Submit")) {
                            if (status.equals("Pending")) {
                                if (assignmentList_Map.containsKey("Pending")) {
                                    assignmentList = assignmentList_Map.get(status);
                                    assignmentList.add(0, listData);
                                } else {
                                    assignmentList = new ArrayList<>();
                                    assignmentList.add(0, listData);
                                    assignmentList_Map.put("Pending", assignmentList);
                                }
                            }
                        } else if (status.equals("Approved")) {
                            if (assignmentList_Map.containsKey("Approved")) {
                                assignmentList = assignmentList_Map.get(status);
                                assignmentList.add(0, listData);
                            } else {
                                assignmentList = new ArrayList<>();
                                assignmentList.add(0, listData);
                                assignmentList_Map.put("Approved", assignmentList);
                            }
                        }
                    }

//                    AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);
//                    if (assignmentIdStudentLikeHashMap.containsKey(val)) {
//                        int countLike = assignmentIdStudentLikeHashMap.get(val).size();
//                        listData.setLikeCount(countLike);
//                        List<String> likeList = assignmentIdStudentLikeHashMap.get(val);
//                        if (likeList.contains(userDetails.get(SessionManager.KEY_STUDENT_ID))) {
//                            listData.setLikeCheck(true);
//                        }
//                    }

                }
            }
        }


//        if (userRole.equals("Teacher")) {
//            addAssignmentBtn.setVisibility(View.VISIBLE);
//
//
//            for (Map.Entry<String, List<AssignmentListData>> entry : assignmentList_Map.entrySet()) {
//                ArrayList<AssignmentListData> sortedList = new ArrayList<>();
//                Bundle fragmentBundle = new Bundle();
//                sortedList.addAll(entry.getValue());
//                System.out.println("------- sortedList " + sortedList);
//                System.out.println("------- sortedList.size() " + sortedList.size());
//                Collections.sort(sortedList, new Comparator<AssignmentListData>() {
//                    @Override
//                    public int compare(AssignmentListData obj1, AssignmentListData obj2) {
//                        // ## Ascending order
//                        //return obj1.getIssueDate().compareToIgnoreCase(obj2.getIssueDate());
//                        // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values
//
//                        return obj2.getIssueDate().compareTo(obj1.getIssueDate());
//                        // ## Descending order
//                        // To compare string values
//                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
//                    }
//                });
//
//
//                fragmentBundle.putSerializable("assignmentList", (Serializable) sortedList);
//
//                 fragmentBundle.putSerializable("assignmentList", (Serializable) assignmentList_Map.get(fragmentsNameList.get(i)));
//                Assignment_Tabs_Fragment assignment_tabs_fragment = new Assignment_Tabs_Fragment();
//                assignment_tabs_fragment.setArguments(fragmentBundle);
//                fragmentsList.add(assignment_tabs_fragment);
//
//            }
//
//        }
//        if (userRole.equals("Student")) {
//            addAssignmentBtn.setVisibility(View.INVISIBLE);
//            fragmentsNameList.add("Pending");
//            fragmentsNameList.add("Approved");
//            stick.setVisibility(View.INVISIBLE);
//
//            for (Map.Entry<String, List<AssignmentListData>> entry : assignmentList_Map.entrySet()) {
//                System.out.println("Entry---------  " + entry.getKey());
//
//                ArrayList<AssignmentListData> sortedList = new ArrayList<>();
//                Bundle fragmentBundle = new Bundle();
//                sortedList.addAll(entry.getValue());
//                System.out.println("------- sortedList " + sortedList);
//                System.out.println("------- sortedList.size() " + sortedList.size());
//                Collections.sort(sortedList, new Comparator<AssignmentListData>() {
//                    @Override
//                    public int compare(AssignmentListData obj1, AssignmentListData obj2) {
//                        // ## Ascending order
//                        //return obj1.getIssueDate().compareToIgnoreCase(obj2.getIssueDate());
//                        // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values
//
//                        return obj2.getIssueDate().compareTo(obj1.getIssueDate());
//                        // ## Descending order
//                        // To compare string values
//                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
//                    }
//                });
//
//
//                fragmentBundle.putSerializable("assignmentList", (Serializable) sortedList);
//
//                // fragmentBundle.putSerializable("assignmentList", (Serializable) assignmentList_Map.get(fragmentsNameList.get(i)));
//                Assignment_Tabs_Fragment assignment_tabs_fragment = new Assignment_Tabs_Fragment();
//                assignment_tabs_fragment.setArguments(fragmentBundle);
//                fragmentsList.add(assignment_tabs_fragment);
//
//            }
//        }
        if (userRole.equals("Teacher")) {
            addAssignmentBtn.setVisibility(View.VISIBLE);


            for (int i = 0; i < fragmentsNameList.size(); i++) {
                Bundle fragmentBundle = new Bundle();
                fragmentBundle.putSerializable("assignmentList", (Serializable) assignmentList_Map.get(fragmentsNameList.get(i)));
                Assignment_Tabs_Fragment assignment_tabs_fragment = new Assignment_Tabs_Fragment();
                assignment_tabs_fragment.setArguments(fragmentBundle);
                fragmentsList.add(assignment_tabs_fragment);
            }
        }
        if (userRole.equals("Student")) {
            addAssignmentBtn.setVisibility(View.INVISIBLE);
            fragmentsNameList.add("Pending");
            fragmentsNameList.add("Approved");
            for (int i = 0; i < fragmentsNameList.size(); i++) {
                Bundle fragmentBundle = new Bundle();
                fragmentBundle.putSerializable("assignmentList", (Serializable) assignmentList_Map.get(fragmentsNameList.get(i)));
                Assignment_Tabs_Fragment assignment_tabs_fragment = new Assignment_Tabs_Fragment();
                assignment_tabs_fragment.setArguments(fragmentBundle);
                fragmentsList.add(assignment_tabs_fragment);
            }
        }

        setNotificationForAssignmentDueDate();
        assignmentMainLayout.setVisibility(View.VISIBLE);

        setTabs();
        assignmentFilterLayout.setVisibility(View.GONE);
        progressDialog.dismiss();
    }


    private void setTabs() {
        manager = getSupportFragmentManager();
        adapter = new AssignmentPagerAdapter(manager, fragmentsList, fragmentsNameList);
        pager.setAdapter(adapter);
        assignment_tabLayout.setupWithViewPager(pager);
    }

    @Override
    public void onFinishDialog(String date) {
        if (dateClickFlag == 1) {
            startDateForFilterAssignmentTextView.setText(date);
        } else {
            endDateForFilterAssignmentTextView.setText(date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.assignment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                break;

            case R.id.action_filter:
                System.out.println("Action Filter--");
                if (userRole.equalsIgnoreCase("Student")) {
                    filterClassSpinner.setVisibility(View.INVISIBLE);
                    filterDivisionSpinner.setVisibility(View.INVISIBLE);
                    classSpinnerImage.setVisibility(View.INVISIBLE);
                    divisionSpinnerImage.setVisibility(View.INVISIBLE);
                }
                if (connectionDetector.isConnectingToInternet()) {
                    if (userRole.equals("Student")) {
                        FetchSubject();
                    }
                } else {
                    FancyToast.makeText(AssignmentListActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                assignmentFilterLayout.setVisibility(View.VISIBLE);
                assignmentMainLayout.setVisibility(View.INVISIBLE);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (connectionDetector.isConnectingToInternet()) {
//            FetchAssignment();
//        } else {
//            noDataAvailableTextView.setText(R.string.noDataAvailable);
//            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
//        }
//    }

    private void setNotificationForAssignmentDueDate() {

        ClearAllAlarm(pendingIntentList);

        alarmManagerForSetNotificaion = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentForNotification = new Intent(AssignmentListActivity.this, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("Notification_Tag", "Assignment");
        intentForNotification.putExtras(bundle);

        for (Map.Entry<String, Long> entry : assignmentDueDateNotification_Map.entrySet()) {
            pendingIntentForSetNotification = PendingIntent.getBroadcast(this, PENDING_INTENT_REQUEST_CODE, intentForNotification, 0);
            PENDING_INTENT_REQUEST_CODE++;
            pendingIntentList.add(pendingIntentForSetNotification);
            System.out.println("pendingIntentList In loop " + pendingIntentList.size());
            timeInMillisecondForAlarm = entry.getValue();
            alarmManagerForSetNotificaion.set(AlarmManager.RTC_WAKEUP, timeInMillisecondForAlarm, pendingIntentForSetNotification);
        }
    }

    private void ClearAllAlarm(List<PendingIntent> pendingIntentList) {
        System.out.println("pendingIntentList---- " + pendingIntentList.size());
        for (int loop = 0; loop < this.pendingIntentList.size(); loop++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(this.pendingIntentList.get(loop));
        }
    }

    public long GetMillisecondMethod(Date date_time) {
        Calendar calculateDueDateForNotification = Calendar.getInstance();
        calculateDueDateForNotification.setTime(date_time);
        calculateDueDateForNotification.add(Calendar.DATE, 0);

//        String time = sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER);
        String time = "19:37";

        String[] hhmm = time.split(":");
        int hh = Integer.parseInt(hhmm[0]);
        int min = Integer.parseInt(hhmm[1]);

        int yy = calculateDueDateForNotification.get(Calendar.YEAR);
        int mm = calculateDueDateForNotification.get(Calendar.MONTH);
        int dd = calculateDueDateForNotification.get(Calendar.DAY_OF_MONTH);
        System.out.println("GetMillisecondMethod DATE TIME " + yy + " " + mm + " " + dd + " " + hh + " " + min);
        calculateDueDateForNotification.set(yy, mm, dd, hh, min, 00);
        Date calculatedDueDateForAssignmentAlert = calculateDueDateForNotification.getTime();

        long dueDateMilliSecond = calculatedDueDateForAssignmentAlert.getTime();

        return dueDateMilliSecond;
    }


    private void FetchSubject() {

        JsonObject jsonObjectToFetchSubject = new JsonObject();
        jsonObjectToFetchSubject.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("subjectFetchApi", jsonObjectToFetchSubject);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(getApplicationContext())
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FetchSubject();
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
                System.out.println(" response " + response);
                parseSubjectJSON(response);
            }
        });
    }

    private void parseSubjectJSON(JsonElement response) {
        JsonArray subjectJsonArray = response.getAsJsonArray();
        if (subjectJsonArray.size() == 0) {
        } else {
            for (int i = 0; i < subjectJsonArray.size(); i++) {

                JsonObject jsonObjectForIteration = subjectJsonArray.get(i).getAsJsonObject();
                String subject = jsonObjectForIteration.get("subject").toString().replace("\"", "");
                subjectList.add(subject);

                if (!subjectMap.containsKey(subject)) {
                    subjectMap.put(subject, "");
                }
            }
        }
        List<String> subjectList = new ArrayList<>(subjectMap.keySet());
        subjectList.add("[ Subject ]");
        ArrayAdapter<String> adapterSubject = new ArrayAdapter<String>(AssignmentListActivity.this, R.layout.spinner_item, subjectList);
        adapterSubject.setDropDownViewResource(R.layout.spinner_item);
        selectedSubjectSpinner.setAdapter(adapterSubject);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is IntentId
        if (requestCode == IntentId) {
            if (resultCode == RESULT_OK) {

                System.out.println("AssignmentList  INSIDE RESULT_OK ");
                if (connectionDetector.isConnectingToInternet()) {
                    FetchAssignment();
                    System.out.println(" connectionDetector ");
                } else {
                    noDataAvailableTextView.setText(R.string.noDataAvailable);
                    FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                //If result code is OK then get String extra and set message
//                String message = data.getStringExtra("message");
//                resultMessage.setText(message);
            }

            if (resultCode == RESULT_CANCELED)
                System.out.println("AssignmentList INSIDE RESULT_CANCELED ");
        }
    }

}
