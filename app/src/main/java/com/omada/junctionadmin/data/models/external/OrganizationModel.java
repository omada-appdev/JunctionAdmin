package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;

import com.omada.junctionadmin.data.models.internal.remote.OrganizationModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableOrganizationModel;

import java.util.List;

public class OrganizationModel extends BaseModel {

    protected String name;

    protected String profilePhoto;

    protected Integer attendedUsersNumber;
    protected Integer heldEventsNumber;
    protected String institute;

    protected List<String> interests;
    protected String mail;
    protected String phone;

    protected String type;

    protected OrganizationModel(){
    }
    
    protected OrganizationModel(OrganizationModel externalModel){
        
        id = externalModel.getId();

        type = externalModel.getType();
        institute = externalModel.getInstitute();

        name = externalModel.getName();
        mail = externalModel.getMail();
        phone = externalModel.getPhone();
        profilePhoto = externalModel.getProfilePhoto();

        interests = externalModel.getInterests();
        attendedUsersNumber = externalModel.getAttendedUsersNumber();
        heldEventsNumber = externalModel.getHeldEventsNumber();

    }

    protected OrganizationModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        profilePhoto = in.readString();
        if (in.readByte() == 0) {
            attendedUsersNumber = null;
        } else {
            attendedUsersNumber = in.readInt();
        }
        if (in.readByte() == 0) {
            heldEventsNumber = null;
        } else {
            heldEventsNumber = in.readInt();
        }
        institute = in.readString();
        interests = in.createStringArrayList();
        mail = in.readString();
        phone = in.readString();
        type = in.readString();
    }

    public static final Creator<OrganizationModel> CREATOR = new Creator<OrganizationModel>() {
        @Override
        public OrganizationModel createFromParcel(Parcel in) {
            return new OrganizationModel(in);
        }

        @Override
        public OrganizationModel[] newArray(int size) {
            return new OrganizationModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public Integer getAttendedUsersNumber() {
        return attendedUsersNumber;
    }

    public Integer getHeldEventsNumber() {
        return heldEventsNumber;
    }

    public String getInstitute() {
        return institute;
    }

    public List<String> getInterests() {
        return interests;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(profilePhoto);
        if (attendedUsersNumber == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(attendedUsersNumber);
        }
        if (heldEventsNumber == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(heldEventsNumber);
        }
        dest.writeString(institute);
        dest.writeStringList(interests);
        dest.writeString(mail);
        dest.writeString(phone);
        dest.writeString(type);
    }
}