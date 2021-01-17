package com.omada.junctionadmin.data.models.mutable;

import com.omada.junctionadmin.data.models.external.OrganizationModel;

import java.util.List;

public class MutableOrganizationModel extends OrganizationModel {

    public MutableOrganizationModel(){
    }

    public MutableOrganizationModel(OrganizationModel model){
        super(model);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setAttendedUsersNumber(Integer attendedUsersNumber) {
        this.attendedUsersNumber = attendedUsersNumber;
    }

    public void setHeldEventsNumber(Integer heldEventsNumber) {
        this.heldEventsNumber = heldEventsNumber;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setType(String type) {
        this.type = type;
    }
}
