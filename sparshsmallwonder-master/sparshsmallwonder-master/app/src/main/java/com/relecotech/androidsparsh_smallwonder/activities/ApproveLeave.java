package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.util.HashMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ApproveLeave extends AppCompatActivity {

    private TextView leaveFromTextView, leaveToTextView;
    private TextView dayCountTextView, causeTextView, leaveStatusTextView, replyTextHeader;
    private EditText replyEditText;
    private Bundle getBundleLeaveData;
    private Button leaveApproveBtn, leaveDenyBtn;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private ProgressDialog progressDialog;
    private JsonObject jsonObjectApproveLeave;
    private ImageView backArrowImageView;
    private TextView studentNameTextView, rollNoTextView;
    private LinearLayout denyApproveLinear;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_leave);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getBundleLeaveData = getIntent().getExtras();
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        //All component initialization
        leaveFromTextView = (TextView) findViewById(R.id.leavesApproveLeavesFromTextView);
        leaveToTextView = (TextView) findViewById(R.id.leavesApproveLeavesToTextView);
        dayCountTextView = (TextView) findViewById(R.id.leaveApproveLeavesCount);
        causeTextView = (TextView) findViewById(R.id.leavesApproveCauseEditText);
        leaveStatusTextView = (TextView) findViewById(R.id.leaveStatusTextView);

        replyTextHeader = (TextView) findViewById(R.id.leave_approve_reply_header);
        replyEditText = (EditText) findViewById(R.id.leaveReplyEditText);
        leaveApproveBtn = (Button) findViewById(R.id.leaveApproveBtn);
        leaveDenyBtn = (Button) findViewById(R.id.leavesDenyButton);
        denyApproveLinear = (LinearLayout) findViewById(R.id.deny_approve_linear);


        if (userRole.equals("Student")) {
            getSupportActionBar().setTitle(getBundleLeaveData.getString("StudentName"));
        } else {
            getSupportActionBar().setTitle(getBundleLeaveData.getString("StudentName"));
        }

        leaveFromTextView.setText(getBundleLeaveData.getString("StartDay"));
        leaveToTextView.setText(getBundleLeaveData.getString("EndDay"));
        dayCountTextView.setText(String.valueOf(getBundleLeaveData.getInt("DaysCount")));
        causeTextView.setText(getBundleLeaveData.getString("Cause").replace("\\n", "\n"));
//        causeTextView.setText(getBundleLeaveData.getString("Cause").replaceAll("\\\\n", "\n"));
        replyEditText.setText(getBundleLeaveData.getString("Reply").replace("\\n", "\n"));

        if (getBundleLeaveData.getString("Status").equals("Pending")) {
            if (userRole.equals("Student")) {
                denyApproveLinear.setVisibility(View.GONE);
                leaveStatusTextView.setText("Waiting for Approval");
                leaveStatusTextView.setTypeface(null, Typeface.BOLD_ITALIC);
                replyEditText.setKeyListener(null);
                if (getBundleLeaveData.getString("Reply").equals("")) {
                    replyEditText.setVisibility(View.INVISIBLE);
                    replyTextHeader.setVisibility(View.INVISIBLE);
                }
            } else {
                leaveApproveBtn.setText("Approve");
                leaveDenyBtn.setText("Deny");
                leaveApproveBtn.setVisibility(View.VISIBLE);
                leaveDenyBtn.setVisibility(View.VISIBLE);
                leaveStatusTextView.setVisibility(View.INVISIBLE);
            }
        } else if (getBundleLeaveData.getString("Status").equals("Approved")) {
            denyApproveLinear.setVisibility(View.INVISIBLE);
            leaveStatusTextView.setVisibility(View.VISIBLE);
            leaveStatusTextView.setBackgroundColor(Color.parseColor("#009688"));

            leaveStatusTextView.setText("Approved");
            leaveStatusTextView.setTypeface(null, Typeface.BOLD_ITALIC);
            replyEditText.setKeyListener(null);
            if (getBundleLeaveData.getString("Reply").equals("")) {
                replyEditText.setVisibility(View.INVISIBLE);
                replyTextHeader.setVisibility(View.INVISIBLE);
            }

        } else {
            denyApproveLinear.setVisibility(View.GONE);
            leaveStatusTextView.setVisibility(View.VISIBLE);
            leaveStatusTextView.setBackgroundColor(Color.parseColor("#929393"));

            leaveStatusTextView.setText("Denied");
            replyEditText.setKeyListener(null);
            if (getBundleLeaveData.getString("Reply").equals("")) {
                replyEditText.setVisibility(View.INVISIBLE);
                replyTextHeader.setVisibility(View.INVISIBLE);
            }
        }

        leaveApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ApproveLeave.this)
                        .setTitle("Confirm Leave Approval")
                        .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                leaveAction("Approved");
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create().show();
            }
        });
        leaveDenyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ApproveLeave.this)
                        .setTitle("Confirm Leave Denial")
                        .setPositiveButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                leaveAction("Denied");
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create().show();
            }
        });
    }

    private void leaveAction(String status) {
        jsonObjectApproveLeave = new JsonObject();
        jsonObjectApproveLeave.addProperty("Reply", replyEditText.getText().toString());
        jsonObjectApproveLeave.addProperty("Status", status);
        jsonObjectApproveLeave.addProperty("Id", getBundleLeaveData.getString("LeaveId"));
        jsonObjectApproveLeave.addProperty("Category", "Leave");
        jsonObjectApproveLeave.addProperty("Student_id", getBundleLeaveData.getString("StudentId"));
        jsonObjectApproveLeave.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_ID));

        ApproveLeaveApiCall();
    }

    private void ApproveLeaveApiCall() {

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("parentZoneUpdateApi", jsonObjectApproveLeave);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Approve Leave exception    " + exception);
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        progressDialog.cancel();
                        new AlertDialog.Builder(ApproveLeave.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ApproveLeaveApiCall();
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
                pdCanceller.postDelayed(progressRunnable, 10000);

            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("Approve Leave  API   response    " + response);
                progressDialog.dismiss();
                if (response.toString().equals("true")) {
                    new AlertDialog.Builder(ApproveLeave.this)
                            .setTitle("Response Submitted.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (userRole.equals("Teacher")) {
            menu.add(Menu.NONE, 0, Menu.NONE, "Roll No." + getBundleLeaveData.getString("StudentRollNo"))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
