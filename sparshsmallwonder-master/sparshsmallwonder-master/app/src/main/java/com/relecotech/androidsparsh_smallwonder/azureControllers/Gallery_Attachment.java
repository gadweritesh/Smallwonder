package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;

/**
 * Created by Amey on 06-04-2018.
 */

public class Gallery_Attachment implements Serializable {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("Gallery_id")
    private String Gallery_id;

    @com.google.gson.annotations.SerializedName("active")
    private String active;

    @com.google.gson.annotations.SerializedName("imageName")
    private String imageName;

    public Gallery_Attachment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGallery_id() {
        return Gallery_id;
    }

    public void setGallery_id(String gallery_id) {
        Gallery_id = gallery_id;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}


