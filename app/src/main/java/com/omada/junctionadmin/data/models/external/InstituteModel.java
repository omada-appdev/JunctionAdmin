package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;

public class InstituteModel extends BaseModel {

    protected String handle;
    protected String name;
    protected String image;

    protected InstituteModel() {
    }

    protected InstituteModel(Parcel in) {
        id = in.readString();
        handle = in.readString();
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<InstituteModel> CREATOR = new Creator<InstituteModel>() {
        @Override
        public InstituteModel createFromParcel(Parcel in) {
            return new InstituteModel(in);
        }

        @Override
        public InstituteModel[] newArray(int size) {
            return new InstituteModel[size];
        }
    };

    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(handle);
        dest.writeString(name);
        dest.writeString(image);
    }
}
