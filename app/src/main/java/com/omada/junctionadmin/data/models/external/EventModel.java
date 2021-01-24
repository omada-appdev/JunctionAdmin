package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.common.collect.ImmutableList;
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
    protected Date startTime;
    protected Date endTime;

    protected String venue;
    protected String venueName;
    protected String venueAddress;
    protected String venueInstitute;

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
        startTime = new Date(in.readLong());
        endTime = new Date(in.readLong());
        venue = in.readString();
        venueName = in.readString();
        venueAddress = in.readString();
        venueInstitute = in.readString();
        tags = ImmutableList.copyOf(in.createStringArrayList());
        timeCreated = new Date(in.readLong());
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

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
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
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(creator);
        dest.writeString(creatorName);
        dest.writeString(creatorPhone);
        dest.writeString(creatorMail);
        dest.writeString(creatorProfilePicture);
        dest.writeString(image);
        dest.writeString(status);
        dest.writeLong(startTime.getTime());
        dest.writeLong(endTime.getTime());
        dest.writeString(venue);
        dest.writeString(venueName);
        dest.writeString(venueAddress);
        dest.writeString(venueInstitute);
        dest.writeStringList(tags);
        dest.writeLong(timeCreated.getTime());
    }
}