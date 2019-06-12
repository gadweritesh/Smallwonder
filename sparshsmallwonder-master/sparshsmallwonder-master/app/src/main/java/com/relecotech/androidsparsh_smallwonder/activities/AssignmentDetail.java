package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.AssignmentAttachmentAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Users_Like;
import com.relecotech.androidsparsh_smallwonder.models.AttachmentListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;
import static com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration.getContainer;

/**
 * Created by amey on 10/16/2015.
 */
public class AssignmentDetail extends AppCompatActivity implements ItemClickListener {

    RecyclerView attachmentRecyclerView;
    private TextView submittedByTextTv;
    private TextView postDateTextView, dueDateTv;
    private TextView descriptionTv;
    private Bundle getBundleData;
    private String assId, classForApproval, divisionForApproval, scoreTypeForApproval, assignAttachCount, assignAttachmentIdentifier;
    private int maxCreditsForsApproval;
    private CardView attachmentPanel;
    private String attachmentFileName;
    ArrayList<AttachmentListData> assignmentAttachmentList;
    private AssignmentAttachmentAdapter assignmentAttachmentAdapter;
    private String directory;
    private LikeButton likeButton;
    private SessionManager sessionManager;
    private String studentId;
    private Boolean assignLikeCheck;
    private String branchId;
    private Users_Like userLikeItem;
    private MobileServiceTable<Users_Like> userLikeTable;
    private String userRole;
    private FloatingActionButton approveFab;
    private TextView commentTextView;
    private String assignmentStatusComment;
    private ConnectionDetector connectionDetector;
    private RelativeLayout relativeLikeLayout;
    private TextView assignmentSubmittedBy;
    private CardView assignment_detail_top_panel_cv;
    private String assignmentSubject;
    private MobileServiceClient mClient;
    private CardView teacherCommentPanel;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_detail);

        mClient = Singleton.Instance().mClientMethod(this);

        userLikeTable = mClient.getTable(Users_Like.class);
        userLikeItem = new Users_Like();



        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);
        studentId = sessionManager.getUserDetails().get(SessionManager.KEY_STUDENT_ID);
        branchId = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignmentAttachmentList = new ArrayList<>();
        initializeViews();

        likeButton.setIcon(IconType.Thumb);
        getBundleDataMethod();
        if (userRole.equals("Student")) {
            approveFab.setVisibility(View.INVISIBLE);
            try {
                if (assignmentStatusComment.isEmpty()) {
                    commentTextView.setText("No Comments");
                } else {
                    commentTextView.setText(assignmentStatusComment.replace("\\n", "\n").replace("\\", ""));
                }
            } catch (Exception e) {
                System.out.println("assignmentStatusComment******* " + e.getMessage());
            }
        }

        if (userRole.equals("Teacher") || userRole.equals("Administrator")) {
//            RelativeLayout.LayoutParams cardViewParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 120);
//            assignment_detail_top_panel_cv.setLayoutParams(cardViewParam);

            approveFab.setVisibility(View.INVISIBLE);
            teacherCommentPanel.setVisibility(View.GONE);
            teacherCommentPanel.setVisibility(View.GONE);
            likeButton.setVisibility(View.INVISIBLE);
            assignmentSubmittedBy.setVisibility(View.GONE);
            submittedByTextTv.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10f);
//            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0f);
            relativeLikeLayout.setLayoutParams(layoutParams);
