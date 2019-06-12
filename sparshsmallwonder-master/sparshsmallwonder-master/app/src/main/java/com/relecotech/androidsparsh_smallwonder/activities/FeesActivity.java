package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
import com.relecotech.androidsparsh_smallwonder.adapters.FeesAdapter;
import com.relecotech.androidsparsh_smallwonder.models.FeesInformationListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeSet;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;


public class FeesActivity extends AppCompatActivity {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private ArrayList<FeesInformationListData> feesInformationListDataList;
    private ListView paymentHistoryListView;
    private TextView amoutPayableTextView, nextInstallmentDueDateTextView;
    private TreeSet<Date> getMaximumDateFromList;
    private FeesInformationListData feesInformationListData;
    private SimpleDateFormat targetDateFormat;
    private ProgressDialog progressDialog;
    private FeesAdapter feesAdapterAdapter;
    private int amountPayableInteger = 0;
    private int calculate_amountPayable = 0;
    private TextView feeNoDataAvailableTextView;
    private MobileServiceClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fees_activity);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        feesInformationListDataList = new ArrayList<>();

        paymentHistoryListView = (ListView) findViewById(R.id.paymentHistoryListView);
        feeNoDataAvailableTextView = (TextView) findViewById(R.id.feeNoDataTextView);
        amoutPayableTextView = (TextView) findViewById(R.id.nextInstallmentInputTextView);
        nextInstallmentDueDateTextView = (TextView) findViewById(R.id.nextInstallmentDueDateTextView);
        getMaximumDateFromList = new TreeSet<Date>();

        paymentHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FeesInformationListData selectedFeesInformationListItem = feesInformationListDataList.get(position);
                OpenDialogForFeesComment(selectedFeesInformationListItem);
            }
        });

        if (connectionDetector.isConnectingToInternet()) {
            CallingFeesApi();
        } else {
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }


    private void CallingFeesApi() {

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectFeesInformation = new JsonObject();
        jsonObjectFeesInformation.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("feesInformationFetch", jsonObjectFeesInformation);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" fees information Api response  exception  " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(FeesActivity.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CallingFeesApi();
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
                System.out.println(" fees information Api response   " + response);

                ParseJsonResponse(response);

            }
        });
    }

    private void ParseJsonResponse(JsonElement response) {
        JsonArray feesInformationJsonArray = null;
        try {
            feesInformationJsonArray = response.getAsJsonArray();
        } catch (Exception e) {
            System.out.println("feesInformationJsonArray catch " + e.getMessage());
        }
        if (feesInformationJsonArray.size() == 0) {
            progressDialog.dismiss();
            feeNoDataAvailableTextView.setVisibility(View.VISIBLE);
            System.out.println("Fees Information Json Response is null");

        } else {
            feeNoDataAvailableTextView.setVisibility(View.INVISIBLE);
            System.out.println("Fees Information Json Response is NOT null");
            if ((feesInformationJsonArray.size()) == 0) {
                System.out.println(" array size is zero ");
            } else {
                for (int feesLoop = 0; feesLoop < feesInformationJsonArray.size(); feesLoop++) {
                    JsonObject jsonObjectForIteration = feesInformationJsonArray.get(feesLoop).getAsJsonObject();

                    String fees_title = jsonObjectForIteration.get("feesTitle").toString().replace("\"", "");
                    String feesTotalAmount = jsonObjectForIteration.get("feesTotalAmount").toString().replace("\"", "");
                    String fees_amount_payable = jsonObjectForIteration.get("feesAmountPayable").toString().replace("\"", "");
                    String feesDiscount = jsonObjectForIteration.get("feesDiscount").toString().replace("\"", "");
                    String fees_due_date = jsonObjectForIteration.get("feesDueDate").toString().replace("\"", "");
                    String fees_paid_date = jsonObjectForIteration.get("feesPaidDate").toString().replace("\"", "");
                    String fees_Student_fee_category = jsonObjectForIteration.get("studentFeeCategory").toString().replace("\"", "");
                    String fees_status = jsonObjectForIteration.get("feesStatus").toString().replace("\"", "");
                    String fees_Student_id = jsonObjectForIteration.get("Student_id").toString().replace("\"", "");
                    String fees_Comment = jsonObjectForIteration.get("feesComment").toString().replace("\"", "");


                    SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS", Locale.getDefault());
                    sourceDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    targetDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
                    targetDateFormat.setTimeZone(TimeZone.getDefault());

                    if (fees_paid_date.isEmpty()) {
                        System.out.println("yes its empty");

                    } else {
                        System.out.println("yes its not empty");
                        Date datePost = null;
                        try {
                            System.out.println("fees_paid_date-------------------------------------- " + fees_paid_date);
                            datePost = sourceDateFormat.parse(fees_paid_date);
                            fees_paid_date = targetDateFormat.format(datePost);
                            System.out.println("fees_paid_date--------------  " + fees_paid_date);
                        } catch (ParseException e) {
                            System.out.println("Exception--occurred in date " + e.getMessage());
                        }
                    }


                    if (fees_due_date.isEmpty()) {
                        System.out.println("yes its empty");
                    } else {
                        System.out.println("yes its not empty");
                        Date datepost = null;
                        try {
                            System.out.println("fees_due_date--------------  " + fees_due_date);
                            datepost = sourceDateFormat.parse(fees_due_date);
                            getMaximumDateFromList.add(datepost);
                            fees_due_date = targetDateFormat.format(datepost);

                            System.out.println("fees_due_date--------------  " + fees_due_date);
                        } catch (ParseException e) {
                            System.out.println("Exception----------- occured in date");
                            e.printStackTrace();
                        }
                    }

                    if (fees_status.contains("Unpaid")) {
                        amountPayableInteger = Integer.parseInt(fees_amount_payable);
                        calculate_amountPayable = calculate_amountPayable + amountPayableInteger;
                    } else {
                        System.out.println("fees status paid");
                        //   calculate_amountPayable = 0.00;
                    }

                    feesInformationListData = new FeesInformationListData(fees_title, feesTotalAmount, fees_amount_payable, fees_due_date, fees_paid_date, fees_Student_fee_category, fees_status, fees_Student_id, fees_Comment);
                    feesInformationListDataList.add(feesInformationListData);
                }

                progressDialog.dismiss();
                amoutPayableTextView.setText("₹ " + calculate_amountPayable);
                Date MaximumDueDate = getMaximumDateFromList.last();
                String maximumDueDate = targetDateFormat.format(MaximumDueDate);
                nextInstallmentDueDateTextView.setText("Pay before  :  " + maximumDueDate);

                feesAdapterAdapter = new FeesAdapter(this, feesInformationListDataList);
                paymentHistoryListView.setAdapter(feesAdapterAdapter);

            }
        }

    }

    private void OpenDialogForFeesComment(FeesInformationListData data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(data.getFees_title());
        builder.setMessage("Fees Category - " + data.getFees_Student_fee_category() + "\n\nDescription - " + data.getFees_Comment());
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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

