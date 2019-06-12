package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

public class AchievementListData implements Serializable {

    String achievementId;
    String achievementStudentName;
    String achievementTitle;
    String achievementDescription;
    String achievementCategory;
    String achievementImageUrl;
    String achievementYear;
    String student_id;
    String achievementPostDate;
    String uploaderName;
    String uploaderId;

    public AchievementListData(String achievementId, String achievementStudentName, String achievementTitle, String achievementDescription, String achievementCategory, String achievementImageUrl, String achievementYear, String student_id, String achievementPostDate, String uploaderName, String uploaderId) {
        this.achievementId = achievementId;
        this.achievementStudentName = achievementStudentName;
        this.achievementTitle = achievementTitle;
        this.achievementDescription = achievementDescription;
        this.achievementCategory = achievementCategory;
        this.achievementImageUrl = achievementImageUrl;
        this.achievementYear = achievementYear;
        this.student_id = student_id;
        this.achievementPostDate = achievementPostDate;
        this.uploaderName = uploaderName;
        this.uploaderId = uploaderId;
    }

    public String getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(String achievementId) {
        this.achievementId = achievementId;
    }

    public String getAchievementStudentName() {
        return achievementStudentName;
    }

    public void setAchievementStudentName(String achievementStudentName) {
        this.achievementStudentName = achievementStudentName;
    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }

    public String getAchievementCategory() {
        return achievementCategory;
    }

    public void setAchievementCategory(String achievementCategory) {
        this.achievementCategory = achievementCategory;
    }

    public String getAchievementImageUrl() {
        return achievementImageUrl;
    }

    public void setAchievementImageUrl(String achievementImageUrl) {
        this.achievementImageUrl = achievementImageUrl;
    }

    public String getAchievementYear() {
        return achievementYear;
    }

    public void setAchievementYear(String achievementYear) {
        this.achievementYear = achievementYear;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getAchievementPostDate() {
        return achievementPostDate;
    }

    public void setAchievementPostDate(String achievementPostDate) {
        this.achievementPostDate = achievementPostDate;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }
}
