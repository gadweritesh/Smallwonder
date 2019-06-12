package com.relecotech.androidsparsh_smallwonder.azureControllers;

/**
 * Created by Amey on 02-04-2018.
 */

public class Attachment {
    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("fileName")
    String fileName;
    @com.google.gson.annotations.SerializedName("containerName")
    String containerName;
    @com.google.gson.annotations.SerializedName("attachmentIdentifier")
    String attachmentIdentifier;
    @com.google.gson.annotations.SerializedName("active")
    private Integer active;

    public Attachment() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getAttachmentIdentifier() {
        return attachmentIdentifier;
    }

    public void setAttachmentIdentifier(String attachmentIdentifier) {
        this.attachmentIdentifier = attachmentIdentifier;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
