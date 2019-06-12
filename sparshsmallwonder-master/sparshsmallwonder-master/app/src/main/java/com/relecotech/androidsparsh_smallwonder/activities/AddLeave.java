package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.azureControllers.School_Class;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Teacher;
import com.relecotech.androidsparsh_smallwonder.fragments.DatePickerFragment;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AddLeave extends AppCompatActivity implements DatePickerFragment.DateDialogListener {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private TextView leaveFromTextView, leaveToTextView;
    private ProgressDialog progressDialog;
    private EditText leaveDescEditText;
    private EditText dayCountEditText;
    private Date dateObjectFor_FromDate;
    private Date dateObjectFor_TODate;
    private JsonObject jsonObjectAddLeave;
    private int dateClickFlag;
    private Spinner spinnerTeacherName;
    private MobileServiceTable<Teacher> mTeacherTable;
    private MobileServiceTable<School_Class> mSchoolClassTable;
    private List<String> teacherNameList;
    private Map<String, String> teacherNameMap;
    private String teacherIdForLeave;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_leave);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        mSchoolClassTable = mClient.getTable(School_Class.class);




        leaveFromTextView = (TextView) findViewById(R.id.leavesFromTextView);
        leaveToTextView = (TextView) findViewById(R.id.leavesToTextView);
        dayCountEditText = (EditText) findViewById(R.id.leavesCount);
        dayCountEditText.setText("1");
        leaveDescEditText = (EditText) findViewById(R.id.leavesCauseEditText);
        Button leaveSubmitBtn = (Button) findViewById(R.id.leavesSubmitButton);

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonths = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);


        if (connectionDetector.isConnectingToInternet()) {
            new GetTeacherId().execute();
        } else {
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
//        } else {
//            teacherIdForLeave = userDetails.get(SessionManager.KEY_TEACHER_ID);
//        }


        String currentDate = currentDay + "-" + currentMonths + "-" + currentYear;
        leaveFromTextView.setText(currentDate);
        leaveToTextView.setText(currentDate);

        leaveFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateClickFlag = 1;
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("dob", leaveFromTextView.getText().toString());
                bundle.putString("value", "greater");
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });

        leaveToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateClickFlag = 2;
                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("dob", leaveToTextView.getText().toString());
                bundle.putString("value", "greater");
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });

        leaveSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long getExactDaysOfCount = DaysCount(leaveFromTextView.getText().toString(), leaveToTextView.getText().toString());
                System.out.println("getEaxctDaysOfCount " + getExactDaysOfCount);
                int dayCountOfLeave = Integer.parseInt(dayCountEditText.getText().toString());

                if (connectionDetector.isConnectingToInternet()) {
//                    if (!teacherIdForLeave.isEmpty()) {
                    if (teacherIdForLeave != null) {
//                    if (!spinnerTeacherName.getSelectedItem().toString().contains("[ Teacher Name ]")) {
                        if (getExactDaysOfCount > 0) {
                            if (dayCountOfLeave <= getExactDaysOfCount || dayCountEditText.getText().toString().equals("1")) {
                                if (leaveDescEditText.getText().toString().length() > 0) {

                                    AddLeaveApiCall();
                                } else {
                                    FancyToast.makeText(AddLeave.this, "Add cause for the leave", FancyToast.WARNING, FancyToast.ERROR, false).show();
                                }
                            } else {
                                FancyToast.makeText(AddLeave.this, "Please Enter Correct Day Count", FancyToast.WARNING, FancyToast.ERROR, false).show();
                            }
                        } else {
                            FancyToast.makeText(AddLeave.this, "Leave To Date should be greater than Leave From date", FancyToast.WARNING, FancyToast.ERROR, false).show();
                        }
                    } else {
                        FancyToast.makeText(AddLeave.this, "No Class Teacher Assigned.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                } else {
                    FancyToast.makeText(AddLeave.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });
    }


    private class GetTeacherId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                System.out.println(" userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID) " + userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                List<School_Class> result = mSchoolClassTable.where().field("id").eq(userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID))
                        .select("Teacher_id", "id").execute().get();
                System.out.println("result " + result.get(0).getId());
                System.out.println("result " + result.get(0).getSchoolClass());
                System.out.println("result " + result.get(0).getTeacher_id());

                teacherIdForLeave = result.get(0).getTeacher_id();
//                sessionManager.setSharedPrefItem(SessionManager.KEY_TEACHER_ID, teacherIdForLeave);
            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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

    public void JsonObject() {

        Calendar calendarDate = Calendar.getInstance();
        long fromDateMilli = 0;
        long toDateMilli = 0;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            dateObjectFor_FromDate = format.parse(leaveFromTextView.getText().toString());
            dateObjectFor_TODate = format.parse(leaveToTextView.getText().toString());
            System.out.println(" dateObjectFor_FromDate " + dateObjectFor_FromDate);
            System.out.println(" dateObjectFor_TODate " + dateObjectFor_TODate);
            calendarDate.setTime(dateObjectFor_FromDate);
            fromDateMilli = calendarDate.getTimeInMillis();
            calendarDate.setTime(dateObjectFor_TODate);
            toDateMilli = calendarDate.getTimeInMillis();
        } catch (ParseException e) {
            System.out.println(" Add leave date parse exception " + e.getMessage());
        }

        Calendar calendar = Calendar.getInstance();

        jsonObjectAddLeave = new JsonObject();
        System.out.println(" dayCountEditText.getText().toString() " + dayCountEditText.getText().toString());
        jsonObjectAddLeave.addProperty("No_of_days", dayCountEditText.getText().toString());
        jsonObjectAddLeave.addProperty("Description_cause", leaveDescEditText.getText().toString());
        jsonObjectAddLeave.addProperty("Category", "Leave");
        jsonObjectAddLeave.addProperty("StartDate", fromDateMilli);
        jsonObjectAddLeave.addProperty("EndDate", toDateMilli);
        jsonObjectAddLeave.addProperty("Student_id", userDetails.get(SessionManager.KEY_STUDENT_ID));

        jsonObjectAddLeave.addProperty("Teacher_id", teacherIdForLeave);
//        jsonObjectAddLeave.addProperty("Teacher_id", teacherIdForLeave);
        jsonObjectAddLeave.addProperty("School_class_id", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectAddLeave.addProperty("Branch_id", userDetails.get(SessionManager.KEY_BRANCH_ID));
        jsonObjectAddLeave.addProperty("PostDate", calendar.getTimeInMillis());
        jsonObjectAddLeave.addProperty("Status", "Pending");
        jsonObjectAddLeave.addProperty("Reply", "");
        jsonObjectAddLeave.addProperty("MeetingSchedule", "");
        jsonObjectAddLeave.addProperty("Active", 1);

    }

    private void AddLeaveApiCall() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("parentZoneInsertApi", jsonObjectAddLeave);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Add Leave API exception    " + exception);
                failureDialog();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("  Add Leave API   response    " + response);

                if (response.toString().equals("true")) {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddLeave.this)
                            .setTitle("Leave Submitted.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                } else {
                    failureDialog();
                }
            }
        });
    }

    private void failureDialog() {
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
                new AlertDialog.Builder(AddLeave.this)
                        .setMessage(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddLeaveApiCall();
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
    public void onFinishDialog(String date) {
        if (dateClickFlag == 1) {
            leaveFromTextView.setText(date);
        } else {
            leaveToTextView.setText(date);
        }
    }

    public long DaysCount(String fromdate, String todate) {

        Date toDate = null;
        Date fromDate = null;
        long getDaysCount = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            toDate = dateFormat.parse(todate);
            fromDate = dateFormat.parse(fromdate);
            System.out.println("todate------" + todate);
            System.out.println("fromdate------" + fromdate);
            /*get Date  difference milliseconds */
            //  long diffBetweenFromDateAndToDate = fromDate.getTime() - toDate.getTime();
            long diffBetweenFromDateAndToDate = toDate.getTime() - fromDate.getTime();
            getDaysCount = diffBetweenFromDateAndToDate / (24 * 60 * 60 * 1000);
            System.out.println("getDaysCout " + getDaysCount);

        } catch (Exception e) {
            System.out.println("Days cout Exception----" + e.getMessage());
        }

        return ++getDaysCount;
    }
}
