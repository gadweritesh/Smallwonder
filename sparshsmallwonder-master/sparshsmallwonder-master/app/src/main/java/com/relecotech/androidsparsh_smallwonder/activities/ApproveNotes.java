package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ApproveNotes extends AppCompatActivity {

    TextView notesCategoryTextView, meetingRequestedSchedule, notesDescriptionTextView;
    EditText replyEditText;
    ImageView backpressarrowimageView;
    Button meetingApproveBtn, meetingDenyBtn, noteReplyBtn;
    private ProgressDialog progressDialog;
    private JsonObject jsonObjectApproveNotes;
    private Bundle getBundleNotesData;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private TextView replyTextHeader;
    private MobileServiceClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_notes);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notesCategoryTextView = (TextView) findViewById(R.id.notesApproveCategory);
        meetingRequestedSchedule = (TextView) findViewById(R.id.notesApproveScheduleMeetTextView);
        notesDescriptionTextView = (TextView) findViewById(R.id.notesApproveDescriptionTextView);
        replyEditText = (EditText) findViewById(R.id.notesApproveReplyEditText);
        replyTextHeader = (TextView) findViewById(R.id.replyHeaderTextView1);
        meetingApproveBtn = (Button) findViewById(R.id.notesApproveMeetingApproveButton);
        meetingDenyBtn = (Button) findViewById(R.id.notesApproveMeetingDenyButton);
        noteReplyBtn = (Button) findViewById(R.id.notesApproveReplyButton);

        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        getBundleNotesData = getIntent().getExtras();


        notesCategoryTextView.setText(getBundleNotesData.getString("Category"));
        notesDescriptionTextView.setText(String.valueOf(getBundleNotesData.getString("Description").replace("\\s", "").replaceAll("\\\\n", "\n")));
        replyEditText.setText(getBundleNotesData.getString("Reply"));


        if (userRole.equals("Student")) {
            getSupportActionBar().setTitle(getBundleNotesData.getString("ConcernedTeacher"));

            replyEditText.setMovementMethod(new ScrollingMovementMethod());// to make assignment description scrollable
            replyEditText.setKeyListener(null);

            noteReplyBtn.setVisibility(View.VISIBLE);
            meetingApproveBtn.setVisibility(View.INVISIBLE);
            meetingDenyBtn.setVisibility(View.INVISIBLE);
            if (getBundleNotesData.getString("Reply").equals("")) {
                replyEditText.setVisibility(View.INVISIBLE);
                replyTextHeader.setVisibility(View.INVISIBLE);
            }

            if (getBundleNotesData.getString("Category").equals("Meeting")) {

                meetingRequestedSchedule.setText("Requested Schedule" + "\n" + getBundleNotesData.getString("MeetingSchedule"));
                if (getBundleNotesData.getString("Status").equals("Approved")) {
                    noteReplyBtn.setText("Meeting Approved");
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#009688"));

                } else if (getBundleNotesData.getString("Status").equals("Denied")) {
                    noteReplyBtn.setText("Meeting Refused");
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#ff4343"));

                } else {
                    noteReplyBtn.setText("Waiting for Approval");
                    noteReplyBtn.setTypeface(null, Typeface.BOLD_ITALIC);
                }

            } else {

                if (getBundleNotesData.getString("Status").equals("Replied")) {
                    noteReplyBtn.setText("Replied");
                } else {
                    noteReplyBtn.setText("Waiting for Reply");
                    noteReplyBtn.setTypeface(null, Typeface.BOLD_ITALIC);
                }
            }
        } else {

            getSupportActionBar().setTitle(getBundleNotesData.getString("ConcernedStudent"));
            replyEditText.setHint("Reply");

            if (getBundleNotesData.getString("Category").equals("Meeting")) {
                meetingRequestedSchedule.setText("Requested Schedule" + "\n" + getBundleNotesData.getString("MeetingSchedule"));

                if (getBundleNotesData.getString("Status").equals("Approved")) {

                    noteReplyBtn.setVisibility(View.VISIBLE);
                    meetingApproveBtn.setVisibility(View.INVISIBLE);
                    meetingDenyBtn.setVisibility(View.INVISIBLE);
                    noteReplyBtn.setText(getBundleNotesData.getString("Status"));
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#009688"));
                    replyEditText.setKeyListener(null);

                    if (getBundleNotesData.getString("Reply").equals("")) {
                        replyEditText.setVisibility(View.INVISIBLE);
                        replyTextHeader.setVisibility(View.INVISIBLE);
                    }

                } else if (getBundleNotesData.getString("Status").equals("Denied")) {

                    noteReplyBtn.setText("Meeting Refused");
                    replyEditText.setKeyListener(null);
                    noteReplyBtn.setText("Meeting Refused");
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#ff4343"));
                    meetingApproveBtn.setVisibility(View.INVISIBLE);
                    meetingDenyBtn.setVisibility(View.INVISIBLE);
                    replyEditText.setKeyListener(null);

                    if (getBundleNotesData.getString("Status").equals("")) {
                        replyEditText.setVisibility(View.INVISIBLE);
                        replyTextHeader.setVisibility(View.INVISIBLE);
                    }

                } else {

                    noteReplyBtn.setVisibility(View.INVISIBLE);
                    meetingApproveBtn.setVisibility(View.VISIBLE);
                    meetingDenyBtn.setVisibility(View.VISIBLE);
                    meetingApproveBtn.setText("Approve");
                    meetingDenyBtn.setText("Deny");

                }
            } else {

                noteReplyBtn.setVisibility(View.VISIBLE);
                meetingApproveBtn.setVisibility(View.INVISIBLE);
                meetingDenyBtn.setVisibility(View.INVISIBLE);

                if (getBundleNotesData.getString("Status").equals("Replied")) {

                    noteReplyBtn.setText(getBundleNotesData.getString("Status"));
                    replyEditText.setKeyListener(null);
                    if (getBundleNotesData.getString("Status").equals("")) {
                        replyEditText.setVisibility(View.INVISIBLE);
                        replyTextHeader.setVisibility(View.INVISIBLE);
                    }

                } else {
                    noteReplyBtn.setText("Reply");

                }
            }
        }

        meetingApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                alertDialog("Confirm Meeting Approval", "Approve", getString(R.string.cancel), "Approved");
            }
        });

        meetingDenyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                alertDialog("Confirm Meeting Denial", "Deny", getString(R.string.cancel), "Denied");
            }
        });
        noteReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (userRole.equals("Teacher") && !(getBundleNotesData.getString("Status").equals("Replied"))
                        && !(getBundleNotesData.getString("Status").equals("Approved"))) {
                    alertDialog("Confirm Reply", "Reply", getString(R.string.cancel), "Replied");

                }
            }
        });

    }

    private void alertDialog(String confirmation, String positiveButton, String cancel, final String action) {
        new AlertDialog.Builder(ApproveNotes.this)
                .setTitle(confirmation)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        noteAction(action);

                    }
                }).setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }


    private void noteAction(String status) {

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        String reply = replyEditText.getText().toString().replace("'", "''");
        jsonObjectApproveNotes = new JsonObject();
        jsonObjectApproveNotes.addProperty("Reply", reply);
        jsonObjectApproveNotes.addProperty("Status", status);
        jsonObjectApproveNotes.addProperty("Id", getBundleNotesData.getString("NotesId"));
        jsonObjectApproveNotes.addProperty("Category", getBundleNotesData.getString("Category"));
        jsonObjectApproveNotes.addProperty("Student_id", getBundleNotesData.getString("StudentId"));
        jsonObjectApproveNotes.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_ID));

        ApproveNotesApiCall();
    }


    private void ApproveNotesApiCall() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("parentZoneUpdateApi", jsonObjectApproveNotes);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" ApproveNotes exception    " + exception);
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        progressDialog.cancel();
                        new AlertDialog.Builder(ApproveNotes.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ApproveNotesApiCall();
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
                System.out.println(" ApproveNotes  API   response    " + response);
                progressDialog.dismiss();
                if (response.toString().equals("true")) {

                    new AlertDialog.Builder(ApproveNotes.this)
                            .setTitle("Response submitted")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create().show();

                } else {
                    Log.d("leave_parentZoneItem", "is null");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (userRole.equals("Teacher")) {
            menu.add(Menu.NONE, 0, Menu.NONE, "Roll No." + getBundleNotesData.getString("StudentRollNo"))
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
