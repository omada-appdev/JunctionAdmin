package com.omada.junctionadmin.data.models.mutable;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.external.MemberModel;

public class MutableMemberModel extends MemberModel {

    public void setUser(String user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setDateJoined(Timestamp dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
