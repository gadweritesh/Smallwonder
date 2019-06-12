package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;

/**
 * Created by Relecotech on 20-03-2018.
 */

public class School_Class implements Serializable {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("class")
    String schoolClass;
    @com.google.gson.annotations.SerializedName("division")
    String division;
    @com.google.gson.annotations.SerializedName("Teacher_id")
    String Teacher_id;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        Teacher_id = teacher_id;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }
}
