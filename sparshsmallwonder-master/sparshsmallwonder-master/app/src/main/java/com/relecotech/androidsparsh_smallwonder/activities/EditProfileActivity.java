package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.EditProfileAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Admin;
import com.relecotech.androidsparsh_smallwonder.azureControllers.School_Config;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Student;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Teacher;
import com.relecotech.androidsparsh_smallwonder.fragments.DatePickerFragment;
import com.relecotech.androidsparsh_smallwonder.models.UserProfileListData;
import com.relecotech.androidsparsh_smallwonder.utils.AlarmReceiver;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Relecotech on 10-02-2018.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.DateDialogListener {

    private ArrayList<UserProfileListData> userProfileList;
    private LinearLayoutManager layoutManager;
    private EditProfileAdapter editProfileAdapter;
    private MobileServiceTable<Student> studentMobileServiceTable;
    private MobileServiceTable<Teacher> teacherMobileServiceTable;
    private SessionManager sessionManager;
    private String studentId;
    private String teacherId;
    private EditText editProfile;
    private String userRole;
    private Student studentItem;
    private Teacher teacherItem;
    private Calendar calendar;
    private int year, month, day;
    private String adminId;
    private Admin adminItem;
    private MobileServiceTable<Admin> adminMobileServiceTable;
    private MobileServiceTable<School_Config> schoolConfigMobileServiceTable;
    private List<String> houseColorList;
    private List<String> genderList;
    private List<String> bloodgroupList;
    private Calendar cal;
    private String dob;
    private HashMap<String, String> userDetails;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String studentName;
    private MobileServiceClient mClient;
    private Date dobForCalendar;
    private Calendar birthdayCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        mClient = Singleton.Instance().mClientMethod(this);

        calendar = Calendar.getInstance();

        birthdayCalendar = Calendar.getInstance();

        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);
        studentName = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);

        schoolConfigMobileServiceTable = mClient.getTable(School_Config.class);
        studentMobileServiceTable = mClient.getTable(Student.class);
        teacherMobileServiceTable = mClient.getTable(Teacher.class);
        adminMobileServiceTable = mClient.getTable(Admin.class);

        new FetchConfigData().execute();

        studentItem = new Student();
        teacherItem = new Teacher();
        adminItem = new Admin();

        houseColorList = new ArrayList<>();
        userProfileList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.userEditRecyclerView);
        layoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        recyclerView.setLayoutAnimation(animation);

        if (userRole.equals("Student")) {
            studentId = sessionManager.getUserDetails().get(SessionManager.KEY_STUDENT_ID);
            studentItem = (Student) getIntent().getSerializableExtra("studentObj");
        } else if (userRole.equals("Teacher")) {
            teacherId = sessionManager.getUserDetails().get(SessionManager.KEY_TEACHER_ID);
            teacherItem = (Teacher) getIntent().getSerializableExtra("teacherObj");
        } else {
            adminId = sessionManager.getUserDetails().get(SessionManager.KEY_ADMIN_ID);
            adminItem = (Admin) getIntent().getSerializableExtra("adminObj");
        }

        ArrayList<UserProfileListData> profileListData = new ArrayList<UserProfileListData>();
        profileListData = (ArrayList<UserProfileListData>) getIntent().getSerializableExtra("profileData");


        System.out.println(profileListData.size());
        for (int loop = 0; loop < profileListData.size(); loop++) {
            userProfileList.add(new UserProfileListData(profileListData.get(loop).getTitle(), profileListData.get(loop).getAzureTitle(), profileListData.get(loop).getDescription(), 0, profileListData.get(loop).getEditable()));

            System.out.println("title " + profileListData.get(loop).getTitle());
            System.out.println("Description " + profileListData.get(loop).getDescription());
        }

