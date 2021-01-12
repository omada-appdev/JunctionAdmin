package com.omada.junctionadmin.data.models;

import com.google.firebase.Timestamp;

public class MemberModel {

    private final String id;

    private String user;
    private String name;
    private String photo;

    private Timestamp dateJoined;

    private String position;

    public MemberModel(MemberModelRemoteDB modelRemoteDB){

        id = modelRemoteDB.getId();

        setUser(modelRemoteDB.getUser());
        setName(modelRemoteDB.getName());
        setPhoto(modelRemoteDB.getPhoto());

        setDateJoined(modelRemoteDB.getDateJoined());
        setPosition(modelRemoteDB.getPosition());
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Timestamp getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Timestamp dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
