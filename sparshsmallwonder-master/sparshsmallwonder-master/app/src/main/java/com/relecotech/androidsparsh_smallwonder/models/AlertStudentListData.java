package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Relecotech on 21-03-2018.
 */

public class AlertStudentListData {
        String studentId;
        String fullName;
        String studentPhone;
        String studentRollNo;
        boolean selected = false;

    public AlertStudentListData(String studentId, String fullName, String studentPhone, String studentRollNo, boolean selected) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.studentPhone = studentPhone;
        this.studentRollNo = studentRollNo;
        this.selected = selected;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
