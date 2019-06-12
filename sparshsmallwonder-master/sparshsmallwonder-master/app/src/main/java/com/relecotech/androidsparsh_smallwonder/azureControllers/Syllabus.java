package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.util.Date;

/**
 * Created by Amey on 12-05-2018.
 */

public class Syllabus {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("syllabusSubject")
    String syllabusSubject;
    @com.google.gson.annotations.SerializedName("syllabusDescription")
    String syllabusDescription;
    @com.google.gson.annotations.SerializedName("School_Class_id")
    String School_Class_id;
    @com.google.gson.annotations.SerializedName("Uploader_id")
    String Uploader_id;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;
    @com.google.gson.annotations.SerializedName("syllabusPostDate")
    Date syllabusPostDate;

    @com.google.gson.annotations.SerializedName("attachmentIdentifier")
    String attachmentIdentifier;
    @com.google.gson.annotations.SerializedName("attachmentCount")
    int attachmentCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSyllabusSubject() {
        return syllabusSubject;
    }

    public void setSyllabusSubject(String syllabusSubject) {
        this.syllabusSubject = syllabusSubject;
    }

    public String getSyllabusDescription() {
        return syllabusDescription;
    }

    public void setSyllabusDescription(String syllabusDescription) {
        this.syllabusDescription = syllabusDescription;
    }

    public String getSchool_Class_id() {
        return School_Class_id;
    }

    public void setSchool_Class_id(String school_Class_id) {
        School_Class_id = school_Class_id;
    }

    public String getUploader_id() {
        return Uploader_id;
    }

    public void setUploader_id(String uploader_id) {
        Uploader_id = uploader_id;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }

    public Date getSyllabusPostDate() {
        return syllabusPostDate;
    }

    public void setSyllabusPostDate(Date syllabusPostDate) {
        this.syllabusPostDate = syllabusPostDate;
    }

    public String getAttachmentIdentifier() {
        return attachmentIdentifier;
    }

    public void setAttachmentIdentifier(String attachmentIdentifier) {
        this.attachmentIdentifier = attachmentIdentifier;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
}
