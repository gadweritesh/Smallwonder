package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.relecotech.androidsparsh_smallwonder.azureControllers.Syllabus;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import me.iwf.photopicker.PhotoPicker;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;
import static com.relecotech.androidsparsh_smallwonder.activities.AlertPost.REQUEST_OPEN_GALLERY;
import static com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration.getContainer;

public class SyllabusPost extends AppCompatActivity implements ItemClickListener {


    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private MenuItem sendMenuItem;
    static final int REQUEST_OPEN_EXPLORER = 1;
    private Uri uri;
    private File mFileExplorerTempFile;
    private Spinner spinnerSyllabusClass;
    private Spinner spinnerSyllabusDivision;
    private Spinner spinnerSyllabusSubject;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private ArrayList<String> syllabusClassList;
    private ArrayList<String> syllabusDivList;
    private ArrayList<String> subjectSpinnerList;
    private HashMap<String, String> conditionHashMap;
    private String schoolClassId;

    private ArrayList<String> getGallerySelectedPhotoList;
    private ArrayList<String> getSelectedPhotoList;
    private AttachmentPostAdapter attachmentPostAdapter;
    private ArrayList<String> selectedPhotoNameList;
    private HashMap<String, Uri> mapForSelectedFileNameAndUri;
    private String KEY_FILE_NAME_OF_SELECTED_ATTACHMENT;
    private String attachedFilePath_From_SaveImage;
    private String selectedItemNameToRemoveFromUploadMap;
    private int index;
    private RecyclerView syllabusPostRecyclerView;
    private CardView syllabusPost_attachment_cv;
    private EditText syllabusDesc;
    private ProgressDialog progressDialog;
    private ImageView syllabus_add_ImageView;
    private ArrayList<String> selectedDocumentToShowInList;
    private MobileServiceTable<Syllabus> mSyllabus;
    private CloudBlobContainer container;
    private String attachmentUniqueIdentifier;
    private MobileServiceTable<Attachment> mAssignment_Attachment;
    private String mainClass;
    private String mainDiv;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_post);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        spinnerSyllabusClass = (Spinner) findViewById(R.id.spinnerSyllabusClass);
        spinnerSyllabusDivision = (Spinner) findViewById(R.id.spinnerSyllabusDivision);
        spinnerSyllabusSubject = (Spinner) findViewById(R.id.spinnerSyllabusSubject);
        syllabusDesc = (EditText) findViewById(R.id.syllabusTextDescription);

        syllabusPostRecyclerView = (RecyclerView) findViewById(R.id.syllabusPostRecyclerView);
        syllabusPostRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        syllabusClassList = new ArrayList<>();
        syllabusDivList = new ArrayList<>();
        subjectSpinnerList = new ArrayList<>();
        conditionHashMap = new HashMap<>();

        mapForSelectedFileNameAndUri = new HashMap<>();
        selectedPhotoNameList = new ArrayList<>();
        getSelectedPhotoList = new ArrayList<>();
        selectedDocumentToShowInList = new ArrayList<>();

        databaseHandler = new DatabaseHandler(this);
        resultSet = databaseHandler.getClassDataByCursor();

        mSyllabus = mClient.getTable(Syllabus.class);

        if (userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID).isEmpty() || userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID) == null
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
            if (!syllabusClassList.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                syllabusClassList.add(resultSet.getString(resultSet.getColumnIndex("class")));
            }
            resultSet.moveToNext();
        }

        syllabusClassList.add("[ Class ]");
        ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, syllabusClassList);
        adapterClass.setDropDownViewResource(R.layout.spinner_item);
        spinnerSyllabusClass.setAdapter(adapterClass);
        try {
            spinnerSyllabusClass.setSelection(syllabusClassList.indexOf(mainClass));
        } catch (Exception e) {
            System.out.println(" classSpinner.setSelection " + e.getMessage());
        }
        spinnerSyllabusClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String classString = spinnerSyllabusClass.getSelectedItem().toString();
                syllabusDivList.clear();
                if (!classString.contains("[ Class ]")) {
                    conditionHashMap.put("class", classString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        if (!syllabusDivList.contains(resultSet.getString(resultSet.getColumnIndex("division")))) {
                            syllabusDivList.add(resultSet.getString(resultSet.getColumnIndex("division")));
                        }
                        resultSet.moveToNext();
                    }
                } else {
                    syllabusDivList = new ArrayList<>();
                }
                syllabusDivList.add("All");
                syllabusDivList.add("[ Division ]");
                ArrayAdapter<String> adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, syllabusDivList);
                adapterDivision.setDropDownViewResource(R.layout.spinner_item);
                spinnerSyllabusDivision.setAdapter(adapterDivision);
                try {
                    spinnerSyllabusDivision.setSelection(syllabusDivList.indexOf(mainClass));
                } catch (Exception e) {
                    System.out.println(" classSpinner.setSelection " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerSyllabusDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String divisionString = spinnerSyllabusDivision.getSelectedItem().toString();
                subjectSpinnerList.clear();
                if (!divisionString.equals("[ Division ]")) {
                    if (!divisionString.equals("All")) {
                        conditionHashMap.put("class", spinnerSyllabusClass.getSelectedItem().toString());
                        conditionHashMap.put("division", divisionString);
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
                        conditionHashMap.put("class", spinnerSyllabusClass.getSelectedItem().toString());
                        resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                        resultSet.moveToFirst();
                        schoolClassId = "All";
                        for (int i = 0; i < resultSet.getCount(); i++) {
                            if (!subjectSpinnerList.contains(resultSet.getString(resultSet.getColumnIndex("subject")))) {
                                subjectSpinnerList.add(resultSet.getString(resultSet.getColumnIndex("subject")));
                            }
                            resultSet.moveToNext();
                        }
                    }

                } else {
                    subjectSpinnerList = new ArrayList<>();
                }
                subjectSpinnerList.add("[ Subject ]");
                ArrayAdapter<String> adapterSubject = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, subjectSpinnerList);
                adapterSubject.setDropDownViewResource(R.layout.spinner_item);
                spinnerSyllabusSubject.setAdapter(adapterSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.syllabus_menu, menu);
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
                    FancyToast.makeText(SyllabusPost.this, "Only One Attachment Allowed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                } else {
                    ShowAttachmentPopup();
                }

                return true;
            case R.id.action_send:
                if (connectionDetector.isConnectingToInternet()) {
                    SubmitSyllabus();
                } else {
                    FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pickFileFromFileExplorer() {
        FilePickerBuilder.getInstance().setMaxCount(Integer.parseInt("1"))
                .setSelectedFiles(getSelectedPhotoList)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(this, REQUEST_OPEN_EXPLORER);
    }

    private void ShowAttachmentPopup() {
        View menuItemView = findViewById(R.id.action_attachment);
        PopupMenu attachmentPopupMenu = new PopupMenu(this, menuItemView);
        MenuInflater inflate = attachmentPopupMenu.getMenuInflater();
        inflate.inflate(R.menu.syllabus_attachment_popup, attachmentPopupMenu.getMenu());
        attachmentPopupMenu.show();
        attachmentPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_camera:
                        //.openCamera();
                        break;
                    case R.id.action_document:
                        pickFileFromFileExplorer();
                        break;

                    case R.id.action_gallery:
                        PhotoPicker.builder()
                                .setPhotoCount(Integer.parseInt("5"))
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .setSelected(getGallerySelectedPhotoList)
                                .start(SyllabusPost.this, REQUEST_OPEN_GALLERY);
                        break;
                }
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_OPEN_EXPLORER:
                try {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<String> documentPathsList = new ArrayList<>();
                        documentPathsList.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        System.out.println("docPaths--------------  " + documentPathsList.size());

                        for (String attachedFilePath : documentPathsList) {
                            System.out.println("s---------For Each Loop--  " + attachedFilePath);
                            KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = attachedFilePath;
                            index = KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.lastIndexOf('/');
                            KEY_FILE_NAME_OF_SELECTED_ATTACHMENT = KEY_FILE_NAME_OF_SELECTED_ATTACHMENT.substring(index + 1);
                            System.out.println("KEY_FILE_NAME_OF_SELECTED_ATTACHMENT-------------  " + KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);

                            String addFileString1 = "file://" + attachedFilePath;
                            Uri getUriToSendAzure1 = Uri.parse(addFileString1);
                            selectedDocumentToShowInList.add(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);
                            selectedPhotoNameList.add(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);
                            mapForSelectedFileNameAndUri.put(KEY_FILE_NAME_OF_SELECTED_ATTACHMENT, getUriToSendAzure1);
                        }

                        attachmentPostAdapter = new AttachmentPostAdapter(this, selectedDocumentToShowInList, selectedPhotoNameList);
                        syllabusPostRecyclerView.setAdapter(attachmentPostAdapter);
                        attachmentPostAdapter.setClickListener(this);
                    }
                    break;
                } catch (Exception e) {
                    // syllabus_add_ImageView.setVisibility(View.VISIBLE);
                    System.out.println("Exception----in OPEN_EXPLORER");
                }
                break;

            case REQUEST_OPEN_GALLERY:
                try {
                    System.out.println("Check Open Gallery----");
                    getGallerySelectedPhotoList = new ArrayList<>();
                    if (data != null) {
                        getGallerySelectedPhotoList = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        System.out.println("getSelectedPhotoList---------------  " + getGallerySelectedPhotoList.size());
                    }
                    addThemToView(getGallerySelectedPhotoList);
                } catch (Exception e) {
                    System.out.println("Exception in REQUEST_OPEN_GALLERY ");
                }
                break;
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
            selectedDocumentToShowInList.add(compressSelectedFile);

            System.out.println("compressSelectedFile-------------------- " + compressSelectedFile);
            System.out.println("UrgetfinalUriToSendAzurei--------" + getUriToSendAzure);
            System.out.println("KEY_FILE_NAME_OF_SELECTED_ATTACHMENT--------" + KEY_FILE_NAME_OF_SELECTED_ATTACHMENT);

        }

        attachmentPostAdapter = new AttachmentPostAdapter(this, selectedDocumentToShowInList, selectedPhotoNameList);
        syllabusPostRecyclerView.setAdapter(attachmentPostAdapter);
        System.out.println("Set Item Click Listener 1 ");
        attachmentPostAdapter.setClickListener(SyllabusPost.this);
        System.out.println("Set Item Click Listener 2 ");

    }


    private void SubmitSyllabus() {

        if (!spinnerSyllabusClass.getSelectedItem().toString().equals("[ Class ]")) {

            if (!spinnerSyllabusDivision.getSelectedItem().toString().equals("[ Division ]")) {

                if (!spinnerSyllabusSubject.getSelectedItem().toString().equals("[ Subject ]")) {

                    if (mapForSelectedFileNameAndUri.size() > 0) {


                        syllabusDesc.setSelection(syllabusDesc.getText().length());
                        String timestamp = new SimpleDateFormat(("yyyyMMdd_HHmmss"), Locale.getDefault()).format(new Date());
                        attachmentUniqueIdentifier = getString(R.string.folderName) + timestamp;
                        System.out.println("Syllbus Map Size Above Zero   ---   Because need to Send Atleast one Attachment");
                        new AlertDialog.Builder(this)
                                .setTitle("Confirm Submit ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        SubmitSyllabus();
                                        if (syllabusDesc.getText().length() > 0) {
                                        } else {
                                            syllabusDesc.setText("Syllabus for class " + spinnerSyllabusClass.getSelectedItem().toString() + " " + spinnerSyllabusDivision.getSelectedItem().toString() + ".");
                                        }
                                        uploadAttachment();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create().show();
                    } else {
                        FancyToast.makeText(this, "Please Select Attachment", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
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


    @Override
    public void onClick(View view, final int position) {
        System.out.println("----------Click Position-----" + position);
        String getSelectedAttachmentName = selectedDocumentToShowInList.get(position);

        int index = getSelectedAttachmentName.lastIndexOf('/');
        selectedItemNameToRemoveFromUploadMap = getSelectedAttachmentName.substring(index + 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Want To Remove ?");
        builder.setMessage(selectedItemNameToRemoveFromUploadMap);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("old map Size ------- " + mapForSelectedFileNameAndUri.size());
                selectedDocumentToShowInList.remove(position);
                mapForSelectedFileNameAndUri.remove(selectedItemNameToRemoveFromUploadMap);
                attachmentPostAdapter.notifyDataSetChanged();
                System.out.println("getSelectedPhotoList--------------------  " + getSelectedPhotoList.size());
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
        File myDir = new File(root + "/" + getString(R.string.folderName) + "/Syllabus_Post");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imageFileName = getString(R.string.folderName) + "-Syllabus-" + n + ".jpg";
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


    public void uploadAttachment() {
        System.out.println(" mapForSelectedFileNameAndUri------------   " + mapForSelectedFileNameAndUri.size());

        mAssignment_Attachment = mClient.getTable(Attachment.class);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            Boolean checkAttachmentSucess = true;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMax(mapForSelectedFileNameAndUri.size());
                progressDialog.setMessage("Uploading " + getSelectedPhotoList.size() + " Attachment");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    for (Map.Entry<String, Uri> entry : mapForSelectedFileNameAndUri.entrySet()) {
                        progressDialog.incrementProgressBy(1);
                        System.out.println(entry.getKey() + " - //// - " + entry.getValue());
                        System.out.println("Uploading Syllbus Attachment------------------");

                        Attachment item = new Attachment();
                        item.setAttachmentIdentifier(attachmentUniqueIdentifier);
                        item.setFileName(entry.getKey());
                        item.setContainerName(AzureConfiguration.containerName);
                        mAssignment_Attachment.insert(item);

                        final InputStream imageStream = getContentResolver().openInputStream(entry.getValue());
                        final int imageLength = imageStream.available();
                        container = getContainer();
                        CloudBlockBlob attachmentBlob = container.getBlockBlobReference(entry.getKey());
                        attachmentBlob.upload(imageStream, imageLength);
                    }

                    Syllabus itemSyllabus = new Syllabus();
                    itemSyllabus.setSyllabusSubject(spinnerSyllabusSubject.getSelectedItem().toString());
                    itemSyllabus.setSyllabusDescription(syllabusDesc.getText().toString());
                    itemSyllabus.setSyllabusPostDate(new Date());
                    itemSyllabus.setUploader_id(userDetails.get(SessionManager.KEY_TEACHER_ID));
                    itemSyllabus.setBranch_id(userDetails.get(SessionManager.KEY_BRANCH_ID));
                    itemSyllabus.setSchool_Class_id(schoolClassId);
                    itemSyllabus.setAttachmentIdentifier(attachmentUniqueIdentifier);
                    itemSyllabus.setAttachmentCount(mapForSelectedFileNameAndUri.size());
                    mSyllabus.insert(itemSyllabus);

                } catch (final Exception e) {
                    checkAttachmentSucess = false;
                    System.out.println("Attachment Execption--- " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //condition to check, if there is some problem uploading attachments
                if (checkAttachmentSucess) {
                    addSyllabusToAzure();
                } else {
                    progressDialog.setMessage("Unable to post Syllabus \n Please Retry ");
                    progressDialog.show();
                    System.out.println("Unable to post Syllbus");
                }
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


    private void addSyllabusToAzure() {
        progressDialog.show();

        Calendar calender = Calendar.getInstance();
        JsonObject jsonObjectAddSyllabus = new JsonObject();
        jsonObjectAddSyllabus.addProperty("Subject", spinnerSyllabusSubject.getSelectedItem().toString());
        jsonObjectAddSyllabus.addProperty("Postdate", calender.getTimeInMillis());
        jsonObjectAddSyllabus.addProperty("Submitted_by_Name", userDetails.get(SessionManager.KEY_NAME));
        jsonObjectAddSyllabus.addProperty("School_class_id", schoolClassId);
        jsonObjectAddSyllabus.addProperty("Branch_id", userDetails.get(SessionManager.KEY_BRANCH_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("syllabusAddApi", jsonObjectAddSyllabus);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(SyllabusPost.this)
                                .setMessage(R.string.check_network)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addSyllabusToAzure();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                    progressDialog.dismiss();
                    new AlertDialog.Builder(SyllabusPost.this)
                            .setMessage("Syllabus Submitted.")
                            .setCancelable(false)
                            .setNegativeButton("Add more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SyllabusPost syllabusPost = new SyllabusPost();
                                    syllabusPost.finish();

                                    Intent addAlertIntent = new Intent(getApplicationContext(), SyllabusPost.class);
                                    startActivity(addAlertIntent);
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
                            new AlertDialog.Builder(SyllabusPost.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addSyllabusToAzure();
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
}

