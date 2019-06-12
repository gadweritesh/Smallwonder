package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Amey on 12-04-2018.
 */

public class AttendanceReviewListData {

    String fName;
    String mName;
    String lName;
    String presentStatus;
    String rollNo;
//    int nullId = 0;


    public AttendanceReviewListData(String fName, String mName, String lName, String presentStatus, String rollNo) {
        this.fName = fName;
        this.mName = mName;
        this.lName = lName;
        this.presentStatus = presentStatus;
        this.rollNo = rollNo;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
}

