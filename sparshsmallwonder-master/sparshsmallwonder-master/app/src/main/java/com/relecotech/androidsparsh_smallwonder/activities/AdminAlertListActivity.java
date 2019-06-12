package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.Listview_communicator;
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

import javax.annotation.Nullable;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;


/**
 * Created by Amey on 22-05-2018.
 */

public class AdminAlertListActivity extends AppCompatActivity implements ItemClickListener, Listview_communicator {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private TextView adminAlertNoDataAvailableTextView;
    private RecyclerView adminAlertRecyclerView;

    private Alert alertListData;
    private List<Alert> alertList;
    private List<Alert> alertListMain;
    private Map<String, Alert> alertIdAlertObjHashMap;
    private Map<String, List<Alert>> alertIdAlertListHashMap;

    private List<String> studentLikeList;
    private HashMap<String, List<String>> studentLikeMap;
    private String alertAttachmentIdentifier;
    private MobileServiceClient mClient;
    private SimpleDateFormat targetDateFormat;
    private ProgressDialog progressDialog;
    private int IntentId = 1;
    ProgressDialog mProgressDialog;
    private android.app.AlertDialog optionDialog;
    private String getSelectedAlertAlertID;
    private ArrayList<Alert> alertListDataDetails;
    Alert alertItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_alert_list_activity);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        alertListDataDetails = new ArrayList<>();

        alertIdAlertListHashMap = new HashMap<>();
        alertIdAlertObjHashMap = new HashMap<>();
        alertList = new ArrayList<>();
        alertListMain = new ArrayList<>();
        studentLikeList = new ArrayList<>();
        studentLikeMap = new HashMap<String, List<String>>();


        adminAlertNoDataAvailableTextView = (TextView) findViewById(R.id.adminAlertNoDataAvailableTextView);
        adminAlertRecyclerView = (RecyclerView) findViewById(R.id.adminAlertRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adminAlertRecyclerView.setLayoutManager(layoutManager);
        FloatingActionButton adminAddAlertFab = (FloatingActionButton) findViewById(R.id.adminAddAlertButton);


        adminAddAlertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isConnectingToInternet()) {
                    AlertDialog.Builder alertDialogForBroadcastandIndiviual = new AlertDialog.Builder(AdminAlertListActivity.this);
                    alertDialogForBroadcastandIndiviual.setTitle("Select Alert ");
                    alertDialogForBroadcastandIndiviual.setPositiveButton("Individual", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent alertIndividual = new Intent(AdminAlertListActivity.this, AlertPost.class);
                            startActivityForResult(alertIndividual, IntentId);
//                            startActivity(alertIndividual);
                        }
                    });
                    alertDialogForBroadcastandIndiviual.setNegativeButton("Broadcast", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent alertAddIntent = new Intent(AdminAlertListActivity.this, AdminAlertPost.class);
                            startActivityForResult(alertAddIntent, IntentId);
//                            startActivity(alertAddIntent);
                        }
                    });
                    alertDialogForBroadcastandIndiviual.create().show();
                } else {
                    Toast.makeText(AdminAlertListActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            }
        });


        if (connectionDetector.isConnectingToInternet()) {
            FetchAlert();
        } else {
            adminAlertNoDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }


    private void FetchAlert() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectAlertParameters = new JsonObject();

        jsonObjectAlertParameters.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
        jsonObjectAlertParameters.addProperty("userRole", userRole);


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("alertFetchApi", jsonObjectAlertParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception in failure--   " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new android.app.AlertDialog.Builder(AdminAlertListActivity.this)
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

//        try {
        JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
        if (alertJsonArray.size() == 0) {
            progressDialog.dismiss();
            adminAlertNoDataAvailableTextView.setVisibility(View.VISIBLE);
            adminAlertNoDataAvailableTextView.setText(R.string.noDataAvailable);

        } else {
            adminAlertNoDataAvailableTextView.setVisibility(View.INVISIBLE);
            for (int i = 0; i < alertJsonArray.size(); i++) {
                JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();
                String postDateString = jsonObjectForIteration.get("alertPostDate").toString().replace("\"", "");
                System.out.println(" postDateString " + postDateString);

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
                String SubmittedTeacher_firstName = jsonObjectForIteration.get("SubmittedTeacher_firstName").toString().replace("\"", "");
                String SubmittedTeacher_lastName = jsonObjectForIteration.get("SubmittedTeacher_lastName").toString().replace("\"", "");
                //String likeCount = jsonObjectForIteration.get("likeCount").toString().replace("\"", "");

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
                    alertIdAlertObjHashMap.get(val).setLikeCount(countLike);
                    alertIdAlertObjHashMap.get(val).setLikeCheck(true);
                    Alert listData = alertIdAlertObjHashMap.get(val);
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
                        alertListMain.add(listData);
                    } else {
                        alertList = new ArrayList<>();
                        alertList.add(0, listData);
                        alertListMain.add(listData);
                        alertIdAlertListHashMap.put(listData.getAlertId(), alertList);
                    }
                }
                Collections.sort(alertListMain, new Comparator<Alert>() {
                    @Override
                    public int compare(Alert obj1, Alert obj2) {
                        // Descending order
                        System.out.println(" obj2.getPostDate() " + obj2.getPostDate());
                        System.out.println(" obj1.getPostDate() " + obj1.getPostDate());
                        return obj2.getPostDate().compareTo(obj1.getPostDate());
                        // To compare string values
                    }
                });
            }

            progressDialog.dismiss();
            AlertRecyclerAdapter alertRecyclerAdapter = new AlertRecyclerAdapter(this, alertListMain);
            alertRecyclerAdapter.setClickListener(AdminAlertListActivity.this);
            adminAlertRecyclerView.setAdapter(alertRecyclerAdapter);
        }
    }


    @Override
    public void onClick(View view, int position) {
        System.out.println(" position " + position);

        Alert selectedAlertListData = alertListMain.get(position);
        Intent alertDetailIntent = new Intent(AdminAlertListActivity.this, AlertDetail.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is IntentId
        if (requestCode == IntentId) {
            if (resultCode == RESULT_OK) {
                System.out.println(" INSIDE RESULT_OK ");
                //alertListMain = new ArrayList<>();
                if (connectionDetector.isConnectingToInternet()) {
                    // FetchAlert();
                    System.out.println(" connectionDetector ");
                } else {
                    adminAlertNoDataAvailableTextView.setText(R.string.noDataAvailable);
                    FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }

            if (resultCode == RESULT_CANCELED)
                System.out.println(" INSIDE RESULT_CANCELED ");
        }
    }


    public void listViewOnclick(final int position, int click_code) {
        System.out.println("position------------  " + position);
        Alert alertListData = alertListMain.get(position);
        String getSelectedAlertItemTitle = alertListData.getTitle();
        getSelectedAlertAlertID = alertListData.getAlertId();
        int ckCode = click_code;


        if(ckCode == 1) {
            android.app.AlertDialog.Builder deleteConfirmationDailog = new android.app.AlertDialog.Builder(AdminAlertListActivity.this);

            //AlertDialog.Builder deleteConfirmationDailog = new AlertDialog.Builder(AlertListActivity.this);
            optionDialog = new android.app.AlertDialog.Builder(AdminAlertListActivity.this).create();
            deleteConfirmationDailog.setMessage("Do you want to Delete  "+getSelectedAlertItemTitle+ " ?");
            deleteConfirmationDailog.setCancelable(false);
            deleteConfirmationDailog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteAlert(getSelectedAlertAlertID, position);
                }
            });

            deleteConfirmationDailog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    optionDialog.dismiss();
                }
            });

            deleteConfirmationDailog.create().show();
        }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(ckCode == 2) {
            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(AdminAlertListActivity.this);
            optionDialog = new android.app.AlertDialog.Builder(AdminAlertListActivity.this).create();
            builder1.setMessage("Do you want to Edit "+getSelectedAlertItemTitle+ " ?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.print("Going to update");
                            updateAlert(getSelectedAlertAlertID, position);
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            android.app.AlertDialog alert11 = builder1.create();
            alert11.show();
        }


    }

    private void deleteAlert(String getSelectedAlertAlertID , final int position){
        mProgressDialog = new ProgressDialog(AdminAlertListActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Deleting Alert...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AlertId" , getSelectedAlertAlertID);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("alertDelete" , jsonObject);

        mProgressDialog.show();

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(@Nullable JsonElement response) {
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        new android.support.v7.app.AlertDialog.Builder(AdminAlertListActivity.this)
                                .setMessage("Alert Deleted Successfully...")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertListMain.remove(position);
                                        //alertStudentListAdapter.notifyDataSetChanged();
                                        FetchAlert();
                                    }
                                }).show();
                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 2000);
            }

            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("alertDelete exception" + exception);
                mProgressDialog.dismiss();

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {

                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable , 3000);
            }
        });
    }

    public class deleteAlertTask extends AsyncTask<String , Void , Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String identifier = strings[0];
            System.out.println("identifier" +identifier);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("AlertId" , identifier );

            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("AlertDelete" , jsonObject);

            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(@Nullable JsonElement response) {
                    resultFuture.set(response);
                }

                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println("AlertDelete exception" + exception);

                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {

                        }
                    };

                    Handler pdCanceller = new Handler();
                    pdCanceller.postDelayed(progressRunnable , 5000);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.setMessage("Alert Delete Successfully");
            mProgressDialog.dismiss();
        }
    }

    private void updateAlert(String getSelectedAlertAlertID , final int position){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AlertId" , getSelectedAlertAlertID);
        jsonObject.addProperty("userRole", userRole);
        jsonObject.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("alertUpdateFetch" , jsonObject);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(@android.support.annotation.Nullable JsonElement response) {

                resultFuture.set(response);
                System.out.println(" response " + response);
                parseJSON2(response);
            }

            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("alertUpdate exception" + exception);
            }
        });
    }

    private void parseJSON2(JsonElement alertFetchResponse) {
        try {
            JsonArray alertJsonArray = alertFetchResponse.getAsJsonArray();
            if (alertJsonArray.size() == 0) {
                progressDialog.dismiss();
            }
            else {
                for (int i = 0; i < alertJsonArray.size(); i++) {
                    JsonObject jsonObjectForIteration = alertJsonArray.get(i).getAsJsonObject();
                    String postDateString = jsonObjectForIteration.get("alertPostDate").toString().replace("\"", "");
                    String alertId1 = jsonObjectForIteration.get("id").toString().replace("\"", "");
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
                    String schoolClassId =jsonObjectForIteration.get("School_Class_id").toString().replace("\"", "");

                    //String likeCount = jsonObjectForIteration.get("likeCount").toString().replace("\"", "");
                    if (!alertAttachmentCount.equals("0")) {
                        alertAttachmentIdentifier = jsonObjectForIteration.get("alertAttachmentIdentifier").toString().replace("\"", "");
                    }
                    else {
                        alertAttachmentIdentifier = "";
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

                    alertItem = new Alert(alertId1, alertTitle, alertDescription, postDateInDate, alertCategory, "", studentId, alertClass, alertDivision,  firstName+" "+lastName , alertAttachmentCount, alertPriority, 0, true, alertAttachmentIdentifier, true);
                    System.out.println("setAlertListData............." + alertListDataDetails);

                    alertListDataDetails.add(new Alert("Title",alertTitle,true));
                    alertListDataDetails.add(new Alert("Description",alertDescription,true));
                    alertListDataDetails.add(new Alert("Category",alertCategory,true));
                    alertListDataDetails.add(new Alert("Priority",alertPriority,true));
                    alertListDataDetails.add(new Alert("Class",alertClass,true));
                    alertListDataDetails.add(new Alert("Division",alertDivision,true));

                    alertListDataDetails.add(new Alert("Student",firstName+" "+lastName,true));


                    System.out.println("UPDATE ALERT RETURN SUCCESSFULLY");
                    Intent intent = new Intent(AdminAlertListActivity.this, EditAlertActivity.class);
                    intent.putExtra("setAlertListData", alertListDataDetails);
                    System.out.println("setAlertListData............." + alertListDataDetails);
                    intent.putExtra("alertObj", alertItem);
                    intent.putExtra("alertID", getSelectedAlertAlertID);
                    System.out.println("ALERT ID............." + getSelectedAlertAlertID);
                    startActivity(intent);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Exception in ParentParsing " + e.getMessage());
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (connectionDetector.isConnectingToInternet()) {
            FetchAlert();
            alertListDataDetails = new ArrayList<>();
            System.out.println(" connectionDetector ");
        } else {
            adminAlertNoDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

}
