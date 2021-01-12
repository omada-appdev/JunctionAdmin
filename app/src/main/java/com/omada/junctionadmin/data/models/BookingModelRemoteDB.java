package com.omada.junctionadmin.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Map;

public class BookingModelRemoteDB {

    private String id;

    private String venue;

    private String event;
    private String eventName;
    private String timeCreated;

    private Timestamp startTime;
    private Timestamp endTime;

    private String creator;
    private Map<String, String> creatorCache;

    private String photo;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("event")
    public String getEvent() {
        return event;
    }

    @PropertyName("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @PropertyName("name")
    public String getEventName() {
        return eventName;
    }

    @PropertyName("name")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @PropertyName("timeCreated")
    public String getTimeCreated() {
        return timeCreated;
    }

    @PropertyName("timeCreated")
    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    @PropertyName("startTime")
    public Timestamp getStartTime() {
        return startTime;
    }

    @PropertyName("startTime")
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @PropertyName("endTime")
    public Timestamp getEndTime() {
        return endTime;
    }

    @PropertyName("endTime")
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @PropertyName("creator")
    public String getCreator() {
        return creator;
    }

    @PropertyName("creator")
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @PropertyName("creatorCache")
    public Map<String, String> getCreatorCache() {
        return creatorCache;
    }

    @PropertyName("creatorCache")
    public void setCreatorCache(Map<String, String> creatorCache) {
        this.creatorCache = creatorCache;
    }

    @PropertyName("photo")
    public String getPhoto() {
        return photo;
    }

    @PropertyName("photo")
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @PropertyName("venue")
    public String getVenue() {
        return venue;
    }

    @PropertyName("venue")
    public void setVenue(String venue) {
        this.venue = venue;
    }
}
