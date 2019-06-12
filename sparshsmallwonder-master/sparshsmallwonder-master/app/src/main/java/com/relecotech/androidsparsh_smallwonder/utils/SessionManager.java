package com.relecotech.androidsparsh_smallwonder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {

    // All Shared Preferences Keys
    public static final String PREF_NAME = "appPreferences";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_NAME = "name";
    public static final String KEY_SCHOOL_CLASS_ID = "schoolClassId";
    public static final String KEY_BRANCH_ID = "branchId";
    public static final String KEY_STUDENT_ID = "studentId";
    public static final String KEY_TEACHER_ID = "teacherId";
    public static final String KEY_ADMIN_ID = "adminId";
    public static final String KEY_BRANCH_NAME = "branchName";

    public static final String KEY_PARENT_PIN = "parentZonePin";
    public static final String KEY_PARENT_PIN_BOOLEAN = "parentZonePinl";
    public static final String KEY_CLASS = "class";
    public static final String KEY_DIVISION = "division";
    public static final String KEY_DESIGNATION = "designation";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ROLE = "userRole";

    public static final String KEY_ACTIVE = "active";
    public static final String KEY_USERDATA = "data";
    public static final String KEY_NAME_LIST = "nameList";
    public static final String KEY_NAME_LIST2 = "nameList2";
    //Used for Assignment Reminder
    public static final String KEY_REMINDER = "reminder";
    public static final String KEY_NOTIFICATION_SWITCH = "notificationSwitch";

    public static final String KEY_LATEST_ATTENDANCE_MARK_DATE = "latestAttendanceDate";


    public static final String KEY_NOTIFICATION_COUNT = "notificationCount";
    public static final String KEY_USER_PROFILE_IMAGE_NAME = "userProfileImageName";
    public static final String KEY_BUS_ID = "busId";



    SharedPreferences pref ;
    SharedPreferences.Editor editor;
    Context _context;
    // Shared pref mode
    public static int PRIVATE_MODE = 0;
    private HashMap<String, String> user;


    public SessionManager(Context context) {
        this._context = context;

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public SessionManager(Context context, String preferenceName) {
        this._context = context;
        System.out.println("preferenceName  " + preferenceName);
        pref = _context.getSharedPreferences(preferenceName, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    //    public void createLoginSession(String userId,String fullName, UserData userData) {
    public void createLoginSession(String userId, String branchId, String schoolClassId, String fullName, String studentId, String teacherId, String adminId, String userRole, String branchName, String pin, String clas, String div,String designation) {

//        Gson gson = new Gson();

//        String serializedData = gson.toJson(userData);
//        System.out.println(" serializedData " + serializedData);
//        System.out.println(" serializedData 2  " + gson.fromJson(serializedData, UserData.class));
//        editor.putString(KEY_USERDATA, serializedData);

        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_BRANCH_ID, branchId);
        editor.putString(KEY_SCHOOL_CLASS_ID, schoolClassId);
        editor.putString(KEY_NAME, fullName);
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_TEACHER_ID, teacherId);
        editor.putString(KEY_ADMIN_ID, adminId);
        editor.putString(KEY_USER_ROLE, userRole);
        editor.putString(KEY_BRANCH_NAME, branchName);
        editor.putString(KEY_PARENT_PIN, pin);
        editor.putString(KEY_CLASS, clas);
        editor.putString(KEY_DIVISION, div);
        editor.putString(KEY_DESIGNATION, designation);

        editor.putString(KEY_LATEST_ATTENDANCE_MARK_DATE, null);

        editor.putString(KEY_REMINDER, "18:00");
        editor.putBoolean(KEY_NOTIFICATION_SWITCH, true);
        editor.putBoolean(KEY_PARENT_PIN_BOOLEAN, false);
        editor.commit();
    }

    public void createList2(ArrayList<String> fullName2) {

        Gson gson = new Gson();
        String nameSerializedList = gson.toJson(fullName2);
        System.out.println(" serializedData...2 " + nameSerializedList);

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME_LIST2, nameSerializedList);

        editor.commit();
    }
    public void createList(ArrayList<String> fullName) {

        Gson gson = new Gson();

        String nameSerializedList = gson.toJson(fullName);
        System.out.println(" serializedData " + nameSerializedList);

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME_LIST, nameSerializedList);
        editor.commit();
    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public HashMap<String, String> getUserDetails() {
        user = new HashMap<String, String>();

        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_BRANCH_ID, pref.getString(KEY_BRANCH_ID, null));
        user.put(KEY_SCHOOL_CLASS_ID, pref.getString(KEY_SCHOOL_CLASS_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_STUDENT_ID, pref.getString(KEY_STUDENT_ID, null));
        user.put(KEY_TEACHER_ID, pref.getString(KEY_TEACHER_ID, null));
        user.put(KEY_ADMIN_ID, pref.getString(KEY_ADMIN_ID, null));
        user.put(KEY_USER_ROLE, pref.getString(KEY_USER_ROLE, null));
        user.put(KEY_BRANCH_NAME, pref.getString(KEY_BRANCH_NAME, null));
        user.put(KEY_PARENT_PIN, pref.getString(KEY_PARENT_PIN, null));
        user.put(KEY_CLASS, pref.getString(KEY_CLASS, null));
        user.put(KEY_DIVISION, pref.getString(KEY_DIVISION, null));
        user.put(KEY_DESIGNATION, pref.getString(KEY_DESIGNATION, null));

//        user.put(KEY_USERDATA, pref.getString(KEY_USERDATA, null));

        user.put(KEY_NAME_LIST, pref.getString(KEY_NAME_LIST, null));
        user.put(KEY_NAME_LIST2, pref.getString(KEY_NAME_LIST2, null));
        user.put(KEY_LATEST_ATTENDANCE_MARK_DATE, pref.getString(KEY_LATEST_ATTENDANCE_MARK_DATE, null));

        user.put(KEY_NOTIFICATION_COUNT, pref.getString(KEY_NOTIFICATION_COUNT, null));

        return user;
    }


    //gives single value from shared pref
    public String getSharedPrefItem(String dataKEY) {
        return pref.getString(dataKEY, null);
    }

    //Inserts single value into shared pref
    public void setSharedPrefItem(String dataKEY, String dataValue) {
        System.out.println(" setSharedPrefItem dataKEY " + dataKEY + " dataValue " + dataValue);
        editor.putString(dataKEY, dataValue);
        editor.commit();
    }

    public void setBooleanSharedPrefItem(String dataKEY, Boolean dataValue) {
        System.out.println(" setBooleanSharedPrefItem dataKEY " + dataKEY + " dataValue " + dataValue);
        editor.putBoolean(dataKEY, dataValue);
        editor.commit();
    }

    public Boolean getBooleanSharedPrefItem(String dataKEY) {
        return pref.getBoolean(dataKEY, true);
    }


    public void updateLoginSession(String fullName, String schoolClassId, String clas, String div, String branchId) {

        editor.putString(KEY_NAME, fullName);
        editor.putString(KEY_SCHOOL_CLASS_ID, schoolClassId);
        editor.putString(KEY_CLASS, clas);
        editor.putString(KEY_DIVISION, div);
        editor.putString(KEY_BRANCH_ID, branchId);

        editor.commit();
    }


}