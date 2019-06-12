package com.relecotech.androidsparsh_smallwonder.azureControllers;


import java.io.Serializable;
import java.util.Date;

/**
 * Created by Amey on 06-04-2018.
 */

public class Gallery implements Serializable {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("galleryDescription")
    private String galleryDescription;
    @com.google.gson.annotations.SerializedName("active")
    private String active;
    @com.google.gson.annotations.SerializedName("galleryFolderName")
    private String galleryFolderName;
    @com.google.gson.annotations.SerializedName("galleryTitle")
    private String galleryTitle;
    @com.google.gson.annotations.SerializedName("galleryPostDate")
    private Date galleryPostDate;
    @com.google.gson.annotations.SerializedName("galleryURL")
    private String galleryURL;
    @com.google.gson.annotations.SerializedName("galleryImageCount")
    private int galleryImageCount;
    @com.google.gson.annotations.SerializedName("School_Class_id")
    private String School_Class_id;
    @com.google.gson.annotations.SerializedName("Branch_id")
    private String Branch_id;
    @com.google.gson.annotations.SerializedName("galleryCategory")
    private String galleryCategory;

    public String getGalleryCategory() {
        return galleryCategory;
    }

    public void setGalleryCategory(String galleryCategory) {
        this.galleryCategory = galleryCategory;
    }

    public Gallery() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGalleryDescription() {
        return galleryDescription;
    }

    public void setGalleryDescription(String galleryDescription) {
        this.galleryDescription = galleryDescription;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getGalleryFolderName() {
        return galleryFolderName;
    }

    public void setGalleryFolderName(String galleryFolderName) {
        this.galleryFolderName = galleryFolderName;
    }

    public String getGalleryTitle() {
        return galleryTitle;
    }

    public void setGalleryTitle(String galleryTitle) {
        this.galleryTitle = galleryTitle;
    }

    public Date getGalleryPostDate() {
        return galleryPostDate;
    }

    public void setGalleryPostDate(Date galleryPostDate) {
        this.galleryPostDate = galleryPostDate;
    }

    public String getGalleryURL() {
        return galleryURL;
    }

    public void setGalleryURL(String galleryURL) {
        this.galleryURL = galleryURL;
    }

    public int getGalleryImageCount() {
        return galleryImageCount;
    }

    public void setGalleryImageCount(int galleryImageCount) {
        this.galleryImageCount = galleryImageCount;
    }

    public String getSchool_Class_id() {
        return School_Class_id;
    }

    public void setSchool_Class_id(String school_Class_id) {
        School_Class_id = school_Class_id;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }
}
