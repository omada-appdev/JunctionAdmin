package com.omada.junctionadmin.data.models.mutable;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.external.BookingModel;


public class MutableBookingModel extends BookingModel {

    public void setEvent(String event) {
        this.event = event;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setCreatorProfilePicture(String creatorProfilePicture) {
        this.creatorProfilePicture = creatorProfilePicture;
    }

    public void setCreatorMail(String creatorMail) {
        this.creatorMail = creatorMail;
    }

    public void setCreatorPhone(String creatorPhone) {
        this.creatorPhone = creatorPhone;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
