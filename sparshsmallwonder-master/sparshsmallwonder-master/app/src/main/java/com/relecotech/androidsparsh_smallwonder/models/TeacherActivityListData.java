package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Amey on 07-06-2018.
 */

public class TeacherActivityListData {
    private String TeacherName;
    private String ActivityClass;
    private String Teacherid;


    public TeacherActivityListData(String teacherName, String activityClass, String teacherid) {
        TeacherName = teacherName;
        ActivityClass = activityClass;
        Teacherid = teacherid;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        TeacherName = teacherName;
    }

    public String getActivityClass() {
        return ActivityClass;
    }

    public void setActivityClass(String activityClass) {
        ActivityClass = activityClass;
    }

    public String getTeacherid() {
        return Teacherid;
    }

    public void setTeacherid(String teacherid) {
        Teacherid = teacherid;
    }
}
