package com.relecotech.androidsparsh_smallwonder.activities;

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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.AlertStudentListAdapter;
import com.relecotech.androidsparsh_smallwonder.adapters.AttachmentPostAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Attachment;
import com.relecotech.androidsparsh_smallwonder.azureControllers.School_Config;
import com.relecotech.androidsparsh_smallwonder.models.AlertStudentListData;
import com.relecotech.androidsparsh_smallwonder.models.SchoolClassDivisionModel;
import com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.relecotech.androidsparsh_smallwonder.utils.SendSmsManager;
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
 * Created by amey on 10/15/2015.
 */
public class AlertPost extends AppCompatActivity implements ItemClickListener {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private String branchId;

    List<SchoolClassDivisionModel> classDivisionModelList;

    private Map<String, List<AlertStudentListData>> schoolClassIdStudentHashMap;
    private Map<String, List<SchoolClassDivisionModel>> classIdDivObjHashMap;
    private Spinner alertClassSpinner, alertDivisionSpinner;
    private ArrayList<String> alertClassList;
    private ArrayList<String> alertDivList;
    private ArrayList<String> alertTagList;
    private ArrayList<String> alertPriorityList;
    private ArrayList<String> sendToStudentList;
    private Spinner alertCategory;
    private String schoolClassId;
    private List<AlertStudentListData> studentSpinnerList;
    private List<AlertStudentListData> displayStudentList;
    private TextView alertStudent;
    private AlertStudentListAdapter adapter;
    private Spinner alertPrioritySpinner;
    private EditText alertTitleEditText, alertDescriptionEditText;
    private MenuItem sendMenuItem;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private HashMap<String, String> conditionHashMap;
    private JsonArray jsonSendAlertArray;
    private JsonObject jsonObjectAddAlert;

    static final int REQUEST_TAKE_PHOTO = 3;
    static final int REQUEST_OPEN_GALLERY = 2;
    static final int REQUEST_OPEN_EXPLORER = 1;
    private ArrayList<String> getSelectedAttachmentList;
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
    private RecyclerView alertPostRecyclerView;
    private ProgressDialog progressDialog;
    private MobileServiceTable<Attachment> mAssignment_Attachment;
    private String studentId;

