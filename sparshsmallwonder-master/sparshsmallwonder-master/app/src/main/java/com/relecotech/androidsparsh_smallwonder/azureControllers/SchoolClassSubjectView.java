package com.relecotech.androidsparsh_smallwonder.azureControllers;

public class SchoolClassSubjectView {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("subject")
    String subject;
    @com.google.gson.annotations.SerializedName("class")
    String schoolClass;
    @com.google.gson.annotations.SerializedName("division")
    String division;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

}

