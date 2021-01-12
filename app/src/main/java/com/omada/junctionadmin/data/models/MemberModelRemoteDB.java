package com.omada.junctionadmin.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class MemberModelRemoteDB {

    private String id;

    private String user;
    private String name;
    private String photo;

    private String position;

    private Timestamp dateJoined;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("user")
    public String getUser() {
        return user;
    }

    @PropertyName("user")
    public void setUser(String user) {
        this.user = user;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("photo")
    public String getPhoto() {
        return photo;
    }

    @PropertyName("photo")
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @PropertyName("position")
    public String getPosition() {
        return position;
    }

    @PropertyName("position")
    public void setPosition(String position) {
        this.position = position;
    }

    @PropertyName("dateJoined")
    public Timestamp getDateJoined() {
        return dateJoined;
    }

    @PropertyName("dateJoined")
    public void setDateJoined(Timestamp dateJoined) {
        this.dateJoined = dateJoined;
    }
}