//            likeButton.setLayoutParams(layoutParams2);
        }

        System.out.println(" assignLikeCheck " + assignLikeCheck);
        if (assignLikeCheck) {
            likeButton.setLiked(true);
            likeButton.setEnabled(false);
        }

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                System.out.println(" INSIDE LIKE");
                likeButton.setLiked(true);
                likeButton.setEnabled(false);

                System.out.println("assId " + assId + " studentId " + studentId + "branchId " + branchId);
                userLikeItem.setSelectedItemId(assId);
                userLikeItem.setStudent_id(studentId);
                userLikeItem.setBranch_id(branchId);
                new LikeAsyncTask().execute();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                System.out.println(" INSIDE unLiked");
            }
        });

        assignmentSubject = getBundleData.getString("AssignSubject");

        getSupportActionBar().setTitle(assignmentSubject);

        submittedByTextTv.setText(getBundleData.getString("AssignSubmittedBy"));
        postDateTextView.setText(getBundleData.getString("AssignIssueDate"));
        dueDateTv.setText(getBundleData.getString("AssignDueDate"));
        descriptionTv.setText(getBundleData.getString("AssignDescription"));

        File dir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Assignment_Download");
        directory = dir.getPath();
        System.out.println("Directory created " + directory);

        try {
            if (dir.mkdirs()) {
                System.out.println("Directory  created");
            } else {
                System.out.println("Directory not created");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTION" + e.getMessage());
        }


        System.out.println(" assignAttachCount " + assignAttachCount);
        if (assignAttachCount.equals("0")) {
            attachmentPanel.setVisibility(View.INVISIBLE);
        } else {
            attachmentPanel.setVisibility(View.VISIBLE);
            fetchAttachmentMetadata();
        }
    }

    private void initializeViews() {
        //Initializing all the components
        likeButton = (LikeButton) findViewById(R.id.likeButton);
        commentTextView = (TextView) findViewById(R.id.commentTextView);
        approveFab = (FloatingActionButton) findViewById(R.id.assignmentApproveButton);
        assignment_detail_top_panel_cv = (CardView) findViewById(R.id.assignment_detail_top_panel_cv);
        assignmentSubmittedBy = (TextView) findViewById(R.id.assignmentSubmittedBy);
        submittedByTextTv = (TextView) findViewById(R.id.submittedByTextView);
        postDateTextView = (TextView) findViewById(R.id.postDateTextView);
        dueDateTv = (TextView) findViewById(R.id.dueDateTextView);
        descriptionTv = (TextView) findViewById(R.id.descriptionTextView);
        attachmentRecyclerView = (RecyclerView) findViewById(R.id.assignmentAttachmentRecyclerView);
        attachmentPanel = (CardView) findViewById(R.id.assignment_detail_attachment_panel_cv);
        teacherCommentPanel = (CardView) findViewById(R.id.assignment_detail_comment_panel_cv);
        relativeLikeLayout = (RelativeLayout) findViewById(R.id.assignRelativeLayoutLike);
        attachmentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        attachmentRecyclerView.setLayoutManager(layoutManager);
    }

    private void getBundleDataMethod() {
        getBundleData = getIntent().getExtras();
        assId = getBundleData.getString("AssignId");
        classForApproval = getBundleData.getString("AssignClassStd");
        divisionForApproval = getBundleData.getString("AssignDivision");
        maxCreditsForsApproval = Integer.parseInt(getBundleData.getString("AssignMaxCredits"));
        scoreTypeForApproval = getBundleData.getString("AssignScoreType");
        assignAttachCount = getBundleData.getString("AssignAttachCount");
        assignAttachmentIdentifier = getBundleData.getString("AssignAttachmentIdentifier");
        System.out.println(" assignAttachmentIdentifier " + assignAttachmentIdentifier);
        assignLikeCheck = getBundleData.getBoolean("AssignLikeCheck");
        assignmentStatusComment = getBundleData.getString("AssignStatusComment");
        System.out.println(" assignLikeCheck " + assignLikeCheck);
    }

    public void SendToApproval(View view) {
        Intent intent = new Intent(this, AssignmentApproval.class);
        intent.putExtra("assignmentId",assId);
        intent.putExtra("scoreType",scoreTypeForApproval);
        intent.putExtra("maxCredit",maxCreditsForsApproval);
        intent.putExtra("assignmentSubject",assignmentSubject);
        startActivity(intent);
    }

    private class LikeAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (userRole.equals("Student")) {
                    Users_Like usersLike = userLikeTable.insert(userLikeItem).get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void fetchAttachmentMetadata() {
        System.out.println("fetchAttachmentMetadata CALLED");
        JsonObject jsonObjectToFetchAttachmentMetadata = new JsonObject();
        jsonObjectToFetchAttachmentMetadata.addProperty("attachmentIdentifier", assignAttachmentIdentifier);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentFetchDetail", jsonObjectToFetchAttachmentMetadata);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Assignment detail API exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("  Assignment detail API   response    " + response);
                JsonArray attachmentListArray = response.getAsJsonArray();
                addItemsToAttachList(attachmentListArray);
            }
        });
    }

    private void addItemsToAttachList(JsonArray attachmentListArray) {

        for (int loop = 0; loop <= attachmentListArray.size() - 1; loop++) {
            System.out.println("loop no " + loop);
            JsonObject jsonObjectForIteration = attachmentListArray.get(loop).getAsJsonObject();

            attachmentFileName = jsonObjectForIteration.get("fileName").toString();
            attachmentFileName = attachmentFileName.substring(1, attachmentFileName.length() - 1);
            assignmentAttachmentList.add(new AttachmentListData(attachmentFileName));
        }
        assignmentAttachmentAdapter = new AssignmentAttachmentAdapter(this, assignmentAttachmentList);
        assignmentAttachmentAdapter.setClickListener(AssignmentDetail.this);
        attachmentRecyclerView.setAdapter(assignmentAttachmentAdapter);
    }

    @Override
    public void onClick(View view, int position) {
        System.out.println("position " + position);
        AttachmentListData data = assignmentAttachmentList.get(position);
        String fileNameCheckWeatherPresentOrNot = directory + "/" + data.getAttachmentName();
        File checkFilePresentOrNot = new File(fileNameCheckWeatherPresentOrNot);
        if (checkFilePresentOrNot.exists()) {
            String filePath = "file://" + directory + "/" + data.getAttachmentName();
            System.out.println("File path " + filePath);
            Uri uri = Uri.parse(filePath);
            System.out.println("uri " + uri);
            String extension = uri.getPath();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                System.out.println("ABOVE  NOUGAT ");
                File file1 = new File(filePath.replace("file://", ""));
                Uri aboveNougatURI = FileProvider.getUriForFile(AssignmentDetail.this, getPackageName() + ".provider", file1);

                if (extension.contains(".jpg") || extension.contains(".png") || extension.contains(".jpeg") || extension.contains(".bmp")) {
                    intent.setDataAndType(aboveNougatURI, "image/*");
                } else if (extension.contains(".pdf")) {
                    intent.setDataAndType(aboveNougatURI, "application/pdf");
                } else if (extension.contains(".txt")) {
                    intent.setDataAndType(aboveNougatURI, "text/plain");
                } else if (extension.contains(".doc") || extension.contains(".docx")) {
                    intent.setDataAndType(aboveNougatURI, "application/msword");
                } else if (extension.contains(".ppt") || extension.contains(".pptx")) {
                    intent.setDataAndType(aboveNougatURI, "application/vnd.ms-powerpoint");
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                System.out.println("BELOW  NOUGAT ");
                if (extension.contains(".jpg") || extension.contains(".png") || extension.contains(".jpeg") || extension.contains(".bmp")) {
                    intent.setDataAndType(uri, "image/*");
                } else if (extension.contains(".pdf")) {
                    intent.setDataAndType(uri, "application/pdf");
                } else if (extension.contains(".txt")) {
                    intent.setDataAndType(uri, "text/plain");
                } else if (extension.contains(".doc") || extension.contains(".docx")) {
                    intent.setDataAndType(uri, "application/msword");
                } else if (extension.contains(".ppt") || extension.contains(".pptx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                }
            }
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Problem opening attachment", Toast.LENGTH_SHORT).show();
            }
        } else {
            new downloadingAttachment().execute(data.getAttachmentName());
        }
    }


    public class downloadingAttachment extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                CloudBlobContainer container = getContainer();
                System.out.println("params[0] " + params[0]);

                for (ListBlobItem blobItem : container.listBlobs(params[0])) {
                    System.out.println("blobItem " + blobItem);
                    if (blobItem instanceof CloudBlob) {
                        CloudBlob blob = (CloudBlob) blobItem;
                        System.out.println("STEP 3");
                        try {
                            System.out.println("directory  blob.getName()---------------------------------" + directory + "/" + blob.getName());
                            blob.download(new FileOutputStream(directory + "/" + blob.getName()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception downloadingAttachment" + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            assignmentAttachmentAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, Menu.NONE, getBundleData.getString("AssignClassStd") + " " +
                getBundleData.getString("AssignDivision"))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
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