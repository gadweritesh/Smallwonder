package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.AttachmentPostAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Attachment;
import com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import me.iwf.photopicker.PhotoPicker;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;
import static com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration.getContainer;

public class AchievementPost extends AppCompatActivity implements ItemClickListener {

    private EditText achievementTitleEditText, achievementDescriptionEditText, academicYearEditText;
    private EditText achievementStudentNameEditText;
    private ProgressDialog progressDialog;
    private ConnectionDetector connectionDetector;
    private Spinner achievementCategorySpinner;

    static final int REQUEST_TAKE_PHOTO = 3;
    static final int REQUEST_OPEN_GALLERY = 2;
    static final int REQUEST_OPEN_EXPLORER = 1;
    private ArrayList<String> getGallerySelectedPhotoList;
    private ArrayList<String> getSelectedPhotoList;
    private AttachmentPostAdapter attachmentPostAdapter;
    private ArrayList<String> selectedPhotoNameList;
    private HashMap<String, Uri> mapForSelectedFileNameAndUri;
    private File mCameraImageFile;
    public Uri mCameraImageFileUri = null;
    private String attachedFilePath;
    private String KEY_FILE_NAME_OF_SELECTED_ATTACHMENT;
    private String attachedFilePath_From_SaveImage;
    private String selectedItemNameToRemoveFromUploadMap;
    private String attachmentUniqueIdentifier;
    private CloudBlobContainer container;
    private int index;
    private Uri uri;
    private CardView attachmentCardView;
    private RecyclerView achievementRecyclerView;
    private MobileServiceTable<Attachment> mAssignment_Attachment;
    private MenuItem sendMenuItem;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String imageFileName;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_post);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        mapForSelectedFileNameAndUri = new HashMap<>();
        selectedPhotoNameList = new ArrayList<>();
        getSelectedPhotoList = new ArrayList<>();

        mAssignment_Attachment = mClient.getTable(Attachment.class);
        progressDialog = new ProgressDialog(AchievementPost.this);

        achievementStudentNameEditText = (EditText) findViewById(R.id.achievementStudentNameEditText);
        achievementTitleEditText = (EditText) findViewById(R.id.achievementTitleEditText);
        academicYearEditText = (EditText) findViewById(R.id.academic_year);
        achievementDescriptionEditText = (EditText) findViewById(R.id.achievementDescriptionEditText);
        achievementCategorySpinner = (Spinner) findViewById(R.id.achievementCategorySpinner);
        attachmentCardView = (CardView) findViewById(R.id.achievementPost_attachment_cv);
        achievementRecyclerView = (RecyclerView) findViewById(R.id.achievementRecyclerView);
        achievementRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        String[] categoryList = getResources().getStringArray(R.array.achievementCategory);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        achievementCategorySpinner.setAdapter(categoryAdapter);

    }


    public void submitAchievement() {

        if (achievementTitleEditText.getText().toString().length() != 0) {

            if (achievementStudentNameEditText.getText().toString().length() != 0) {

                String timestamp = new SimpleDateFormat(("yyyyMMdd_HHmmss"), Locale.getDefault()).format(new Date());
                attachmentUniqueIdentifier = timestamp;

                if (mapForSelectedFileNameAndUri.size() == 0) {
                    FancyToast.makeText(this, "Select Image", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }else {
                    if (mapForSelectedFileNameAndUri.size() > 0) {
                        System.out.println("Map Size Above Zero");
                        uploadAttachment();
                    } else {
                        AddAchievement();
                    }
                }
            } else {
                FancyToast.makeText(this, "Enter Student Name", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            }
        } else {
            FancyToast.makeText(this, "Enter Title", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
        }

    }

    private void AddAchievement() {
        progressDialog.setMessage("Sending Achievement...");
        progressDialog.show();

        JsonObject jsonObjectAddAchievement = new JsonObject();
        jsonObjectAddAchievement.addProperty("AchievementStudentName", achievementStudentNameEditText.getText().toString());
        jsonObjectAddAchievement.addProperty("AchievementTitle", achievementTitleEditText.getText().toString());
        jsonObjectAddAchievement.addProperty("AchievementDesc", achievementDescriptionEditText.getText().toString());
        jsonObjectAddAchievement.addProperty("AchievementYear", academicYearEditText.getText().toString());
        jsonObjectAddAchievement.addProperty("AchievementCategory", achievementCategorySpinner.getSelectedItem().toString());
        jsonObjectAddAchievement.addProperty("AchievementUrl", AzureConfiguration.Storage_url + imageFileName);
        jsonObjectAddAchievement.addProperty("Branch_id", userDetails.get(SessionManager.KEY_BRANCH_ID));
        jsonObjectAddAchievement.addProperty("SubmittedByName", userDetails.get(SessionManager.KEY_NAME));
        jsonObjectAddAchievement.addProperty("Postdate", Calendar.getInstance().getTimeInMillis());

        if (userDetails.get(SessionManager.KEY_USER_ROLE).equals("Teacher")){
            jsonObjectAddAchievement.addProperty("Uploader_id", userDetails.get(SessionManager.KEY_TEACHER_ID));
        }else  if (userDetails.get(SessionManager.KEY_USER_ROLE).contains("Admin")){
            jsonObjectAddAchievement.addProperty("Uploader_id", userDetails.get(SessionManager.KEY_ADMIN_ID));
        }


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("achievementAddApi", jsonObjectAddAchievement);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("AddAchievement exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AchievementPost.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddAchievement();
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
                System.out.println("AddAchievement API   response    " + response);
                sendMenuItem.setEnabled(true);
                if (response.toString().equals("true")) {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AchievementPost.this)
                            .setMessage("Achievement Submitted.")
                            .setCancelable(false)
                            .setNegativeButton("Add more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AchievementPost achievementPost = new AchievementPost();
                                    achievementPost.finish();

                                    Intent addAchievementIntent = new Intent(getApplicationContext(), AchievementPost.class);
                                    startActivity(addAchievementIntent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).create().show();
                } else {

                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(AchievementPost.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AddAchievement();
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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alert_menu, menu);
        sendMenuItem = menu.findItem(R.id.action_send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_attachment:
                if (mapForSelectedFileNameAndUri.size() == 1) {
                    FancyToast.makeText(AchievementPost.this, "Only One Attachment Allowed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                } else {
                    ShowAttachmentPopup();
                }
                return true;
            case R.id.action_send:
                if (connectionDetector.isConnectingToInternet()) {
                    submitAchievement();
                } else {
                    FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowAttachmentPopup() {
        View menuItemView = findViewById(R.id.action_attachment);
        PopupMenu attachmentPopupMenu = new PopupMenu(this, menuItemView);
        MenuInflater inflate = attachmentPopupMenu.getMenuInflater();
        inflate.inflate(R.menu.achievement_attachment_popup, attachmentPopupMenu.getMenu());
        attachmentPopupMenu.show();
        attachmentPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_camera:
                        openCamera();
                        break;
//                    case R.id.action_document:
//                        pickFileFromFileExplorer();
//                        break;
                    case R.id.action_gallery:
                        PhotoPicker.builder()
                                .setPhotoCount(1)
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .setSelected(getGallerySelectedPhotoList)
                                .start(AchievementPost.this, REQUEST_OPEN_GALLERY);
                        break;
                }
                return false;
            }
        });
    }

//    public void pickFileFromFileExplorer() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        //  intent.setType("file/*");
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        try {
//            //Create Temporary reference File for fileRrovider to pick actual file
//            mCameraImageFile = createImageFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", mCameraImageFile);
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, REQUEST_OPEN_EXPLORER);
//        //this delete is used for to delete temporary file
//        mCameraImageFile.delete();
//    }

    private void openCamera() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            System.out.println("Open Camera Above Kitkat");
            takePicture();
        } else {
            System.out.println("Mobile Phone Below Kitkat");
            // Intent opencamIntent = new Intent(getApplicationContext(), PickCameraImage.class);
            // startActivity(opencamIntent);
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            System.out.println("takePictureIntent not Null");
            try {
                mCameraImageFile = createImageFile();
                System.out.println("Created Camera Image --- " + mCameraImageFile);

                if (mCameraImageFile != null) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // Do something for Nougat and above versions
                        System.out.println("Above Nougat ");
                        takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        mCameraImageFileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", mCameraImageFile);
                    } else {
                        System.out.println("Below Nougat ");
                        // do something for phones running an SDK before Nougat
                        mCameraImageFileUri = Uri.fromFile(mCameraImageFile);
                    }

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageFileUri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            } catch (Exception e) {
                System.out.println("Camera Exception --" + e.getMessage());
            }
        } else {
            System.out.println("takePictureIntent  Null");

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStorageDirectory();

        File createdImageFile = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */ storageDir /* directory */);
        System.out.println("===createdImageFile---  " + createdImageFile);

        return createdImageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        attachmentCardView.setVisibility(View.VISIBLE);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                try {
                    System.out.println("Check Photo----");
                    attachedFilePath = mCameraImageFile.getAbsolutePath();
                    attachedFilePath_From_SaveImage = CompressionOfImage(attachedFilePath);
                    System.out.println("attachedFilePath_From_SaveImage-------- " + attachedFilePath_From_SaveImage);

                    //Compress File and get file name and file URI to upload in Azure.
                    KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = attachedFilePath_From_SaveImage;
                    index = KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.lastIndexOf('/');
                    KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.substring(index + 1);

                    //check if only file name and memory allocation done not the camera data is stored.
                    // i.e. camera opened but closed w/o capturing
                    // if so goto else part

                    String addFileString = "file://" + attachedFilePath_From_SaveImage;
                    Uri getUriToSendAzure = Uri.parse(addFileString);

                    //if condition to check capture file is Actual file.
                    if (KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.length() > 0) {

                        mapForSelectedFileNameAndUri.put(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT, getUriToSendAzure);
                        selectedPhotoNameList.add(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);
                        getSelectedPhotoList.add(attachedFilePath_From_SaveImage);
                        attachmentPostAdapter = new AttachmentPostAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
                        achievementRecyclerView.setAdapter(attachmentPostAdapter);
                        attachmentPostAdapter.setClickListener(AchievementPost.this);
                    } else {
                        System.out.println("--Length of Photo is Zero--");
                    }
                    mCameraImageFile.delete();
                    System.out.println("mapForSelectedFileNameAndUri-----------  " + mapForSelectedFileNameAndUri.size());
                } catch (Exception e) {
                    System.out.println("image not capture from camera " + e.getMessage());
                }
                break;

            case REQUEST_OPEN_GALLERY:
                try {
                    System.out.println("Check Open Galleery----");
                    getGallerySelectedPhotoList = new ArrayList<>();
                    if (data != null) {
                        getGallerySelectedPhotoList = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        System.out.println("getSelectedPhotoList---------------  " + getGallerySelectedPhotoList.size());
                    }
                    addThemToView(getGallerySelectedPhotoList);
                } catch (Exception e) {
                    System.out.println("Exception in REQUEST_OPEN_GALLERY ");
                    System.out.println("Exception in REQUEST_OPEN_GALLERY ");
                }
                break;
        }
    }

    public String CompressionOfImage(String selectedImagePath) {
        //************************compress logic start****************
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 500;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
            System.out.println("Scaling start@@@@@@@@@@@");
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        System.out.println("selectedImagePath" + selectedImagePath);
        Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
        System.out.println("selectedImagefile bitmap" + bm);

        return SaveImage(bm);

        //************************compress logic end****************
    }

    private String SaveImage(Bitmap finalBitmap) {
        String filePath = null;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + getString(R.string.folderName) + "/Achievement_Post");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imageFileName = getString(R.string.folderName) + "-Achievement-" + n + ".jpg";
        File file = new File(myDir, imageFileName);
        System.out.println("myDir : " + file);
        System.out.println("_File_Name : " + imageFileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            System.out.println("out" + out);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Uri imagePath = Uri.fromFile(file);
            filePath = imagePath.getPath();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.d("AsyncTask", "if..calling");
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.d("AsyncTask", "else..calling");
            return task.execute();
        }
    }

    private void addThemToView(ArrayList<String> getGallerySelectedPhotoList) {
        System.out.println("getSelectedPhotoList-------------  " + getGallerySelectedPhotoList.size());

        for (int loopforgeturi = 0; loopforgeturi < getGallerySelectedPhotoList.size(); loopforgeturi++) {
            System.out.println("getSelectedPhotoList---------------  " + getGallerySelectedPhotoList.get(loopforgeturi));
            String compressSelectedFile = CompressionOfImage(getGallerySelectedPhotoList.get(loopforgeturi));
            System.out.println("compressSelectedFile-----------------  " + compressSelectedFile);
            int index = compressSelectedFile.lastIndexOf('/');
            KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = compressSelectedFile.substring(index + 1);

            String addFileString = "file://" + compressSelectedFile;
            Uri getUriToSendAzure = Uri.parse(addFileString);

            mapForSelectedFileNameAndUri.put(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT, getUriToSendAzure);
            selectedPhotoNameList.add(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);
            getSelectedPhotoList.add(compressSelectedFile);

            System.out.println("compressSelectedFile-------------------- " + compressSelectedFile);
            System.out.println("UrgetfinalUriToSendAzurei--------" + getUriToSendAzure);
            System.out.println("KEY_FILE_NAME_OF_SELECTED_ATTACHMENT--------" + KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);

        }

        attachmentPostAdapter = new AttachmentPostAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
        achievementRecyclerView.setAdapter(attachmentPostAdapter);
        System.out.println("Set Item Click Listener 1 ");
        attachmentPostAdapter.setClickListener(AchievementPost.this);
        System.out.println("Set Item Click Listener 2 ");

    }

    @Override
    public void onClick(View view, final int position) {
        String getSelectedAttachmentName = getSelectedPhotoList.get(position);

        int index = getSelectedAttachmentName.lastIndexOf('/');
        selectedItemNameToRemoveFromUploadMap = getSelectedAttachmentName.substring(index + 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(AchievementPost.this);
        builder.setTitle("Want To Remove ?");
        builder.setMessage(selectedItemNameToRemoveFromUploadMap);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("old map Size ------- " + mapForSelectedFileNameAndUri.size());
                getSelectedPhotoList.remove(position);
                mapForSelectedFileNameAndUri.remove(selectedItemNameToRemoveFromUploadMap);
                attachmentPostAdapter.notifyDataSetChanged();

                if (mapForSelectedFileNameAndUri.size()==0){
                    attachmentCardView.setVisibility(View.INVISIBLE);
                }
                System.out.println("new Map Size--------------------  " + mapForSelectedFileNameAndUri.size());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void uploadAttachment() {
        System.out.println(" mapForSelectedFileNameAndUri------------   " + mapForSelectedFileNameAndUri.size());

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            Boolean checkAttachmentSuccess = true;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMax(mapForSelectedFileNameAndUri.size());
                progressDialog.setMessage("Uploading " + getSelectedPhotoList.size() + " Photos");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    for (Map.Entry<String, Uri> entry : mapForSelectedFileNameAndUri.entrySet()) {
                        progressDialog.incrementProgressBy(1);
                        System.out.println(entry.getKey() + " - //// - " + entry.getValue());
                        System.out.println("Uploading Image------------------");
//                        Attachment item = new Attachment();
//                        item.setAttachmentIdentifier(attachmentUniqueIdentifier);
//                        item.setFileName(entry.getKey());
                        imageFileName = entry.getKey();
//                        item.setContainerName(AzureConfiguration.containerName);
//                        mAssignment_Attachment.insert(item);
                        final InputStream imageStream = getContentResolver().openInputStream(entry.getValue());
                        final int imageLength = imageStream.available();
                        container = getContainer();
                        CloudBlockBlob imageBlob = container.getBlockBlobReference(entry.getKey());
                                imageBlob.upload(imageStream, imageLength);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception e) {
                    checkAttachmentSuccess = false;
                    System.out.println("Attachment Exception--- " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //condition to check, if there is some problem uploading attachments
                if (checkAttachmentSuccess) {
                    AddAchievement();
                } else {
                    progressDialog.setMessage("Unable to post Achievement \n Please Retry ");
                    progressDialog.show();
                    System.out.println("Unable to post Achievement");
                }
            }
        };
        runAsyncTask(task);
    }
}
