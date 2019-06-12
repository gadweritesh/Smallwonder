package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import com.relecotech.androidsparsh_smallwonder.adapters.SyllabusSubjectAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Syllabus;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class SyllabusActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ListView syllabusListView;
    private TextView noDataAvailableTextView;
    private List<Syllabus> subjectList;
    private Map<String, Syllabus> subjectMap;
    private FloatingActionButton syllabus_add_fab;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        syllabusListView = (ListView) findViewById(R.id.syllabus_listView);
        noDataAvailableTextView = (TextView) findViewById(R.id.syllabusNoDataAvailableTextView);
        syllabus_add_fab = (FloatingActionButton) findViewById(R.id.syllabus_add_fab);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        if (userDetails.get(SessionManager.KEY_USER_ROLE).equals("Student")) {
            syllabus_add_fab.setVisibility(View.INVISIBLE);
        }

        if (connectionDetector.isConnectingToInternet()) {
            FetchSyllabusData();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        syllabusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(SyllabusActivity.this, SyllabusDetail.class);
//
//                System.out.println(" i " + i);
//                System.out.println(" subjectList.get(i).getSyllabusSubject() " + subjectList.get(i).getId());
//                intent.putExtra("subject", subjectMap.get(subjectList.get(i).getId()).getSyllabusSubject());
//                intent.putExtra("attachmentIdentifier", subjectMap.get(subjectList.get(i).getId()).getAttachmentIdentifier());
//                intent.putExtra("attachmentCount", subjectMap.get(subjectList.get(i).getId()).getAttachmentCount());
//                startActivity(intent);

            }
        });

    }

    private void FetchSyllabusData() {

        subjectList = new ArrayList<>();
        subjectMap = new HashMap<>();

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userRole", userDetails.get(SessionManager.KEY_USER_ROLE));
            jsonObject.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
            jsonObject.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
            jsonObject.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_ID));

            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("syllabusFetchApi", jsonObject);

            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println("FetchSyllabusData exception " + exception);
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(SyllabusActivity.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FetchSyllabusData();
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
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
                    System.out.println(" FetchSyllabusData  response " + response);
                    SyllabusJsonParse(response);
                }
            });

        } catch (Exception e) {
            System.out.println("Exception in FetchSyllabusData " + e.getMessage());
        }
    }

    private void SyllabusJsonParse(JsonElement response) {
        try {
            JsonArray achievementJsonArray = response.getAsJsonArray();
            if (achievementJsonArray.size() == 0) {
                noDataAvailableTextView.setText(R.string.noDataAvailable);
                progressDialog.dismiss();
                System.out.println(" not received");
            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                for (int loop = 0; loop < achievementJsonArray.size(); loop++) {
                    JsonObject jsonObjectForIteration = achievementJsonArray.get(loop).getAsJsonObject();
                    String syllabusId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String syllabusSubject = jsonObjectForIteration.get("syllabusSubject").toString().replace("\"", "");
                    String syllabusDescription = jsonObjectForIteration.get("syllabusDescription").toString().replace("\"", "");
//                    String syllabusUrl = jsonObjectForIteration.get("syllabusUrl").toString().replace("\"", "");
//                    String syllabusAttachmentTitle = jsonObjectForIteration.get("syllabus_attachment_title").toString().replace("\"", "");
                    String attachmentIdentifier = jsonObjectForIteration.get("attachmentIdentifier").toString().replace("\"", "");
                    String attachmentCount = jsonObjectForIteration.get("attachmentCount").toString().replace("\"", "");

//                    syllabusUrl = syllabusUrl + syllabusAttachmentTitle;

                    System.out.println(" syllabusId " + syllabusId);
                    System.out.println(" syllabusSubject " + syllabusSubject);
                    System.out.println(" attachmentIdentifier " + attachmentIdentifier);
                    Syllabus syllabus = new Syllabus();
                    syllabus.setId(syllabusId);
                    syllabus.setAttachmentIdentifier(attachmentIdentifier);
                    syllabus.setAttachmentCount(Integer.parseInt(attachmentCount));
                    syllabus.setSyllabusSubject(syllabusSubject);
                    syllabus.setSyllabusDescription(syllabusDescription);
                    subjectMap.put(syllabusId, syllabus);
                    subjectList.add(syllabus);
                }

                SyllabusSubjectAdapter subjectAdapter = new SyllabusSubjectAdapter(this, subjectList);
                syllabusListView.setAdapter(subjectAdapter);

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

    public void addSyllabus(View view) {
        Intent intent = new Intent(this, SyllabusPost.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (connectionDetector.isConnectingToInternet()) {
            FetchSyllabusData();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }
}
