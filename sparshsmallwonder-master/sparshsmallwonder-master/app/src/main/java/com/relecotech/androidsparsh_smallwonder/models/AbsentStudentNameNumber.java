package com.relecotech.androidsparsh_smallwonder.models;

public class AbsentStudentNameNumber {

    String name;
    String mobileNo;

    public AbsentStudentNameNumber(String name, String mobileNo) {
        this.name = name;
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
