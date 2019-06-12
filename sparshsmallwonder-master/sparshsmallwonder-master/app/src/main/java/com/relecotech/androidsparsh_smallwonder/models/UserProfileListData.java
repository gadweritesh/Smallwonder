package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

/**
 * Created by Relecotech on 09-02-2018.
 */

public class UserProfileListData implements Serializable {
    String title;
    String azureTitle;
    String description;
    private int titleImage;
    private Boolean editable;

    public UserProfileListData(String title, String azureTitle, String description, int titleImage, Boolean editable) {
        this.title = title;
        this.azureTitle = azureTitle;
        this.description = description;
        this.titleImage = titleImage;
        this.editable = editable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAzureTitle() {
        return azureTitle;
    }

    public void setAzureTitle(String azureTitle) {
        this.azureTitle = azureTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(int titleImage) {
        this.titleImage = titleImage;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}