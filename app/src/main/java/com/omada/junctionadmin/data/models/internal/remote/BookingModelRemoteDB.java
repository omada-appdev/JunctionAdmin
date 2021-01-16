package com.omada.junctionadmin.data.models.internal.remote;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.internal.BaseModelInternal;

import java.util.Map;

public class BookingModelRemoteDB extends BaseModelInternal {

    private String venue;

    private String event;
    private String eventName;
    private String timeCreated;

    private Timestamp startTime;
    private Timestamp endTime;

    private String creator;
    private Map<String, String> creatorCache;

    private String photo;

    public BookingModelRemoteDB(){
    }

    public BookingModelRemoteDB(String id) {
        super(id);
    }

    @PropertyName("event")
    public String getEvent() {
        return event;
    }

    @PropertyName("name")
    public String getEventName() {
        return eventName;
    }

    @PropertyName("timeCreated")
    public String getTimeCreated() {
        return timeCreated;
    }

    @PropertyName("startTime")
    public Timestamp getStartTime() {
        return startTime;
    }

    @PropertyName("endTime")
    public Timestamp getEndTime() {
        return endTime;
    }

    @PropertyName("creator")
    public String getCreator() {
        return creator;
    }

    @PropertyName("creatorCache")
    public Map<String, String> getCreatorCache() {
        return creatorCache;
    }

    @PropertyName("photo")
    public String getPhoto() {
        return photo;
    }

    @PropertyName("venue")
    public String getVenue() {
        return venue;
    }

    @PropertyName("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @PropertyName("name")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @PropertyName("timeCreated")
    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    @PropertyName("startTime")
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @PropertyName("endTime")
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @PropertyName("creator")
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @PropertyName("creatorCache")
    public void setCreatorCache(Map<String, String> creatorCache) {
        this.creatorCache = creatorCache;
    }

    @PropertyName("photo")
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @PropertyName("venue")
    public void setVenue(String venue) {
        this.venue = venue;
    }
}