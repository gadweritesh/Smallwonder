package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
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
import com.relecotech.androidsparsh_smallwonder.adapters.AbsentStudentAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AbsentStudentListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Amey on 29-05-2018.
 */

public class AbsentAttendanceReview extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private TextView noDataAvailableTextView;
    private ListView absentAttendanceReviewListView;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private AbsentStudentListData absentStudentListData;
    private List<AbsentStudentListData> absentStudentList;
    private AbsentStudentAdapter absentStudentAdapter;
    private TextView countOfAbsentTextView;
    private TextView absentAttendanceDateTextView;
    private MobileServiceClient mClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absent_attendance_review);

        mClient = Singleton.Instance().mClientMethod(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noDataAvailableTextView = (TextView) findViewById(R.id.noDataAvailableTextView);
        countOfAbsentTextView = (TextView) findViewById(R.id.countOfAbsentTextView);
        absentAttendanceDateTextView = (TextView) findViewById(R.id.absentAttendanceDateTextView);
        absentAttendanceReviewListView = (ListView) findViewById(R.id.absentAttendanceReviewListView);

        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        absentStudentList = new ArrayList<>();


        Date currentDate = new Date();
        SimpleDateFormat curreDateFormate = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
        System.out.println("Current Date " + curreDateFormate.format(currentDate));
        absentAttendanceDateTextView.setText(curreDateFormate.format(currentDate));

        GetStudentAbsentList();

        progressDialog = new ProgressDialog(AbsentAttendanceReview.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(this.getString(R.string.loading));
    }


    public void GetStudentAbsentList() {
        //   progressDialog.show();

        JsonObject jsonObjectParametersForAbsentStudent = new JsonObject();
        jsonObjectParametersForAbsentStudent.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchAbsentStudentsApi", jsonObjectParametersForAbsentStudent);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Api Failure Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                try {
                    System.out.println(" Try Success Attendance " + response);
                    ParseJson(response);
                } catch (Exception e) {
                    System.out.println(" Catch Error  Attendance ");
                    System.out.println(" Error Mesage---" + e.getMessage());
                }
            }
        });

    }

    public void ParseJson(JsonElement response) {
        System.out.println("response---------------" + response);

        if (response == null || response.equals(null)) {
            System.out.println("Json Response null Please check");
            noDataAvailableTextView.setVisibility(View.VISIBLE);
        } else {
            progressDialog.dismiss();

            noDataAvailableTextView.setVisibility(View.INVISIBLE);
            JsonArray getAbsentStudentJsonArray = response.getAsJsonArray();
            System.out.println("getAbsentStudentJsonArray--------- " + getAbsentStudentJsonArray.size());

            if (getAbsentStudentJsonArray.size() == 0) {
                noDataAvailableTextView.setVisibility(View.VISIBLE);
            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
            }

            for (int loopForIteration = 0; loopForIteration < getAbsentStudentJsonArray.size(); loopForIteration++) {
                JsonObject jsonObjectForIteration = getAbsentStudentJsonArray.get(loopForIteration).getAsJsonObject();

                String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                String studentEnrollmentNo = jsonObjectForIteration.get("studentEnrollmentNo").toString().replace("\"", "");
                String studentclass = jsonObjectForIteration.get("class").toString().replace("\"", "");
                String studentdivision = jsonObjectForIteration.get("division").toString().replace("\"", "");


                String student_fullName = student_firstName + " " + student_lastName;
                String student_schoolclassdivision = studentclass + " " + studentdivision;

                Integer serialNo = loopForIteration + 1;
                absentStudentListData = new AbsentStudentListData(student_fullName, String.valueOf(serialNo), student_schoolclassdivision);
                absentStudentList.add(absentStudentListData);
            }

            System.out.println("getAbsentStundetList-----------------" + absentStudentList.size());
            int absentStudentCount = absentStudentList.size();
            String formatted = String.format("%02d", absentStudentCount);
            countOfAbsentTextView.setText("#" + formatted);

            absentStudentAdapter = new AbsentStudentAdapter(AbsentAttendanceReview.this, absentStudentList);
            absentAttendanceReviewListView.setAdapter(absentStudentAdapter);
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
