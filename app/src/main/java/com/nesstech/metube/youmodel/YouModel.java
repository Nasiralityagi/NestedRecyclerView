package com.nesstech.metube.youmodel;

import java.io.Serializable;

/**
 * Created by Nasir on 9/20/2017.
 */
public class YouModel implements Serializable{
    private String id;
    private String duration;
    private String viewCount;
    private String channelTitle;
    private String thumbnails;
    private String publishedAt;
    private String title;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}