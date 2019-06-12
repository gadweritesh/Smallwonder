package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
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
import com.relecotech.androidsparsh_smallwonder.adapters.AchievementAdapter;
import com.relecotech.androidsparsh_smallwonder.fragments.AchievementDialogFragment;
import com.relecotech.androidsparsh_smallwonder.models.AchievementListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AchievementActivity extends AppCompatActivity {

    private List<AchievementListData> achievementList;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ProgressDialog progressDialog;
    private AchievementListData achievementListData;
    private ListView myAchievementListView;
    private ListView achievementListView;
    private TextView myAchievementTextView, othersAchievementTextView;
    private String userRole;
    private TextView noDataAvailableTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        achievementList = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        File dir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Achievement");
        try {
            if (dir.mkdirs()) {
                System.out.println("Directory  created");
            } else {
                System.out.println("Directory not created");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTION" + e.getMessage());
        }

        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        noDataAvailableTextView = (TextView) findViewById(R.id.achievementNoDataAvailableTextView);
        FloatingActionButton achievementPostFab = (FloatingActionButton) findViewById(R.id.addAchievementButton);

        if (userRole.equals("Student")) {
            achievementPostFab.setVisibility(View.INVISIBLE);
        }

        achievementListView = (ListView) findViewById(R.id.achievement_listView);

        if (connectionDetector.isConnectingToInternet()) {
            FetchAchievementData();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        achievementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("AchievementListData", achievementList.get(i));
//                bundle.putInt("position", position);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                AchievementDialogFragment newFragment = AchievementDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(fragmentTransaction, "slideshow");
            }
        });
    }

    private void FetchAchievementData() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        try {

            JsonObject jsonObject = new JsonObject();
            if (userRole.equalsIgnoreCase("Teacher")){
                jsonObject.addProperty("uploaderId", userDetails.get(SessionManager.KEY_TEACHER_ID));
            }else if (userRole.equalsIgnoreCase("Administrator")) {
                jsonObject.addProperty("uploaderId", userDetails.get(SessionManager.KEY_ADMIN_ID));
            }
            jsonObject.addProperty("userRole", userRole);
            jsonObject.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));

            MobileServiceClient mClient = Singleton.Instance().mClientMethod(this);
            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("achievementFetchApi", jsonObject);

            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println("FetchAchievementData exception " + exception);
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(AchievementActivity.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FetchAchievementData();
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
                    System.out.println(" FetchAchievementData  response " + response);
                    AchievementJsonParse(response);
                }
            });

        } catch (Exception e) {
            System.out.println("Exception in FetchExamTimeTableData " + e.getMessage());
        }
    }

    private void AchievementJsonParse(JsonElement response) {
        try {
            JsonArray achievementJsonArray = response.getAsJsonArray();
            if (achievementJsonArray.size() == 0) {
                noDataAvailableTextView.setText(R.string.noDataAvailable);
                progressDialog.dismiss();
                System.out.println(" not received");
            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                for (int examReportLoop = 0; examReportLoop < achievementJsonArray.size(); examReportLoop++) {
                    JsonObject jsonObjectForIteration = achievementJsonArray.get(examReportLoop).getAsJsonObject();

                    String achievementId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String achievementTitle = jsonObjectForIteration.get("achievementTitle").toString().replace("\"", "");
                    String achievementDescription = jsonObjectForIteration.get("achievementDescription").toString().replace("\"", "");
                    String achievementCategory = jsonObjectForIteration.get("achievementCategory").toString().replace("\"", "");
                    String achievementStudentName = jsonObjectForIteration.get("achievementStudentName").toString().replace("\"", "");
//                    String achievementAttachmentIdentifier = jsonObjectForIteration.get("achievementAttachmentIdentifier").toString().replace("\"", "");
                    String achievementUrl = jsonObjectForIteration.get("achievementUrl").toString().replace("\"", "");
                    String achievementYear = jsonObjectForIteration.get("achievementYear").toString().replace("\"", "");
                    String achievementPostDate = jsonObjectForIteration.get("achievementPostDate").toString().replace("\"", "");

                    String uploaderId = jsonObjectForIteration.get("uploader_id").toString().replace("\"", "");
                    String firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");

                    String uploaderName = firstName + " " + lastName;
//                    String student_id = jsonObjectForIteration.get("student_id").toString().replace("\"", "");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    try {
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());
                        Date datePost = dateFormat.parse(achievementPostDate);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());
                        achievementPostDate = targetDateFormat.format(datePost);
                        System.out.println("galleryPostDate---------- " + achievementPostDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    achievementListData = new AchievementListData(achievementId, achievementStudentName, achievementTitle, achievementDescription, achievementCategory, achievementUrl, achievementYear, "",achievementPostDate,uploaderName,uploaderId);                    achievementList.add(achievementListData);
                }

                AchievementAdapter achievementAdapter = new AchievementAdapter(this, achievementList);
                achievementListView.setAdapter(achievementAdapter);


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

    public void addAchievementButton(View view) {
        Intent intent = new Intent(this, AchievementPost.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        achievementList = new ArrayList<>();
        if (connectionDetector.isConnectingToInternet()) {
            FetchAchievementData();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }
}
