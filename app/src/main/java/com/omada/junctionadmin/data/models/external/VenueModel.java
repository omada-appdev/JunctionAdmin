package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

import java.util.Objects;

public class VenueModel extends BaseModel {

    protected String name;
    protected String address;
    protected String institute;

    protected VenueModel() {
    }

    protected VenueModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        institute = in.readString();
    }

    public static final Creator<VenueModel> CREATOR = new Creator<VenueModel>() {
        @Override
        public VenueModel createFromParcel(Parcel in) {
            return new VenueModel(in);
        }

        @Override
        public VenueModel[] newArray(int size) {
            return new VenueModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getInstitute() {
        return institute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(institute);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VenueModel that = (VenueModel) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
