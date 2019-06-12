package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
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
import com.relecotech.androidsparsh_smallwonder.adapters.AlertRecyclerAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Alert;
import com.relecotech.androidsparsh_smallwonder.models.AlertListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

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

/**
 * Created by Relecotech on 26-02-2018.
 */

public class AlertListActivity extends AppCompatActivity implements ItemClickListener {
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private Alert alertListData;
    private List<Alert> alertList;
    private List<Alert> alertListMain;
    private Map<String, Alert> alertIdAlertObjHashMap;
    private Map<String, List<Alert>> alertIdAlertListHashMap;
    private List<String> studentLikeList;
    private HashMap<String, List<String>> studentLikeMap;
    private boolean likeCheck;
    private String alertAttachmentIdentifier;
    private TextView noDataAvailableTextView;
    private MobileServiceClient mClient;
    private SimpleDateFormat targetDateFormat;
    private int IntentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_list_activity);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        noDataAvailableTextView = (TextView) findViewById(R.id.alertNoDataAvailableTextView);
        recyclerView = (RecyclerView) findViewById(R.id.alertRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FloatingActionButton addAlertFab = (FloatingActionButton) findViewById(R.id.addAlertButton);

        if (userRole.equals("Student")) {
            addAlertFab.setVisibility(View.INVISIBLE);
        }

        addAlertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isConnectingToInternet()) {
                    Intent intent = new Intent(AlertListActivity.this, AlertPost.class);
                    startActivityForResult(intent, IntentId);
//                startActivity(intent);
                } else {
                    FancyToast.makeText(AlertListActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });

        alertIdAlertListHashMap = new HashMap<>();
        alertIdAlertObjHashMap = new HashMap<>();
        alertList = new ArrayList<>();
        alertListMain = new ArrayList<>();
        studentLikeList = new ArrayList<>();
        studentLikeMap = new HashMap<String, List<String>>();

        if (connectionDetector.isConnectingToInternet()) {
            FetchAlert();
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is IntentId
        if (requestCode == IntentId) {
            if (resultCode == RESULT_OK) {
                System.out.println(" INSIDE RESULT_OK ");

                alertListMain = new ArrayList<>();
                if (connectionDetector.isConnectingToInternet()) {
                    FetchAlert();
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
                System.out.println(" INSIDE RESULT_CANCELED ");
        }
    }

    private void FetchAlert() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectAlertParameters = new JsonObject();

        if (userRole.equals("Teacher")) {
            jsonObjectAlertParameters.addProperty("userRole", userRole);
            jsonObjectAlertParameters.addProperty("teacherId", userDetails.get(SessionManager.KEY_TEACHER_ID));
            jsonObjectAlertParameters.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
        }
        if (userRole.equals("Student")) {

            jsonObjectAlertParameters.addProperty("userRole", userRole);
            jsonObjectAlertParameters.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
            jsonObjectAlertParameters.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
            jsonObjectAlertParameters.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        }

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("alertFetchApi", jsonObjectAlertParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new android.app.AlertDialog.Builder(AlertListActivity.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FetchAlert();
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
                System.out.println(" response " + response);
                parseJSON(response);
            }
        });
    }

    private void parseJSON(JsonElement alertFetchResponse) {
        studentLikeList.clear();
        alertList.clear();
        alertListMain.clear();
        alertListMain = new ArrayList<>();
        alertIdAlertObjHashMap.clear();
        studentLikeMap.clear();

        try {
            JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
            if (alertJsonArray.size() == 0) {
                progressDialog.dismiss();
                noDataAvailableTextView.setVisibility(View.VISIBLE);
                noDataAvailableTextView.setText(R.string.noDataAvailable);

            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                for (int i = 0; i < alertJsonArray.size(); i++) {
                    JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();
                    String postDateString = jsonObjectForIteration.get("alertPostDate").toString().replace("\"", "");

                    String alertId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String selectedItemId = jsonObjectForIteration.get("selectedItemId").toString().replace("\"", "");
                    String studentLikeId = jsonObjectForIteration.get("studentLikeId").toString().replace("\"", "");

                    String alertTitle = jsonObjectForIteration.get("alertTitle").toString().replace("\\n", "").replace("\\r", "").replace("\"", "").replace("\\", "");
                    String alertDescription = jsonObjectForIteration.get("alertDescription").toString().replace("\"", "").replace("\\n", "\n").replace("\\r", "").replace("\\", "");
                    String alertCategory = jsonObjectForIteration.get("alertCategory").toString().replace("\"", "");
                    String alertClass = jsonObjectForIteration.get("alertClass").toString().replace("\"", "");
                    String alertDivision = jsonObjectForIteration.get("alertDivision").toString().replace("\"", "");
                    String firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String studentId = jsonObjectForIteration.get("Student_id").toString().replace("\"", "");
                    String alertAttachmentCount = jsonObjectForIteration.get("alertAttachmentCount").toString().replace("\"", "");
                    String alertPriority = jsonObjectForIteration.get("alertPriority").toString().replace("\"", "");


                    if (!alertAttachmentCount.equals("0")) {
                        alertAttachmentIdentifier = jsonObjectForIteration.get("alertAttachmentIdentifier").toString().replace("\"", "");
                    }

                    if (!selectedItemId.equals("null")) {
                        if (studentLikeMap.containsKey(selectedItemId)) {
                            studentLikeList = studentLikeMap.get(selectedItemId);
                            studentLikeList.add(studentLikeId);
                        } else {
                            studentLikeList = new ArrayList<>();
                            studentLikeList.add(studentLikeId);
                            studentLikeMap.put(selectedItemId, studentLikeList);
                        }
                    } else {
                        System.out.println("selectedItemId " + selectedItemId + " studentLikeId " + studentLikeId);
                    }


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date postDateInDate = null;
                    try {
                        targetDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
                        postDateInDate = dateFormat.parse(postDateString);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());
                        postDateString = targetDateFormat.format(postDateInDate);
                    } catch (Exception e) {
                        System.out.println("Date Parse Exception " + e.getMessage());
                    }

                    if (alertIdAlertObjHashMap.containsKey(alertId)) {
                        alertListData = alertIdAlertObjHashMap.get(alertId);
                    } else {
                        alertIdAlertObjHashMap.put(alertId, new Alert(alertId, alertTitle, alertDescription, postDateInDate, alertCategory, "", studentId, alertClass, alertDivision, firstName + " " + lastName, alertAttachmentCount, alertPriority, 0, false, alertAttachmentIdentifier,true));
                    }

                }

                for (String val : alertIdAlertObjHashMap.keySet()) {
                    if (studentLikeMap.containsKey(val)) {
                        System.out.println("IF studentLikeMap.containsKey(val) ");
                        int countLike = studentLikeMap.get(val).size();
                        Alert listData = alertIdAlertObjHashMap.get(val);
                        listData.setLikeCount(countLike);

                        List<String> likeList = studentLikeMap.get(val);
                        if (likeList.contains(userDetails.get(SessionManager.KEY_STUDENT_ID))) {
                            listData.setLikeCheck(true);
                        }
//                        AlertListData listData = alertIdAlertObjHashMap.get(val);
                        System.out.println("IF listData.getAlertId() " + listData.getAlertId());
                        if (alertIdAlertListHashMap.containsKey(listData.getAlertId())) {
                            alertList = alertIdAlertListHashMap.get(listData.getAlertId());
                            alertList.add(0, listData);
                            alertListMain.add(0, listData);
                        } else {
                            alertList = new ArrayList<>();
                            alertList.add(0, listData);
                            alertListMain.add(0, listData);
                            alertIdAlertListHashMap.put(listData.getAlertId(), alertList);
                        }
                    } else {
                        System.out.println("ELSE studentLikeMap.containsKey(val) ");
                        Alert listData = alertIdAlertObjHashMap.get(val);
                        System.out.println("ELSE listData.getAlertId() " + listData.getAlertId());
                        if (alertIdAlertListHashMap.containsKey(listData.getAlertId())) {
                            alertList = alertIdAlertListHashMap.get(listData.getAlertId());
                            alertList.add(0, listData);
                            alertListMain.add(0, listData);
                        } else {
                            alertList = new ArrayList<>();
                            alertList.add(0, listData);
                            alertListMain.add(0, listData);
                            alertIdAlertListHashMap.put(listData.getAlertId(), alertList);
                        }
                    }
                    Collections.sort(alertListMain, new Comparator<Alert>() {
                        @Override
                        public int compare(Alert obj1, Alert obj2) {
                            // Descending order
                            return obj2.getPostDate().compareTo(obj1.getPostDate()); // To compare string values
                        }
                    });
                }

                progressDialog.dismiss();
//            alertList.acdd(0, alertData);
                AlertRecyclerAdapter alertRecyclerAdapter = new AlertRecyclerAdapter(this, alertListMain);
                alertRecyclerAdapter.setClickListener(AlertListActivity.this);
                recyclerView.setAdapter(alertRecyclerAdapter);
            }
        } catch (Exception e) {
            System.out.println("Exception in ParentParsing " + e.getMessage());
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

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        alertListMain = new ArrayList<>();
//        if (connectionDetector.isConnectingToInternet()) {
//            FetchAlert();
//            System.out.println(" connectionDetector ");
//        } else {
//            noDataAvailableTextView.setText(R.string.noDataAvailable);
//            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
//        }
//    }

    @Override
    public void onClick(View view, int position) {
        System.out.println(" position " + position);

        Alert selectedAlertListData = alertListMain.get(position);
        Intent alertDetailIntent = new Intent(AlertListActivity.this, AlertDetail.class);
        alertDetailIntent.putExtra("AlertTitle", selectedAlertListData.getTitle());
        alertDetailIntent.putExtra("AlertDescription", selectedAlertListData.getDescription());
        alertDetailIntent.putExtra("AlertSubmittedBy", selectedAlertListData.getSubmitted_By_to());
        alertDetailIntent.putExtra("AlertCategory", selectedAlertListData.getCategory());
        alertDetailIntent.putExtra("AlertPostDate", targetDateFormat.format(selectedAlertListData.getPostDate()));
        alertDetailIntent.putExtra("AlertId", selectedAlertListData.getAlertId());
        alertDetailIntent.putExtra("AlertAttachmentCount", selectedAlertListData.getAttachmentCount());
        alertDetailIntent.putExtra("AlertAttachmentIdentifier", selectedAlertListData.getAlertAttachmentIdentifier());
        System.out.println("AlertLikeCheck" + selectedAlertListData.isLikeCheck());
        alertDetailIntent.putExtra("AlertLikeCheck", selectedAlertListData.isLikeCheck());
        startActivity(alertDetailIntent);
    }
}
