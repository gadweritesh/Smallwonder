package com.relecotech.androidsparsh_smallwonder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.relecotech.androidsparsh_smallwonder.fragments.DatePickerFragment;
import com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import me.iwf.photopicker.PhotoPicker;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;
import static com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration.getContainer;

/**
 * Created by Relecotech on 06-03-2018.
 */

public class AssignmentPost extends AppCompatActivity implements DatePickerFragment.DateDialogListener, ItemClickListener {

    private RecyclerView assignmentPostRecyclerView;
    static final int REQUEST_TAKE_PHOTO = 3;
    static final int REQUEST_OPEN_GALLERY = 2;
    static final int REQUEST_OPEN_EXPLORER = 1;
    private Spinner classSpinner, divisionSpinner, subjectSpinner, scoreTypeSpinner;
    private TextView dueDateTextView;
    private EditText descEditText;
    private TextView creditCountTextView;
    private AppCompatSeekBar creditsSeekBar;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String branchId;
    private ArrayList<String> assignmentClassList;
    private ArrayList<String> assignmentDivisionList;
    private List<String> subjectSpinnerList;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private HashMap<String, String> conditionHashMap;
    private String dueDate;
    private String schoolClassId;
    private ProgressDialog assignmentProgressDialog;
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
    private String timestamp;
    private String attachmentUniqueIdentifier;
    private CloudBlobContainer container;
    private MobileServiceTable<Attachment> mAssignment_Attachment;
    private ProgressDialog progressDialog;
    private CardView attachmentCardView;
    private int index;
    private ArrayList<String> getSelectedAttachmentList;
    private MobileServiceClient mClient;
    private String mainClass, mainDiv;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_post);

        mClient = Singleton.Instance().mClientMethod(this);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        branchId = userDetails.get(SessionManager.KEY_BRANCH_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assignmentClassList = new ArrayList<>();
        assignmentDivisionList = new ArrayList<>();
        subjectSpinnerList = new ArrayList<>();


        progressDialog = new ProgressDialog(AssignmentPost.this);

        mAssignment_Attachment = mClient.getTable(Attachment.class);


        conditionHashMap = new HashMap<>();
        mapForSelectedFileNameAndUri = new HashMap<>();
        selectedPhotoNameList = new ArrayList<>();
        getSelectedPhotoList = new ArrayList<>();
        databaseHandler = new DatabaseHandler(this);
        resultSet = databaseHandler.getClassDataByCursor();

        if (userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID) == null
                || userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID).equalsIgnoreCase("null")) {
            System.out.println(" INSIDE IF Of KEY_SCHOOL_CLASS_ID");
        } else {
            try {
                resultSet.moveToFirst();
                do {
                    if (userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID).contains(resultSet.getString(resultSet.getColumnIndex("schoolClassId")))) {
                        mainClass = resultSet.getString(resultSet.getColumnIndex("class"));
                        System.out.println("mainClass " + mainClass);
                        mainDiv = resultSet.getString(resultSet.getColumnIndex("division"));
                        System.out.println("mainDiv " + mainDiv);
                    }
                } while (resultSet.moveToNext());

            } catch (Exception e) {
                System.out.println(" Exception" + e.getMessage());
            }
        }

        resultSet.moveToFirst();
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!assignmentClassList.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                assignmentClassList.add(resultSet.getString(resultSet.getColumnIndex("class")));
            }
            resultSet.moveToNext();
        }

        findViewByIdMethod();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        assignmentClassList.add("[ Class ]");
        ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, assignmentClassList);
        adapterClass.setDropDownViewResource(R.layout.spinner_item);
        classSpinner.setAdapter(adapterClass);
        try {
            classSpinner.setSelection(assignmentClassList.indexOf(mainClass));
        } catch (Exception e) {
            System.out.println(" classSpinner.setSelection " + e.getMessage());
        }
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String assignmentClassString = classSpinner.getSelectedItem().toString();
                assignmentDivisionList.clear();
                if (!assignmentClassString.contains("[ Class ]")) {
                    conditionHashMap.put("class", assignmentClassString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        if (!assignmentDivisionList.contains(resultSet.getString(resultSet.getColumnIndex("division")))) {
                            assignmentDivisionList.add(resultSet.getString(resultSet.getColumnIndex("division")));
                        }
                        resultSet.moveToNext();
                    }
                } else {
                    assignmentDivisionList = new ArrayList<>();
                }
                assignmentDivisionList.add("[ Division ]");
                ArrayAdapter<String> adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, assignmentDivisionList);
                adapterDivision.setDropDownViewResource(R.layout.spinner_item);
                divisionSpinner.setAdapter(adapterDivision);
                try {
                    divisionSpinner.setSelection(assignmentDivisionList.indexOf(mainDiv));
                } catch (Exception e) {
                    System.out.println(" divisionSpinner.setSelection " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String assignmentDivisionString = divisionSpinner.getSelectedItem().toString();
                subjectSpinnerList.clear();
                if (!assignmentDivisionString.equals("[ Division ]")) {
                    conditionHashMap.put("class", classSpinner.getSelectedItem().toString());
                    conditionHashMap.put("division", assignmentDivisionString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    schoolClassId = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        if (!subjectSpinnerList.contains(resultSet.getString(resultSet.getColumnIndex("subject")))) {
                            subjectSpinnerList.add(resultSet.getString(resultSet.getColumnIndex("subject")));
                        }
                        resultSet.moveToNext();
                    }
                } else {
                    subjectSpinnerList = new ArrayList<>();
                }
                subjectSpinnerList.add("[ Subject ]");
                ArrayAdapter<String> adapterSubject = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, subjectSpinnerList);
                adapterSubject.setDropDownViewResource(R.layout.spinner_item);
                subjectSpinner.setAdapter(adapterSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        List<String> scoreTypeList = new ArrayList<>();
        scoreTypeList.add("Marks");
        scoreTypeList.add("Grades");
        scoreTypeList.add("[ Score type ]");
        ArrayAdapter<String> markAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, scoreTypeList);
        markAdapter.setDropDownViewResource(R.layout.spinner_item);
        scoreTypeSpinner.setAdapter(markAdapter);
        scoreTypeSpinner.setSelection(markAdapter.getCount() - 2);
        scoreTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String scoreTypeString = scoreTypeSpinner.getSelectedItem().toString();
                if (scoreTypeString.equals("Marks")) {
                    revealCreditSeekBar();
                    creditCountTextView.setVisibility(View.VISIBLE);
                } else {
                    hideCreditSeekBar();
                    creditCountTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Calendar currentCal = Calendar.getInstance();
        String currentDate = dateFormat.format(currentCal.getTime());
        currentCal.add(Calendar.DATE, 1);
        String toDate = dateFormat.format(currentCal.getTime());
        System.out.println(" toDate " + toDate);

        dueDateTextView.setText(toDate);

        dueDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.AM_PM, Calendar.PM);
                dueDate = format1.format(cal.getTime());

                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("dob", dueDate);
                bundle.putString("value", "greater");
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        creditsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                creditCountTextView.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void findViewByIdMethod() {
        classSpinner = (Spinner) findViewById(R.id.spinnerAssignmentClass);
        divisionSpinner = (Spinner) findViewById(R.id.spinnerAssignmentDivision);
        subjectSpinner = (Spinner) findViewById(R.id.spinnerAssignmentSubject);

        scoreTypeSpinner = (Spinner) findViewById(R.id.spinnerAssignmentScoreType);
        creditsSeekBar = (AppCompatSeekBar) findViewById(R.id.creditsSeekBar);
        creditCountTextView = (TextView) findViewById(R.id.creditsCountTextView);

        dueDateTextView = (TextView) findViewById(R.id.spinnerAssignmentDueDate);
        descEditText = (EditText) findViewById(R.id.assignmentTextDescription);
        mAdView  = (AdView)findViewById(R.id.adView);
        attachmentCardView = (CardView) findViewById(R.id.assignmentPost_attachment_cv);
        assignmentPostRecyclerView = (RecyclerView) findViewById(R.id.assignPostRecyclerView);
        assignmentPostRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alert_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_attachment:
                ShowAttachmentPopup();
                return true;
            case R.id.action_send:
                if (connectionDetector.isConnectingToInternet()) {
                    submitAssignment();
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
        PopupMenu attachmentPopupMenu = new PopupMenu(AssignmentPost.this, menuItemView);
        MenuInflater inflate = attachmentPopupMenu.getMenuInflater();
        inflate.inflate(R.menu.attachment_popup, attachmentPopupMenu.getMenu());
        attachmentPopupMenu.show();
        attachmentPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_camera:
                        openCamera();
                        break;
                    case R.id.action_document:
                        pickFileFromFileExplorer();
                        break;
                    case R.id.action_gallery:
                        getSelectedAttachmentList = new ArrayList<>();
                        PhotoPicker.builder()
                                .setPhotoCount(3)
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .setSelected(getSelectedAttachmentList)
                                .start(AssignmentPost.this, REQUEST_OPEN_GALLERY);
                        break;
                }
                return false;
            }
        });


    }


    public void pickFileFromFileExplorer() {
        getSelectedAttachmentList = new ArrayList<>();
        FilePickerBuilder.getInstance().setMaxCount(2)
                .setSelectedFiles(getSelectedAttachmentList)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(this, REQUEST_OPEN_EXPLORER);


    }

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
                        assignmentPostRecyclerView.setAdapter(attachmentPostAdapter);
                        attachmentPostAdapter.setClickListener(AssignmentPost.this);
                    } else {
                        System.out.println("--Length of Photo is Zero--");
                    }
                    mCameraImageFile.delete();
                    if (mapForSelectedFileNameAndUri.size()!=0){
                        attachmentCardView.setVisibility(View.VISIBLE);
                    }
                    System.out.println("mapForSelectedFileNameAndUri-----------  " + mapForSelectedFileNameAndUri.size());
                } catch (Exception e) {
                    System.out.println("image not capture from camera " + e.getMessage());
                }
                break;


            case REQUEST_OPEN_EXPLORER:
                try {


                    ArrayList<String> documentPathsList = new ArrayList<>();
                    documentPathsList.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    System.out.println("documentPathsList--------------  " + documentPathsList.size());

                    for (String attachedFilePath : documentPathsList) {

                        KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = attachedFilePath;
                        index = KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.lastIndexOf('/');
                        KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.substring(index + 1);
                        System.out.println("KEY_FILE_NAME_OF_SELECTED_ATTACHMENT-------------  " + KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);

                        String addFileString1 = "file://" + attachedFilePath;
                        Uri getUriToSendAzure1 = Uri.parse(addFileString1);
                        selectedPhotoNameList.add(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);
                        getSelectedPhotoList.add(attachedFilePath);
                        mapForSelectedFileNameAndUri.put(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT, getUriToSendAzure1);
                    }
                    if (mapForSelectedFileNameAndUri.size()!=0){
                        attachmentCardView.setVisibility(View.VISIBLE);
                    }

                    attachmentPostAdapter = new AttachmentPostAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
                    assignmentPostRecyclerView.setAdapter(attachmentPostAdapter);
                    attachmentPostAdapter.setClickListener(AssignmentPost.this);
                } catch (Exception e) {
                    System.out.println("Exception --------------  e "+e.getMessage());
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

    public void uploadAttachment() {
        System.out.println(" mapForSelectedFileNameAndUri------------   " + mapForSelectedFileNameAndUri.size());

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            Boolean checkAttahmentSucess = true;

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
                        System.out.println("UploadinG Image------------------");
                        Attachment item = new Attachment();
                        item.setAttachmentIdentifier(attachmentUniqueIdentifier);
                        item.setFileName(entry.getKey());
                        item.setContainerName(AzureConfiguration.containerName);
                        mAssignment_Attachment.insert(item);
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


                    checkAttahmentSucess = false;
                    System.out.println("Attachment Execption--- " + e.getMessage());

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //condition to check, if there is some problem uploading attachments
                if (checkAttahmentSucess) {
                    addAssignmentToAzure();
                } else {
                    progressDialog.setMessage("Unable to post Assignment \n Please Retry ");
                    progressDialog.show();
                    System.out.println("Unable to post Assignment");
                }

            }

        };
        runAsyncTask(task);
    }

    /** Run an ASync task on the corresponding executor
     * @param task
     * @return  */

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
        if (mapForSelectedFileNameAndUri.size()!=0){
            attachmentCardView.setVisibility(View.VISIBLE);
        }

        attachmentPostAdapter = new AttachmentPostAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
        assignmentPostRecyclerView.setAdapter(attachmentPostAdapter);
        System.out.println("Set Item Click Listener 1 ");
        attachmentPostAdapter.setClickListener(AssignmentPost.this);
        System.out.println("Set Item Click Listener 2 ");

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
        File myDir = new File(root + "/" + getString(R.string.folderName) + "/Assignment_Post");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imageFileName = getString(R.string.folderName) + "-Assignment-" + n + ".jpg";
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


    @Override
    public void onFinishDialog(String date) {
        dueDateTextView.setText(date);
    }

    private void hideCreditSeekBar() {
        // previously visible view

        // get the center for the clipping circle
        int cx = creditsSeekBar.getWidth() / 2;
        int cy = creditsSeekBar.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(creditsSeekBar, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    creditsSeekBar.setVisibility(View.GONE);
                }
            });

            // start the animation
            anim.start();
        }//chnge by Yogesh
        else {
            creditsSeekBar.setVisibility(View.GONE);
        }
    }

    private void revealCreditSeekBar() {
        // previously invisible view
        //        View myView = findViewById(R.id.my_view);

        // get the center for the clipping circle
        int cx = creditsSeekBar.getWidth() / 2;
        int cy = creditsSeekBar.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(creditsSeekBar, cx, cy, 0, finalRadius);
        }

        // make the view visible and start the animation
        creditsSeekBar.setVisibility(View.VISIBLE);
        if (anim != null) {
            anim.start();
        }
    }

    public void submitAssignment() {

        if (!classSpinner.getSelectedItem().toString().equals("[ Class ]")) {

            if (!divisionSpinner.getSelectedItem().toString().equals("[ Division ]")) {

                if (!subjectSpinner.getSelectedItem().toString().equals("[ Subject ]")) {

                    if (!dueDateTextView.getText().toString().equals("[ Due date ]")) {

                        if (!scoreTypeSpinner.getSelectedItem().toString().equals("[ Score type ]")) {

                            timestamp = new SimpleDateFormat(("yyyyMMdd_HHmmss"), Locale.getDefault()).format(new Date());
                            attachmentUniqueIdentifier = classSpinner.getSelectedItem().toString() + subjectSpinner.getSelectedItem().toString() + subjectSpinner.getSelectedItem().toString() + timestamp;

                            if (mapForSelectedFileNameAndUri.size() > 0) {
                                System.out.println("Map Size Above Zero");
                                if (descEditText.getText().length() > 0) {
                                    descEditText.append("\nThis message has an attachment.");
                                } else {
                                    descEditText.setText("Assignment for class " + classSpinner.getSelectedItem().toString() + " " + divisionSpinner.getSelectedItem().toString());
                                    descEditText.append("\n\nThis message has an attachment.");
                                }
                                uploadAttachment();
                            } else {
                                if (descEditText.getText().length() > 0) {
                                } else {
                                    descEditText.setText("Assignment for class " + classSpinner.getSelectedItem().toString() + " " + divisionSpinner.getSelectedItem().toString());
                                }
                                addAssignmentToAzure();
                            }
                        } else {
                            FancyToast.makeText(this, "Select Score type", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        }
                    } else {
                        FancyToast.makeText(this, "Select Due Date", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    }
                } else {
                    FancyToast.makeText(this, "Select Subject", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }
            } else {
                FancyToast.makeText(this, "Select Division", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            }
        } else {
            FancyToast.makeText(this, "Select Class", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
        }

    }

    private void addAssignmentToAzure() {
        // assignmentProgressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        progressDialog.setMessage("Sending Assignment...");
        progressDialog.show();
        JsonObject jsonObjectForAddAssignment = new JsonObject();

        Calendar calenderDue = Calendar.getInstance();
        long dueDateMilli = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(dueDateTextView.getText().toString());
            calenderDue.setTime(date);
            calenderDue.set(Calendar.AM_PM, Calendar.PM);
            dueDateMilli = calenderDue.getTimeInMillis();

            System.out.println("dueDateMilli--------------------   " + calenderDue.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calender = Calendar.getInstance();

        // Yogesh changes for invoke api
        jsonObjectForAddAssignment.addProperty("Assignment_class", classSpinner.getSelectedItem().toString());
        jsonObjectForAddAssignment.addProperty("Assignment_division", divisionSpinner.getSelectedItem().toString());
        jsonObjectForAddAssignment.addProperty("Assignment_subject", subjectSpinner.getSelectedItem().toString());
        jsonObjectForAddAssignment.addProperty("Assignment_dueDate", dueDateMilli);
        jsonObjectForAddAssignment.addProperty("Assignment_description", descEditText.getText().toString());
        jsonObjectForAddAssignment.addProperty("Score_type", scoreTypeSpinner.getSelectedItem().toString());
        jsonObjectForAddAssignment.addProperty("Active", true);
        jsonObjectForAddAssignment.addProperty("Assignment_submitted_by", userDetails.get(SessionManager.KEY_TEACHER_ID));
        System.out.println("Assignment_postdate--------------------   " + calender.getTimeInMillis());
        jsonObjectForAddAssignment.addProperty("Assignment_postdate", calender.getTimeInMillis());
        jsonObjectForAddAssignment.addProperty("Assignment_credit", Integer.parseInt(creditCountTextView.getText().toString()));
        jsonObjectForAddAssignment.addProperty("School_class_id", schoolClassId);
        jsonObjectForAddAssignment.addProperty("Branch_id", branchId);
        jsonObjectForAddAssignment.addProperty("Assignment_Attachment_Count", mapForSelectedFileNameAndUri.size());
        jsonObjectForAddAssignment.addProperty("Attachment_identifier", attachmentUniqueIdentifier);
//        jsonObjectForAddAssignment.addProperty("Attachment_count", attachmap.size());


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("assignmentAddApi", jsonObjectForAddAssignment);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        //assignmentProgressDialog.dismiss();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AssignmentPost.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addAssignmentToAzure();
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
                System.out.println(" Assignment Post API   response    " + response);
                if (response.toString().equals("true")) {
                    //  assignmentProgressDialog.dismiss();
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AssignmentPost.this)
                            .setMessage("Assignment Submitted.")
                            .setCancelable(false)
                            .setNegativeButton("Add more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // finish current activity..
                                    AssignmentPost assignmentPost = new AssignmentPost();
                                    assignmentPost.finish();

                                    // start same activity..
                                    Intent addAssignmentIntent = new Intent(getApplicationContext(), AssignmentPost.class);
                                    startActivity(addAssignmentIntent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK,intent);//Set result OK
                                    finish();
//                                    onBackPressed();
                                }
                            }).create().show();
                } else {
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //assignmentProgressDialog.dismiss();
                            progressDialog.dismiss();
                            new AlertDialog.Builder(AssignmentPost.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addAssignmentToAzure();
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
    public void onClick(View view, final int position) {
        System.out.println("----------Click Position-----" + position);
        String getSelectedAttachmentName = getSelectedPhotoList.get(position);

        int index = getSelectedAttachmentName.lastIndexOf('/');
        selectedItemNameToRemoveFromUploadMap = getSelectedAttachmentName.substring(index + 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentPost.this);
        builder.setTitle("Want To Remove ?");
        builder.setMessage(selectedItemNameToRemoveFromUploadMap);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("old map Size ------- " + mapForSelectedFileNameAndUri.size());
                getSelectedPhotoList.remove(position);
                selectedPhotoNameList.remove(position);
                mapForSelectedFileNameAndUri.remove(selectedItemNameToRemoveFromUploadMap);
                attachmentPostAdapter.notifyDataSetChanged();
                System.out.println("new Map Size--------------------  " + mapForSelectedFileNameAndUri.size());
                if (mapForSelectedFileNameAndUri.size()==0){
                    attachmentCardView.setVisibility(View.INVISIBLE);
                }

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
}
