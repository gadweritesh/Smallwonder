package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;


import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.GalleryAdapter;
import com.relecotech.androidsparsh_smallwonder.adapters.GalleryRecyclerAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Gallery;
import com.relecotech.androidsparsh_smallwonder.models.SchoolGalleryListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.Listview_communicator;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Amey on 05-04-2018.
 */

public class SchoolGallery extends AppCompatActivity implements Listview_communicator {
    private EditText createGalleryDailogTitleTextView;
    private EditText createGalleryDailogDescTextView;
    private Spinner createGalleryDailogSchoolClassSpinner;
    private ArrayList<String> schoolClassArrayList,classArrayList;
    private Map<String, String> schoolClassMap;
    private Map<String, List<SchoolGalleryListData>> galleryMapData;
    private SchoolGalleryListData galleryListData;
    private List<SchoolGalleryListData> galleryList;
    private RecyclerView recyclerView;
    private GalleryRecyclerAdapter galleryRecyclerAdapter;
    private GalleryAdapter galleryAdapter;
    private ListView listViewGallery;
    private List<SchoolGalleryListData> listOfGallery;
    private TextView noDataAvailableTextView;
    private ProgressDialog progressDialog;
    private ConnectionDetector connectionDetector;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private HashMap<String, String> conditionHashMap;
    private ArrayList<String> classList;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private String schoolClassId;
    private MobileServiceClient mClient;
    public static final int GALLERY_BACK_BUTTON = 1;
    private RadioButton forAllRadioButton;
    private RadioButton forSchoolRadioButton;
    private RadioGroup radioGroup;
    private AlertDialog createGalleryDialog;
    private AlertDialog optionDialog;
    private MobileServiceTable<Gallery> galleryMobileServiceTable;
    private Gallery gallery;
    private String getSelectedGalleryGalleryID;
    ProgressDialog mProgressDialog;

