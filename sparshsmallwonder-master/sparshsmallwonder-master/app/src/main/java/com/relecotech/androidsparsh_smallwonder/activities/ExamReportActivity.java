package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.relecotech.androidsparsh_smallwonder.adapters.ExamResultHistoryAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ExamSubjectResultListData;
import com.relecotech.androidsparsh_smallwonder.models.ExamSummaryListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

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
import java.util.TimeZone;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ExamReportActivity extends AppCompatActivity {

    private ListView examResultHistoryListView;
    private CardView summaryCardView;
    private TextView examTitleTextView, finalGradeTextView, examResultSummaryTextView, examSummaryCommentTextView;
    private ConnectionDetector connectionDetector;
    private ProgressDialog progressDialog;

    private HashMap<String, String> userDetails;
    private SessionManager sessionManager;
    private Map<ExamSummaryListData, List<ExamSubjectResultListData>> Exam_Report_Subject_Map;
    private ArrayList<ExamSummaryListData> examSummaryList;
    private ExamSummaryListData examSummaryListData;
    private ExamSubjectResultListData examSubjectResultListData;
    private List<ExamSubjectResultListData> examSubjectResultList;
    private ExamResultHistoryAdapter examReportSumaaryAdapter;
    private SimpleDateFormat targetDateFormat;
    private Date examDateGlobal;
    private TextView detailTextView;
    private int listItemPosition;
    private TextView examNoDataAvailableTextView;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClient = Singleton.Instance().mClientMethod(this);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        examResultHistoryListView = (ListView) findViewById(R.id.examResultHistoryListView);
        summaryCardView = (CardView) findViewById(R.id.examReportSummaryCardView);
        examTitleTextView = (TextView) findViewById(R.id.examTitleTextView);
        finalGradeTextView = (TextView) findViewById(R.id.finalGradeTextView);
        examResultSummaryTextView = (TextView) findViewById(R.id.examResultSummaryTextView);
        examSummaryCommentTextView = (TextView) findViewById(R.id.examSummaryCommentTextView);
        examNoDataAvailableTextView = (TextView) findViewById(R.id.examNoDataTextView);

        examSummaryList = new ArrayList<>();
        Exam_Report_Subject_Map = new HashMap<>();

        if (connectionDetector.isConnectingToInternet()) {
            CallingExamApi();
        } else {
            FancyToast.makeText(ExamReportActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        examResultHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Item Position " + examSummaryList.get(position));
                listItemPosition = position;
                examTitleTextView.setText(examSummaryList.get(position).getExamType());
                examResultSummaryTextView.setText(examSummaryList.get(position).getObtainedMarks() + " / " + examSummaryList.get(position).getTotalMarks());
                finalGradeTextView.setText(examSummaryList.get(position).getExamFinalGrade());

            }
        });


        summaryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<ExamSubjectResultListData> getValueOfExam = Exam_Report_Subject_Map.get(examSummaryList.get(listItemPosition));
                    Intent intent = new Intent(ExamReportActivity.this, ExamSubjectResultView.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("examList", (ArrayList<? extends android.os.Parcelable>) getValueOfExam);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println(" Exception " + e.getMessage());
                }
            }
        });

    }


    private void CallingExamApi() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectForExamResult = new JsonObject();
        jsonObjectForExamResult.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("examResultFetch", jsonObjectForExamResult);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Exam Result Exception  " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ExamReportActivity.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CallingExamApi();
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
                System.out.println(" Exam Result Response   " + response);
                ParseJsonResponse(response);
            }
        });
    }

    private void ParseJsonResponse(JsonElement response) {
        try {

            JsonArray getExamReportJsonArray = response.getAsJsonArray();
            if (getExamReportJsonArray.size() == 0) {
                progressDialog.dismiss();
                examNoDataAvailableTextView.setVisibility(View.VISIBLE);
                examResultSummaryTextView.setText("0 / 0");
                finalGradeTextView.setText("-");
                System.out.println("Response is not Null But");
            } else {
                examNoDataAvailableTextView.setVisibility(View.INVISIBLE);
                for (int examReportLoop = 0; examReportLoop < getExamReportJsonArray.size(); examReportLoop++) {
                    JsonObject jsonObjectForIteration = getExamReportJsonArray.get(examReportLoop).getAsJsonObject();

                    String examDateParsed = jsonObjectForIteration.get("examDate").toString().replace("\"", "");
                    String examType = jsonObjectForIteration.get("examType").toString().replace("\"", "");
                    String examResultSummaryComment = jsonObjectForIteration.get("resultSummaryComment").toString().replace("\"", "");
                    String examSubjectFinalGrade = jsonObjectForIteration.get("finalGrade").toString().replace("\"", "");
                    String examSubjectTotalMarkObatained = jsonObjectForIteration.get("totalMarkObtained").toString().replace("\"", "");
                    String outOfMark = jsonObjectForIteration.get("outOfMark").toString().replace("\"", "");
                    String examSubject = jsonObjectForIteration.get("subject").toString().replace("\"", "");
                    String examSubjectGrade = jsonObjectForIteration.get("subjectGrade").toString().replace("\"", "");
                    String examSubjectObtainedMark = jsonObjectForIteration.get("subjectObtainedMarks").toString().replace("\"", "");
                    String examSubjectOutOfMark = jsonObjectForIteration.get("subjectOutOfMarks").toString().replace("\"", "");
                    String examSubjectComment = jsonObjectForIteration.get("subjectResultComment").toString().replace("\"", "");


                    SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS", Locale.getDefault());
                    sourceDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                    try {
                        Date examDate = sourceDateFormat.parse(examDateParsed);
                        examDateGlobal = examDate;
                    } catch (ParseException e) {
                        System.out.println("Exception--occurred in date " + e.getMessage());
                    }


                    if (Exam_Report_Subject_Map.containsKey(examSummaryListData) && examSummaryListData.getExamType().equals(examType)) {
                        System.out.println("map contain value..");

                        examSubjectResultList = Exam_Report_Subject_Map.get(examSummaryListData);
                        examSubjectResultListData = new ExamSubjectResultListData(examSubject, examSubjectObtainedMark, examSubjectOutOfMark, examSubjectGrade, examSubjectComment);
                        examSubjectResultList.add(examSubjectResultListData);
                        Exam_Report_Subject_Map.put(examSummaryListData, examSubjectResultList);


                    } else {
                        System.out.println("Map is Empty first time value is add");

                        examSummaryListData = new ExamSummaryListData(examType, outOfMark, examSubjectTotalMarkObatained, examResultSummaryComment, examDateGlobal, examSubjectFinalGrade);
                        examSubjectResultListData = new ExamSubjectResultListData(examSubject, examSubjectObtainedMark, examSubjectOutOfMark, examSubjectGrade, examSubjectComment);
                        examSubjectResultList = new ArrayList<>();
                        examSubjectResultList.add(examSubjectResultListData);
                        Exam_Report_Subject_Map.put(examSummaryListData, examSubjectResultList);
                        examSummaryList.add(examSummaryListData);
                    }
                }
                //Below code for sorting Exam with date
                Collections.sort(examSummaryList, new Comparator<ExamSummaryListData>() {
                    @Override
                    public int compare(ExamSummaryListData obj1, ExamSummaryListData obj2) {
                        // ## Ascending order
                        return obj1.getExamDate().compareTo(obj2.getExamDate()); // To compare string values
                        // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                    }
                });

                progressDialog.dismiss();
                examReportSumaaryAdapter = new ExamResultHistoryAdapter(examSummaryList, ExamReportActivity.this);
                examResultHistoryListView.setAdapter(examReportSumaaryAdapter);

                //Set Data in Exam Result Summary Card View
                try {
                    examTitleTextView.setText(examSummaryList.get(0).getExamType());
                    examResultSummaryTextView.setText(examSummaryList.get(0).getObtainedMarks() + " / " + examSummaryList.get(0).getTotalMarks());
                    finalGradeTextView.setText(examSummaryList.get(0).getExamFinalGrade());
                } catch (Exception e) {
                    System.out.println("Error while setting up first list item value in Exam Result Summmary.");
                    e.getMessage();
                    examTitleTextView.setText("Not Available");
                    examResultSummaryTextView.setText("Not Available");
                }
            }
        } catch (Exception e) {
            System.out.println("Exam Report Parse catch " + e.getMessage());
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