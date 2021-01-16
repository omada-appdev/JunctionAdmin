package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.internal.remote.EventModelRemoteDB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;

public class EventModel extends PostModel {

    protected String description;

    protected Map<String, Map<String, Map<String, String>>> form;

    protected String status;
    protected Timestamp startTime;
    protected Timestamp endTime;

    protected String venue;
    protected String venueName;
    protected String venueAddress;

    protected ArrayList<String> tags;

    protected EventModel() {
    }


    protected EventModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        creator = in.readString();
        creatorName = in.readString();
        creatorPhone = in.readString();
        creatorMail = in.readString();
        creatorProfilePicture = in.readString();
        image = in.readString();
        status = in.readString();
        startTime = in.readParcelable(Timestamp.class.getClassLoader());
        endTime = in.readParcelable(Timestamp.class.getClassLoader());
        venue = in.readString();
        venueName = in.readString();
        venueAddress = in.readString();
        tags = in.createStringArrayList();
    }

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel in) {
            return new EventModel(in);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public Map <String, Map<String, Map<String, String>>> getForm() {
        return form;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getVenue() {
        return venue;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(creator);
        dest.writeString(creatorName);
        dest.writeString(creatorPhone);
        dest.writeString(creatorMail);
        dest.writeString(creatorProfilePicture);
        dest.writeString(image);
        dest.writeString(status);
        dest.writeParcelable(startTime, flags);
        dest.writeParcelable(endTime, flags);
        dest.writeString(venue);
        dest.writeString(venueName);
        dest.writeString(venueAddress);
        dest.writeStringList(tags);
    }
}