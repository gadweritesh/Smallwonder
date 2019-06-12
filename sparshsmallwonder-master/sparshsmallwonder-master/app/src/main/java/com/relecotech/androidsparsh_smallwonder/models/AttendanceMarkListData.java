package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceMarkListData {
    String studentId;
    String rollNo;
    String firstName;
    String fullName;
    String presentStatus;
    String studentPhone;

    public AttendanceMarkListData(String studentId, String rollNo, String firstName, String fullName, String presentStatus, String studentPhone) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.firstName = firstName;
        this.fullName = fullName;
        this.presentStatus = presentStatus;
        this.studentPhone = studentPhone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }
}
