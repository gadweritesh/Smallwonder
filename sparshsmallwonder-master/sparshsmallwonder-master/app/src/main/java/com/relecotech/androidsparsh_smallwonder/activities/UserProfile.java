package com.relecotech.androidsparsh_smallwonder.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
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
import com.relecotech.androidsparsh_smallwonder.adapters.UserProfileAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Admin;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Student;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Teacher;
import com.relecotech.androidsparsh_smallwonder.models.UserProfileListData;
import com.relecotech.androidsparsh_smallwonder.utils.AlarmReceiver;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Relecotech on 30-01-2018.
 */

public class UserProfile extends AppCompatActivity {
    private SessionManager sessionManager;
    private ArrayList<UserProfileListData> userProfileList;
    private ArrayList<UserProfileListData> userProfileListFull;
    private UserProfileAdapter userProfileAdapter;
    private RecyclerView userProfileReviewRecyclerView;
    private LinearLayoutManager layoutManager;
    private TextView editProfileTextView;
    private String name, branchId, schoolClassId, teacherId, studentId, userRole, branchName;
    private TextView nameTextView;
    Student studentItem;
    Teacher teacherItem;
    private Admin adminItem;
    private String dateOfBirth;
    private String adminId;
    private ConnectionDetector connectionDetector;
    private ProgressBar progressBar;
    private TextView noDataAvailableTextView;
    private CircleImageView userProfileImageView;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private AlertDialog alertDialog;
    private String profile_pic_name;
    private String directory;
    private File myDir;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mClient = Singleton.Instance().mClientMethod(this);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);

        name = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
        branchId = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_ID);
        studentId = sessionManager.getUserDetails().get(SessionManager.KEY_STUDENT_ID);
        teacherId = sessionManager.getUserDetails().get(SessionManager.KEY_TEACHER_ID);
        adminId = sessionManager.getUserDetails().get(SessionManager.KEY_ADMIN_ID);
        schoolClassId = sessionManager.getUserDetails().get(SessionManager.KEY_SCHOOL_CLASS_ID);
        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);
        branchName = sessionManager.getUserDetails().get(SessionManager.KEY_BRANCH_NAME);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        userProfileList = new ArrayList<>();
        userProfileListFull = new ArrayList<>();

        progressBar = (ProgressBar) findViewById(R.id.userProfileProgressBar);
        noDataAvailableTextView = (TextView) findViewById(R.id.userProfileNoDataAvailableTextView);
        userProfileImageView = (CircleImageView) findViewById(R.id.imageView);

        if (userRole.equalsIgnoreCase("Student")) {
            userProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_student));
        }

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(name);

        myDir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.folderName) + "/Profile_Pic");
        try {
            if (myDir.mkdirs()) {
                System.out.println("Directory  created");
            } else {
                System.out.println("Directory not created");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTION" + e.getMessage());
        }
        directory = myDir.getPath();

        profile_pic_name = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
        if (sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME) != null) {
            String profile_file_name = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
            String string_file_path = directory + "/" + profile_file_name;
            File file_path = new File(string_file_path);
            Bitmap myBitmap = BitmapFactory.decodeFile(file_path.getAbsolutePath());
            userProfileImageView.setImageBitmap(myBitmap);
        } else {
            System.out.println("Set image-----------------else");
        }

        editProfileTextView = (TextView) findViewById(R.id.editProfile);
        userProfileReviewRecyclerView = (RecyclerView) findViewById(R.id.userProfileRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        userProfileReviewRecyclerView.setLayoutManager(layoutManager);
//        userProfileReviewRecyclerView.setItemAnimator(new DefaultItemAnimator());

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        userProfileReviewRecyclerView.setLayoutAnimation(animation);

        if (connectionDetector.isConnectingToInternet()) {
            progressBar.setVisibility(View.VISIBLE);
            fetchUserDetails();
        } else {
            FancyToast.makeText(UserProfile.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, EditProfileActivity.class);
                intent.putExtra("profileData", userProfileListFull);

                if (userRole.equals("Student")) {
                    intent.putExtra("studentObj", studentItem);
                } else if (userRole.equals("Teacher")) {
                    intent.putExtra("teacherObj", teacherItem);
                } else {
                    intent.putExtra("adminObj", adminItem);
                }
                startActivity(intent);
            }
        });


        userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

