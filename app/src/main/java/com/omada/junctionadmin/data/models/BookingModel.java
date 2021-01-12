package com.omada.junctionadmin.data.models;

import com.google.firebase.Timestamp;

import java.util.Map;

public class BookingModel {

    private String id;

    private String venue;

    private String event;
    private String eventName;
    private String timeCreated;

    private Timestamp startTime;
    private Timestamp endTime;

    private String creator;
    private String creatorName;
    private String creatorProfilePicture;
    private String creatorMail;
    private String creatorPhone;

    private String photo;

    public BookingModel(BookingModelRemoteDB modelRemoteDB){

        setId(modelRemoteDB.getId());

        setVenue(modelRemoteDB.getVenue());

        setEvent(modelRemoteDB.getEvent());
        setEventName(modelRemoteDB.getEventName());
        setPhoto(modelRemoteDB.getPhoto());
        setStartTime(modelRemoteDB.getStartTime());
        setEndTime(modelRemoteDB.getEndTime());

        setTimeCreated(modelRemoteDB.getTimeCreated());

        setCreator(modelRemoteDB.getCreator());
        Map<String, String> creatorData = modelRemoteDB.getCreatorCache();
        setCreatorName(creatorData.get("name"));
        setCreatorName(creatorData.get("profilePicture"));
        setCreatorName(creatorData.get("mail"));
        setCreatorName(creatorData.get("phone"));

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorProfilePicture() {
        return creatorProfilePicture;
    }

    public void setCreatorProfilePicture(String creatorProfilePicture) {
        this.creatorProfilePicture = creatorProfilePicture;
    }

    public String getCreatorMail() {
        return creatorMail;
    }

    public void setCreatorMail(String creatorMail) {
        this.creatorMail = creatorMail;
    }

    public String getCreatorPhone() {
        return creatorPhone;
    }

    public void setCreatorPhone(String creatorPhone) {
        this.creatorPhone = creatorPhone;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
