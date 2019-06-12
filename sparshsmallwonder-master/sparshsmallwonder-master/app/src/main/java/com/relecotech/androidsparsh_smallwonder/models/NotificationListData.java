package com.relecotech.androidsparsh_smallwonder.models;

public class NotificationListData {

    String notifaction_Assignment_Id;
    String notifaction_Assignment_Due_Date;
    String notifaction_Tag;
    String notification_Message_Body;
    String notifaction_Post_Date;
    String notifaction_SubmittedBy;

    public NotificationListData(String notifaction_Assignment_Id, String notifaction_Assignment_Due_Date, String notifaction_Tag, String notification_Message_Body, String notifaction_Post_Date, String notifaction_SubmittedBy) {
        this.notifaction_Assignment_Id = notifaction_Assignment_Id;
        this.notifaction_Assignment_Due_Date = notifaction_Assignment_Due_Date;
        this.notifaction_Tag = notifaction_Tag;
        this.notification_Message_Body = notification_Message_Body;
        this.notifaction_Post_Date = notifaction_Post_Date;
        this.notifaction_SubmittedBy = notifaction_SubmittedBy;
    }

    public String getNotifaction_Assignment_Id() {
        return notifaction_Assignment_Id;
    }

    public void setNotifaction_Assignment_Id(String notifaction_Assignment_Id) {
        this.notifaction_Assignment_Id = notifaction_Assignment_Id;
    }

    public String getNotifaction_Assignment_Due_Date() {
        return notifaction_Assignment_Due_Date;
    }

    public void setNotifaction_Assignment_Due_Date(String notifaction_Assignment_Due_Date) {
        this.notifaction_Assignment_Due_Date = notifaction_Assignment_Due_Date;
    }

    public String getNotifaction_Tag() {
        return notifaction_Tag;
    }

    public void setNotifaction_Tag(String notifaction_Tag) {
        this.notifaction_Tag = notifaction_Tag;
    }

    public String getNotification_Message_Body() {
        return notification_Message_Body;
    }

    public void setNotification_Message_Body(String notification_Message_Body) {
        this.notification_Message_Body = notification_Message_Body;
    }

    public String getNotifaction_Post_Date() {
        return notifaction_Post_Date;
    }

    public void setNotifaction_Post_Date(String notifaction_Post_Date) {
        this.notifaction_Post_Date = notifaction_Post_Date;
    }

    public String getNotifaction_SubmittedBy() {
        return notifaction_SubmittedBy;
    }

    public void setNotifaction_SubmittedBy(String notifaction_SubmittedBy) {
        this.notifaction_SubmittedBy = notifaction_SubmittedBy;
    }
}