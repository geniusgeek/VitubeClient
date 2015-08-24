package com.example.genius.vitubeclient.model;


import android.annotation.SuppressLint;

import java.util.Objects;

/**
 * This "Plain Ol' Java Object" (POJO) class represents meta-data of
 * interest downloaded in Json from the Video Service via the
 * VideoServiceProxy.
 */
public class Video {
    /**
     * Various fields corresponding to data downloaded in Json from
     * the Video WebService.
     */
    private long id;
    private String title;
    private long duration;
    private String contentType;
    private String dataUrl;
    private double ratings;

    /**
     * No-op constructor
     */
    public Video() {
    }

    /**
     * Constructor that initializes title, duration, and contentType.
     */
    public Video(String title,
                 long duration,
                 String contentType) {
        this.setTitle(title);
        this.setDuration(duration);
        this.setContentType(contentType);
    }

    /**
     * Constructor that initializes all the fields of interest.
     */
    public Video(long id,
                 String title,
                 long duration,
                 String contentType,
                 String dataUrl,
                 double rating) {
        this(title, duration, contentType);
        this.setRatings(rating);
        this.setId(id);
        this.setDataUrl(dataUrl);
    }

    /*
     * Getters and setters to access Video.
     */

    /**
     * Get the Id of the Video.
     *
     * @return id of video
     */
    public long getId() {
        return id;
    }

    /**
     * Get the Video by Id
     *
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the Title of Video.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the Title of Video.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the Duration of Video.
     *
     * @return Duration of Video.
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set the Duration of Video.
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Get the DataUrl of Video
     *
     * @return dataUrl of Video
     */
    public String getDataUrl() {
        return dataUrl;
    }

    /**
     * Set the DataUrl of the Video.
     */
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    /**
     * Get ContentType of Video.
     *
     * @return contentType of Video.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the ContentType of Video.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

   /* *//**
     * @return the textual representation of Video object.
     *//*
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "title: " + title + ", " +
                "duration: " + duration + ", " +
                "contentType: " + contentType + ", " +
                "dataURL: " + dataUrl + ", " +
                "rating:" + ratings +
                "}";
    }*/
    /**
     * @return the textual representation of Video object.
     */
    @Override
    public String toString() {
        return "{" +
                 "title:\"" + title + "\", " +
                "duration:" + duration + ", " +
                "contentType:\"" + contentType + "\" " +
                "}";
    }

    /**
     * @return an Integer hash code for this object.
     */
    @SuppressLint("NewApi")
    @Override
    public int hashCode() {
        return Objects.hash(getTitle(),
                getDuration());
    }

    /**
     * @return Compares this Video instance with specified
     * Video and indicates if they are equal.
     */
    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Video)
                && Objects.equals(getTitle(),
                ((Video) obj).getTitle())
                && getDuration() == ((Video) obj).getDuration();
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }
}