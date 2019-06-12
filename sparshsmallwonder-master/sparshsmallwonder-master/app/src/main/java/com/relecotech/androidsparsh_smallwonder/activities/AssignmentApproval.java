package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.relecotech.androidsparsh_smallwonder.adapters.AssignmentApproveAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AssignmentApproveListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AssignmentApproval extends AppCompatActivity {

    private Button assSubmitAndConfirm;
    private ProgressBar progressBar;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private JsonArray jsonArrayForStatusUpdation;
    ArrayList<AssignmentApproveListData> studentNameList;
    private HashMap<String, AssignmentApproveListData> getStudentAfterApprovedAssignmentData;
    private ListView studentNameListView;
    private String assignmentId;
    private AssignmentApproveListData selectedAssApproveData;
    private Spinner assmntApprovalScoresSpinner;
    private EditText dialogNotesTextView;
    private TextView dialogApprovedScoreTextView;

    ArrayList<String> approvalCreditSpinnerList;
    private String scoreType;
    private int maxCredit;

    private String approvedMarks;
    private String approvedGrades;
    private ProgressDialog assApproveProgressDialog;
    private String assignmentSubject;
    private MobileServiceClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_approve);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();


        assApproveProgressDialog = new ProgressDialog(this);
        assApproveProgressDialog.setMessage(getString(R.string.loading));
        assApproveProgressDialog.setCancelable(false);

        assignmentId = getIntent().getStringExtra("assignmentId");
        scoreType = getIntent().getStringExtra("scoreType");
        maxCredit = getIntent().getIntExtra("maxCredit", 0);
        assignmentSubject = getIntent().getStringExtra("assignmentSubject");

        studentNameList = new ArrayList<>();
        approvalCreditSpinnerList = new ArrayList<>();
        getStudentAfterApprovedAssignmentData = new HashMap<>();

        if (scoreType.equals("Grades")) {

            approvalCreditSpinnerList.add("A+");
            approvalCreditSpinnerList.add("A");
            approvalCreditSpinnerList.add("B+");
            approvalCreditSpinnerList.add("B");
            approvalCreditSpinnerList.add("C+");
            approvalCreditSpinnerList.add("C");
            approvalCreditSpinnerList.add("D+");
            approvalCreditSpinnerList.add("D");
            approvalCreditSpinnerList.add("[ Select grades ]");
        } else {
            for (int creditCounter = 1; creditCounter <= maxCredit; creditCounter++) {
                approvalCreditSpinnerList.add("" + creditCounter);
            }
            approvalCreditSpinnerList.add("[ Select marks ]");
        }

        assSubmitAndConfirm = (Button) findViewById(R.id.assApproveSubmitBtn);
        studentNameListView = (ListView) findViewById(R.id.assApproveListView);
        progressBar = (ProgressBar) findViewById(R.id.assApproveProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (connectionDetector.isConnectingToInternet()) {
            FetchStudentList();
            progressBar.setVisibility(View.VISIBLE);
        } else {
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        studentNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                selectedAssApproveData = studentNameList.get(itemPosition);
                showApprovalDialog(itemPosition, selectedAssApproveData);
            }
        });
    }

    private void showApprovalDialog(final int itemPosition, final AssignmentApproveListData selectedAssApproveData) {

        final AssignmentApproveAdapter adapterRefresh = new AssignmentApproveAdapter(getApplicationContext(), studentNameList);

        final String approvalDialogAssmntStatus = selectedAssApproveData.getStatus();

        LayoutInflater flater = this.getLayoutInflater();
        final View view = flater.inflate(R.layout.assignment_approve_alert_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(selectedAssApproveData.getFirstName() + " " + selectedAssApproveData.getmName() + " " + selectedAssApproveData.getLastName());

        ArrayAdapter<String> creditsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, approvalCreditSpinnerList);
        assmntApprovalScoresSpinner = (Spinner) view.findViewById(R.id.ass_credits_awardes_spinner);
        dialogNotesTextView = (EditText) view.findViewById(R.id.ass_notes_textView);
        dialogApprovedScoreTextView = (TextView) view.findViewById(R.id.approvedScoresTextView);

        creditsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        switch (approvalDialogAssmntStatus) {

            case "Pending":
                assmntApprovalScoresSpinner.setVisibility(View.VISIBLE);
                assmntApprovalScoresSpinner.setAdapter(creditsAdapter);
                assmntApprovalScoresSpinner.setSelection(approvalCreditSpinnerList.size() - 1);
                dialogApprovedScoreTextView.setVisibility(View.INVISIBLE);
                if (scoreType.equals("Grades")) {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getGrades());
                } else {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getCredits());
                }

                builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //logic to maintain position even after list is changed
                        //it takes few indexes top etc and sets the value for setSelection(index,top)

                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        System.out.println("assmntApprovalScoresSpinner :" + assmntApprovalScoresSpinner.getSelectedItem().toString());
                        System.out.println("dialogApprovedScoreTextView :" + dialogApprovedScoreTextView.getText().toString());
                        System.out.println("dialogNotesTextView :" + dialogNotesTextView.getText().toString());


                        if (scoreType.equals("Grades")) {

                            approvedMarks = "--";
                            approvedGrades = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            System.out.println("approvedGrades : " + approvedGrades);
                            if (approvedGrades.equals("[ Select grades ]")) {
                                System.out.println("approvedGrades IN : " + approvedGrades);
                                showErrorMessage("Grades");

                            } else {
                                System.out.println("pending grades OK");
                                updateApprovalList(itemPosition);
                            }
                        } else {

                            approvedGrades = "--";
                            approvedMarks = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedMarks.equals("[ Select marks ]")) {
                                showErrorMessage("Marks");
                            } else {
                                System.out.println("pending marks OK");
                                updateApprovalList(itemPosition);
                            }
                        }

                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                builder.setNeutralButton("Re-Submit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //logic to maintain position even after list is changed
                        //it takes few indexes top etc and sets the value for setSelection(index,top)
                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        AssignmentApproveListData assApproveData = new AssignmentApproveListData(selectedAssApproveData.getFirstName(),
                                selectedAssApproveData.getLastName(), "Re-Submit", selectedAssApproveData.getRollNo(),
                                selectedAssApproveData.getmName(), selectedAssApproveData.getAssmntStatusTableId(),
                                selectedAssApproveData.getAssmntId(), selectedAssApproveData.getStudentId(),
                                selectedAssApproveData.getCredits(), selectedAssApproveData.getGrades(),
                                dialogNotesTextView.getText().toString());

                        studentNameList.set(itemPosition, assApproveData);
                        getStudentAfterApprovedAssignmentData.put(selectedAssApproveData.getStudentId(), assApproveData); // UPADTE STUDENT APPROVED MAP
                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                break;

            case "Re-Submit":
                assmntApprovalScoresSpinner.setVisibility(View.VISIBLE);
                assmntApprovalScoresSpinner.setAdapter(creditsAdapter);
                assmntApprovalScoresSpinner.setSelection(approvalCreditSpinnerList.size() - 1);
                dialogNotesTextView.setText(selectedAssApproveData.getNotes());
                dialogApprovedScoreTextView.setVisibility(View.INVISIBLE);

                builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        System.out.println("assmntApprovalScoresSpinner : " + assmntApprovalScoresSpinner.getSelectedItem().toString());
                        System.out.println("dialogApprovedScoreTextView :" + dialogApprovedScoreTextView.getText().toString());
                        System.out.println("dialogNotesTextView :" + dialogNotesTextView.getText().toString());


                        if (scoreType.equals("Grades")) {

                            approvedMarks = "--";
                            approvedGrades = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedGrades.equals("[ Select grades ]")) {
                                showErrorMessage("Grades");
                            } else {
                                System.out.println("Re-Submit grades OK");
                                updateApprovalList(itemPosition);
                            }
                        } else {

                            approvedGrades = "--";
                            approvedMarks = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedMarks.equals("[ Select marks ]")) {
                                showErrorMessage("Marks");
                            } else {
                                System.out.println("Re-Submit marks OK");
                                updateApprovalList(itemPosition);
                            }
                        }

                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                builder.setNeutralButton("Re-Submit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //it takes few indexes top etc and sets the value for setSelection(index,top)
                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        AssignmentApproveListData assApproveData = new AssignmentApproveListData(selectedAssApproveData.getFirstName(),
                                selectedAssApproveData.getLastName(), "Re-Submit", selectedAssApproveData.getRollNo(),
                                selectedAssApproveData.getmName(), selectedAssApproveData.getAssmntStatusTableId(),
                                selectedAssApproveData.getAssmntId(), selectedAssApproveData.getStudentId(),
                                selectedAssApproveData.getCredits(), selectedAssApproveData.getGrades(),
                                dialogNotesTextView.getText().toString());

                        studentNameList.set(itemPosition, assApproveData);
                        getStudentAfterApprovedAssignmentData.put(selectedAssApproveData.getStudentId(), assApproveData);
                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                break;

            case "Approved":
                assmntApprovalScoresSpinner.setVisibility(View.VISIBLE);
                assmntApprovalScoresSpinner.setAdapter(creditsAdapter);
                dialogApprovedScoreTextView.setVisibility(View.INVISIBLE);
                dialogNotesTextView.setText(selectedAssApproveData.getNotes());
                if (scoreType.equals("Grades")) {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getGrades());
                    assmntApprovalScoresSpinner.setSelection(approvalCreditSpinnerList.indexOf(selectedAssApproveData.getGrades()));

                } else {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getCredits());
                    assmntApprovalScoresSpinner.setSelection(approvalCreditSpinnerList.indexOf(selectedAssApproveData.getCredits()));

                }

                builder.setPositiveButton("Approved", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNeutralButton("Update", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logic to maintain position even after list is changed
                        //it takes few indexes top etc and sets the value for setSelection(index,top)

                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        System.out.println("assmntApprovalScoresSpinner : " + assmntApprovalScoresSpinner.getSelectedItem().toString());
                        System.out.println("dialogApprovedScoreTextView :" + dialogApprovedScoreTextView.getText().toString());
                        System.out.println("dialogNotesTextView :" + dialogNotesTextView.getText().toString());

                        if (scoreType.equals("Grades")) {
                            approvedMarks = "--";
                            approvedGrades = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedGrades.equals("[ Select grades ]")) {
                                showErrorMessage("Grades");

//                                Toast.makeText(AssigmentApproval.this, "Select grades", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println("Approved grades OK");
                                updateApprovalList(itemPosition);
                            }
                        } else {
                            approvedGrades = "--";
                            approvedMarks = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedMarks.equals("[ Select marks ]")) {

                                showErrorMessage("Marks");

                            } else {
                                System.out.println("Approved marks OK");
                                updateApprovalList(itemPosition);
                            }
                        }

                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });
                break;
        }

        if (approvalDialogAssmntStatus.equals("Approved")) {

            //This condition is only for update approval function
            //these lines for getting dialog btns and then enable/disable their click
            //BCOZ builder.create().show(); using directly does not provide   dialog.getButton() method
            // hence write the full form by converting builder.create().show(); --to-->  AlertDialog dialog = builder.create();
            // dialog.show();
            //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);


            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        } else {
            //this line for normal dialog function
            builder.create().show();
        }
    }


    private void showErrorMessage(String type) {
        FancyToast.makeText(this, "Select " + type, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
    }


    private void updateApprovalList(int itemPosition) {

        AssignmentApproveListData assApproveData = new AssignmentApproveListData(selectedAssApproveData.getFirstName(), selectedAssApproveData.getLastName(), "Approved",
                selectedAssApproveData.getRollNo(), selectedAssApproveData.getmName(), selectedAssApproveData.getAssmntStatusTableId(),
                selectedAssApproveData.getAssmntId(), selectedAssApproveData.getStudentId(),
                approvedMarks, approvedGrades, dialogNotesTextView.getText().toString());

        getStudentAfterApprovedAssignmentData.put(selectedAssApproveData.getStudentId(), assApproveData);

        studentNameList.set(itemPosition, assApproveData);
    }

    public void ApproveAssignment(View view) {

        AssignmentApproveListData approveListData;
        JsonObject jsonObjectForIteration;
        jsonArrayForStatusUpdation = new JsonArray();
        for (Map.Entry<String, AssignmentApproveListData> entry : getStudentAfterApprovedAssignmentData.entrySet()) {

            approveListData = entry.getValue();
            jsonObjectForIteration = new JsonObject();
            jsonObjectForIteration.addProperty("assignment_status_id", approveListData.getAssmntStatusTableId());
            jsonObjectForIteration.addProperty("assignment_status_credits", approveListData.getCredits());
            jsonObjectForIteration.addProperty("assignment_status_grades", approveListData.getGrades());
            jsonObjectForIteration.addProperty("assignment_status_notes", approveListData.getNotes());
            jsonObjectForIteration.addProperty("assignment_status_status", approveListData.getStatus());
            jsonObjectForIteration.addProperty("assignment_status_Student_id", approveListData.getStudentId());
            jsonObjectForIteration.addProperty("assignment_submitted_by", userDetails.get(SessionManager.KEY_TEACHER_ID));
            jsonObjectForIteration.addProperty("assignment_subject",assignmentSubject);

            jsonArrayForStatusUpdation.add(jsonObjectForIteration);
        }

        approveAssmnt();
    }


    private void FetchStudentList() {
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        JsonObject jsonObjectAssignmentApproval = new JsonObject();
        jsonObjectAssignmentApproval.addProperty("assignmentId", assignmentId);
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentApproveFetchtStudentList", jsonObjectAssignmentApproval);
        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElementResponse) {
                resultFuture.set(jsonElementResponse);
                System.out.println(" jsonElementResponse " + jsonElementResponse);

                JsonArray jsonArray = jsonElementResponse.getAsJsonArray();
                for (int i = 0; i <= jsonArray.size() - 1; i++) {
                    JsonObject jsonObjectForIteration = jsonArray.get(i).getAsJsonObject();

                    //fetch data from (assmnt status-Student) JSON
                    String studentFirstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String studentLastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String studentAssignmentStatus = jsonObjectForIteration.get("assignmentStatus").toString().replace("\"", "");
                    String studentMiddleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
                    String studentRollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                    String assmntStatusTableId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String assmntId = jsonObjectForIteration.get("Assignment_id").toString().replace("\"", "");
                    String studentId = jsonObjectForIteration.get("Student_id").toString().replace("\"", "");
                    String credits = jsonObjectForIteration.get("credits").toString().replace("\"", "");
                    String grades = jsonObjectForIteration.get("grades").toString().replace("\"", "");
                    String notes = jsonObjectForIteration.get("notes").toString().replace("\"", "");

                    AssignmentApproveListData listData = new AssignmentApproveListData(studentFirstName, studentLastName, studentAssignmentStatus, studentRollNo, studentMiddleName, assmntStatusTableId, assmntId, studentId, credits, grades, notes);
                    studentNameList.add(listData);
                }

                AssignmentApproveAdapter approveAdapter = new AssignmentApproveAdapter(getApplicationContext(), studentNameList);
                studentNameListView.setAdapter(approveAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Throwable throwable) {
                resultFuture.setException(throwable);
            }
        });

    }

    private void approveAssmnt() {

        assApproveProgressDialog.show();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentApproveApi", jsonArrayForStatusUpdation);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Assignment Approval exception    " + exception);

                new AlertDialog.Builder(AssignmentApproval.this)
                        .setMessage(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                approveAssmnt();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                }).show();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Assignment approved   response    " + response);
                assApproveProgressDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
