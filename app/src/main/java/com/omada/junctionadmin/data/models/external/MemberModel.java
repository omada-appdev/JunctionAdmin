package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.internal.remote.MemberModelRemoteDB;

import java.util.Date;

public class MemberModel extends BaseModel {

    protected String user;
    protected String name;
    protected String photo;

    protected Date dateJoined;

    protected String position;

    protected MemberModel(){
    }

    protected MemberModel(Parcel in) {
        id = in.readString();
        user = in.readString();
        name = in.readString();
        photo = in.readString();
        dateJoined = new Date(in.readLong());
        position = in.readString();
    }

    public static final Creator<MemberModel> CREATOR = new Creator<MemberModel>() {
        @Override
        public MemberModel createFromParcel(Parcel in) {
            return new MemberModel(in);
        }

        @Override
        public MemberModel[] newArray(int size) {
            return new MemberModel[size];
        }
    };

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public String getPosition() {
        return position;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(name);
        dest.writeString(photo);
        dest.writeLong(dateJoined.getTime());
        dest.writeString(position);
    }
}
