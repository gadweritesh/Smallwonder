package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.relecotech.androidsparsh_smallwonder.adapters.BusScheduleAdapter;
import com.relecotech.androidsparsh_smallwonder.models.BusRouteListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class BusRouteActivity extends AppCompatActivity {

    private TextView noDataAvailable_TextView;
    private ProgressDialog progressDialog;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private ListView busScheduleListView;
    private List<BusRouteListData> scheduleList;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        mClient = Singleton.Instance().mClientMethod(this);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        busScheduleListView = (ListView) findViewById(R.id.busScheduleListView);
        noDataAvailable_TextView = (TextView) findViewById(R.id.busTrackNoDataAvailable_textView);

        if (sessionManager.getSharedPrefItem(SessionManager.KEY_BUS_ID) != null) {
            String busId = sessionManager.getSharedPrefItem(SessionManager.KEY_BUS_ID);
        } else {
            FancyToast.makeText(this, "Update Bus No in User Profile.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        scheduleList = new ArrayList<>();

        if (connectionDetector.isConnectingToInternet()) {
            fetchBusScheduleData();
        } else {
            noDataAvailable_TextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        busScheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                BusRouteListData busRouteListData = scheduleList.get(i);
                Intent intent = new Intent(BusRouteActivity.this, MapActivity.class);
                intent.putExtra("BusId", busRouteListData.getBusId());
                intent.putExtra("BusNo", busRouteListData.getRouteNo());
                startActivity(intent);
            }
        });
    }


    private void fetchBusScheduleData() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        JsonObject busScheduleItem = new JsonObject();
        busScheduleItem.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("busScheduleFetch", busScheduleItem);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" getBusSchedule exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new android.app.AlertDialog.Builder(BusRouteActivity.this)
                                .setMessage(R.string.check_network)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetchBusScheduleData();
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
                System.out.println(" getBusSchedule  response    " + response);
                parseBusScheduleJson(response);
            }
        });
    }

    private void parseBusScheduleJson(JsonElement response) {

        try {
            System.out.println("busTrackerResponse " + response);
            JsonArray busJsonArray = response.getAsJsonArray();

            if (busJsonArray.size() == 0) {
                noDataAvailable_TextView.setText(R.string.noDataAvailable);
                noDataAvailable_TextView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            } else {
                noDataAvailable_TextView.setVisibility(View.VISIBLE);
                for (int loop = 0; loop < busJsonArray.size(); loop++) {

                    JsonObject jsonObjectForIteration = busJsonArray.get(loop).getAsJsonObject();

                    String busRoute = jsonObjectForIteration.get("busRoute").toString().replace("\"", "");
                    String vehicleNo = jsonObjectForIteration.get("vehicleNo").toString().replace("\"", "");
                    String routeNo = jsonObjectForIteration.get("routeNo").toString().replace("\"", "");
                    String firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String mobileNo = jsonObjectForIteration.get("mobileNo").toString().replace("\"", "");
                    String startTime = jsonObjectForIteration.get("startTime").toString().replace("\"", "");
                    String busId = jsonObjectForIteration.get("Bus_id").toString().replace("\"", "");

                    String name = firstName + " " + lastName;

                    if (mobileNo.contains("null")) {
                        mobileNo = "Not Available";
                    }
                    if (name.contains("null")) {
                        name = "Not Available";
                    }
//
                    if (busId.equalsIgnoreCase(sessionManager.getSharedPrefItem(SessionManager.KEY_BUS_ID))) {
                        scheduleList.add(0, new BusRouteListData(busRoute, vehicleNo, routeNo, name, mobileNo, startTime, busId));
                    } else {
                        scheduleList.add(new BusRouteListData(busRoute, vehicleNo, routeNo, name, mobileNo, startTime, busId));
                    }
                }

                BusScheduleAdapter busScheduleAdapter = new BusScheduleAdapter(this, scheduleList);
                busScheduleListView.setAdapter(busScheduleAdapter);
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            System.out.println(" parseBusScheduleJson exception " + e.getMessage());
        }
    }
}