    private MobileServiceTable<School_Config> mSchoolConfigTable;
    private String smsUrl;
    private String onOrOffValue;
    List<String> allStudentAlertNumberList = new ArrayList<>();
    private MobileServiceClient mClient;
    private String mainClass, mainDiv;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_post);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        try {
            mAssignment_Attachment = mClient.getTable(Attachment.class);
            mSchoolConfigTable = mClient.getTable(School_Config.class);
        } catch (Exception e) {
            System.out.println(" Exception Azure table " + e.getMessage());
        }

        new CheckSmsOnOff().execute();

        progressDialog = new ProgressDialog(this);
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        branchId = userDetails.get(SessionManager.KEY_BRANCH_ID);


        alertClassList = new ArrayList<>();
        alertDivList = new ArrayList<>();
        studentSpinnerList = new ArrayList<>();
        displayStudentList = new ArrayList<>();
        sendToStudentList = new ArrayList<>();

        classDivisionModelList = new ArrayList<>();
        classIdDivObjHashMap = new HashMap<>();
        schoolClassIdStudentHashMap = new HashMap<>();
        conditionHashMap = new HashMap<>();

        selectedPhotoNameList = new ArrayList<>();
        getSelectedPhotoList = new ArrayList<>();
        mapForSelectedFileNameAndUri = new HashMap<>();


        alertClassSpinner = (Spinner) findViewById(R.id.spinnerAlertClass);
        alertDivisionSpinner = (Spinner) findViewById(R.id.spinnerAlertDivision);
        alertCategory = (Spinner) findViewById(R.id.spinnerAlertTag);
        alertPrioritySpinner = (Spinner) findViewById(R.id.spinnerAlertPriority);

        alertTitleEditText = (EditText) findViewById(R.id.alertTitleTextView);
        alertDescriptionEditText = (EditText) findViewById(R.id.alertTextDescription);

        attachmentCardView = (CardView) findViewById(R.id.alertPost_attachment_cv);
        alertPostRecyclerView = (RecyclerView) findViewById(R.id.alertPostRecyclerView);
        alertPostRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));


        alertStudent = (TextView) findViewById(R.id.spinnerAlertStudent);
        alertStudent.setText("[ Select Student ]");

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
            if (!alertClassList.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                alertClassList.add(resultSet.getString(resultSet.getColumnIndex("class")));
            }
            resultSet.moveToNext();
        }

        alertClassList.add("[ Class ]");
        ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertClassList);
        adapterClass.setDropDownViewResource(R.layout.spinner_item);
        alertClassSpinner.setAdapter(adapterClass);
        try {
            alertClassSpinner.setSelection(alertClassList.indexOf(mainClass));
        } catch (Exception e) {
            System.out.println(" classSpinner.setSelection " + e.getMessage());
        }
        alertClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String alertClassString = alertClassSpinner.getSelectedItem().toString();
                alertDivList.clear();
                if (!alertClassString.contains("[ Class ]")) {
                    conditionHashMap.put("class", alertClassString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        if (!alertDivList.contains(resultSet.getString(resultSet.getColumnIndex("division")))) {
                            alertDivList.add(resultSet.getString(resultSet.getColumnIndex("division")));
                        }
                        resultSet.moveToNext();
                    }
                } else {
                    alertDivList = new ArrayList<>();
                }
                alertDivList.add("All");
                alertDivList.add("[ Division ]");
                ArrayAdapter<String> adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertDivList);
                adapterDivision.setDropDownViewResource(R.layout.spinner_item);
                alertDivisionSpinner.setAdapter(adapterDivision);
                try {
                    alertDivisionSpinner.setSelection(alertDivList.indexOf(mainDiv));
                } catch (Exception e) {
                    System.out.println(" classSpinner.setSelection " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        alertTagList = new ArrayList<>();
        String[] alertTagList = getResources().getStringArray(R.array.alertCategory);

        alertPriorityList = new ArrayList<>();
        alertPriorityList.add("[ Priority ]");
        alertPriorityList.add("Urgent");
        alertPriorityList.add("Normal");


        ArrayAdapter<String> adapterTag = new ArrayAdapter<String>(getApplicationContext(), R.layout.category_spinner_item, alertTagList);
        adapterTag.setDropDownViewResource(R.layout.spinner_item);
        alertCategory.setAdapter(adapterTag);
        alertCategory.setSelection(adapterTag.getCount() - 1);

        alertCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tagSpinnerSelectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("tagSpinnerSelectedItem--------" + tagSpinnerSelectedItem);
                if (!tagSpinnerSelectedItem.equals("[ Category ]")) {
                    if (!tagSpinnerSelectedItem.equals("Student Unwell") && !tagSpinnerSelectedItem.equals("Complaint")) {
                        System.out.println("Yes Sick not selected ");
                        alertPrioritySpinner.setSelection(alertPriorityList.indexOf("Normal"));
                        alertTitleEditText.setText("");
                        alertDescriptionEditText.setText("");
                        alertTitleEditText.setHint("Title");
                        alertDescriptionEditText.setHint("Add Description here");
                    } else {
                        if (tagSpinnerSelectedItem.contains("Complaint")) {
                            alertPrioritySpinner.setSelection(alertPriorityList.indexOf("Urgent"));
                            alertTitleEditText.setText("Complaint Note");
                            alertTitleEditText.setSelection(alertTitleEditText.getText().length());
                            alertDescriptionEditText.setText("Hello Parents, Please take note of below : ");
                        }
                        if (tagSpinnerSelectedItem.contains("Student Unwell")) {
                            System.out.println("Yes Sick selected ");
                            alertPrioritySpinner.setSelection(alertPriorityList.indexOf("Urgent"));
                            alertTitleEditText.setText("Student Unwell - Urgent");
                            alertTitleEditText.setSelection(alertTitleEditText.getText().length());
                            alertDescriptionEditText.setText("Hello Parents, Your child is not well. Please report to school.");
                        }
                    }
                } else {
                    alertPrioritySpinner.setSelection(alertPriorityList.indexOf("[ Priority ]"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterAlertPriority = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertPriorityList);
        adapterAlertPriority.setDropDownViewResource(R.layout.spinner_item);
        alertPrioritySpinner.setAdapter(adapterAlertPriority);
        alertPrioritySpinner.setSelection(adapterAlertPriority.getCount() - 1);

        alertDivisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String alertDivString = alertDivisionSpinner.getSelectedItem().toString();

                if (!alertDivString.equals("[ Division ]")) {
                    if (!alertDivString.equals("All")) {
                        conditionHashMap.put("class", alertClassSpinner.getSelectedItem().toString());
                        conditionHashMap.put("division", alertDivString);
                        resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                        resultSet.moveToFirst();
                        schoolClassId = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));
//                        schoolClassId = classIdDivObjHashMap.get(alertClassSpinner.getSelectedItem().toString()).get(position).getSchoolClassId();
                        System.out.println("schoolClassId " + schoolClassId);

                        if (schoolClassIdStudentHashMap.containsKey(schoolClassId)) {
                            studentSpinnerList = schoolClassIdStudentHashMap.get(schoolClassId);
                        } else {
                            FetchStudentList();
                        }
                    } else {
                        schoolClassId = "All";
//                        if (tagSpinnerSelectedItem.equals("All")) {
//                            selectedItemValue = 0;
                        studentId = "All";
                        alertStudent.setText(studentId);
//                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        alertStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alertDivisionSpinner.getSelectedItem().toString().equals("All")) {
                    displayStudentListMethod(studentSpinnerList);
                } else {
                    FancyToast.makeText(AlertPost.this, "Change Division", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }
            }
        });

    }

    private void displayStudentListMethod(final List<AlertStudentListData> studentSpinnerList) {

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.alert_dialog_listview, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);
        builder.setTitle("Student List");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int count = 0;
                for (int i = 0; i < studentSpinnerList.size(); i++) {
                    if (studentSpinnerList.get(i).isSelected()) {
                        count++;
                    }
                }
                if (studentSpinnerList.size() > 0) {
                    if (studentSpinnerList.get(0).isSelected()) {
                        alertStudent.setText("All");
                    } else {
                        alertStudent.setText(String.valueOf(count) + " Selected");
                    }
                }else {
                    FancyToast.makeText(AlertPost.this, "No Student Selected ", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }
            }
        });

        final ListView studentListView = (ListView) view.findViewById(R.id.alertStudentListView);
        studentListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        adapter = new AlertStudentListAdapter(getApplicationContext(), studentSpinnerList, studentListView);
        studentListView.setAdapter(adapter);

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertStudentListData alertStudentListData = (AlertStudentListData) adapterView.getItemAtPosition(position);
                if (alertStudentListData.getStudentId().equalsIgnoreCase("all")) {
                    for (int i = 0; i < studentSpinnerList.size(); i++) {
                        if (alertStudentListData.isSelected()) {
                            System.out.println(" alertStudentListData.isSelected() ");
                            studentSpinnerList.get(i).setSelected(false);
                        } else {
                            if (i <= 20) {
                                studentSpinnerList.get(i).setSelected(true);
                            } else {

                            }
                            System.out.println(" alertStudentListData.isSelected() ELSE");
                        }
                    }
                    if (!studentSpinnerList.get(0).isSelected()) {
                        if (studentSpinnerList.size() > 20) {
                            FancyToast.makeText(AlertPost.this, "First 20 Student Selected ", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                        }
                    }

                } else {
                    if (alertStudentListData.isSelected()) {
                        alertStudentListData.setSelected(false);
                    } else {
                        alertStudentListData.setSelected(true);
                        if (studentSpinnerList.get(0).isSelected()) {
                            studentSpinnerList.get(0).setSelected(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
//                FancyToast.makeText(AlertPost.this, alertStudentListData.getFullName(), FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void FetchStudentList() {

        JsonObject jsonObjectForStudentList = new JsonObject();
        jsonObjectForStudentList.addProperty("SchoolClassId", schoolClassId);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchStudentListApi", jsonObjectForStudentList);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fetch Student List Exception    " + exception);
                studentSpinnerList = new ArrayList<>();
                studentSpinnerList.add(new AlertStudentListData("All", "All", "", "", false));
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Fetch Student List  API   response    " + response);
                StudentListJsonParse(response);
            }
        });
    }

    private void StudentListJsonParse(JsonElement response) {
        JsonArray getJsonListResponse = response.getAsJsonArray();
        studentSpinnerList = new ArrayList<>();
        studentSpinnerList.add(new AlertStudentListData("All", "All", "", "", false));
        try {
            if (getJsonListResponse.size() == 0) {
                System.out.println("json not received");
            } else {
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String student_phoneNo = jsonObjectForIteration.get("phone").toString().replace("\"", "");
                    String student_rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");

                    String student_fullName = student_firstName + " " + student_lastName;

                    studentSpinnerList.add(new AlertStudentListData(student_Id, student_fullName, student_phoneNo, student_rollNo, false));
                }
                schoolClassIdStudentHashMap.put(schoolClassId, studentSpinnerList);
                System.out.println("schoolClassIdStudentHashMap " + schoolClassIdStudentHashMap.size());
            }
            schoolClassIdStudentHashMap.put(schoolClassId, studentSpinnerList);
//            if (studentSpinnerList.get(0).isSelected()) {
//                alertStudent.setText("All");
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
                ShowAttachmentPopup();
                return true;
            case R.id.action_send:
                if (connectionDetector.isConnectingToInternet()) {
                    submitAlert();
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
                        PhotoPicker.builder()
                                .setPhotoCount(3)
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .setSelected(getGallerySelectedPhotoList)
                                .start(AlertPost.this, REQUEST_OPEN_GALLERY);
                        break;
                }
                return false;
            }
        });
    }

    public void pickFileFromFileExplorer() {

        FilePickerBuilder.getInstance().setMaxCount(2)
                .setSelectedFiles(getGallerySelectedPhotoList)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(AlertPost.this, REQUEST_OPEN_EXPLORER);


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
                        alertPostRecyclerView.setAdapter(attachmentPostAdapter);
                        attachmentPostAdapter.setClickListener(AlertPost.this);
                    } else {
                        System.out.println("--Length of Photo is Zero--");
                    }
                    mCameraImageFile.delete();

                    if (mapForSelectedFileNameAndUri.size() != 0) {
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

                        System.out.println("s---------For Each Loopp--  " + attachedFilePath);
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

                    if (mapForSelectedFileNameAndUri.size() != 0) {
                        attachmentCardView.setVisibility(View.VISIBLE);
                    }

                    attachmentPostAdapter = new AttachmentPostAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
                    alertPostRecyclerView.setAdapter(attachmentPostAdapter);
                    attachmentPostAdapter.setClickListener(AlertPost.this);

                } catch (Exception e) {
                    System.out.println("Exception----in OPEN_EXPLORER");
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
                    AddAlert();
                } else {
                    progressDialog.setMessage("Unable to post Alert \n Please Retry ");
                    progressDialog.show();
                    System.out.println("Unable to post Assignment");
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

        if (mapForSelectedFileNameAndUri.size() != 0) {
            attachmentCardView.setVisibility(View.VISIBLE);
        }

        attachmentPostAdapter = new AttachmentPostAdapter(this, getSelectedPhotoList, selectedPhotoNameList);
        alertPostRecyclerView.setAdapter(attachmentPostAdapter);
        System.out.println("Set Item Click Listener 1 ");
        attachmentPostAdapter.setClickListener(AlertPost.this);
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
        File myDir = new File(root + "/" + getString(R.string.folderName) + "/Alert_Post");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imageFileName = getString(R.string.folderName) + "-Alert-" + n + ".jpg";
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

    public void submitAlert() {
//        try {

        String AlertClassString = alertClassSpinner.getSelectedItem().toString();
        String AlertDivString = alertDivisionSpinner.getSelectedItem().toString();
        String AlertTagString = alertCategory.getSelectedItem().toString();
        String AlertDesText = alertDescriptionEditText.getText().toString();
        String AlertTitleText = alertTitleEditText.getText().toString();

        if (!AlertClassString.contains("[ Class ]")) {

            if (!AlertDivString.contains("[ Division ]")) {

                if (!alertStudent.getText().toString().contains("[ Select Student ]")) {

                    if (!alertStudent.getText().toString().equals("0 Selected")) {

                        if (!AlertTagString.contains("[ Alert Category ]")) {

                            if (!alertPrioritySpinner.getSelectedItem().toString().contains("[ Priority ]")) {

                                if (AlertTitleText.length() > 0) {

                                    if (AlertTitleText.length() < 100) {
                                        String timestamp = new SimpleDateFormat(("yyyyMMdd_HHmmss"), Locale.getDefault()).format(new Date());
                                        attachmentUniqueIdentifier = AlertClassString + AlertDivString + AlertTagString + timestamp;
//
//                                        sendMenuItem.setEnabled(false);
                                        if (mapForSelectedFileNameAndUri.size() > 0) {
                                            System.out.println("Map Size Above Zero");
                                            if (alertDescriptionEditText.getText().length() > 0) {
                                                alertDescriptionEditText.append("\nThis message has an attachment.");
                                            } else {
                                                alertDescriptionEditText.setText("Alert for class " + alertClassSpinner.getSelectedItem().toString() + " " + alertDivisionSpinner.getSelectedItem().toString() + ".");
                                                alertDescriptionEditText.append("\n\nThis message has an attachment.");
                                            }
                                            uploadAttachment();
                                        } else {
                                            if (alertDescriptionEditText.getText().length() > 0) {

                                            } else {
                                                alertDescriptionEditText.setText("Alert for class " + alertClassSpinner.getSelectedItem().toString() + " " + alertDivisionSpinner.getSelectedItem().toString() + ".");
                                            }
                                            AddAlert();
                                        }

                                    } else {
                                        FancyToast.makeText(this, "Title Too Long", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                    }
                                } else {
                                    FancyToast.makeText(this, "Add Title", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                }
                            } else {
                                FancyToast.makeText(this, "Select Priority", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                            }
                        } else {
                            FancyToast.makeText(this, "Select Category", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        }
                    } else {
                        FancyToast.makeText(this, "Select Student", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    }
                } else {
                    FancyToast.makeText(this, "Select Student", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }
            } else {
                FancyToast.makeText(this, "Select Division", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            }
        } else {
            FancyToast.makeText(this, "Select class", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
        }

    }

    private void AddAlert() {
//        alertProgressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        progressDialog.setMessage("Sending Alert...");
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();

        jsonSendAlertArray = new JsonArray();

        System.out.println("studentId  sendToStudentList.size() " + sendToStudentList.size());
        if (alertDivisionSpinner.getSelectedItem().toString().equals("All")) {

            FetchStudentListForSms();

            jsonObjectAddAlert = new JsonObject();
            jsonObjectAddAlert.addProperty("Alert_class", alertClassSpinner.getSelectedItem().toString());
            jsonObjectAddAlert.addProperty("Alert_division", alertDivisionSpinner.getSelectedItem().toString());
            jsonObjectAddAlert.addProperty("Category", alertCategory.getSelectedItem().toString());
            jsonObjectAddAlert.addProperty("Alert_priority", alertPrioritySpinner.getSelectedItem().toString());
            jsonObjectAddAlert.addProperty("Title", alertTitleEditText.getText().toString());
            jsonObjectAddAlert.addProperty("Alert_description", alertDescriptionEditText.getText().toString());
            jsonObjectAddAlert.addProperty("Active", true);
            jsonObjectAddAlert.addProperty("Postdate", calendar.getTimeInMillis());
            jsonObjectAddAlert.addProperty("Teacher_Name", userDetails.get(SessionManager.KEY_NAME));
            jsonObjectAddAlert.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_ID));
            jsonObjectAddAlert.addProperty("Student_id", studentId);
            jsonObjectAddAlert.addProperty("School_class_id", schoolClassId);
            jsonObjectAddAlert.addProperty("Branch_id", branchId);
            jsonObjectAddAlert.addProperty("Attachment_identifier", attachmentUniqueIdentifier);
            jsonObjectAddAlert.addProperty("Attachment_count", mapForSelectedFileNameAndUri.size());
            jsonSendAlertArray.add(jsonObjectAddAlert);
        } else {
            System.out.println(" adapter.spinnerStudentLists.size() " + adapter.spinnerStudentLists.size());
            for (int i = 0; i < adapter.spinnerStudentLists.size(); i++) {
                AlertStudentListData data = adapter.spinnerStudentLists.get(i);

                allStudentAlertNumberList.add(data.getStudentPhone());
                if (data.isSelected()) {
                    studentId = data.getStudentId();
                    jsonObjectAddAlert = new JsonObject();
                    jsonObjectAddAlert.addProperty("Alert_class", alertClassSpinner.getSelectedItem().toString());
                    jsonObjectAddAlert.addProperty("Alert_division", alertDivisionSpinner.getSelectedItem().toString());
                    jsonObjectAddAlert.addProperty("Category", alertCategory.getSelectedItem().toString());
                    jsonObjectAddAlert.addProperty("Alert_priority", alertPrioritySpinner.getSelectedItem().toString());
                    jsonObjectAddAlert.addProperty("Title", alertTitleEditText.getText().toString());
                    jsonObjectAddAlert.addProperty("Alert_description", alertDescriptionEditText.getText().toString());
                    jsonObjectAddAlert.addProperty("Active", true);
                    jsonObjectAddAlert.addProperty("Postdate", calendar.getTimeInMillis());
                    jsonObjectAddAlert.addProperty("Teacher_Name", userDetails.get(SessionManager.KEY_NAME));
                    jsonObjectAddAlert.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_ID));
                    jsonObjectAddAlert.addProperty("Student_id", studentId);
                    jsonObjectAddAlert.addProperty("School_class_id", schoolClassId);
                    jsonObjectAddAlert.addProperty("Branch_id", branchId);
                    jsonObjectAddAlert.addProperty("Attachment_identifier", attachmentUniqueIdentifier);
                    jsonObjectAddAlert.addProperty("Attachment_count", mapForSelectedFileNameAndUri.size());
                    jsonSendAlertArray.add(jsonObjectAddAlert);
                }
            }
        }

        System.out.println(" allStudentAlertList " + allStudentAlertNumberList.size());

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("alertAddApi", jsonSendAlertArray);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Alert Post exception    " + exception);
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AlertPost.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddAlert();
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
                System.out.println("Alert POst API   response    " + response);
                sendMenuItem.setEnabled(true);
                if (response.toString().equals("true")) {
                    progressDialog.dismiss();
                    try {
                        if (onOrOffValue.equalsIgnoreCase("on")) {
                            SendSmsManager sendSmsManager = new SendSmsManager(AlertPost.this);
                            String title = alertTitleEditText.getText().toString();
                            String description = alertDescriptionEditText.getText().toString();
                            sendSmsManager.SendAlertSmsToStudents(smsUrl, allStudentAlertNumberList, title, description);
                        } else {
                            if (onOrOffValue.equalsIgnoreCase("off")) {
                                System.out.println("else if SMS");
//                                FancyToast.makeText(AlertPost.this, "Sms Service is Off.", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                            } else {
                                System.out.println("ELSE ELSE SMS");
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println(" onOrOffValue " + e.getMessage());
                    }

                    new AlertDialog.Builder(AlertPost.this)
                            .setMessage("Alert Submitted.")
                            .setCancelable(false)
                            .setNegativeButton("Add more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertPost alertPost = new AlertPost();
                                    alertPost.finish();

                                    Intent addAlertIntent = new Intent(getApplicationContext(), AlertPost.class);
                                    startActivity(addAlertIntent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
//                                    intent.putExtra("message",message);//Put Message to pass over intent
                                    setResult(RESULT_OK,intent);//Set result OK
                                    finish();
//                                    onBackPressed();
                                }
                            }).create().show();
                } else {

                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(AlertPost.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AddAlert();
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
        String getSelectedAttachmentName = getSelectedPhotoList.get(position);

        int index = getSelectedAttachmentName.lastIndexOf('/');
        selectedItemNameToRemoveFromUploadMap = getSelectedAttachmentName.substring(index + 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(AlertPost.this);
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
                if (mapForSelectedFileNameAndUri.size() == 0) {
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

    private class CheckSmsOnOff extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<School_Config> result = mSchoolConfigTable.where().field("Type").eq("smsUrl").and().field("Branch_id").eq(userDetails.get(SessionManager.KEY_BRANCH_ID))
                        .select("Type", "Value", "value_one", "Branch_id").execute().get();
                System.out.println("result " + result);

                smsUrl = result.get(0).getValue();
                onOrOffValue = result.get(0).getValue_one();
                String branchId = result.get(0).getBranch_id();

                System.out.println(" smsUrl " + smsUrl);
                System.out.println(" onOrOffVal " + onOrOffValue);
                System.out.println(" branchId " + branchId);

            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
            return null;
        }
    }

    private void FetchStudentListForSms() {

        JsonObject jsonObjectForStudentList = new JsonObject();
        jsonObjectForStudentList.addProperty("TAG", "DIVISION_ALL");
        jsonObjectForStudentList.addProperty("Class", alertClassSpinner.getSelectedItem().toString());
        jsonObjectForStudentList.addProperty("branchId", branchId);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchPhoneNoFromAdmin", jsonObjectForStudentList);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fetch Student List Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Fetch Student List  API   response    " + response);
                StudentListJsonParseForSms(response);
            }
        });
    }

    private void StudentListJsonParseForSms(JsonElement response) {
        JsonArray getJsonListResponse = response.getAsJsonArray();
        allStudentAlertNumberList = new ArrayList<>();
        try {
            if (getJsonListResponse.size() == 0) {
                System.out.println("json not received");
            } else {
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    String student_phoneNo = jsonObjectForIteration.get("phone").toString().replace("\"", "");
                    allStudentAlertNumberList.add(student_phoneNo);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}