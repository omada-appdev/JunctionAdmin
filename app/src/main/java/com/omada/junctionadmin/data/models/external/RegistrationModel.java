package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Map;

public class RegistrationModel extends BaseModel implements Parcelable {

    private String userMail;
    private String userPhone;
    private String userInstitute;
    private String user;
    private Timestamp timeCreated;

    private Map<String, Map<String, Map <String, String>>> responses;

    protected RegistrationModel(){
        
    }

    protected RegistrationModel(Parcel in) {
        id = in.readString();
        userMail = in.readString();
        userPhone = in.readString();
        userInstitute = in.readString();
        user = in.readString();
        timeCreated = in.readParcelable(Timestamp.class.getClassLoader());
        try {
            responses = (Map<String, Map<String, Map<String, String>>>)in.readSerializable();
        }
        catch (ClassCastException e){
            Log.e("RegistrationModel", "Error parcelling registration");
            e.printStackTrace();
        }
    }

    public static final Creator<RegistrationModel> CREATOR = new Creator<RegistrationModel>() {
        @Override
        public RegistrationModel createFromParcel(Parcel in) {
            return new RegistrationModel(in);
        }

        @Override
        public RegistrationModel[] newArray(int size) {
            return new RegistrationModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userMail);
        dest.writeString(userPhone);
        dest.writeString(userInstitute);
        dest.writeString(user);
        dest.writeParcelable(timeCreated, flags);
        dest.writeSerializable((Serializable)responses);
    }
}