//        Gson gson = new Gson();
//        String abc = sessionManager.getUserDetails().get(SessionManager.KEY_USERDATA);
//        UserData serializedData = gson.fromJson(abc, UserData.class);
//        System.out.println(serializedData.getStudentId());

//        for (Map.Entry<String, UserData> entry : LauncherActivity.studentSession.entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println(" FULL NAME "+entry.getValue().getName());
//        }

    }

    private void fetchUserDetails() {
        JsonObject jsonFetchUserDetails = new JsonObject();
        jsonFetchUserDetails.addProperty("userRole", userRole);
        jsonFetchUserDetails.addProperty("studentId", studentId);
        jsonFetchUserDetails.addProperty("schoolClassId", schoolClassId);
        jsonFetchUserDetails.addProperty("branchId", branchId);
        jsonFetchUserDetails.addProperty("teacherId", teacherId);
        jsonFetchUserDetails.addProperty("adminId", adminId);
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchUserDetails", jsonFetchUserDetails);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception.getMessage());
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        new AlertDialog.Builder(UserProfile.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetchUserDetails();
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
                System.out.println("response    " + response);

                ParseUserProfileJson(response);
            }
        });
    }

    private void ParseUserProfileJson(JsonElement userProfileResponse) {

        try {
            JsonArray userProfileResponseArray = userProfileResponse.getAsJsonArray();
            if (userProfileResponseArray.size() == 0) {
                noDataAvailableTextView.setText(R.string.noDataAvailable);
                progressBar.setVisibility(View.INVISIBLE);
                System.out.println(" not received");
            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                for (int i = 0; i <= userProfileResponseArray.size() - 1; i++) {

                    JsonObject jsonObjectForIteration = userProfileResponseArray.get(i).getAsJsonObject();

                    String userEmail = jsonObjectForIteration.get("userEmail").toString().replace("\"", "");
                    String userPassword = jsonObjectForIteration.get("userPassword").toString().replace("\"", "");
                    String userPin = jsonObjectForIteration.get("userPin").toString().replace("\"", "");
                    String dateOfBirthParsed = jsonObjectForIteration.get("dateOfBirth").toString().replace("\"", "");
                    String phone = jsonObjectForIteration.get("phone").toString().replace("\"", "");
                    String firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");

                    String id = null;
                    String middleName = null;
                    String address = null;
                    String gender = null;
                    String bloodGrp = null;
                    String email = null;
                    String bus_id = null;
                    String busPickUpPoint = null;
                    String nationality = null;
                    String aadharCardNo = null;
                    String schoolClass = null;
                    String division = null;
                    if (!userRole.equals("Administrator")) {
                        id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                        firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                        lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                        middleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
                        address = jsonObjectForIteration.get("address").toString().replace("\\n", "").replace("\"", "");
                        gender = jsonObjectForIteration.get("gender").toString().replace("\"", "");
                        bloodGrp = jsonObjectForIteration.get("bloodGrp").toString().replace("\"", "");
                        email = jsonObjectForIteration.get("email").toString().replace("\"", "");
                        bus_id = jsonObjectForIteration.get("bus_id").toString().replace("\"", "");
                        busPickUpPoint = jsonObjectForIteration.get("busPickUpPoint").toString().replace("\"", "");
                        nationality = jsonObjectForIteration.get("nationality").toString().replace("\"", "");
                        aadharCardNo = jsonObjectForIteration.get("aadharCardNo").toString().replace("\"", "");
//            String branch_id = jsonObjectForIteration.get("Branch_id").toString().replace("\"", "");
                        schoolClass = jsonObjectForIteration.get("class").toString().replace("\"", "");
                        division = jsonObjectForIteration.get("division").toString().replace("\"", "");
                    }

                    Calendar birthdayCalendar = Calendar.getInstance();
                    String studentName = firstName + " " + lastName;

                    SimpleDateFormat targetDateFormat = null;

                    Date dateOfBirthDate = null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getDefault());
                    try {
                        targetDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        dateOfBirthDate = dateFormat.parse(dateOfBirthParsed);
                        birthdayCalendar.setTime(dateOfBirthDate);

                        dateOfBirth = targetDateFormat.format(dateOfBirthDate);
                    } catch (ParseException e) {
                        System.out.println(" INSIDE POST DATE CATCH");
                        e.printStackTrace();
                    }

                    birthdayCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

                    Calendar normalCalendar = Calendar.getInstance();
                    normalCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    normalCalendar.set(Calendar.MINUTE, 0);
                    normalCalendar.set(Calendar.SECOND, 0);

                    java.util.Date bdyDate = new java.util.Date(birthdayCalendar.getTimeInMillis());
                    java.util.Date normalDate = new java.util.Date(normalCalendar.getTimeInMillis());

                    System.out.println(" birthdayCalendar  " + bdyDate);
                    System.out.println(" normalCalendar  " + normalDate);
                    if (normalDate.equals(bdyDate)) {
                        System.out.println(" DATE IS SAME");
                        normalCalendar.add(Calendar.YEAR, 0);
                    } else {
                        System.out.println(" DATE IS Different");
                        if (birthdayCalendar.getTime().before(normalCalendar.getTime())) {
                            normalCalendar.add(Calendar.YEAR, 1);
                        } else if (birthdayCalendar.getTime().after(normalCalendar.getTime())) {
                            normalCalendar.add(Calendar.YEAR, 0);
                        }
                    }
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intentForNotification = new Intent(UserProfile.this, AlarmReceiver.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Notification_Tag", "Birthday");
                    bundle.putString("Notification_Title", "\uD83C\uDF82\uD83C\uDF82 Happy Birthday \uD83C\uDF82\uD83C\uDF82");
                    bundle.putString("Notification_Description", "\uD83C\uDF89\uD83C\uDF89 Happy Birthday to " + studentName + " \uD83C\uDF89\uD83C\uDF89");
                    intentForNotification.putExtras(bundle);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(UserProfile.this, 0, intentForNotification, 0);

                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                    normalCalendar.set(normalCalendar.get(Calendar.YEAR), birthdayCalendar.get(Calendar.MONTH), birthdayCalendar.get(Calendar.DAY_OF_MONTH), 9, 0);
                    System.out.println("UserProfile normalCalendar " + normalCalendar.getTime());
                    System.out.println("UserProfile normalCalendar getTimeInMillis " + normalCalendar.getTimeInMillis());

                    if (normalCalendar.getTimeInMillis() >= System.currentTimeMillis()) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, normalCalendar.getTimeInMillis(), pendingIntent);
                    }

                    userProfileList.add(new UserProfileListData("User Name", "userEmail", userEmail, R.drawable.ic_account_box_blue_grey_600_24dp, false));
                    userProfileList.add(new UserProfileListData("Password", "userPassword", userPassword, R.drawable.ic_lock_outline_blue_grey_600_24dp, false));
//                    if (!userPin.equals("null")) {
//                        userProfileList.add(new UserProfileListData("Parent Zone Pin", "userPin", userPin, R.drawable.ic_fiber_pin_grey_600_24dp, false));
//                    }

                    if (!branchName.equals("null")) {
                        userProfileList.add(new UserProfileListData("Branch Name", "branchName", branchName, R.drawable.ic_school, false));
                    }

                    if (userRole.equals("Student")) {
                        String studentEnrollmentNo = jsonObjectForIteration.get("studentEnrollmentNo").toString().replace("\"", "");
                        String admissionNumber = jsonObjectForIteration.get("admissionNumber").toString().replace("\"", "");
                        String rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                        String houseColor = jsonObjectForIteration.get("houseColor").toString().replace("\"", "");
                        String hostelStudents = jsonObjectForIteration.get("hostelStudents").toString().replace("\"", "");
                        String caste = jsonObjectForIteration.get("caste").toString().replace("\"", "");
                        String religion = jsonObjectForIteration.get("religion").toString().replace("\"", "");
                        String category = jsonObjectForIteration.get("category").toString().replace("\"", "");
                        String information = jsonObjectForIteration.get("information").toString().replace("\"", "");
                        String illness = jsonObjectForIteration.get("illness").toString().replace("\"", "");
                        String motherName = jsonObjectForIteration.get("motherName").toString().replace("\"", "");
                        String emergencyContact = jsonObjectForIteration.get("emergencyContact").toString().replace("\"", "");
                        String specialInfo = jsonObjectForIteration.get("specialInfo").toString().replace("\"", "");

                        studentItem = new Student(id, firstName, lastName, middleName, address, phone, dateOfBirthDate, gender, bloodGrp, email, bus_id, busPickUpPoint, nationality, aadharCardNo, studentEnrollmentNo, admissionNumber, rollNo, houseColor, hostelStudents, caste, religion, category, information, illness, motherName, emergencyContact, specialInfo);
                        String classDivision = String.format("%s  %s", schoolClass, division);
//                classTextView.setText(classDivision);


                        userProfileList.add(new UserProfileListData("Class ", "classDivision", classDivision, R.drawable.ic_class, false));

                        if (!studentEnrollmentNo.equals("null")) {
                            userProfileList.add(new UserProfileListData("Enrollment No.", "studentEnrollmentNo", studentEnrollmentNo, R.drawable.ic_assignment_ind_indigo_500_24dp, false));
                        }
//                userProfileListFull.add(new UserProfileListData("Enrollment No.", "studentEnrollmentNo", studentEnrollmentNo, R.drawable.ic_assignment_ind_indigo_500_24dp, false));

                        if (!admissionNumber.equals("null")) {
                            userProfileList.add(new UserProfileListData("Admission No.", "admissionNumber", admissionNumber, R.drawable.ic_admission_no, false));
                        }
//                userProfileListFull.add(new UserProfileListData("Admission No.", "admissionNumber", admissionNumber, R.drawable.ic_home_purple_400_24dp, false));

                        if (!rollNo.equals("null")) {
                            userProfileList.add(new UserProfileListData("Roll No.", "rollNo", rollNo, R.drawable.ic_roll_no, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Roll No.", "rollNo", rollNo, R.drawable.ic_roll_no, true));

                        if (!middleName.equals("null")) {
                            userProfileList.add(new UserProfileListData("Middle Name", "middleName", middleName, R.drawable.ic_user_male, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Middle Name", "middleName", middleName, R.drawable.ic_user_male, true));


                        if (!motherName.equals("null")) {
                            userProfileList.add(new UserProfileListData("Mother Name", "motherName", motherName, R.drawable.ic_female_user, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Mother Name", "motherName", motherName, R.drawable.ic_female_user, true));

                        if (!phone.equals("null")) {
                            userProfileList.add(new UserProfileListData("Mobile No.", "phone", phone, R.drawable.ic_phone_iphone_blue_grey_600_24dp, false));
                        }
//                userProfileListFull.add(new UserProfileListData("Mobile No.", "phone", phone, R.drawable.ic_phone_iphone_red_500_24dp, false));

                        if (dateOfBirth != null) {
                            userProfileList.add(new UserProfileListData("Date of Birth", "dateOfBirth", dateOfBirth, R.drawable.ic_cake_deep_orange_400_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Date of Birth", "dateOfBirth", dateOfBirth, R.drawable.ic_cake_deep_orange_400_24dp, true));


                        if (!gender.equals("null")) {
                            userProfileList.add(new UserProfileListData("Gender", "gender", gender, R.drawable.ic_gender, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Gender", "gender", gender, R.drawable.ic_gender, true));

                        if (!address.equals("null")) {
                            userProfileList.add(new UserProfileListData("Address", "address", address, R.drawable.ic_home_purple_400_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Address", "address", address, R.drawable.ic_home_purple_400_24dp, true));

                        if (!emergencyContact.equals("null")) {
                            userProfileList.add(new UserProfileListData("Emergency Contact", "emergencyContact", emergencyContact, R.drawable.ic_phone_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Emergency Contact", "emergencyContact", emergencyContact, R.drawable.ic_phone_blue_grey_600_24dp, true));

                        if (!bloodGrp.equals("null")) {
                            userProfileList.add(new UserProfileListData("Blood Group", "bloodGrp", bloodGrp, R.drawable.ic_invert_colors_red_900_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Blood Group", "bloodGrp", bloodGrp, R.drawable.ic_invert_colors_red_900_24dp, true));

                        if (!houseColor.equals("null")) {
                            userProfileList.add(new UserProfileListData("House Color", "houseColor", houseColor, R.drawable.ic_home_purple_400_24dp, true));
//                            userProfileList.add(new UserProfileListData("House Color", "houseColor", houseColor, R.drawable.housecolor, true));
                        }
                        userProfileListFull.add(new UserProfileListData("House Color", "houseColor", houseColor, R.drawable.ic_home_purple_400_24dp, true));

                        if (!specialInfo.equals("null")) {
                            userProfileList.add(new UserProfileListData("Special Info", "specialInfo", specialInfo, R.drawable.ic_info_outline_blue_900_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Special Info", "specialInfo", specialInfo, R.drawable.ic_info_outline_blue_900_24dp, true));

                        if (!aadharCardNo.equals("null")) {
                            userProfileList.add(new UserProfileListData("Aadhaar Card No", "aadharCardNo", aadharCardNo, R.drawable.aadhaar, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Aadhaar Card No", "aadharCardNo", aadharCardNo, R.drawable.aadhaar, true));

                        if (!email.equals("null")) {
                            userProfileList.add(new UserProfileListData("Email", "email", email, R.drawable.ic_email_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Email", "email", email, R.drawable.ic_email_blue_grey_600_24dp, true));

                        if (!nationality.equals("null")) {
                            userProfileList.add(new UserProfileListData("Nationality", "nationality", nationality, R.drawable.ic_flag_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Nationality", "nationality", nationality, R.drawable.ic_flag_blue_grey_600_24dp, true));

                        if (!bus_id.equals("null")) {
                            userProfileList.add(new UserProfileListData("Bus No", "bus_id", bus_id, R.drawable.ic_directions_bus_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Bus No", "bus_id", bus_id, R.drawable.ic_directions_bus_blue_grey_600_24dp, true));

                        if (!busPickUpPoint.equals("null")) {
                            userProfileList.add(new UserProfileListData("Bus PickupPoint", "busPickUpPoint", busPickUpPoint, R.drawable.ic_person_pin_circle_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Bus PickupPoint", "busPickUpPoint", busPickUpPoint, R.drawable.ic_person_pin_circle_blue_grey_600_24dp, true));

                        if (!hostelStudents.equals("null")) {
                            userProfileList.add(new UserProfileListData("Hostel Student", "hostelStudents", hostelStudents, R.drawable.ic_home_purple_400_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Hostel Student", "hostelStudents", hostelStudents, R.drawable.ic_home_purple_400_24dp, true));

                        if (!caste.equals("null")) {
                            userProfileList.add(new UserProfileListData("caste", "caste", caste, R.drawable.ic_copyright_orange_a700_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("caste", "caste", caste, R.drawable.ic_copyright_orange_a700_24dp, true));

                        if (!religion.equals("null")) {
                            userProfileList.add(new UserProfileListData("religion", "religion", religion, R.drawable.ic_religion, true));
                        }
                        userProfileListFull.add(new UserProfileListData("religion", "religion", religion, R.drawable.ic_religion, true));

                        if (!category.equals("null")) {
                            userProfileList.add(new UserProfileListData("category", "category", category, R.drawable.ic_copyright_orange_a700_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("category", "category", category, R.drawable.ic_copyright_orange_a700_24dp, true));

                        if (!information.equals("null")) {
                            userProfileList.add(new UserProfileListData("information", "information", information, R.drawable.ic_info_outline_blue_900_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("information", "information", information, R.drawable.ic_info_outline_blue_900_24dp, true));


                    } else if (userRole.equals("Teacher")) {

                        String designation = jsonObjectForIteration.get("designation").toString().replace("\"", "");
                        String qualification = jsonObjectForIteration.get("qualification").toString().replace("\"", "");
                        String speciality = jsonObjectForIteration.get("speciality").toString().replace("\"", "");
//                String maritalStatus = jsonObjectForIteration.get("maritalStatus").toString().replace("\"", "");
//                String teacherRegid = jsonObjectForIteration.get("teacherRegid").toString().replace("\"", "");

                        teacherItem = new Teacher(id, firstName, lastName, middleName, address, phone, dateOfBirthDate, gender, bloodGrp, email, bus_id, busPickUpPoint, nationality, aadharCardNo, designation, branchId);

                        if (!designation.equals("null")) {
                            userProfileList.add(new UserProfileListData("Designation", "designation", designation, R.drawable.ic_designation, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Designation", "designation", designation, R.drawable.ic_designation, true));

                        if (!middleName.equals("null")) {
                            userProfileList.add(new UserProfileListData("Middle Name", "middleName", middleName, R.drawable.ic_user_male, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Middle Name", "middleName", middleName, R.drawable.ic_user_male, true));

                        if (dateOfBirth != null) {
                            userProfileList.add(new UserProfileListData("Date of Birth", "dateOfBirth", dateOfBirth, R.drawable.ic_cake_deep_orange_400_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Date of Birth", "dateOfBirth", dateOfBirth, R.drawable.ic_cake_deep_orange_400_24dp, true));

                        if (!gender.equals("null")) {
                            userProfileList.add(new UserProfileListData("Gender", "gender", gender, R.drawable.ic_gender, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Gender", "gender", gender, R.drawable.ic_gender, true));

                        if (!address.equals("null")) {
                            userProfileList.add(new UserProfileListData("Address", "address", address, R.drawable.ic_home_purple_400_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Address", "address", address, R.drawable.ic_home_purple_400_24dp, true));

                        if (!bloodGrp.equals("null")) {
                            userProfileList.add(new UserProfileListData("Blood Group", "bloodGrp", bloodGrp, R.drawable.ic_invert_colors_red_900_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Blood Group", "bloodGrp", bloodGrp, R.drawable.ic_invert_colors_red_900_24dp, true));

//                if (!teacherRegid.equals("null")) {
//                    userProfileList.add(new UserProfileListData("Teacher Reg id", "teacherRegid", teacherRegid, R.drawable.ic_invert_colors_red_900_24dp, true));
//                }
//                userProfileListFull.add(new UserProfileListData("Teacher Reg id", "teacherRegid", teacherRegid, R.drawable.ic_invert_colors_red_900_24dp, true));

                        if (!qualification.equals("null")) {
                            userProfileList.add(new UserProfileListData("Qualification", "qualification", qualification, R.drawable.ic_qualification, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Qualification", "qualification", qualification, R.drawable.ic_qualification, true));

//                if (!maritalStatus.equals("null")) {
//                    userProfileList.add(new UserProfileListData("Marital Status", "maritalStatus", maritalStatus, R.drawable.ic_menu_profile, true));
//                }
//                userProfileListFull.add(new UserProfileListData("Marital Status", "maritalStatus", maritalStatus, R.drawable.ic_menu_profile, true));

                        if (!speciality.equals("null")) {
                            userProfileList.add(new UserProfileListData("Speciality", "speciality", speciality, R.drawable.ic_speciality, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Speciality", "speciality", speciality, R.drawable.ic_speciality, true));

                        if (!bus_id.equals("null")) {
                            userProfileList.add(new UserProfileListData("Bus No", "bus_id", bus_id, R.drawable.ic_directions_bus_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Bus No", "bus_id", bus_id, R.drawable.ic_directions_bus_blue_grey_600_24dp, true));

                        if (!busPickUpPoint.equals("null")) {
                            userProfileList.add(new UserProfileListData("Bus PickupPoint", "busPickUpPoint", busPickUpPoint, R.drawable.ic_person_pin_circle_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Bus PickupPoint", "busPickUpPoint", busPickUpPoint, R.drawable.ic_person_pin_circle_blue_grey_600_24dp, true));

                        if (!aadharCardNo.equals("null")) {
                            userProfileList.add(new UserProfileListData("Aadhaar Card No", "aadharCardNo", aadharCardNo, R.drawable.aadhaar, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Aadhaar Card No", "aadharCardNo", aadharCardNo, R.drawable.aadhaar, true));

                        if (!email.equals("null")) {
                            userProfileList.add(new UserProfileListData("Email", "email", email, R.drawable.ic_email_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Email", "email", email, R.drawable.ic_email_blue_grey_600_24dp, true));

                        if (!nationality.equals("null")) {
                            userProfileList.add(new UserProfileListData("Nationality", "nationality", nationality, R.drawable.ic_flag_blue_grey_600_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Nationality", "nationality", nationality, R.drawable.ic_flag_blue_grey_600_24dp, true));

                    } else {
                        String designation = jsonObjectForIteration.get("designation").toString().replace("\"", "");
                        String qualification = jsonObjectForIteration.get("qualification").toString().replace("\"", "");

                        adminItem = new Admin(id, firstName, lastName, phone, dateOfBirthDate, designation, qualification, aadharCardNo);

                        if (dateOfBirth != null) {
                            userProfileList.add(new UserProfileListData("Date of Birth", "dateOfBirth", dateOfBirth, R.drawable.ic_cake_deep_orange_400_24dp, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Date of Birth", "dateOfBirth", dateOfBirth, R.drawable.ic_cake_deep_orange_400_24dp, true));

                        if (!designation.equals("null")) {
                            userProfileList.add(new UserProfileListData("Designation", "designation", designation, R.drawable.ic_designation, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Designation", "designation", designation, R.drawable.ic_designation, true));

                        if (!qualification.equals("null")) {
                            userProfileList.add(new UserProfileListData("Qualification", "qualification", qualification, R.drawable.ic_qualification, true));
                        }
                        userProfileListFull.add(new UserProfileListData("Qualification", "qualification", qualification, R.drawable.ic_qualification, true));
                    }
                }

                System.out.println(userProfileList.size());
                progressBar.setVisibility(View.INVISIBLE);
                userProfileAdapter = new UserProfileAdapter(this, userProfileList);
                userProfileReviewRecyclerView.setAdapter(userProfileAdapter);
            }
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
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

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println(" INSIDE onRestart ");
        userProfileList = new ArrayList<>();
        userProfileListFull = new ArrayList<>();
        if (connectionDetector.isConnectingToInternet()) {
            progressBar.setVisibility(View.VISIBLE);
            fetchUserDetails();
        } else {
            FancyToast.makeText(UserProfile.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Remove Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {

                    if (PermissionChecker.checkSelfPermission(UserProfile.this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                        System.out.println("Self permission Start");

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);

                        System.out.println("Self Permission End");


                    } else {
                        alertDialog = new AlertDialog.Builder(UserProfile.this).create();
                        alertDialog.setMessage("Allow " + getString(R.string.folderName) + " App to access Camera");
                        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();

                    }
                } else if (items[item].equals("Choose from Gallery")) {

                    if (PermissionChecker.checkSelfPermission(UserProfile.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                        if (PermissionChecker.checkSelfPermission(UserProfile.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {

                            System.out.println("Self permission");

                            // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // startActivityForResult(intent, REQUEST_CAMERA);

                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                        } else {
                            alertDialog = new AlertDialog.Builder(UserProfile.this).create();
                            alertDialog.setMessage("Allow " + getString(R.string.folderName) + " App to access Gallery");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });

                            alertDialog.show();
                        }

                    } else {
                        alertDialog = new AlertDialog.Builder(UserProfile.this).create();
                        alertDialog.setMessage("Allow " + getString(R.string.folderName) + " App to access your Gallery");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();

                    }

                } else if (items[item].equals("Remove Photo")) {
                    alertDialog = new AlertDialog.Builder(UserProfile.this).create();
                    alertDialog.setMessage("Do you want to remove ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String userProfileFileName = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
                            System.out.println(" userProfileFileName " + userProfileFileName);
                            if (userProfileFileName != null) {
                                File file = new File(myDir, userProfileFileName);
                                System.out.println(" file " + file);
                                System.out.println(" file.exists() " + file.exists());
                                if (file.exists()) {
                                    if (file.delete()) {
                                        System.out.println("file Deleted :" + file.getAbsolutePath());
                                    } else {
                                        System.out.println("file not Deleted :" + file.getAbsolutePath());
                                    }
                                }
                                sessionManager.setSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME, null);
                                userProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_student));
                            } else {
                                FancyToast.makeText(UserProfile.this, "Cannot Remove Photo", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                                System.out.println(" userProfileFileName is NULL" + userProfileFileName);
                            }

                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                SaveImage(thumbnail);
                userProfileImageView.setImageBitmap(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                try {

                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    System.out.println("selectedImagePath" + selectedImagePath);

                    //************************compress logic start****************
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    //************************compress logic start****************
                    userProfileImageView.setImageBitmap(bm);
                    SaveImage(bm);
                } catch (Exception e) {
                    System.out.println(" Inside System Catch ---");
                }
            }
        }

    }

    private void SaveImage(Bitmap finalBitmap) {

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String userProfileFileName = getString(R.string.folderName) + "-" + n + ".jpg";
        File file = new File(myDir, userProfileFileName);
        sessionManager.setSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME, userProfileFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


