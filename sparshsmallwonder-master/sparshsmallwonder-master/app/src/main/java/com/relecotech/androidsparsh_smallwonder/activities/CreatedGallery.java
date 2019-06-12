package com.relecotech.androidsparsh_smallwonder.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.relecotech.androidsparsh_smallwonder.adapters.GalleryPhotoPickerAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Gallery;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Gallery_Attachment;
import com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import me.iwf.photopicker.PhotoPicker;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;
import static com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration.getContainer;

/**
 * Created by Amey on 06-04-2018.
 */

public class CreatedGallery extends AppCompatActivity implements ItemClickListener {

    private String KEY_FILE_NAME_OF_SELECTED_PHOTO;
    private ArrayList<String> filePaths;
    private TextView addGalleryTextView;
    private TextView gallerySchoolClassIDTextView;
    private TextView galleryDescTextView;
    private TextView galleryTitleTextView;
    private EditText createGalleryDailogTitleTextView;
    private EditText createGalleryDailogDescTextView;
    private Spinner createGalleryDailogSchoolClassSpinner;
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO};
    private Bundle getGalleryBundle;
    private ArrayList<String> getSelectedPhotoList;
    private HashMap<String, Uri> mapForSelectedFileNameAndUri;
    private ArrayList<String> selectedPhotoNameList;
    private RecyclerView recyclerView;
    private GalleryPhotoPickerAdapter imageAdapter;

    private Gallery createGallaryobjectForGalleryTable;
    private MobileServiceTable<Gallery> galleryMobileServiceTable;
    private MobileServiceTable<Gallery_Attachment> galleryAttachmentMobileServiceTable;
    private String createdGalleryID;
    private CloudBlobContainer container;
    private ProgressDialog galleryUploadProgressDialog;
    public int noOfAttachments = 0;
    private int attachmentLoopCounter = 0;
    private ProgressDialog progressDoalog;
    private ArrayList<String> schoolClassArrayList;
    private String gallerySchoolClassId;
    private ConnectionDetector connectionDetector;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private HashMap<String, String> conditionHashMap;
    private String schoolClassId;
    private SessionManager sessionManager;
    private Map<String, String> schoolClassMap;
    private MobileServiceClient mClient;
    private ArrayList<File> deleteTemporaryResizeGalleryPhotoList;
    private String galleryPostedFor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.created_gallery);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this, sharedPrefValue);

        filePaths = new ArrayList<>();
        recyclerView = findViewById(R.id.addphotorecyclerview);
        addGalleryTextView = (TextView) findViewById(R.id.addGalleryTextView);
        gallerySchoolClassIDTextView = (TextView) findViewById(R.id.gallerySchoolClassIDTextView);
        galleryDescTextView = (TextView) findViewById(R.id.galleryDescTextView);
        galleryTitleTextView = (TextView) findViewById(R.id.galleryTitleTextView);


        getGalleryBundle = this.getIntent().getExtras();
        String galleryTitle = getGalleryBundle.getString("CreateGalleryTitle");
        String galleryDesc = getGalleryBundle.getString("CreateGalleryDesc");
        String gallerySchoolClass = getGalleryBundle.getString("CreateGallerySchoolClass");
        schoolClassId = getGalleryBundle.getString("CreateGallerySchoolClass");


        galleryTitleTextView.setText(galleryTitle);
        galleryDescTextView.setText(galleryDesc);
        gallerySchoolClassIDTextView.setText(gallerySchoolClass);

        schoolClassArrayList = new ArrayList<>();
        conditionHashMap = new HashMap<>();
        schoolClassMap = new TreeMap<>();
        resultSet = databaseHandler.getClassDataByCursor();

        resultSet.moveToFirst();
        schoolClassArrayList.add("Select Class Division");
        System.out.println(" resultSet.getCount() " + resultSet.getCount());


        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!schoolClassMap.containsKey(resultSet.getString(resultSet.getColumnIndex("class")) + " " + resultSet.getString(resultSet.getColumnIndex("division")))) {
                schoolClassMap.put(resultSet.getString(resultSet.getColumnIndex("class")) + " " + resultSet.getString(resultSet.getColumnIndex("division")), resultSet.getString(resultSet.getColumnIndex("schoolClassId")));
            }

            resultSet.moveToNext();
        }
        schoolClassArrayList.addAll(schoolClassMap.keySet());
        galleryMobileServiceTable = mClient.getTable(Gallery.class);
        galleryAttachmentMobileServiceTable = mClient.getTable(Gallery_Attachment.class);
        galleryUploadProgressDialog = new ProgressDialog(CreatedGallery.this);

        addGalleryTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                System.out.println("Print---------------------");

                PhotoPicker.builder()
                        .setPhotoCount(10)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .setSelected(getSelectedPhotoList)
                        .start(CreatedGallery.this, PhotoPicker.REQUEST_CODE);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                getSelectedPhotoList = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                System.out.println("getSelectedPhotoList---------------  " + getSelectedPhotoList.size());
            }
        }
        addThemToView(getSelectedPhotoList);
    }

    private void addThemToView(ArrayList<String> getSelectedPhotoList) {
        try {
            System.out.println("getSelectedPhotoList----------  " + getSelectedPhotoList.size());

            mapForSelectedFileNameAndUri = new HashMap<>();
            selectedPhotoNameList = new ArrayList<>();
            deleteTemporaryResizeGalleryPhotoList = new ArrayList<>();
            for (int loopToGetUri = 0; loopToGetUri < getSelectedPhotoList.size(); loopToGetUri++) {
                String compressSelectedFile = CompressionOfImage(getSelectedPhotoList.get(loopToGetUri));
                int index = compressSelectedFile.lastIndexOf('/');
                KEY_FILE_NAME_OF_SELECTED_PHOTO = compressSelectedFile.substring(index + 1);
                System.out.println("KEY_FILE_NAME_OF_SELECTED_PHOTO----------- " + KEY_FILE_NAME_OF_SELECTED_PHOTO);
                String addFileString = "file://" + compressSelectedFile;
                Uri getUriToSendAzure = Uri.parse(addFileString);

                mapForSelectedFileNameAndUri.put(KEY_FILE_NAME_OF_SELECTED_PHOTO, getUriToSendAzure);
                selectedPhotoNameList.add(KEY_FILE_NAME_OF_SELECTED_PHOTO);
            }
            if (recyclerView != null) {
                int numberOfColumns = 3;
                recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns, GridLayoutManager.VERTICAL, false));
                imageAdapter = new GalleryPhotoPickerAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
                recyclerView.setAdapter(imageAdapter);
                imageAdapter.setClickListener(CreatedGallery.this);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

        } catch (Exception e) {
            System.out.println(" Exception in Created Gallery " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.created_gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload_gallery:
                new InsertDataAsyncTask().execute();
                return true;

            case R.id.edit_action:
                ShowCreateGalleryDialog(galleryTitleTextView.getText().toString(), galleryDescTextView.getText().toString(), gallerySchoolClassIDTextView.getText().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void ShowCreateGalleryDialog(String galleryTitle, String galleryDesc, String gallerySchoolClass) {

        LayoutInflater flater = this.getLayoutInflater();
        final View view = flater.inflate(R.layout.create_gallery_alert_dialog, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        createGalleryDailogSchoolClassSpinner = (Spinner) view.findViewById(R.id.createGalleryDailogSchoolClassSpinner);
        createGalleryDailogTitleTextView = (EditText) view.findViewById(R.id.createGalleryDailogTitleTextView);
        createGalleryDailogDescTextView = (EditText) view.findViewById(R.id.createGalleryDailogDescTextView);


        final ArrayAdapter<String> createGalleryDailogSchoolClassSpinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolClassArrayList);

        createGalleryDailogSchoolClassSpinner.setSelection(createGalleryDailogSchoolClassSpinnerAdapter.getCount() - 1);
        createGalleryDailogSchoolClassSpinner.setAdapter(createGalleryDailogSchoolClassSpinnerAdapter);

        createGalleryDailogSchoolClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().contains("Select")) {
                    conditionHashMap.put("class", createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().substring(0, createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().indexOf(" ")));
                    conditionHashMap.put("division", createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().substring(createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().indexOf(" ") + 1, createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().length()));
                    System.out.println("CLass " + createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().substring(0, createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().indexOf(" ")));
                    System.out.println("division " + createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().substring(createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().indexOf(" ") + 1, createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().length()));
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    schoolClassId = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        createGalleryDailogTitleTextView.setText(galleryTitle);
        createGalleryDailogTitleTextView.setSelection(galleryTitle.length());
        createGalleryDailogDescTextView.setText(galleryDesc);
        createGalleryDailogDescTextView.setSelection(galleryDesc.length());

        builder.setCancelable(false);
        builder.setView(view);
        builder.setTitle("Edit Gallery");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().contains("Select Class Division")) {
                    galleryTitleTextView.setText(createGalleryDailogTitleTextView.getText().toString());
                    galleryDescTextView.setText(createGalleryDailogDescTextView.getText().toString());
                    gallerySchoolClassIDTextView.setText(createGalleryDailogSchoolClassSpinner.getSelectedItem().toString());
                    dialog.dismiss();
                } else {
                    FancyToast.makeText(CreatedGallery.this, "Select Class Division", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });


    }


    @Override
    public void onClick(View view, final int position) {
        System.out.println("Check--------postion---" + position);
        String getSelectedPhotoFromList = getSelectedPhotoList.get(position);
        System.out.println("getSelectedPhotoFromList--------------   " + getSelectedPhotoFromList);

        int index = getSelectedPhotoList.get(position).lastIndexOf('/');
        KEY_FILE_NAME_OF_SELECTED_PHOTO = getSelectedPhotoList.get(position).substring(index + 1);
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatedGallery.this);
        builder.setTitle("Want To Delete ");
        builder.setMessage(KEY_FILE_NAME_OF_SELECTED_PHOTO);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mapForSelectedFileNameAndUri.remove(KEY_FILE_NAME_OF_SELECTED_PHOTO);
                getSelectedPhotoList.remove(position);
                imageAdapter.notifyDataSetChanged();
            }
        });

        builder.create().show();

    }


    private class InsertDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDoalog = new ProgressDialog(CreatedGallery.this);
            progressDoalog.setMax(getSelectedPhotoList.size());
            progressDoalog.setCancelable(false);
            progressDoalog.setMessage("Uploading " + getSelectedPhotoList.size() + " Photos");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.show();

            createGallaryobjectForGalleryTable = new Gallery();
            createGallaryobjectForGalleryTable.setGalleryURL(AzureConfiguration.Storage_url);
            createGallaryobjectForGalleryTable.setGalleryTitle(galleryTitleTextView.getText().toString());
            createGallaryobjectForGalleryTable.setGalleryDescription(galleryDescTextView.getText().toString());
            createGallaryobjectForGalleryTable.setGalleryImageCount(getSelectedPhotoList.size());
            createGallaryobjectForGalleryTable.setGalleryFolderName(galleryTitleTextView.getText().toString() + "/");
            if (schoolClassId.contains("All")) {
                galleryPostedFor = "All";
                createGallaryobjectForGalleryTable.setSchool_Class_id(galleryPostedFor);
            } else {

                galleryPostedFor = schoolClassMap.get(schoolClassId);
                System.out.println("Inside Schoolclass ID --- galleryPostedFor" + galleryPostedFor);
                createGallaryobjectForGalleryTable.setSchool_Class_id(galleryPostedFor);
            }
            System.out.println("outside Schoolclass ID --- galleryPostedFor" + galleryPostedFor);

            createGallaryobjectForGalleryTable.setBranch_id(sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID));
            createGallaryobjectForGalleryTable.setActive("1");
            createGallaryobjectForGalleryTable.setGalleryPostDate(new Date());
            createGallaryobjectForGalleryTable.setGalleryCategory(sessionManager.getUserDetails().get(SessionManager.KEY_NAME));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                createdGalleryID = galleryMobileServiceTable.insert(createGallaryobjectForGalleryTable).get().getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("createdGalleryID    " + createdGalleryID);
            uploadAttachment(mapForSelectedFileNameAndUri, createdGalleryID);
        }

        public void uploadAttachment(Map<String, Uri> attachmentUriMap, final String createdGalleryID) {

            System.out.println("HANDLER STARTED");
            final Map<String, Uri> mapofattachmenturi = attachmentUriMap;


            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        for (Map.Entry<String, Uri> entry : mapofattachmenturi.entrySet()) {

                            progressDoalog.incrementProgressBy(1);
                            Gallery_Attachment gallery_attachment = new Gallery_Attachment();
                            gallery_attachment.setImageName(entry.getKey());
                            gallery_attachment.setGallery_id(createdGalleryID);
                            gallery_attachment.setActive("1");
                            galleryAttachmentMobileServiceTable.insert(gallery_attachment).get().getId();
                            String key = entry.getKey();

                            final InputStream imageStream = getContentResolver().openInputStream(entry.getValue());
                            final int imageLength = imageStream.available();
                            container = getContainer();
                            System.out.println("ImageManager : UploadImage : container---------- " + container);

                            System.out.println("bLOB aTTACHEMNT ");
                            CloudBlockBlob imageBlob = container.getBlockBlobReference(galleryTitleTextView.getText().toString() + "\\" + key);
                            imageBlob.upload(imageStream, imageLength);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    } catch (final Exception e) {
                        //createAndShowDialogFromTask(e, "Error");
                        System.out.println("Error While Uploading Photos--- AsysnTask");
                        e.printStackTrace();

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    progressDoalog.setMessage("Upload Photo Successfully...");
                    progressDoalog.dismiss();
                    callGalleryNotifcation();
                    Intent replyIntent = new Intent();
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            };

            runAsyncTask(task);
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
        File myDir = new File(root + "/" + getString(R.string.folderName) + "/Gallery_post");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imageFileName = getString(R.string.folderName) + "-Gallery-" + n + ".jpg";
        File file = new File(myDir, imageFileName);
        deleteTemporaryResizeGalleryPhotoList.add(file);

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

    public void callGalleryNotifcation() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gallery", galleryTitleTextView.getText().toString());
        jsonObject.addProperty("schoolclassid", galleryPostedFor);
        jsonObject.addProperty("branchid", sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID));
        jsonObject.addProperty("SubmittedByName", sessionManager.getUserDetails().get(SessionManager.KEY_NAME));
        jsonObject.addProperty("Postdate", Calendar.getInstance().getTimeInMillis());

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("notificationApi", jsonObject);


        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("gallerDelete exception " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {


                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);


                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {


                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 2000);

            }
        });
    }
}
