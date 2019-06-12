package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.relecotech.androidsparsh_smallwonder.adapters.ActiveUserLoginAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ActiveUserLoginListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Amey on 04-06-2018.
 */

public class TeacherActivityReport extends AppCompatActivity {
    private JsonObject jsonObjectForFetchReport;
    private MobileServiceClient mClient;
    private ConnectionDetector connectionDetector;
    private List<ActiveUserLoginListData> activeUserLoginListData;
    private ActiveUserLoginAdapter activeUserLoginAdapter;
    private ListView activeLoginListView;
    private TextView activeUserCountTextView;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ProgressDialog progressDialog;
    private TextView noDataTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_report);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        System.out.println(" sharedPrefValue " + sharedPrefValue);
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        mClient = Singleton.Instance().mClientMethod(TeacherActivityReport.this);
        connectionDetector = new ConnectionDetector(TeacherActivityReport.this);
        activeLoginListView = (ListView) findViewById(R.id.activeLoginListView);
        activeUserCountTextView = (TextView) findViewById(R.id.activeUserCountTextView);
        noDataTextView = (TextView) findViewById(R.id.noDataTextView);

        activeUserLoginListData = new ArrayList<>();
        if (connectionDetector.isConnectingToInternet()) {
            fetchTeacherActivityReport();
        } else {
            FancyToast.makeText(TeacherActivityReport.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    private void fetchTeacherActivityReport() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectForLogin = new JsonObject();
        jsonObjectForLogin.addProperty("Branch_id", userDetails.get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchActiveLoginUserDetail", jsonObjectForLogin);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(TeacherActivityReport.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetchTeacherActivityReport();
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
                System.out.println(" Success response " + response);
                parseActiveUserLoginData(response);
            }
        });

    }

    private void parseActiveUserLoginData(JsonElement response) {

        try {
            JsonArray array = response.getAsJsonArray();
            if (array.size() == 0) {
                System.out.println(" parseActiveUserLoginData Null -----");
                progressDialog.dismiss();
                noDataTextView.setVisibility(View.VISIBLE);
            } else {
                progressDialog.dismiss();
                noDataTextView.setVisibility(View.INVISIBLE);
                System.out.println("getAbsentStudentJsonArray--------- " + array.size());

                for (int loopForIteration = 0; loopForIteration < array.size(); loopForIteration++) {
                    JsonObject jsonObjectForIteration = array.get(loopForIteration).getAsJsonObject();

                    String user_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String user_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
//                String userEmail = jsonObjectForIteration.get("userEmail").toString().replace("\"", "");
//                System.out.println(" userEmail " + userEmail);
//                String user_firstName = userEmail.substring(0, userEmail.indexOf("."));
//                String user_lastName = userEmail.substring(userEmail.indexOf(".") + 1, userEmail.length());
                    String user_role = jsonObjectForIteration.get("userRole").toString().replace("\"", "");
                    activeUserLoginListData.add(new ActiveUserLoginListData(user_firstName + " " + user_lastName, user_role, loopForIteration));
                }
                int getActiveUserCount = activeUserLoginListData.size();
                activeUserCountTextView.setText("#" + String.valueOf(getActiveUserCount));
                activeUserLoginAdapter = new ActiveUserLoginAdapter(TeacherActivityReport.this, activeUserLoginListData);
                activeLoginListView.setAdapter(activeUserLoginAdapter);
            }
        } catch (Exception e) {
            System.out.println(" parseActiveUserLoginData " + e.getMessage());
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
