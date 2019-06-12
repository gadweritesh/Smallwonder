package com.relecotech.androidsparsh_smallwonder.utils;

/**
 * Created by Relecotech on 31-01-2018.
 */

public class UserData {


    String userId;
    String fullName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentOrTeacherId() {
        return studentOrTeacherId;
    }

    public void setStudentOrTeacherId(String studentOrTeacherId) {
        this.studentOrTeacherId = studentOrTeacherId;
    }

    public String getSchoolClassId() {
        return schoolClassId;
    }

    public void setSchoolClassId(String schoolClassId) {
        this.schoolClassId = schoolClassId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    String studentOrTeacherId;
    String schoolClassId;
    String branchId;

    public UserData() {
    }

    public UserData(String userId, String fullName, String studentOrTeacherId, String schoolClassId, String branchId) {
        this.userId = userId;
        this.fullName = fullName;
        this.studentOrTeacherId = studentOrTeacherId;
        this.schoolClassId = schoolClassId;
        this.branchId = branchId;
    }
}
