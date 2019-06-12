package com.relecotech.androidsparsh_smallwonder.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.relecotech.androidsparsh_smallwonder.models.AttendanceMarkListData;
import com.relecotech.androidsparsh_smallwonder.models.NotificationListData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Relecotech on 26-03-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SparshAppDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String CLASS_DIVISION_TABLE = "classDivisionTable";
    private static final String SCHOOL_CLASS_ID = "schoolClassId";
    private static final String KEY_CLASS = "class";
    private static final String KEY_DIVISION = "division";
    private static final String KEY_SUBJECT = "subject";

    private static final String TABLE_ATTENDANCE = "Attendance";

    private static final String KEY_ATTENDANCE_ID = "id";
    private static final String KEY_ATTENDANCE_CLASS_ID = "attendanceClassid";
    private static final String KEY_ATTENDANCE_STUDENT_ID = "attendanceStudentId";
    private static final String KEY_ATTENDANCE_STUDENT_FIRST_NAME = "attendanceStudentFirstName";
    private static final String KEY_ATTENDANCE_STUDENT_FULL_NAME = "attendanceStudentFullName";
    private static final String KEY_ATTENDANCE_STUDENT_ROLL_NO = "attendanceStudentRollNo";
    private static final String KEY_ATTENDANCE_STATUS = "attendanceStatus";
    private static final String KEY_ATTENDANCE_DATE = "attendanceDate";
    private static final String KEY_ATTENDANCE_DRAFT_STATUS = "attendanceDraftStatus";
    private static final String KEY_ATTENDANCE_STUDENT_PHONENO = "attendanceStudentPhone";


    //NOTIFICATION TABLE COLUMNS
    private static final String TABLE_NOTIFICATION = "Notification";

    private static final String KEY_NOTIFICATION_ID = "id";
    private static final String KEY_NOTIFICATION_CATEGORY = "notificationCategory";
    private static final String KEY_NOTIFICATION_ASSIGNMENT_IDS = "notificationAssignmentId";
    private static final String KEY_NOTIFICATION_TITLE = "notificationTitle";
    private static final String KEY_NOTIFICATION_MESSAGE = "notificationMessage";
    private static final String KEY_NOTIFICATION_SUBMITTED_BY = "notificationSubmittedBy";
    private static final String KEY_NOTIFICATION_ASSMNT_DUE_DATE = "notificationAssmntDueDate";
    private static final String KEY_NOTIFICATION_POST_DATE = "notificationPostDate";

    //Headline TABLE COLUMNS
    private static final String TABLE_HEADLINE = "Headline";

    public static final String KEY_HEADLINE_ID = "id";
    public static final String KEY_HEADLINE_TITLE = "title";
    public static final String KEY_HEADLINE_END_DATE = "endDate";
    public static final String KEY_HEADLINE_ACTIVE = "active";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + "("
                + KEY_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NOTIFICATION_CATEGORY + " TEXT," +
                KEY_NOTIFICATION_ASSIGNMENT_IDS + " TEXT," + KEY_NOTIFICATION_TITLE
                + " TEXT," + KEY_NOTIFICATION_MESSAGE + " TEXT," + KEY_NOTIFICATION_SUBMITTED_BY + " TEXT," + KEY_NOTIFICATION_ASSMNT_DUE_DATE + " TEXT," + KEY_NOTIFICATION_POST_DATE + " TEXT" + ")";


        String CREATE_TABLE_CLASS_DIVISION = "CREATE TABLE IF NOT EXISTS " + CLASS_DIVISION_TABLE + "("
                + SCHOOL_CLASS_ID + " TEXT," + KEY_CLASS + " TEXT ," + KEY_DIVISION + " TEXT ," + KEY_SUBJECT + " TEXT" + ", unique (class, division,subject))";

        String CREATE_TABLE_ATTENDANCE = "CREATE TABLE IF NOT EXISTS " + TABLE_ATTENDANCE + "(" +
                KEY_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                KEY_ATTENDANCE_CLASS_ID + " TEXT," +
                KEY_ATTENDANCE_STUDENT_ID + " TEXT," +
                KEY_ATTENDANCE_STUDENT_FIRST_NAME + " TEXT," +
                KEY_ATTENDANCE_STUDENT_FULL_NAME + " TEXT," +
                KEY_ATTENDANCE_STUDENT_ROLL_NO + " TEXT," +
                KEY_ATTENDANCE_DATE + " TEXT," +
                KEY_ATTENDANCE_STATUS + " TEXT," +
                KEY_ATTENDANCE_STUDENT_PHONENO + " TEXT," +
                KEY_ATTENDANCE_DRAFT_STATUS + " INTEGER)";

        String CREATE_TABLE_HEADLINE = "CREATE TABLE IF NOT EXISTS " + TABLE_HEADLINE + "(" + KEY_HEADLINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_HEADLINE_TITLE + " TEXT," +
                KEY_HEADLINE_END_DATE + " TEXT," +
                KEY_HEADLINE_ACTIVE + " INTEGER)";

        sqLiteDatabase.execSQL(CREATE_TABLE_NOTIFICATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_CLASS_DIVISION);
        sqLiteDatabase.execSQL(CREATE_TABLE_ATTENDANCE);
        sqLiteDatabase.execSQL(CREATE_TABLE_HEADLINE);

        System.out.println("CREATE_TABLE_CLASS_DIVISION : " + CREATE_TABLE_CLASS_DIVISION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CLASS_DIVISION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HEADLINE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HEADLINE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addClassToDatabase(String schoolClassId, String clazz, String division, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues classValues = new ContentValues();
        classValues.put(SCHOOL_CLASS_ID, schoolClassId);
        classValues.put(KEY_CLASS, clazz);
        classValues.put(KEY_DIVISION, division);
        classValues.put(KEY_SUBJECT, subject);
        try {
            db.insertOrThrow(CLASS_DIVISION_TABLE, null, classValues);
        } catch (SQLException e) {
            System.out.println("Unique constraint " + e.getMessage());
        }
        db.close();
    }

    public Cursor getClassDataByCursor() {
        String selectQuery = "SELECT  * FROM " + CLASS_DIVISION_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    public Cursor getClassDivSubjectData(HashMap hashMap) {
        String selectQuery = "SELECT  * FROM " + CLASS_DIVISION_TABLE + " WHERE ";
        Map<String, String> map = hashMap;
        Boolean check_single_para = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (check_single_para) {
                selectQuery = selectQuery + entry.getKey() + " = '" + entry.getValue() + "'";
                check_single_para = false;
            } else {
                selectQuery = selectQuery + " AND " + entry.getKey() + " = '" + entry.getValue() + "'";
            }
        }
        map.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }


    //get att attendance data
    public Cursor getAllAttendanceData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE, null);
    }

    //Deleting single record
    public void deleteAttendanceRecords(String classId, String attendancedate, int draftStatus) {

        String deleteWhereArgs = KEY_ATTENDANCE_CLASS_ID + " = '" + classId + "' AND " + KEY_ATTENDANCE_DATE
                + " = '" + attendancedate + "' AND " + KEY_ATTENDANCE_DRAFT_STATUS + " = '" + draftStatus + "'";

        //this condition is to change param string when date = null.
        //bcoz sqlite will get 'null' (string) rather than  null (value).
        if (attendancedate == null) {
            deleteWhereArgs = KEY_ATTENDANCE_CLASS_ID + " = '" + classId + "' AND " + KEY_ATTENDANCE_DRAFT_STATUS + " = '" + draftStatus + "'";
        }

        SQLiteDatabase deleteObj = this.getReadableDatabase();
        int deleteResponse = deleteObj.delete(TABLE_ATTENDANCE, deleteWhereArgs, null);

        deleteObj.close();
    }

    //ADD student name list without
    public void addStudentNamesToDatabase(ArrayList<AttendanceMarkListData> attendancelist, String classId, String attendanceDate, int draftStatus) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues attendanceValues;

        for (AttendanceMarkListData loopData : attendancelist) {
            attendanceValues = new ContentValues();

            attendanceValues.put(KEY_ATTENDANCE_CLASS_ID, classId);
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_ID, loopData.getStudentId());
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_ROLL_NO, loopData.getRollNo());
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_FULL_NAME, loopData.getFullName());
            attendanceValues.put(KEY_ATTENDANCE_STATUS, loopData.getPresentStatus());
            attendanceValues.put(KEY_ATTENDANCE_DRAFT_STATUS, draftStatus);
            attendanceValues.put(KEY_ATTENDANCE_DATE, attendanceDate);
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_PHONENO, loopData.getStudentPhone());
            System.out.println("attendance looping ");
            // Inserting Row
            long newInsertResponse = db.insert(TABLE_ATTENDANCE, null, attendanceValues);
            System.out.println("newInsertResponse : " + newInsertResponse);
        }

        db.close(); // Closing database connection
    }


    //get student name list for a class
    public Cursor getStudentNames(String schoolClassId) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + KEY_ATTENDANCE_CLASS_ID + " = '" + schoolClassId + "'";
        System.out.println("SINGLE PARAM getStudentNames selectQuery : " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    //get student name list for a class
    public Cursor getStudentNames(String schoolClassId, String attendanceDate) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + KEY_ATTENDANCE_CLASS_ID + " = '" + schoolClassId +
                "' AND " + KEY_ATTENDANCE_DRAFT_STATUS + " = 1 AND " + KEY_ATTENDANCE_DATE + " = '" + attendanceDate + "'";
        System.out.println("MULTI PARAM getStudentNames selectQuery : " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    //method to get draft classes with their date
    public Cursor geAttendanceDraftMetaData() {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("  geAttendanceDraftMetaData  KEY_ATTENDANCE_DATE " + KEY_ATTENDANCE_DATE);
        System.out.println("  geAttendanceDraftMetaData  KEY_ATTENDANCE_CLASS_ID " + KEY_ATTENDANCE_CLASS_ID);
        System.out.println("  geAttendanceDraftMetaData  TABLE_ATTENDANCE " + TABLE_ATTENDANCE);
        return db.rawQuery("SELECT DISTINCT " + KEY_ATTENDANCE_DATE + " , " +
                KEY_ATTENDANCE_CLASS_ID + " FROM " +
                TABLE_ATTENDANCE + " WHERE " + KEY_ATTENDANCE_DATE + " IS NOT NULL", null);
    }

    public Cursor getClassDivisionByClassId(String classId) {
        // Select All Query
        String selectQuery = "SELECT " + KEY_CLASS + "," + KEY_DIVISION +
                " FROM " + CLASS_DIVISION_TABLE + " WHERE " + SCHOOL_CLASS_ID + " = '" + classId + "'";

//        String selectQuery = "SELECT " + KEY_TEACHER_CLASS_CLASS + " , " + KEY_TEACHER_CLASS_DIVISION +
//                " FROM " + TEACHER_CLASS_TABLE + " WHERE " + KEY_TEACHER_CLASS_SCHOOL_CLASS_ID + " = '" + classId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("getClassDivisionByClassId cursor : " + cursor.getCount());
        return cursor;
    }

    public void addNotificationToDatabase(NotificationListData noticeListData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues notificationValues = new ContentValues();

        notificationValues.put(KEY_NOTIFICATION_CATEGORY, noticeListData.getNotifaction_Tag());
        notificationValues.put(KEY_NOTIFICATION_ASSIGNMENT_IDS, noticeListData.getNotifaction_Assignment_Id());
        notificationValues.put(KEY_NOTIFICATION_TITLE, noticeListData.getNotifaction_Tag());
        notificationValues.put(KEY_NOTIFICATION_MESSAGE, noticeListData.getNotification_Message_Body());
        notificationValues.put(KEY_NOTIFICATION_SUBMITTED_BY, noticeListData.getNotifaction_SubmittedBy());
        notificationValues.put(KEY_NOTIFICATION_ASSMNT_DUE_DATE, noticeListData.getNotifaction_Assignment_Due_Date());
        notificationValues.put(KEY_NOTIFICATION_POST_DATE, noticeListData.getNotifaction_Post_Date());

        // Inserting Row
        db.insert(TABLE_NOTIFICATION, null, notificationValues);
        db.close(); // Closing database connection
    }

    public Cursor getAllNotificationDataByCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION;
        return db.rawQuery(selectQuery, null);
    }

    // Date as string becoz sqlite nto support Date format
    public void addHeadlineToDatabase(String title, String dateValue, int value) {
        SQLiteDatabase db = this.getWritableDatabase();

//        String selectQuery = "UPDATE " + TABLE_HEADLINE + " SET " + KEY_HEADLINE_ACTIVE + "=0";
//        db.rawQuery(selectQuery, null);

        System.out.println(" addHeadlineToDatabase title " + title);
        System.out.println(" addHeadlineToDatabase dateValue " + dateValue);
        System.out.println(" addHeadlineToDatabase value " + value);
        ContentValues classValues = new ContentValues();
        classValues.put(KEY_HEADLINE_TITLE, title);
        classValues.put(KEY_HEADLINE_END_DATE, dateValue);
        classValues.put(KEY_HEADLINE_ACTIVE, value);

        db.insert(TABLE_HEADLINE, null, classValues);
        db.close();
    }

    public Cursor getHeadlineTitle() {
        SQLiteDatabase db = this.getWritableDatabase();
//        String selectQuery = "SELECT * FROM " + TABLE_HEADLINE + " Where " + KEY_HEADLINE_ACTIVE + "=1";
        String selectQuery = "SELECT * FROM " + TABLE_HEADLINE + " ORDER BY id DESC LIMIT 1";
        System.out.println("selectQuery " + selectQuery);
        return db.rawQuery(selectQuery, null);
    }

    public void deleteAllTables() {

        SQLiteDatabase deleteObj = this.getReadableDatabase();
        deleteObj.delete(TABLE_NOTIFICATION, null, null);
        deleteObj.delete(CLASS_DIVISION_TABLE, null, null);
        deleteObj.delete(TABLE_ATTENDANCE, null, null);
        deleteObj.delete(TABLE_HEADLINE, null, null);
        deleteObj.close();
    }
}
