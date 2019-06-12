package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.relecotech.androidsparsh_smallwonder.adapters.ExamActivityAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ExamTimeTableListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ExamTimeTableActivity extends AppCompatActivity {

    private ListView examTimeTableListView;
    private ProgressDialog progressDialog;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ExamTimeTableListData examTimeTableListData;
    private List<ExamTimeTableListData> examTimeTableList;
    private Map<String, List<ExamTimeTableListData>> examTimeTableHashMap;
    private String examType;
    private List<ExamTimeTableListData> examDisplayTimeTableList;
    private Date todayDate;
    private Date mainDate;
    private TextView noDataAvailableTextView;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_time_table);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        examDisplayTimeTableList = new ArrayList<>();
        examTimeTableList = new ArrayList<>();
        examTimeTableHashMap = new HashMap<>();

        noDataAvailableTextView = (TextView) findViewById(R.id.examTimeTableNoDataAvailableTextView);
        examTimeTableListView = (ListView) findViewById(R.id.exam_time_table_listView);

        if (connectionDetector.isConnectingToInternet()) {
            FetchExamTimeTableData();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        examTimeTableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ExamTimeTableListData tableListData = examDisplayTimeTableList.get(i);
                Intent examIntent = new Intent(ExamTimeTableActivity.this, ExamTimeTableDetail.class);
//                examIntent.putExtra("examId", tableListData.getExamId());
                examIntent.putExtra("examType", tableListData.getExamTitle());
                examIntent.putExtra("ExamData", (Serializable) examTimeTableHashMap.get(examDisplayTimeTableList.get(i).getExamId()));
                startActivity(examIntent);
            }
        });
    }


    private void FetchExamTimeTableData() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
//            jsonObject.addProperty("TAG", "EXAM");
            jsonObject.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_ID));
            jsonObject.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
            jsonObject.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));

            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("examFetchTimeTable", jsonObject);

            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println("FetchExamTimeTableData exception " + exception);
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ExamTimeTableActivity.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FetchExamTimeTableData();
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
                    System.out.println(" FetchExamTimeTableData  response " + response);
                    ExamTimeTableJsonParse(response);
                }
            });

        } catch (Exception e) {
            System.out.println("Exception in FetchExamTimeTableData " + e.getMessage());
        }
    }

    public void ExamTimeTableJsonParse(JsonElement response) {
        try {
            JsonArray examJsonArray = response.getAsJsonArray();
            if (examJsonArray.size() == 0) {
                noDataAvailableTextView.setText(R.string.noDataAvailable);
                progressDialog.dismiss();
                System.out.println("examJsonArray not received");
            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                for (int examReportLoop = 0; examReportLoop < examJsonArray.size(); examReportLoop++) {
                    JsonObject jsonObjectForIteration = examJsonArray.get(examReportLoop).getAsJsonObject();

                    String examId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    examType = jsonObjectForIteration.get("examType").toString().replace("\"", "");
                    String examDate = jsonObjectForIteration.get("examDate").toString().replace("\"", "");
                    String tentativeDateComment = jsonObjectForIteration.get("tentativeDateComment").toString().replace("\"", "");
                    String examDateParsed = jsonObjectForIteration.get("examScheduleDate").toString().replace("\"", "");
                    String examSubject = jsonObjectForIteration.get("examScheduleSubject").toString().replace("\"", "");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
                    Date startDate = null;
                    Date examDateDate = null;
                    try {
                        startDate = simpleDateFormat.parse(examDateParsed);
                        examDateDate = simpleDateFormat.parse(examDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    SimpleDateFormat examDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
                    String examDateString = examDateFormat.format(startDate);

                    String examDateMain = examDateFormat.format(examDateDate);

//                Calendar cal = Calendar.getInstance();
//                String formatted = examDateFormat.format(cal.getTime());
//                System.out.println(" formatted " + formatted);
//                try {
//                    todayDate = examDateFormat.parse(formatted);
//                    mainDate = examDateFormat.parse(examDateMain);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

//                if (todayDate.after(mainDate)){
//                    System.out.println(" todayDate is Greater");
//                }
//                if (todayDate.before(mainDate)) {
//                } else {
//                }

                    System.out.println(" examDateMain " + examDateMain);

                    long getTime = startDate.getTime();
                    Date date = new Date(getTime);
                    //Note: small h for time in 12hr format
                    //Note: H for time in 24hr format
                    DateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    String examTime = formatter.format(date);
                    if (examTimeTableHashMap.containsKey(examId)) {

                        examTimeTableList = examTimeTableHashMap.get(examId);
                        examTimeTableListData = new ExamTimeTableListData(examId, examType, examDateMain, tentativeDateComment, examDateString, examTime, examSubject);
                        examTimeTableList.add(examTimeTableListData);
                        examTimeTableHashMap.put(examId, examTimeTableList);
                    } else {
                        examTimeTableList = new ArrayList<>();
                        examTimeTableListData = new ExamTimeTableListData(examId, examType, examDateMain, tentativeDateComment, examDateString, examTime, examSubject);
                        examTimeTableList.add(examTimeTableListData);
                        examTimeTableHashMap.put(examId, examTimeTableList);

                        examDisplayTimeTableList.add(examTimeTableListData);
                    }
                }

                Collections.sort(examDisplayTimeTableList, new Comparator<ExamTimeTableListData>() {
                    @Override
                    public int compare(ExamTimeTableListData obj1, ExamTimeTableListData obj2) {
                        // ## Ascending order
                        System.out.println("obj1.getExamDate().compareToIgnoreCase(obj2.getExamDate()) " + obj1.getExamDate().compareToIgnoreCase(obj2.getExamDate()));
                        return obj1.getExamDateMain().compareToIgnoreCase(obj2.getExamDateMain()); // To compare string values
                    }
                });
//            for (Map.Entry<String, List<ExamTimeTableListData>> e : examTimeTableHashMap.entrySet()) {
//                String key = e.getKey();
//                List<ExamTimeTableListData> value = e.getValue();
//                System.out.println(" key " + key + " value " + value.size());
//            }

                ExamActivityAdapter examTimeTableAdapter = new ExamActivityAdapter(this, examDisplayTimeTableList);
                examTimeTableListView.setAdapter(examTimeTableAdapter);
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            System.out.println("Exception in examJsonArray Parsing " + e.getMessage());
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
