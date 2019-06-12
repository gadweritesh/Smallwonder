package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.ParentControlPagerAdapter;
import com.relecotech.androidsparsh_smallwonder.fragments.Parent_control_Leave_Fragment;
import com.relecotech.androidsparsh_smallwonder.fragments.Parent_control_Notes_Fragment;
import com.relecotech.androidsparsh_smallwonder.models.LeavesListData;
import com.relecotech.androidsparsh_smallwonder.models.NotesListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ParentZoneActivity extends AppCompatActivity {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ProgressDialog progressDialog;
    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;
    private Map<String, List<NotesListData>> notes_map;
    private Map<String, List<LeavesListData>> leave_map;
    private ParentControlPagerAdapter adapter;
    private ViewPager pager;
    private Button passCodeEnterBtn;
    private TabLayout tabLayout;
    private RelativeLayout passCodeLayout;
    private EditText passCodeInputEditText;
    private int currentTab;
    private ArrayList leavesArrayList;
    private ArrayList notesArrayList;
    private FloatingActionButton addFab;
    private String userRole;
    private String studentRollNo;
    private String postDate;
    private String meetingSchedule;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_zone);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        pager = (ViewPager) findViewById(R.id.calendar_view_pager);
        passCodeLayout = (RelativeLayout) findViewById(R.id.passcodelayout);
        passCodeInputEditText = (EditText) findViewById(R.id.parentPasscodeEditText);

        passCodeEnterBtn = (Button) findViewById(R.id.passcodeEnterButton);
        addFab = (FloatingActionButton) findViewById(R.id.parentPageAddFab);
        tabLayout = (TabLayout) findViewById(R.id.parent_control_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Leave"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("Notes"), 1);

        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        leavesArrayList = new ArrayList<>();
        notesArrayList = new ArrayList<>();
        notes_map = new HashMap<>();
        leave_map = new HashMap<>();


        if (userRole.equals("Teacher")) {
            passCodeLayout.setVisibility(View.INVISIBLE);
            addFab.setVisibility(View.INVISIBLE);
            if (connectionDetector.isConnectingToInternet()) {
                CallingParent();
            } else {
                createAndSetFragments();
                FancyToast.makeText(ParentZoneActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }
        System.out.println("userDetails.get(SessionManager.KEY_PARENT_PIN) " + userDetails.get(SessionManager.KEY_PARENT_PIN));
        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(passCodeInputEditText.getWindowToken(), 0);
                    passCodeEnterBtn.setVisibility(View.VISIBLE);
                } else {
                    passCodeEnterBtn.setVisibility(View.INVISIBLE);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        passCodeInputEditText.addTextChangedListener(mTextEditorWatcher);


        passCodeEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passCodeInputEditText.getText().toString().equals(userDetails.get(SessionManager.KEY_PARENT_PIN))) {
                    if (connectionDetector.isConnectingToInternet()) {
                        CallingParent();
                    } else {
                        createAndSetFragments();
                        FancyToast.makeText(ParentZoneActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                    passCodeLayout.setVisibility(View.INVISIBLE);
                } else {
                    passCodeInputEditText.setError("Invalid Code");
                }
            }
        });


        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        Intent addLeaveIntent = new Intent(ParentZoneActivity.this, AddLeave.class);
                        startActivity(addLeaveIntent);

                    } else if (tabLayout.getSelectedTabPosition() == 1) {
                        Intent notesIntent = new Intent(ParentZoneActivity.this, AddNotes.class);
                        startActivity(notesIntent);
                    }
                } else {
                    FancyToast.makeText(ParentZoneActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });

    }


    private void CallingParent() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectParent = new JsonObject();

        jsonObjectParent.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
        jsonObjectParent.addProperty("TAG", "Parent_Control_Frag");
        jsonObjectParent.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectParent.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectParent.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_ID));


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("parentZoneFetchApi", jsonObjectParent);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Parent control exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ParentZoneActivity.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CallingParent();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
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
                System.out.println(" PARENT ZONE  API   response    " + response);
                ParseJsonResponse(response);
            }
        });
    }

    private void ParseJsonResponse(JsonElement response) {

        fragmentsList.clear();
        fragmentsNameList.clear();
        leavesArrayList.clear();
        notesArrayList.clear();
        leave_map.clear();
        notes_map.clear();

        try {
            JsonArray parentZoneJsonArray = response.getAsJsonArray();
            if (parentZoneJsonArray.size() == 0) {
                progressDialog.dismiss();
                createAndSetFragments();
                System.out.println("Response Null");
            } else {
                for (int k = 0; k < parentZoneJsonArray.size(); k++) {
                    JsonObject jsonObjectIteration = parentZoneJsonArray.get(k).getAsJsonObject();

                    String parentZonePostDate = jsonObjectIteration.get("parentZonePostDate").toString().replace("\"", "");
                    String parentZoneCategory = jsonObjectIteration.get("parentZoneCategory").toString().replace("\"", "");
                    String parentZoneStatus = jsonObjectIteration.get("parentZoneStatus").toString().replace("\"", "");
                    String parentZoneDescription = jsonObjectIteration.get("parentZoneDescription").toString().replace("\"", "");
                    String parentZoneLeaveStartDate = jsonObjectIteration.get("parentZoneLeaveStartDate").toString().replace("\"", "");
                    String parentZoneLeaveEndDate = jsonObjectIteration.get("parentZoneLeaveEndDate").toString().replace("\"", "");

                    String parentZoneSchedule = jsonObjectIteration.get("parentZoneSchedule").toString().replace("\"", "");
                    String studentId = jsonObjectIteration.get("Student_id").toString().replace("\"", "");
                    String schoolClassId = jsonObjectIteration.get("School_Class_id").toString().replace("\"", "");
                    String branchId = jsonObjectIteration.get("Branch_id").toString().replace("\"", "");
                    String leaveDayCount = jsonObjectIteration.get("no_of_days").toString().replace("\"", "");

//                    String firstName = jsonObjectIteration.get("firstName").toString().replace("\"", "");
//                    String lastName = jsonObjectIteration.get("lastName").toString().replace("\"", "");
//                    String fullName = firstName + " " + lastName;

                    String teacherFirstName = jsonObjectIteration.get("firstName").toString().replace("\"", "");
                    String teacherLastName = jsonObjectIteration.get("lastName").toString().replace("\"", "");
                    String studentFirstName = jsonObjectIteration.get("firstName").toString().replace("\"", "");
                    String studentLastName = jsonObjectIteration.get("lastName").toString().replace("\"", "");

                    String studentFullName = studentFirstName + " " + studentLastName;
                    String teacherFullName = teacherFirstName + " " + teacherLastName;

                    String parentZoneId = jsonObjectIteration.get("id").toString().replace("\"", "");
                    String parentZoneReply = jsonObjectIteration.get("parentZoneReply").toString().replace("\"", "");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                    try {
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                        Date datepost = dateFormat.parse(parentZonePostDate);
                        targetDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        postDate = targetDateFormat.format(datepost);
                        if (parentZoneCategory.contains("Meeting")) {
                            Date meetingDate = dateFormat.parse(parentZoneSchedule);
                            meetingSchedule = targetDateFormat.format(meetingDate);
                        } else {
                            meetingSchedule = null;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (parentZoneCategory.equals("Leave")) {
                        try {
                            SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());
                            Date dateStart = dateFormat.parse(parentZoneLeaveStartDate);
                            Date dateEnd = dateFormat.parse(parentZoneLeaveEndDate);
                            String startDate = targetDateFormat.format(dateStart);
                            String endDate = targetDateFormat.format(dateEnd);

                            if (userRole.equals("Student")) {
                                leavesArrayList.add(0, new LeavesListData(parentZoneId, studentId, postDate, parentZoneDescription, parentZoneStatus, startDate, endDate, studentFullName, parentZoneReply, studentRollNo, Integer.parseInt(leaveDayCount)));
                            } else {
                                String studentRollNo = jsonObjectIteration.get("rollNo").toString().replace("\"", "");
                                leavesArrayList.add(0, new LeavesListData(parentZoneId, studentId, postDate, parentZoneDescription, parentZoneStatus, startDate, endDate, studentFullName, parentZoneReply, studentRollNo, Integer.parseInt(leaveDayCount)));
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        leave_map.put("Leave", leavesArrayList);
                    } else {
                        if (userRole.equals("Teacher")) {
                            studentRollNo = jsonObjectIteration.get("rollNo").toString().replace("\"", "");
                            notesArrayList.add(0, new NotesListData(parentZoneId, studentId, parentZoneDescription, parentZoneCategory, postDate, meetingSchedule, parentZoneStatus, parentZoneReply, teacherFullName, studentFullName, studentRollNo));
                        } else {
                            notesArrayList.add(0, new NotesListData(parentZoneId, studentId, parentZoneDescription, parentZoneCategory, postDate, meetingSchedule, parentZoneStatus, parentZoneReply, teacherFullName, studentFullName, studentRollNo));
                        }
                        notes_map.put("Notes", notesArrayList);
                    }

                }
                progressDialog.dismiss();
                createAndSetFragments();
            }

        } catch (Exception e) {
            System.out.println("Exception in ParentParsing " + e.getMessage());
        }
    }


    private void createAndSetFragments() {
        setLeaveFragment();
        setNotesFragment();
        currentTab = pager.getCurrentItem();
        setTabs();
        pager.setCurrentItem(currentTab);
    }

    private void setLeaveFragment() {
        Bundle fragmentBundle = new Bundle();
        System.out.println(" leave_map.get(Leave) " + leave_map.get("Leave"));
        fragmentBundle.putSerializable("LeaveList", (Serializable) leave_map.get("Leave"));
        Parent_control_Leave_Fragment parentControlLeaveFragment = new Parent_control_Leave_Fragment();
        parentControlLeaveFragment.setArguments(fragmentBundle);
        fragmentsNameList.add("Leave");
        fragmentsList.add(parentControlLeaveFragment);
    }

    private void setNotesFragment() {
        Bundle fragmentBundle = new Bundle();
        System.out.println(" notes_map.get(Notes) " + notes_map.get("Notes"));
        fragmentBundle.putSerializable("NotesList", (Serializable) notes_map.get("Notes"));
        Parent_control_Notes_Fragment parentControlNotesFragment = new Parent_control_Notes_Fragment();
        parentControlNotesFragment.setArguments(fragmentBundle);
        fragmentsNameList.add("Notes");
        fragmentsList.add(parentControlNotesFragment);
    }

    private void setTabs() {
        FragmentManager manager = getSupportFragmentManager();
        adapter = new ParentControlPagerAdapter(manager, fragmentsList, fragmentsNameList);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTabsFromPagerAdapter(adapter);
        pager.setCurrentItem(currentTab);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        pager.setCurrentItem(0);
        if (connectionDetector.isConnectingToInternet()) {
            CallingParent();
            System.out.println(" connectionDetector ");
        } else {
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }


}
