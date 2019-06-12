package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;
import java.util.Date;

public class Alert implements Serializable {
    @com.google.gson.annotations.SerializedName("id")
    String alertId;
    @com.google.gson.annotations.SerializedName("alertTitle")
    String title;
    @com.google.gson.annotations.SerializedName("alertDescription")
    String description;
    @com.google.gson.annotations.SerializedName("alertPostDate")
    Date postDate;
    @com.google.gson.annotations.SerializedName("alertCategory")
    String category;
    @com.google.gson.annotations.SerializedName("Student_id")
    String alertStudId;
    @com.google.gson.annotations.SerializedName("alertClass")
    String alertClass;
    @com.google.gson.annotations.SerializedName("alertDivision")
    String alertDivision;
    @com.google.gson.annotations.SerializedName("alertAttachmentCount")
    String attachmentCount;
    @com.google.gson.annotations.SerializedName("alertPriority")
    String alert_priority;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;

    String alertStudent;

    String submitted_By_to;

    int likeCount;

    private boolean likeCheck;

    String alertAttachmentIdentifier;

    private Boolean editable;


    public Alert(){

    }

    public Alert(String alertId, String title, String description, Date postDate, String category, String alertStudent, String alertStudId, String alertClass, String alertDivision, String submitted_By_to, String attachmentCount, String alert_priority, int likeCount, boolean likeCheck, String alertAttachmentIdentifier, boolean editable) {
        this.alertId = alertId;
        this.title = title;
        this.description = description;
        this.postDate = postDate;
        this.category = category;
        this.alertStudent = alertStudent;
        this.alertStudId = alertStudId;
        this.alertClass = alertClass;
        this.alertDivision = alertDivision;
        this.submitted_By_to = submitted_By_to;
        this.attachmentCount = attachmentCount;
        this.alert_priority = alert_priority;
        this.alertAttachmentIdentifier = alertAttachmentIdentifier;
        this.editable = editable;
        this.likeCount = likeCount;
        this.likeCheck = likeCheck;
    }

    public Alert(String alertTitle, String alertDescription, boolean editable) {
        this.title = alertTitle;
        this.description = alertDescription;
        this.editable = editable;
    }


    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAlertStudent() {
        return alertStudent;
    }

    public void setAlertStudent(String alertStudent) {
        this.alertStudent = alertStudent;
    }

    public String getAlertStudId() {
        return alertStudId;
    }

    public void setAlertStudId(String alertStudId) {
        this.alertStudId = alertStudId;
    }

    public String getAlertClass() {
        return alertClass;
    }

    public void setAlertClass(String alertClass) {
        this.alertClass = alertClass;
    }

    public String getAlertDivision() {
        return alertDivision;
    }

    public void setAlertDivision(String alertDivision) {
        this.alertDivision = alertDivision;
    }

    public String getSubmitted_By_to() {
        return submitted_By_to;
    }

    public void setSubmitted_By_to(String submitted_By_to) {
        this.submitted_By_to = submitted_By_to;
    }

    public String getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(String attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public String getAlert_priority() {
        return alert_priority;
    }

    public void setAlert_priority(String alert_priority) {
        this.alert_priority = alert_priority;
    }

    public String getAlertAttachmentIdentifier() {
        return alertAttachmentIdentifier;
    }

    public void setAlertAttachmentIdentifier(String alertAttachmentIdentifier) {
        this.alertAttachmentIdentifier = alertAttachmentIdentifier;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikeCheck() {
        return likeCheck;
    }

    public void setLikeCheck(boolean likeCheck) {
        this.likeCheck = likeCheck;
    }


    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }
}