//        Gson gson = new Gson();
//        String abc = sessionManager.getUserDetails().get(SessionManager.KEY_USERDATA);
//        Student serializedData = gson.fromJson(abc, Student.class);
//        System.out.println(serializedData.getStudentId());

        editProfileAdapter = new EditProfileAdapter(this, userProfileList);
        recyclerView.setAdapter(editProfileAdapter);

        recyclerView.addOnItemTouchListener(new EditProfileAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new EditProfileAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (userProfileList.get(position).getEditable()) {

                    String columnTitle = userProfileList.get(position).getTitle();
                    String columnName = userProfileList.get(position).getAzureTitle();
                    String desc = userProfileList.get(position).getDescription();
                    if (columnName.equals("houseColor")) {
                        pickListDialog(position);
                    } else if (columnName.equals("gender")) {
                        pickGenderDialog(position);
                    } else if (columnName.equals("bloodGrp")) {
                        pickBloodGroup(position);
                    } else {
                        editDialog(position, columnTitle, columnName, desc);
                    }
                } else {
                    FancyToast.makeText(EditProfileActivity.this, "Cannot update this", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void pickGenderDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        genderList = Arrays.asList(getResources().getStringArray(R.array.gender));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        arrayAdapter.addAll(genderList);

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                userProfileList.get(positionOfColumn).setDescription(genderList.get(position));

                if (userRole.equalsIgnoreCase("Teacher")) {
                    teacherItem.setGender(genderList.get(position));
                } else if (userRole.equalsIgnoreCase("Student")) {
                    studentItem.setGender(genderList.get(position));
                }
                editProfileAdapter.notifyDataSetChanged();
                new AsyncTaskRunner().execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pickBloodGroup(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Blood Group");
        bloodgroupList = Arrays.asList(getResources().getStringArray(R.array.bloodGroup));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        arrayAdapter.addAll(bloodgroupList);

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                userProfileList.get(positionOfColumn).setDescription(bloodgroupList.get(position));

                if (userRole.equalsIgnoreCase("Teacher")) {
                    teacherItem.setBloodGrp(bloodgroupList.get(position));
                } else if (userRole.equalsIgnoreCase("Student")) {
                    studentItem.setBloodGrp(bloodgroupList.get(position));
                }
                editProfileAdapter.notifyDataSetChanged();
                new AsyncTaskRunner().execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onFinishDialog(String date) {
        editProfile.setText(date);
        System.out.println(" Date " + date);
    }

    private void pickListDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose House Color");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        for (int i = 0; i < houseColorList.size(); i++) {
            arrayAdapter.add(houseColorList.get(i));
        }

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                userProfileList.get(positionOfColumn).setDescription(houseColorList.get(position));
                studentItem.setHouseColor(houseColorList.get(position));
                editProfileAdapter.notifyDataSetChanged();
                new AsyncTaskRunner().execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class FetchConfigData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
//                schoolConfigResult = schoolConfigMobileServiceTable.execute().get();
                List<School_Config> schoolConfigResult = schoolConfigMobileServiceTable.where().field("Type").eq("HouseColor").and().field("Branch_id").eq(userDetails.get(SessionManager.KEY_BRANCH_ID))
                        .select("Type", "Value", "Branch_id").execute().get();
                System.out.println("schoolConfigResult " + schoolConfigResult);

                for (School_Config item : schoolConfigResult) {
                    System.out.println(" item.getValue() " + item.getValue());
                    houseColorList = Arrays.asList(item.getValue().split("%"));
                }
            } catch (Exception e) {
                System.out.println("null pointer exception *********************");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }

    public void editDialog(final int position, String columnTitle, final String columnName, String description) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_profile_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        editProfile = (EditText) dialogView.findViewById(R.id.edit1);

        System.out.println(" columnName " + columnName);

        if (description != null) {
            editProfile.setText(description);
            editProfile.setSelection(description.length());
        }

        if (columnName.equals("aadharCardNo") || columnName.equals("emergencyContact")) {
            editProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if (columnName.equals("dateOfBirth")) {
            editProfile.setInputType(InputType.TYPE_NULL);
            dobForCalendar = null;
            try {
                if (userRole.equals("Student")) {
                    if (studentItem.getDateOfBirth() == null) {
                        calendar = Calendar.getInstance();
                    } else {
                        dobForCalendar = studentItem.getDateOfBirth();
                    }
                } else if (userRole.equals("Teacher")) {
                    if (teacherItem.getDateOfBirth() == null) {
                        calendar = Calendar.getInstance();
                    } else {
                        dobForCalendar = teacherItem.getDateOfBirth();
                    }
                } else {
                    if (adminItem.getDateOfBirth() == null) {
                        calendar = Calendar.getInstance();
                    } else {
                        dobForCalendar = adminItem.getDateOfBirth();
                    }
                }
                System.out.println(" dobForCalendar " + dobForCalendar);
                calendar.setTime(dobForCalendar);

            } catch (Exception e) {
                System.out.println(" Exception in Dob Calender " + e.getMessage());
            }
            editProfile.setOnClickListener(this);
        }

        dialogView.requestFocus();

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage("Edit " + columnTitle);
        dialogBuilder.setPositiveButton("Save", null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (columnName.equalsIgnoreCase("dateOfBirth")) {

                    userProfileList.get(position).setDescription(editProfile.getText().toString());
                    editProfileAdapter.notifyDataSetChanged();

                    switchCaseForUpdate(columnName, editProfile.getText().toString().trim());
                    dialog.dismiss();

                    Date date1= null;
                    try {
                        date1 = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).parse(editProfile.getText().toString().trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    birthdayCalendar.setTime(date1);
                    birthdayCalendar.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR));

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
                    Intent intentForNotification = new Intent(EditProfileActivity.this, AlarmReceiver.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Notification_Tag", "Birthday");
                    bundle.putString("Notification_Title", "\uD83C\uDF82\uD83C\uDF82 Happy Birthday \uD83C\uDF82\uD83C\uDF82");
                    bundle.putString("Notification_Description", "\uD83C\uDF89\uD83C\uDF89 Happy Birthday to " + studentName + " \uD83C\uDF89\uD83C\uDF89");
                    intentForNotification.putExtras(bundle);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(EditProfileActivity.this, 0, intentForNotification, 0);
                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                    normalCalendar.set(normalCalendar.get(Calendar.YEAR), birthdayCalendar.get(Calendar.MONTH), birthdayCalendar.get(Calendar.DAY_OF_MONTH), 9, 0);
                    System.out.println("EditProfileActivity normalCalendar " + normalCalendar.getTime());
                    System.out.println("EditProfileActivity normalCalendar getTimeInMillis " + normalCalendar.getTimeInMillis());
                    if (normalCalendar.getTimeInMillis() >= System.currentTimeMillis()) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, normalCalendar.getTimeInMillis(), pendingIntent);
                    }


                } else if (columnName.equalsIgnoreCase("Email")) {
                    if (editProfile.getText().toString().trim().matches(emailPattern) && editProfile.getText().toString().trim().length() > 0) {
                        userProfileList.get(position).setDescription(editProfile.getText().toString());
                        editProfileAdapter.notifyDataSetChanged();

                        switchCaseForUpdate(columnName, editProfile.getText().toString().trim());
                        dialog.dismiss();
                    } else {
                        editProfile.setError("Invalid email");
                    }
                } else if (columnName.equals("aadharCardNo")) {
                    if (editProfile.getText().length() == 12) {
                        userProfileList.get(position).setDescription(editProfile.getText().toString());
                        editProfileAdapter.notifyDataSetChanged();

                        switchCaseForUpdate(columnName, editProfile.getText().toString());
                        dialog.dismiss();
                    } else {
                        editProfile.setError("Please Enter Proper Aadhaar Number");
                    }
                } else if (columnName.equals("emergencyContact")) {
                    if (editProfile.getText().length() == 10) {
                        userProfileList.get(position).setDescription(editProfile.getText().toString());
                        editProfileAdapter.notifyDataSetChanged();

                        switchCaseForUpdate(columnName, editProfile.getText().toString());
                        dialog.dismiss();
                    } else {
                        editProfile.setError("Please Enter Proper Number");
                    }
                } else {
                    userProfileList.get(position).setDescription(editProfile.getText().toString());
                    editProfileAdapter.notifyDataSetChanged();

                    switchCaseForUpdate(columnName, editProfile.getText().toString());
                    dialog.dismiss();
                }

            }
        });
    }

    private void switchCaseForUpdate(String columnName, String value) {
        System.out.println("columnName " + columnName);
        System.out.println("value " + value);

        if (userRole.equals("Student")) {
            studentItem.setId(studentId);
        } else if (userRole.equals("Teacher")) {
            teacherItem.setId(teacherId);
        } else {
            adminItem.setId(adminId);
        }

        switch (columnName) {
            case "middleName":
                if (userRole.equals("Student")) {
                    studentItem.setMiddleName(value);
                } else {
                    teacherItem.setMiddleName(value);
                }
                break;
            case "address":
                if (userRole.equals("Student")) {
                    studentItem.setAddress(value);
                } else {
                    teacherItem.setAddress(value);
                }
                break;
            case "phone":
                if (userRole.equals("Student")) {
                    studentItem.setPhone(value);
                } else if (userRole.equals("Teacher")) {
                    teacherItem.setPhone(value);
                } else {
                    adminItem.setPhone(value);
                }
                break;
            case "dateOfBirth":
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//                format.setTimeZone(TimeZone.getDefault());
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = null;
                try {
                    date = format.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (userRole.equals("Student")) {
                    studentItem.setDateOfBirth(date);
                } else if (userRole.equals("Teacher")) {
                    teacherItem.setDateOfBirth(date);
                } else {
                    adminItem.setDateOfBirth(date);
                }

                break;
            case "gender":
                if (userRole.equals("Student")) {
                    studentItem.setGender(value);
                } else {
                    teacherItem.setGender(value);
                }
                break;
            case "bloodGrp":
                if (userRole.equals("Student")) {
                    studentItem.setBloodGrp(value);
                } else {
                    teacherItem.setBloodGrp(value);
                }
                break;
            case "email":
                if (userRole.equals("Student")) {
                    studentItem.setEmail(value);
                } else {
                    teacherItem.setEmail(value);
                }
                break;
            case "bus_id":
                if (userRole.equals("Student")) {
                    studentItem.setBus_id(value);
                } else {
                    teacherItem.setBus_id(value);
                }
                sessionManager.setSharedPrefItem(SessionManager.KEY_BUS_ID, value);
                break;
            case "busPickUpPoint":
                if (userRole.equals("Student")) {
                    studentItem.setBusPickUpPoint(value);
                } else {
                    teacherItem.setBusPickUpPoint(value);
                }
                break;
            case "nationality":
                if (userRole.equals("Student")) {
                    studentItem.setNationality(value);
                } else {
                    teacherItem.setNationality(value);
                }
                break;
            case "aadharCardNo":
                if (userRole.equals("Student")) {
                    studentItem.setAadharCardNo(value);
                } else if (userRole.equals("Teacher")) {
                    teacherItem.setAadharCardNo(value);
                } else {
                    adminItem.setAadharCardNo(value);
                }
                break;
            case "rollNo":
                studentItem.setRollNo(value);
                break;
//            case "houseColor":
//                studentItem.setHouseColor(value);
//                break;
            case "hostelStudents":
                studentItem.setHostelStudents(value);
                break;
            case "motherName":
                studentItem.setMotherName(value);
                break;
            case "caste":
                studentItem.setCaste(value);
                break;
            case "religion":
                studentItem.setReligion(value);
                break;
            case "category":
                studentItem.setCategory(value);
                break;
            case "information":
                studentItem.setInformation(value);
                break;
            case "illness":
                studentItem.setIllness(value);
                break;
            case "emergencyContact":
                studentItem.setEmergencyContact(value);
                break;
            case "specialInfo":
                studentItem.setSpecialInfo(value);
                break;


            case "qualification":
                if (userRole.equals("Teacher")) {
                    teacherItem.setQualification(value);
                } else {
                    adminItem.setQualification(value);
                }
                break;

            case "speciality":
                if (userRole.equals("Teacher")) {
                    teacherItem.setSpeciality(value);
                }
                break;
            case "designation":
                if (userRole.equals("Teacher")) {
                    teacherItem.setDesignation(value);
                } else {
                    adminItem.setDesignation(value);
                }
                break;

            default:
                System.out.println("Not in 10, 20 or 30");
        }
        new AsyncTaskRunner().execute();
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (userRole.equals("Student")) {
                    studentItem.setBranch_id(userDetails.get(SessionManager.KEY_BRANCH_ID));
                    studentItem.setUsers_id(userDetails.get(SessionManager.KEY_USER_ID));
                    studentItem.setSchool_Class_id(userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
                    Student student = studentMobileServiceTable.update(studentItem).get();
                } else if (userRole.equals("Teacher")) {
                    teacherItem.setBranch_id(userDetails.get(SessionManager.KEY_BRANCH_ID));
                    teacherItem.setUsers_id(userDetails.get(SessionManager.KEY_USER_ID));
                    Teacher teacher = teacherMobileServiceTable.update(teacherItem).get();
                } else {
                    Admin admin = adminMobileServiceTable.update(adminItem).get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == editProfile) {
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            if (editProfile.getText().length() == 0) {
                cal = Calendar.getInstance();
                dob = format1.format(cal.getTime());
                System.out.println(" Inside if ");
            } else {
                System.out.println(" Inside else");
                dob = editProfile.getText().toString();
            }

            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("dob", dob);
            bundle.putString("value", "lower");
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "datePicker");
//            // Get Current Date
//            int mYear = calendar.get(Calendar.YEAR);
//            int mMonth = calendar.get(Calendar.MONTH);
//            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                    new DatePickerDialog.OnDateSetListener() {
//
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                            editProfile.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                        }
//                    }, mYear, mMonth, mDay) {
//                @Override
//                public void onDateChanged(DatePicker view, int Dyear, int DmonthOfYear, int DdayOfMonth) {
//                    if (Dyear > year)
//                        view.updateDate(year, month, day);
//
//                    if (DmonthOfYear > month && Dyear == year)
//                        view.updateDate(year, month, day);
//
//                    if (DdayOfMonth > day && Dyear == year && DmonthOfYear == month)
//                        view.updateDate(year, month, day);
//
//                }
//            };
//            ;
//            datePickerDialog.show();
        }
    }
}
