package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Relecotech on 19-03-2018.
 */

public class YoutubeVideoModel {
    String VideoName;
    String VideoDesc;
    String URL;
    String VideoId;

    public YoutubeVideoModel() {
    }

    public void setVideoName(String VideoName) {
        this.VideoName = VideoName;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoDesc(String VideoDesc) {
        this.VideoDesc = VideoDesc;
    }

    public String getVideoDesc() {
        return VideoDesc;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void setVideoId(String VideoId) {
        this.VideoId = VideoId;
    }

    public String getVideoId() {
        return VideoId;
    }

}
