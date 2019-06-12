package com.relecotech.androidsparsh_smallwonder.models;

import java.util.Date;

/**
 * Created by Relecotech on 26-02-2018.
 */

public class AlertListData {

    String alertId;
    String title;
    String description;
    Date postDate;
    String category;
    String alertStudent;
    String alertStudId;
    String alertClass;
    String alertDivision;
    String submitted_By_to;
    String attachmentCount;
    String alert_priority;
    int likeCount;
    private boolean likeCheck;
    String alertAttachmentIdentifier;

    public AlertListData(String alertId, String title, String description, Date postDate, String category, String alertStudent, String alertStudId, String alertClass, String alertDivision, String submitted_By_to, String attachmentCount, String alert_priority, int likeCount, boolean likeCheck, String alertAttachmentIdentifier) {
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
        this.likeCount = likeCount;
        this.likeCheck = likeCheck;
        this.alertAttachmentIdentifier = alertAttachmentIdentifier;
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

    public String getAlertAttachmentIdentifier() {
        return alertAttachmentIdentifier;
    }

    public void setAlertAttachmentIdentifier(String alertAttachmentIdentifier) {
        this.alertAttachmentIdentifier = alertAttachmentIdentifier;
    }
}