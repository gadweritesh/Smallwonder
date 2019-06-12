package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.ExamTimeTableAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ExamTimeTableListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ExamTimeTableDetail extends AppCompatActivity {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ListView examDetailListView;
    private ProgressDialog progressDialog;
    private List<ExamTimeTableListData> examTimeTableList;
    private String examId, examType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        examDetailListView = (ListView) findViewById(R.id.exam_detail_listView);
        TextView examDetailTitleTv = (TextView) findViewById(R.id.exam_detail_title_textView);

        examTimeTableList = new ArrayList<>();
        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

//        examId = getIntent().getStringExtra("examId");
        examType = getIntent().getStringExtra("examType");

        examDetailTitleTv.setText(examType);
//        if (connectionDetector.isConnectingToInternet()) {
//            FetchExamTimeTableDetailData();
//        } else {
//            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
//        }

        ArrayList<ExamTimeTableListData> examTimeTableList = (ArrayList<ExamTimeTableListData>) getIntent().getSerializableExtra("ExamData");
//
        ExamTimeTableAdapter examTimeTableAdapter = new ExamTimeTableAdapter(this, examTimeTableList);
        examDetailListView.setAdapter(examTimeTableAdapter);
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


//    private void FetchExamTimeTableDetailData() {
//        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
//        try {
//
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("examId", examId);
////            jsonObject.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
//            jsonObject.addProperty("TAG", "EXAMDETAIL");
////            jsonObject.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_ID));
////            jsonObject.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
////            jsonObject.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
//
//            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
//            ListenableFuture<JsonElement> serviceFilterFuture = LauncherActivity.mClient.invokeApi("examFetchTimeTable", jsonObject);
//
//            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
//                @Override
//                public void onFailure(Throwable exception) {
//                    resultFuture.setException(exception);
//                    System.out.println("FetchExamTimeTableData exception " + exception);
//                    Runnable progressRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            new AlertDialog.Builder(ExamTimeTableDetail.this)
//                                    .setMessage(R.string.check_network)
//                                    .setCancelable(false)
//                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            FetchExamTimeTableDetailData();
//                                        }
//                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                }
//                            }).show();
//                        }
//                    };
//                    Handler pdCanceller = new Handler();
//                    pdCanceller.postDelayed(progressRunnable, 5000);
//                }
//
//                @Override
//                public void onSuccess(JsonElement response) {
//                    resultFuture.set(response);
//                    System.out.println(" FetchExamTimeTableData  response " + response);
//                    ClassScheduleJsonParse(response);
//                }
//            });
//
//        } catch (Exception e) {
//            System.out.println("Exception in FetchExamTimeTableData " + e.getMessage());
//        }
//    }
//
//    public void ClassScheduleJsonParse(JsonElement response) {
////        try {
//        JsonArray examJsonArray = response.getAsJsonArray();
//        if (examJsonArray.size() == 0) {
//            progressDialog.dismiss();
//            System.out.println("examJsonArray not received");
//        } else {
//            for (int examReportLoop = 0; examReportLoop < examJsonArray.size(); examReportLoop++) {
//                JsonObject jsonObjectForIteration = examJsonArray.get(examReportLoop).getAsJsonObject();
//
////                String examId = jsonObjectForIteration.get("id").toString().replace("\"", "");
////                String examType = jsonObjectForIteration.get("examType").toString().replace("\"", "");
//                String examDateParsed = jsonObjectForIteration.get("examScheduleDate").toString().replace("\"", "");
//                String examSubject = jsonObjectForIteration.get("examScheduleSubject").toString().replace("\"", "");
////
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
//                Date startDate = null;
//                try {
//                    startDate = simpleDateFormat.parse(examDateParsed);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                SimpleDateFormat examDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
//                String examDateString = examDateFormat.format(startDate);
//
//                long getTime = startDate.getTime();
//                Date date = new Date(getTime);
//                //Note: small h for time in 12hr format
//                //Note: H for time in 24hr format
//                DateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
//                String examTime = formatter.format(date);
////
//                ExamTimeTableListData tableListData = new ExamTimeTableListData("",examType, examDateString, examTime, examSubject);
//                examTimeTableList.add(tableListData);
//
//            }
//
//            ExamTimeTableAdapter examTimeTableAdapter = new ExamTimeTableAdapter(this, examTimeTableList);
//            examDetailListView.setAdapter(examTimeTableAdapter);
//            progressDialog.dismiss();
//        }
////        } catch (Exception e) {
////            System.out.println("Exception in examJsonArray Parsing " + e.getMessage());
////        }
//    }

}
