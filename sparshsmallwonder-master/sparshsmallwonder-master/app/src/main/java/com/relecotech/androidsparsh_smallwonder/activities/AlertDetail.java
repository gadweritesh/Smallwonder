package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.relecotech.androidsparsh_smallwonder.adapters.AlertAttachmentAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Users_Like;
import com.relecotech.androidsparsh_smallwonder.models.AttachmentListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;
import static com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration.getContainer;

/**
 * Created by Relecotech on 05-03-2018.
 */

public class AlertDetail extends AppCompatActivity implements ItemClickListener {
    private CardView attachmentDetailCardView;
    private String alertId;
    private MobileServiceTable<Users_Like> userLikeTable;
    private Users_Like userLikeItem;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private String userRole, studentId, branchId;
    private String directory;
    private ArrayList<AttachmentListData> alertAttachmentList;
    private AlertAttachmentAdapter alertAttachmentAdapter;
    private RecyclerView alertRecyclerView;
    private String alertAttachmentIdentifier;
    private RelativeLayout relativeLikeLayout;
    private MobileServiceClient mClient;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_detail);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userLikeTable = mClient.getTable(Users_Like.class);
        userLikeItem = new Users_Like();
        alertAttachmentList = new ArrayList<>();

        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);

        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);
        studentId = sessionManager.getUserDetails().get(SessionManager.KEY_STUDENT_ID);
        branchId = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID);

        TextView alertTitleTextView = (TextView) findViewById(R.id.alertDetailTitleTextView);
        TextView alertDescriptionTextView = (TextView) findViewById(R.id.alertDetailDescriptionTextView);
        TextView alertPostDateTextView = (TextView) findViewById(R.id.alertDetailPostDateTextView);
        TextView alertCategoryTextView = (TextView) findViewById(R.id.alertDetailCategoryTextView);
        TextView alertSubmittedByTv = (TextView) findViewById(R.id.alertSubmittedBy);
        TextView alertSubmittedByTextView = (TextView) findViewById(R.id.AlertDetailSubmittedByTextView);
        LikeButton alertDetailLikeButton = (LikeButton) findViewById(R.id.alertDetailLikeButton);
        attachmentDetailCardView = (CardView) findViewById(R.id.alert_detail_attachment_panel_cv);
        alertRecyclerView = (RecyclerView) findViewById(R.id.alertAttachmentRecyclerView);


        alertDetailLikeButton.setIcon(IconType.Thumb);

        relativeLikeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutLike);

        alertRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        alertRecyclerView.setLayoutManager(layoutManager);

        Intent getAlertIntent = getIntent();
        alertId = getAlertIntent.getStringExtra("AlertId");
        String alertTitle = getAlertIntent.getStringExtra("AlertTitle");
        String alertDescription = getAlertIntent.getStringExtra("AlertDescription").replace("\\n", "\n").replace("\\", "");
        String alertPostDate = getAlertIntent.getStringExtra("AlertPostDate");
        System.out.println("alertPostDate-----------  " + alertPostDate);
        String alertCategory = getAlertIntent.getStringExtra("AlertCategory");
        String alertSubmittedBy = getAlertIntent.getStringExtra("AlertSubmittedBy");
        if (alertSubmittedBy.contains("null")){
            alertSubmittedBy =" Admin ";
        }
        String alertAttachmentCount = getAlertIntent.getStringExtra("AlertAttachmentCount");
        boolean alertLikeCheck = getAlertIntent.getBooleanExtra("AlertLikeCheck", false);


        if (userRole.equals("Teacher") || userRole.equals("Administrator")) {
            alertSubmittedByTv.setVisibility(View.GONE);
            alertSubmittedByTextView.setVisibility(View.GONE);
            alertDetailLikeButton.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10f);
            relativeLikeLayout.setLayoutParams(layoutParams);
        }
        if (!alertAttachmentCount.equals("0")) {
            alertAttachmentIdentifier = getAlertIntent.getStringExtra("AlertAttachmentIdentifier");
        }

        System.out.println("alertLikeCheck   " + alertLikeCheck);
        if (alertLikeCheck) {
            alertDetailLikeButton.setLiked(true);
            alertDetailLikeButton.setEnabled(false);
        }

        alertDetailLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                System.out.println(" INSIDE LIKE");
                likeButton.setLiked(true);
                likeButton.setEnabled(false);

                System.out.println("alertId " + alertId + " studentId " + studentId + "branchId " + branchId);
                userLikeItem.setSelectedItemId(alertId);
                userLikeItem.setStudent_id(studentId);
                userLikeItem.setBranch_id(branchId);
                new LikeAsyncTask().execute();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                System.out.println(" INSIDE unLiked");
            }
        });

        System.out.println("alertAttachmentCount " + alertAttachmentCount);
        if (alertAttachmentCount.equals("0")) {
            attachmentDetailCardView.setVisibility(View.INVISIBLE);
        } else {
            attachmentDetailCardView.setVisibility(View.VISIBLE);
            fetchAttachmentMetadata();
        }

        alertTitleTextView.setText(alertTitle);
        alertDescriptionTextView.setText(alertDescription);
        alertPostDateTextView.setText(alertPostDate);
        alertCategoryTextView.setText(alertCategory);
        alertSubmittedByTextView.setText(alertSubmittedBy);


        File dir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Alert_Download");
        directory = dir.getPath();
        try {
            if (dir.mkdirs()) {
                System.out.println("Directory  created");
            } else {
                System.out.println("Directory not created");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTION" + e.getMessage());
        }
    }

    @Override
    public void onClick(View view, int position) {
        System.out.println("position " + position);
        AttachmentListData data = alertAttachmentList.get(position);
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
                Uri aboveNougatURI = FileProvider.getUriForFile(AlertDetail.this, getPackageName() + ".provider", file1);

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
                FancyToast.makeText(AlertDetail.this, "Problem opening attachment", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
//                Toast.makeText(getApplicationContext(), "Problem opening attachment", Toast.LENGTH_SHORT).show();
            }
        } else {
            new downloadingAttachment().execute(data.getAttachmentName());
        }
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
        jsonObjectToFetchAttachmentMetadata.addProperty("attachmentIdentifier", alertAttachmentIdentifier);

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

            String attachmentFileName = jsonObjectForIteration.get("fileName").toString();
            attachmentFileName = attachmentFileName.substring(1, attachmentFileName.length() - 1);
            alertAttachmentList.add(new AttachmentListData(attachmentFileName));
        }
        alertAttachmentAdapter = new AlertAttachmentAdapter(this, alertAttachmentList);
        alertAttachmentAdapter.setClickListener(AlertDetail.this);
        alertRecyclerView.setAdapter(alertAttachmentAdapter);
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
            alertAttachmentAdapter.notifyDataSetChanged();
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