    private TabLayout tabLayout;
    String selectedClassId;
    String selectedItemText;
    private ListView mainListView ;
    ArrayAdapter<String> arrayAdapter;
    boolean doubleBackToExitPressedOnce = false;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_gallery);

        mClient = Singleton.Instance().mClientMethod(this);
        galleryMobileServiceTable = mClient.getTable(Gallery.class);

        listViewGallery = findViewById(R.id.listViewGallery);
        noDataAvailableTextView = (TextView) findViewById(R.id.noDataAvailableTextViewGallery);

        tabLayout = (TabLayout) findViewById(R.id.gallery_control_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Class"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("All"), 1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(this);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        System.out.println("SELECTED USER  = "+userDetails);
        selectedClassId = userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID);
        System.out.println("SELECTED CLASS = "+selectedClassId);
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        schoolClassArrayList = new ArrayList<>();
        classArrayList = new ArrayList<>();
        schoolClassMap = new TreeMap<>();
        listOfGallery = new ArrayList<>();

        classList = new ArrayList<>();
        conditionHashMap = new HashMap<>();
        resultSet = databaseHandler.getClassDataByCursor();

        resultSet.moveToFirst();
        schoolClassArrayList.add("Select Class Division");
        System.out.println(" resultSet.getCount() " + resultSet.getCount());
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!schoolClassMap.containsKey(resultSet.getString(resultSet.getColumnIndex("class")) + " " + resultSet.getString(resultSet.getColumnIndex("division")))) {
                System.out.println("KEY "+resultSet.getString(resultSet.getColumnIndex("class"))+ " " + resultSet.getString(resultSet.getColumnIndex("division"))+" VALUE "+resultSet.getString(resultSet.getColumnIndex("schoolClassId")));
                schoolClassMap.put(resultSet.getString(resultSet.getColumnIndex("class")) + " " + resultSet.getString(resultSet.getColumnIndex("division")), resultSet.getString(resultSet.getColumnIndex("schoolClassId")));
            }
            resultSet.moveToNext();
        }

        for(Map.Entry map  : schoolClassMap.entrySet()){
            System.out.println("KEY "+map.getKey()+" VALUE "+map.getValue());

        }

        schoolClassArrayList.addAll(schoolClassMap.keySet());
        classArrayList.addAll(schoolClassMap.keySet());
        galleryList = new ArrayList<>();
        galleryMapData = new HashMap<>();

        for(int i=0;i<schoolClassArrayList.size();i++)
        {
            System.out.println("SCHOOL CLASS  "+schoolClassArrayList.get(i)+" ");
        }

        if (connectionDetector.isConnectingToInternet()) {
            if(!userRole.equals("Student"))
            {
                setTab();
            }
            else
            {
                DownloadGalleryImages();
            }
            System.out.println(" connectionDetector ");
        } else {
            noDataAvailableTextView.setText(R.string.noDataAvailable);
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition())
                {
                    case 0:
                        if(userRole.equals("Teacher") || userRole.equals("Administrator")) {
                            setTab();
                            //DownloadGalleryImages();
                        }
                        else {
                            DownloadGalleryImages();
                        }
                        break;
                    case 1:
                        DownloadGalleryImages();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch(tab.getPosition())
                {
                    case 0:
                        if(userRole.equals("Teacher") || userRole.equals("Administrator")) {
                            setTab();
                            //DownloadGalleryImages();
                        }
                        else
                        {
                            DownloadGalleryImages();
                        }
                        break;
                    case 1:
                        DownloadGalleryImages();
                        break;
                }
            }
        });

        listViewGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SchoolGalleryListData selectedListItem = listOfGallery.get(position);
                List<SchoolGalleryListData> getListOfGalleryImage = galleryMapData.get(selectedListItem.getGallery_id());
                Intent intent = new Intent(SchoolGallery.this, GalleryDetail.class);
                intent.putParcelableArrayListExtra("GalleryListItem", (ArrayList<? extends Parcelable>) getListOfGalleryImage);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!userRole.equals("Student")) {
            getMenuInflater().inflate(R.menu.gallery_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_create_gallery:
                ShowCreateGalleryDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowCreateGalleryDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.create_gallery_alert_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        createGalleryDailogSchoolClassSpinner = (Spinner) view.findViewById(R.id.createGalleryDailogSchoolClassSpinner);
        createGalleryDailogTitleTextView = (EditText) view.findViewById(R.id.createGalleryDailogTitleTextView);
        createGalleryDailogDescTextView = (EditText) view.findViewById(R.id.createGalleryDailogDescTextView);

        radioGroup = (RadioGroup) view.findViewById(R.id.checkradiogrupl);

        forSchoolRadioButton = (RadioButton) view.findViewById(R.id.forSchoolRadioButton);
        forAllRadioButton = (RadioButton) view.findViewById(R.id.forAllRadioButton);

        //forAllRadioButton.setChecked(true);

        createGalleryDailogSchoolClassSpinner.setVisibility(View.INVISIBLE);

        final ArrayAdapter<String> createGalleryDialogSchoolClassSpinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolClassArrayList);

        createGalleryDailogSchoolClassSpinner.setSelection(createGalleryDialogSchoolClassSpinnerAdapter.getCount() - 1);
        createGalleryDailogSchoolClassSpinner.setAdapter(createGalleryDialogSchoolClassSpinnerAdapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radibuttonclick) {


                switch (radibuttonclick) {
                    case R.id.forSchoolRadioButton:
                        createGalleryDailogSchoolClassSpinner.setVisibility(View.VISIBLE);
                        break;
                    case R.id.forAllRadioButton:
                        createGalleryDailogSchoolClassSpinner.setVisibility(View.INVISIBLE);
                        break;
                }

            }
        });


        builder.setCancelable(false);
        builder.setView(view);
        builder.setTitle("Create Gallery");
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        createGalleryDialog = builder.create();
        createGalleryDialog.show();
        createGalleryDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createGalleryDailogTitleTextView.getText().toString().length() > 0  && createGalleryDailogTitleTextView.getText().toString().length() < 40 ) {
                    if (forSchoolRadioButton.isChecked() && createGalleryDailogDescTextView.getText().toString().length() < 55) {
                        if (!createGalleryDailogSchoolClassSpinner.getSelectedItem().toString().contains("Select Class Division")) {

                            sendValueToPassIntent(createGalleryDailogTitleTextView.getText().toString(), createGalleryDailogSchoolClassSpinner.getSelectedItem().toString(),
                                    createGalleryDailogDescTextView.getText().toString());

                        } else {
                            FancyToast.makeText(SchoolGallery.this, "Select Class Division", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }

                    } else if (forAllRadioButton.isChecked() && createGalleryDailogDescTextView.getText().toString().length() < 55) {
                        sendValueToPassIntent(createGalleryDailogTitleTextView.getText().toString(), "All",
                                createGalleryDailogDescTextView.getText().toString());
                    } else {
                        if(createGalleryDailogDescTextView.getText().toString().length() < 55)
                        {
                            FancyToast.makeText(SchoolGallery.this, "Please Select RadioButton", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                        }
                        else
                        {
                            FancyToast.makeText(SchoolGallery.this, "Please Enter Gallery Description in short", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }

                    }

                } else {
                    createGalleryDailogTitleTextView.setError("Please Enter Gallery Title");
                    if(createGalleryDailogTitleTextView.getText().toString().length() >= 40)
                    {
                        Toast.makeText(SchoolGallery.this, "Please Enter Gallery Title in short", Toast.LENGTH_LONG).show();
                    }
                    else if(createGalleryDailogTitleTextView.getText().toString().length() == 0)
                    {
                        Toast.makeText(SchoolGallery.this, "Please Enter Gallery Title", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void sendValueToPassIntent(String galleryTitle, String gallerySchoolClass, String galleryDesc) {
        Intent openCreateGallery = new Intent(SchoolGallery.this, CreatedGallery.class);
        Bundle bundleForCreateGalleryIntent = new Bundle();
        bundleForCreateGalleryIntent.putString("CreateGalleryTitle", galleryTitle);
        bundleForCreateGalleryIntent.putString("CreateGalleryDesc", galleryDesc);
        bundleForCreateGalleryIntent.putString("CreateGallerySchoolClass", gallerySchoolClass);
        openCreateGallery.putExtras(bundleForCreateGalleryIntent);
        startActivityForResult(openCreateGallery, GALLERY_BACK_BUTTON);
        createGalleryDialog.dismiss();
        tabLayout.getTabAt(1).select();
    }

    private void setTab(){
        alertDialog = new AlertDialog.Builder(SchoolGallery.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.customlist, null);
        alertDialog.setView(convertView);
        alertDialog.setIcon(R.drawable.galleryy);
        alertDialog.setTitle("Select Class");
        mainListView = (ListView) convertView.findViewById(R.id.mainListView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classArrayList);
        mainListView.setAdapter(arrayAdapter);
        final AlertDialog Ddialog = alertDialog.create();
        Ddialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Ddialog.cancel();
                    }
                });
        Ddialog.show();
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = mainListView.getItemAtPosition(position).toString();
                //mainListView.setVisibility(View.INVISIBLE);
                Ddialog.dismiss();
                DownloadGalleryImages();
                doubleBackToExitPressedOnce = false;
            }
        });


        /*
        selectedItemText = "Select Class Division";
        mainListView.setVisibility(View.VISIBLE);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,schoolClassArrayList);
        mainListView.setAdapter( arrayAdapter );

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = mainListView.getItemAtPosition(position).toString();
                mainListView.setVisibility(View.INVISIBLE);
                DownloadGalleryImages();
                doubleBackToExitPressedOnce = false;
            }
        });*/
    }

    private void DownloadGalleryImages() {
        listOfGallery.clear();
        galleryMapData.clear();
        //  progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        JsonObject jsonObjectParameters = new JsonObject();
        jsonObjectParameters.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
        jsonObjectParameters.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        try {
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchGalleryData", jsonObjectParameters);

            Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable exception) {
                    resultFuture.setException(exception);
                    System.out.println(" gallery API exception    " + exception);
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(SchoolGallery.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DownloadGalleryImages();
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
                    pdCanceller.postDelayed(progressRunnable, 5000);
                }

                @Override
                public void onSuccess(JsonElement response) {
                    resultFuture.set(response);
                    System.out.println("Gallery Json Response " + response);
                    parseGalleryJSON(response);
                }
            });
        } catch (Exception e) {
            System.out.println("INSIDE GALLERY EXCEPTION " + e.getMessage());
        }
    }

    private void parseGalleryJSON(JsonElement response) {

        if (response != null) {
            System.out.println("Gallery Json Respone Is Not Null ");
            JsonArray galleryJsonArray = response.getAsJsonArray();
            noDataAvailableTextView.setVisibility(View.INVISIBLE);
            if (galleryJsonArray.size() != 0) {
                System.out.println("Json Array Is not Zero---");
                noDataAvailableTextView.setVisibility(View.INVISIBLE);

                for (int arrayIteration = 0; arrayIteration < galleryJsonArray.size(); arrayIteration++) {

                    JsonObject jsonObjectForIteration = galleryJsonArray.get(arrayIteration).getAsJsonObject();
                    String galleryTitle = jsonObjectForIteration.get("galleryTitle").toString().replace("\"", "");
                    String galleryPostDate = jsonObjectForIteration.get("galleryPostDate").toString().replace("\"", "");
                    String galleryURL = jsonObjectForIteration.get("galleryURL").toString().replace("\"", "");
                    String galleryImageCount = jsonObjectForIteration.get("galleryImageCount").toString().replace("\"", "");
                    String School_Class_id = jsonObjectForIteration.get("School_Class_id").toString().replace("\"", "");

                    String Branch_id = jsonObjectForIteration.get("Branch_id").toString().replace("\"", "");
                    String photoName = jsonObjectForIteration.get("imageName").toString().replace("\"", "");
                    String Gallery_id = jsonObjectForIteration.get("Gallery_id").toString().replace("\"", "");
                    String galleryDescription = jsonObjectForIteration.get("galleryDescription").toString().replace("\"", "");
                    String galleryFolderName = jsonObjectForIteration.get("galleryFolderName").toString().replace("\"", "");
                    String galleryCategory = jsonObjectForIteration.get("galleryCategory").toString().replace("\"", "");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    try {
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());
                        Date datePost = dateFormat.parse(galleryPostDate);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());
                        galleryPostDate = targetDateFormat.format(datePost);
                        System.out.println("galleryPostDate---------- " + galleryPostDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(tabLayout.getSelectedTabPosition()== 0) {
                        if (userRole.equals("Student")) {
                            if (selectedClassId.equals(School_Class_id) || selectedClassId.equals("All")) {
                                String imageUrlToDownloadImge = galleryURL + galleryFolderName + photoName;
                                System.out.println("imageUrlToDownloadImge----------   " + imageUrlToDownloadImge);
                                if (galleryMapData.containsKey(Gallery_id)) {
                                    // Add Value in gallery List of Respective Gallery Id
                                    galleryList = galleryMapData.get(Gallery_id);
                                    galleryListData = new SchoolGalleryListData(galleryTitle, galleryPostDate, galleryURL, galleryImageCount, School_Class_id, Branch_id, photoName, Gallery_id, galleryDescription, imageUrlToDownloadImge, galleryCategory);
                                    galleryList.add(galleryListData);
                                    galleryMapData.put(Gallery_id, galleryList);
                                } else {
                                    //Create Fresh List for Gallery Id
                                    galleryList = new ArrayList<>();
                                    galleryListData = new SchoolGalleryListData(galleryTitle, galleryPostDate, galleryURL, galleryImageCount, School_Class_id, Branch_id, photoName, Gallery_id, galleryDescription, imageUrlToDownloadImge, galleryCategory);
                                    galleryList.add(0, galleryListData);
                                    galleryMapData.put(Gallery_id, galleryList);
                                    listOfGallery.add(0, galleryListData);
                                }
                            }

                        } else if (userRole.equals("Teacher") || userRole.equals("Administrator")) {
                            for (Map.Entry map : schoolClassMap.entrySet()) {
                                System.out.println("KEY " + map.getKey() + " VALUE " + map.getValue());
                                if (selectedItemText.equals(map.getKey()) && School_Class_id.equals(map.getValue())) {
                                    String imageUrlToDownloadImge = galleryURL + galleryFolderName + photoName;
                                    System.out.println("imageUrlToDownloadImge----------   " + imageUrlToDownloadImge);
                                    if (galleryMapData.containsKey(Gallery_id)) {
                                        // Add Value in gallery List of Respective Gallery Id
                                        galleryList = galleryMapData.get(Gallery_id);
                                        galleryListData = new SchoolGalleryListData(galleryTitle, galleryPostDate, galleryURL, galleryImageCount, School_Class_id, Branch_id, photoName, Gallery_id, galleryDescription, imageUrlToDownloadImge, galleryCategory);
                                        galleryList.add(galleryListData);
                                        galleryMapData.put(Gallery_id, galleryList);

                                    } else {
                                        //Create Fresh List for Gallery Id
                                        galleryList = new ArrayList<>();
                                        galleryListData = new SchoolGalleryListData(galleryTitle, galleryPostDate, galleryURL, galleryImageCount, School_Class_id, Branch_id, photoName, Gallery_id, galleryDescription, imageUrlToDownloadImge, galleryCategory);
                                        galleryList.add(0, galleryListData);
                                        galleryMapData.put(Gallery_id, galleryList);
                                        listOfGallery.add(0, galleryListData);
                                    }
                                }
                            }
                            if((listOfGallery.isEmpty() && galleryMapData.isEmpty()) && !selectedItemText.equals(""))
                            {
                                noDataAvailableTextView.setVisibility(View.VISIBLE);
                                noDataAvailableTextView.setText(R.string.noDataAvailable);
                                //FancyToast.makeText(SchoolGallery.this, "No Data Available", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                                // Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else if(tabLayout.getSelectedTabPosition()== 1)
                    {
                        String imageUrlToDownloadImge = galleryURL + galleryFolderName + photoName;
                        System.out.println("imageUrlToDownloadImge----------   " + imageUrlToDownloadImge);
                        if (galleryMapData.containsKey(Gallery_id)) {
                            // Add Value in gallery List of Respective Gallery Id
                            galleryList = galleryMapData.get(Gallery_id);
                            galleryListData = new SchoolGalleryListData(galleryTitle, galleryPostDate, galleryURL, galleryImageCount, School_Class_id, Branch_id, photoName, Gallery_id, galleryDescription, imageUrlToDownloadImge, galleryCategory);
                            galleryList.add(galleryListData);
                            galleryMapData.put(Gallery_id, galleryList);
                        } else {
                            //Create Fresh List for Gallery Id
                            galleryList = new ArrayList<>();
                            galleryListData = new SchoolGalleryListData(galleryTitle, galleryPostDate, galleryURL, galleryImageCount, School_Class_id, Branch_id, photoName, Gallery_id, galleryDescription, imageUrlToDownloadImge, galleryCategory);
                            galleryList.add(0, galleryListData);
                            galleryMapData.put(Gallery_id, galleryList);
                            listOfGallery.add(0, galleryListData);
                        }
                    } else{
                        FancyToast.makeText(this, "No Data Found ", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                }


                System.out.println("Gallery MAp======" + galleryMapData.size());
                System.out.println("Gallery MAp KeySet======" + galleryMapData.keySet());

                for (Map.Entry<String, List<SchoolGalleryListData>> entry : galleryMapData.entrySet()) {
                    System.out.println("Gallery Id =  " + entry.getKey() + "  -----  " + " Gallery Title = " + entry.getValue().size());
                }

//                if (recyclerView != null) {
//                    galleryRecyclerAdapter = new GalleryRecyclerAdapter(galleryList, SchoolGallery.this);
//                    recyclerView.setAdapter(galleryRecyclerAdapter);
//                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                }
                progressDialog.dismiss();
                galleryAdapter = new GalleryAdapter(SchoolGallery.this, listOfGallery);
                listViewGallery.setAdapter(galleryAdapter);
            } else {
                System.out.println("Json Array Is Zero----");
                progressDialog.dismiss();
                noDataAvailableTextView.setVisibility(View.VISIBLE);
                noDataAvailableTextView.setText(R.string.noDataAvailable);
            }
        } else {
            System.out.println("Gallery Json Respone Is  null");
            noDataAvailableTextView.setVisibility(View.VISIBLE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_BACK_BUTTON && resultCode == RESULT_OK) {

            DownloadGalleryImages();
        } else {

        }
    }



    public void listViewOnclick(final int position, int click_code) {

        System.out.println("position------------  " + position);
        SchoolGalleryListData selectedListItem = listOfGallery.get(position);
        String getSelectedGalleryItemTitle = selectedListItem.getGalleryTitle();
        getSelectedGalleryGalleryID = selectedListItem.getGallery_id();

        AlertDialog.Builder deleteConfirmationDailog = new AlertDialog.Builder(SchoolGallery.this);
        optionDialog = new AlertDialog.Builder(SchoolGallery.this).create();
        deleteConfirmationDailog.setMessage("Do you want to Delete  "+getSelectedGalleryItemTitle+ " ?");
        deleteConfirmationDailog.setCancelable(false);
        deleteConfirmationDailog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                deleteGallery(getSelectedGalleryGalleryID, position);
            }
        });

        deleteConfirmationDailog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                optionDialog.dismiss();
            }
        });
        deleteConfirmationDailog.create().show();

    }

    private void deleteGallery(String getSelectedGalleryGalleryID, final int position) {


        mProgressDialog = new ProgressDialog(SchoolGallery.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Deleting Gallery ...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("studentId", getSelectedGalleryGalleryID);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("gallerDelete", jsonObject);

        mProgressDialog.show();

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("gallerDelete exception " + exception);
                mProgressDialog.dismiss();
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
                        mProgressDialog.dismiss();
                        new android.support.v7.app.AlertDialog.Builder(SchoolGallery.this)
                                // .setMessage(R.string.check_network)
                                .setMessage("Gallery Delete Sucessfully...")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listOfGallery.remove(position);
                                        galleryAdapter.notifyDataSetChanged();
                                        //methodtocall(GALLERY_BACK_BUTTON);
                                        DownloadGalleryImages();
                                    }
                                }).show();

                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 2000);

            }
        });

    }

    private void methodtocall(int galleryBackButton) {
        switch (galleryBackButton) {
            case GALLERY_BACK_BUTTON:
                System.out.println("dkojdododadkal;daojdaljdapdjaojdaodjajdapdjajd");
                break;
        }
    }

    public class deleteGalleryTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String identifier = strings[0];
            System.out.println("identifier------------  " + identifier);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("studentId", identifier);

            final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
            ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("gallerDeletet", jsonObject);

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
                    pdCanceller.postDelayed(progressRunnable, 5000);
                }

                @Override
                public void onSuccess(JsonElement response) {
                    resultFuture.set(response);

                    //mProgressDialog.dismiss();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.setMessage("Gallery Delete Successfully.");
            mProgressDialog.dismiss();

        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        tabLayout.getTabAt(0).select();
        FancyToast.makeText(this, "Please click BACK again to exit", FancyToast.LENGTH_SHORT, FancyToast.DEFAULT,false).show();
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }

}
