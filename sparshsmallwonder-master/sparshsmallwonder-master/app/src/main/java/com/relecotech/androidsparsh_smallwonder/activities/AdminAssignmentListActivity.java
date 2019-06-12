package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.relecotech.androidsparsh_smallwonder.adapters.AdminAssignmentRecyclerAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AssignmentListData;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.text.ParseException;
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
import java.util.TreeMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Amey on 24-05-2018.
 */

public class AdminAssignmentListActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String teacherFirstName, teacherLastName;
    private String assignmentId, assignmentStatus;
    private String assignmentPostDate, assignmentDueDate;
    private String maxCredit, assignmentGrade, assignmentSubject, scoreType;
    private String assignmentDescription, assignmentSubmittedBy, assignment_status_notes;
    private String assignmentAttachmentCount, assignment_status_credit, assignment_status_grades;
    private String assignmentAttachmentIdentifier;

    private SimpleDateFormat getParcebleDateFormat;
    private SimpleDateFormat getTargetDateFormat;

    private String assignmentClass;
    private String assignmentDivision;
    private Map<String, List<String>> assignmentIdStudentLikeHashMap;
    private List<String> studentLikeArrayList;
    private Map<String, AssignmentListData> assignmentIdAssignObjHashMap;
    private AssignmentListData getAssignmentListData;
    private boolean likeCheck = false;
    public Map<String, List<AssignmentListData>> assignmentList_Map;
    private List<AssignmentListData> assignmentList;
    private ListView assignmentTabRecyclerView;
    private AdminAssignmentRecyclerAdapter assignment_adapter;
    private MobileServiceClient mClient;
    private TextView noDataAvailableTextView;
    private ProgressDialog progressDialog;
    private SimpleDateFormat detailDisplayDateFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_assignment_list_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClient = Singleton.Instance().mClientMethod(this);
        sessionManager = new SessionManager(this, sharedPrefValue);

        assignmentIdStudentLikeHashMap = new HashMap<>();
        assignmentList_Map = new HashMap<>();
        studentLikeArrayList = new ArrayList<>();
        assignmentIdAssignObjHashMap = new TreeMap<>();
        assignmentList = new ArrayList<>();
        assignmentTabRecyclerView = (ListView) findViewById(R.id.assignmentTabRecyclerView);
        noDataAvailableTextView = (TextView) findViewById(R.id.assignmentNoDataAvailable_textView);

        getParcebleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        getParcebleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
        getTargetDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault());
        detailDisplayDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        fetchAssignment();


        assignmentTabRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AssignmentListData assignmentListData = assignmentList.get(position);
                Intent assDetailIntent = new Intent(AdminAssignmentListActivity.this, AssignmentDetail.class);

                Bundle intentBundle = new Bundle();

                intentBundle.putString("AssignId", assignmentListData.getAssId());
                intentBundle.putString("AssignStatus", assignmentListData.getAssStatus());
                intentBundle.putString("AssignAttachCount", assignmentListData.getAttachmentCount());
                intentBundle.putString("AssignMaxCredits", assignmentListData.getMaxCredits());
                intentBundle.putString("AssignDescription", assignmentListData.getDescription());
                intentBundle.putString("AssignSubject", assignmentListData.getSubject());
                intentBundle.putString("AssignIssueDate", detailDisplayDateFormat.format(assignmentListData.getIssueDate()));
                intentBundle.putString("AssignSubmittedBy", assignmentListData.getSubmittedBy());
                intentBundle.putString("AssignClassStd", assignmentListData.getClassStd());
                intentBundle.putString("AssignDivision", assignmentListData.getDivision());
                intentBundle.putString("AssignGradeEarned", assignmentListData.getGradeEarned());
                intentBundle.putString("AssignScoreType", assignmentListData.getScoreType());
                intentBundle.putString("AssignDueDate", detailDisplayDateFormat.format(assignmentListData.getDueDate()));
                intentBundle.putString("AssignStatusComment", assignmentListData.getNote());
                intentBundle.putString("AssignAttachmentIdentifier", assignmentListData.getAssignmentAttachmentIdentifier());
                intentBundle.putBoolean("AssignLikeCheck", assignmentListData.isLikeCheck());

                assDetailIntent.putExtras(intentBundle);
                startActivity(assDetailIntent);

            }
        });
    }

    private void fetchAssignment() {

        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        System.out.println("sessionManager.getUserDetails().get(SessionManager.KEY_ADMIN_ID)------------  " + sessionManager.getUserDetails().get(SessionManager.KEY_ADMIN_ID));
        System.out.println("sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE)------------  " + sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE));
        JsonObject jsonObjectAssignmentParameters = new JsonObject();
        try {

            jsonObjectAssignmentParameters.addProperty("AdminId", sessionManager.getUserDetails().get(SessionManager.KEY_ADMIN_ID));
            jsonObjectAssignmentParameters.addProperty("userRole", sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE));
            jsonObjectAssignmentParameters.addProperty("branchId", sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID));
            jsonObjectAssignmentParameters.addProperty("status", "Normal");

        } catch (Exception e) {
            System.out.println("Admin Asignment List Activity Exception " + e.getMessage());
        }


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentFetchApi", jsonObjectAssignmentParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AdminAssignmentListActivity.this)
                                .setMessage(R.string.check_network)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetchAssignment();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //onBackPressed();
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

    private void parseJSON(JsonElement adminAssignmentFetchResponse) {

        JsonArray assignmentJsonArray = adminAssignmentFetchResponse.getAsJsonArray();
        if (assignmentJsonArray.size() == 0) {
            progressDialog.dismiss();
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            noDataAvailableTextView.setVisibility(View.VISIBLE);
        } else {
            noDataAvailableTextView.setVisibility(View.INVISIBLE);

            for (int i = 0; i < assignmentJsonArray.size(); i++) {


                JsonObject jsonObjectForIteration = assignmentJsonArray.get(i).getAsJsonObject();

                assignmentId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                assignmentPostDate = jsonObjectForIteration.get("assignmentPostDate").toString().replace("\"", "");
                assignmentDueDate = jsonObjectForIteration.get("assignmentDueDate").toString().replace("\"", "");
                maxCredit = jsonObjectForIteration.get("assignmentCredit").toString().replace("\"", "");
                assignmentGrade = jsonObjectForIteration.get("assignmentGrade").toString().replace("\"", "");
                assignmentSubject = jsonObjectForIteration.get("assignmentSubject").toString().replace("\"", "");
                assignmentDescription = jsonObjectForIteration.get("assignmentDescription").toString().replace("\\n", "\n").replace("\\r", "").replace("\"", "");
                assignmentSubmittedBy = jsonObjectForIteration.get("assignmentSubmittedBy").toString().replace("\"", "");
                scoreType = jsonObjectForIteration.get("scoreType").toString().replace("\"", "");
                assignmentAttachmentCount = jsonObjectForIteration.get("assignmentAttachmentCount").toString().replace("\"", "");
                assignmentAttachmentIdentifier = jsonObjectForIteration.get("assignmentAttachmentIdentifier").toString().replace("\"", "");
                System.out.println(" assignmentAttachmentIdentifier " + assignmentAttachmentIdentifier);
                String SubmittedTeacher_firstName = jsonObjectForIteration.get("SubmittedTeacher_firstName").toString().replace("\"", "");
                String SubmittedTeacher_lastName = jsonObjectForIteration.get("SubmittedTeacher_lastName").toString().replace("\"", "");

//                getParcebleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//                getParcebleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
//                getTargetDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault());
                Date dateIssue = null;
                Date dateDue = null;
                try {
                    dateIssue = getParcebleDateFormat.parse(assignmentPostDate);
                    dateDue = getParcebleDateFormat.parse(assignmentDueDate);
                    assignmentPostDate = getTargetDateFormat.format(dateIssue);
                    assignmentDueDate = getTargetDateFormat.format(dateDue);
                    System.out.println("assignmentDueDate after" + assignmentDueDate);
                } catch (ParseException e) {
                }
                assignmentClass = jsonObjectForIteration.get("assignmentClass").toString().replace("\"", "");
                assignmentDivision = jsonObjectForIteration.get("assignmentDivision").toString().replace("\"", "");
                String student_id = jsonObjectForIteration.get("Student_id").toString().replace("\"", "");
                String selectedItemId = jsonObjectForIteration.get("selectedItemId").toString().replace("\"", "");


                if (!selectedItemId.equals("null")) {
                    if (assignmentIdStudentLikeHashMap.containsKey(selectedItemId)) {
                        studentLikeArrayList = assignmentIdStudentLikeHashMap.get(selectedItemId);
                        studentLikeArrayList.add(student_id);
                    } else {
                        studentLikeArrayList = new ArrayList<>();
                        studentLikeArrayList.add(student_id);
                        assignmentIdStudentLikeHashMap.put(selectedItemId, studentLikeArrayList);
                    }
                } else {
                    System.out.println(" INSIDE  IF  ELSE selectedItemId " + selectedItemId + " student_id " + student_id);
                }


                if (assignmentIdAssignObjHashMap.containsKey(assignmentId)) {
                    getAssignmentListData = assignmentIdAssignObjHashMap.get(assignmentId);
                } else {
                    assignmentIdAssignObjHashMap.put(assignmentId, new AssignmentListData(R.drawable.ic_assignment, assignmentId, maxCredit, assignmentSubject, dateIssue, dateDue, assignmentClass, assignmentDivision, SubmittedTeacher_firstName + " " + SubmittedTeacher_lastName, assignmentDescription, "Pending", assignment_status_credit, assignment_status_grades, assignment_status_notes, scoreType, assignmentAttachmentCount, 0, assignmentAttachmentIdentifier, likeCheck));
                }

            }
            for (String val : assignmentIdAssignObjHashMap.keySet()) {
                if (assignmentIdStudentLikeHashMap.containsKey(val)) {
                    int countLike = assignmentIdStudentLikeHashMap.get(val).size();
                    assignmentIdAssignObjHashMap.get(val).setLikeCount(countLike);
                    assignmentIdAssignObjHashMap.get(val).setLikeCheck(true);
                    AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);
                    assignmentList.add(0, listData);
                } else {
                    AssignmentListData listData = assignmentIdAssignObjHashMap.get(val);
                    assignmentList.add(0, listData);
                }
            }

            //********  Sorting Assignment List   ************ //

            Collections.sort(assignmentList, new Comparator<AssignmentListData>() {
                @Override
                public int compare(AssignmentListData obj1, AssignmentListData obj2) {
                    // ## Ascending order
                    //return obj1.getIssueDate().compareToIgnoreCase(obj2.getIssueDate());
                    // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                    return obj2.getIssueDate().compareTo(obj1.getIssueDate());
                    // ## Descending order
                    // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                }
            });
            //********  Sorting Assignment List ************ //
            progressDialog.dismiss();
            assignment_adapter = new AdminAssignmentRecyclerAdapter(AdminAssignmentListActivity.this, assignmentList);
            assignmentTabRecyclerView.setAdapter(assignment_adapter);

            System.out.println("assignmentIdAssignObjHashMap.size()---------    " + assignmentIdAssignObjHashMap.size());
            System.out.println("assignmentList.size()---------    " + assignmentList.size());
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
