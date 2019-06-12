package com.relecotech.androidsparsh_smallwonder.models;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amey on 05-04-2018.
 */

public class SchoolGalleryListData implements Parcelable {

    String galleryTitle;
    String galleryPostDate;
    String galleryURL;
    String galleryImageCount;
    String School_Class_id;
    String Branch_id;
    String photoName;
    String Gallery_id;
    String galleryDescription;
    String imageUrlToDownloadImage;
    String galleryCategory;

    public SchoolGalleryListData(String galleryTitle, String galleryPostDate, String galleryURL, String galleryImageCount, String school_Class_id, String branch_id, String photoName, String gallery_id, String galleryDescription, String imageUrlToDownloadImge,String galleryCategory) {
        this.galleryTitle = galleryTitle;
        this.galleryPostDate = galleryPostDate;
        this.galleryURL = galleryURL;
        this.galleryImageCount = galleryImageCount;
        School_Class_id = school_Class_id;
        Branch_id = branch_id;
        this.photoName = photoName;
        Gallery_id = gallery_id;
        this.galleryDescription = galleryDescription;
        this.imageUrlToDownloadImage = imageUrlToDownloadImge;
        this.galleryCategory = galleryCategory;
    }

    protected SchoolGalleryListData(Parcel in) {
        galleryTitle = in.readString();
        galleryPostDate = in.readString();
        galleryURL = in.readString();
        galleryImageCount = in.readString();
        School_Class_id = in.readString();
        Branch_id = in.readString();
        photoName = in.readString();
        Gallery_id = in.readString();
        galleryDescription = in.readString();
        imageUrlToDownloadImage = in.readString();
    }

    public static final Creator<SchoolGalleryListData> CREATOR = new Creator<SchoolGalleryListData>() {
        @Override
        public SchoolGalleryListData createFromParcel(Parcel in) {
            return new SchoolGalleryListData(in);
        }

        @Override
        public SchoolGalleryListData[] newArray(int size) {
            return new SchoolGalleryListData[size];
        }
    };

    public String getGalleryTitle() {
        return galleryTitle;
    }

    public void setGalleryTitle(String galleryTitle) {
        this.galleryTitle = galleryTitle;
    }

    public String getGalleryPostDate() {
        return galleryPostDate;
    }

    public void setGalleryPostDate(String galleryPostDate) {
        this.galleryPostDate = galleryPostDate;
    }

    public String getGalleryURL() {
        return galleryURL;
    }

    public void setGalleryURL(String galleryURL) {
        this.galleryURL = galleryURL;
    }

    public String getGalleryImageCount() {
        return galleryImageCount;
    }

    public void setGalleryImageCount(String galleryImageCount) {
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

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getGallery_id() {
        return Gallery_id;
    }

    public void setGallery_id(String gallery_id) {
        Gallery_id = gallery_id;
    }

    public String getGalleryDescription() {
        return galleryDescription;
    }

    public void setGalleryDescription(String galleryDescription) {
        this.galleryDescription = galleryDescription;
    }

    public String getImageUrlToDownloadImage() {
        return imageUrlToDownloadImage;
    }

    public void setImageUrlToDownloadImage(String imageUrlToDownloadImage) {
        this.imageUrlToDownloadImage = imageUrlToDownloadImage;
    }

    public String getGalleryCategory() {
        return galleryCategory;
    }

    public void setGalleryCategory(String galleryCategory) {
        this.galleryCategory = galleryCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(galleryTitle);
        dest.writeString(galleryPostDate);
        dest.writeString(galleryURL);
        dest.writeString(galleryImageCount);
        dest.writeString(School_Class_id);
        dest.writeString(Branch_id);
        dest.writeString(photoName);
        dest.writeString(Gallery_id);
        dest.writeString(galleryDescription);
        dest.writeString(imageUrlToDownloadImage);
    }
}
