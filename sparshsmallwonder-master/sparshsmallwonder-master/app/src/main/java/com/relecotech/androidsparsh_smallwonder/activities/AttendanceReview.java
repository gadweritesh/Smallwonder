package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.relecotech.androidsparsh_smallwonder.adapters.AttendanceReviewAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceReviewListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AttendanceReview extends AppCompatActivity {
    ArrayList<AttendanceReviewListData> attendanceReviewList;
    private ConnectionDetector connectionDetector;
    private String schoolClassIdForReview;
    private TextView reviewPresentCounterTextView, reviewAbsentCounterTextView, dateTextView, classTextView;
    private TextView noAttendanceLabelBodyTextView;
    private TextView noAttendanceLabelHeaderTextView;
    private LinearLayout footerLayout;
    private ListView attendanceReviewListView;
    private SimpleDateFormat targetDateFormat;
    private Date dateForReview;
    private String finalDate;
    private int counterForPresent = 0;
    private int counterForAbsent = 0;
    private ProgressDialog progressDialog;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_review);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        reviewPresentCounterTextView = (TextView) findViewById(R.id.reviewPresentCountTextView);
        reviewAbsentCounterTextView = (TextView) findViewById(R.id.reviewAbsentCountTextView);
        dateTextView = (TextView) findViewById(R.id.attendanceReviewClassDateTextView);
//        classTextView = (TextView) findViewById(R.id.attendanceReviewClassTextView);
        footerLayout = (LinearLayout) findViewById(R.id.attendanceReviewFooterLayout);
        noAttendanceLabelHeaderTextView = (TextView) findViewById(R.id.noAttendanceAvailableHeaderTextView);
        noAttendanceLabelBodyTextView = (TextView) findViewById(R.id.noAttendanceAvailableBodyTextView);
        attendanceReviewListView = (ListView) findViewById(R.id.reviewAttendanceListView);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        attendanceReviewList = new ArrayList<>();

        //getting data through intent   // to fetch student attendance review list
        schoolClassIdForReview = getIntent().getStringExtra("reviewClassId");
        String attendanceClass = getIntent().getStringExtra("class");
        String attendanceDivision = getIntent().getStringExtra("division");
//        classTextView.setText(attendanceClass +  " " + attendanceDivision);

        try {
            DateFormat sourceDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            dateForReview = sourceDateFormat.parse(getIntent().getStringExtra("reviewDate"));
            targetDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.print("exception in date conversion from intent string to date");
        }

        if (targetDateFormat != null) {
            finalDate = targetDateFormat.format(dateForReview);
            dateTextView.setText("On  " + targetDateFormat.format(dateForReview));
        }

        if (connectionDetector.isConnectingToInternet()) {
            AttendanceReviewCallingApi();
        } else {
            FancyToast.makeText(AttendanceReview.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        for (int i = 0; i < attendanceReviewList.size(); i++) {
            AttendanceReviewListData countAttendanceReviewListData = attendanceReviewList.get(i);
            if (countAttendanceReviewListData.getPresentStatus() == "P") {
                counterForPresent++;
            }
            if (countAttendanceReviewListData.getPresentStatus() == "A") {
                counterForAbsent++;
            }
        }
        reviewPresentCounterTextView.setText(Integer.toString(counterForPresent));
        reviewAbsentCounterTextView.setText(Integer.toString(counterForAbsent));
    }


    private void AttendanceReviewCallingApi() {
        JsonObject jsonObjectToReviewAttendance = new JsonObject();
        jsonObjectToReviewAttendance.addProperty("reviewDate", finalDate);
        jsonObjectToReviewAttendance.addProperty("userRole", "Teacher");
        jsonObjectToReviewAttendance.addProperty("schoolClassId", schoolClassIdForReview);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("attendanceReviewApi", jsonObjectToReviewAttendance);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("  Attendance Review Exception    " + exception);

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AttendanceReview.this)
                                .setMessage(R.string.check_network)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AttendanceReviewCallingApi();
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
                System.out.println(" Attendance Review API   response    " + response);
                AttendanceReviewParsing(response);
            }
        });

    }

    private void AttendanceReviewParsing(JsonElement response) {

        try {
            JsonArray attendanceArray = response.getAsJsonArray();
            if (attendanceArray.size() == 0) {
                noAttendanceLabelHeaderTextView.setText(R.string.noDataAvailable);
                noAttendanceLabelHeaderTextView.setVisibility(View.VISIBLE);
                noAttendanceLabelBodyTextView.setVisibility(View.VISIBLE);
                noAttendanceLabelBodyTextView.setText("For " + finalDate);
                footerLayout.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
            } else {
                int totalStudentPresent = 0;
                int totalStudentAbsent = 0;
                for (int loop = 0; loop < attendanceArray.size(); loop++) {

                    JsonObject jsonObjectForIteration = attendanceArray.get(loop).getAsJsonObject();
                    String rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                    String first_name = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_middleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
                    String last_name = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String present_status = jsonObjectForIteration.get("attendanceStatus").toString().replace("\"", "");
                    System.out.print("student_count : " + loop + " rollNo : " + rollNo + " first_name : " + first_name + " student_middleName : " + student_middleName + " last_name : " + last_name + " present_status : " + present_status);

                    //counting total absent and present students
                    if (present_status.equals("A")) {
                        totalStudentAbsent++;
                    }
                    if (present_status.equals("P")) {
                        totalStudentPresent++;
                    }
                    AttendanceReviewListData attendanceReviewdata = new AttendanceReviewListData(first_name, student_middleName, last_name, present_status, rollNo);
                    attendanceReviewList.add(attendanceReviewdata);
                }

                //set data to adapter
                AttendanceReviewAdapter attendanceReviewStudentadapter = new AttendanceReviewAdapter(getApplicationContext(), attendanceReviewList);
                attendanceReviewListView.setAdapter(attendanceReviewStudentadapter);

                //set present and absent counts to respective textViews
                footerLayout.setVisibility(View.VISIBLE);
                reviewPresentCounterTextView.setText(String.valueOf(totalStudentPresent));
                reviewAbsentCounterTextView.setText(String.valueOf(totalStudentAbsent));
                progressDialog.dismiss();
            }

        } catch (Exception e) {
            e.printStackTrace();
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

}
