package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.internal.remote.BookingModelRemoteDB;

import java.util.Map;

public class BookingModel extends BaseModel {

    protected String venue;
    protected String venueName;
    protected String venueAddress;
    protected String venueInstitute;

    protected String event;
    protected String eventName;
    protected String timeCreated;

    protected Timestamp startTime;
    protected Timestamp endTime;

    protected String creator;
    protected String creatorName;
    protected String creatorProfilePicture;
    protected String creatorMail;
    protected String creatorPhone;

    protected String photo;

    public BookingModel(){
    }

    protected BookingModel(Parcel in) {
        id = in.readString();
        venue = in.readString();
        venueName = in.readString();
        venueAddress = in.readString();
        venueInstitute = in.readString();
        event = in.readString();
        eventName = in.readString();
        timeCreated = in.readString();
        startTime = in.readParcelable(Timestamp.class.getClassLoader());
        endTime = in.readParcelable(Timestamp.class.getClassLoader());
        creator = in.readString();
        creatorName = in.readString();
        creatorProfilePicture = in.readString();
        creatorMail = in.readString();
        creatorPhone = in.readString();
        photo = in.readString();
    }

    public static final Creator<BookingModel> CREATOR = new Creator<BookingModel>() {
        @Override
        public BookingModel createFromParcel(Parcel in) {
            return new BookingModel(in);
        }

        @Override
        public BookingModel[] newArray(int size) {
            return new BookingModel[size];
        }
    };

    public String getEvent() {
        return event;
    }

    public String getEventName() {
        return eventName;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getCreator() {
        return creator;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorProfilePicture() {
        return creatorProfilePicture;
    }

    public String getCreatorMail() {
        return creatorMail;
    }

    public String getCreatorPhone() {
        return creatorPhone;
    }

    public String getVenue() {
        return venue;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public String getVenueInstitute() {
        return venueInstitute;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(venue);
        dest.writeString(venueName);
        dest.writeString(venueAddress);
        dest.writeString(venueInstitute);
        dest.writeString(event);
        dest.writeString(eventName);
        dest.writeString(timeCreated);
        dest.writeParcelable(startTime, flags);
        dest.writeParcelable(endTime, flags);
        dest.writeString(creator);
        dest.writeString(creatorName);
        dest.writeString(creatorProfilePicture);
        dest.writeString(creatorMail);
        dest.writeString(creatorPhone);
        dest.writeString(photo);
    }
}
