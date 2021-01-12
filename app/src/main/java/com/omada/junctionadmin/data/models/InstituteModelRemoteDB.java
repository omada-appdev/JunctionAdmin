package com.omada.junctionadmin.data.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class InstituteModelRemoteDB {

    private String id;

    private String handle;
    private String name;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("handle")
    public String getHandle(){
        return handle;
    }

    @PropertyName("name")
    public String getName(){
        return name;
    }

    @PropertyName("handle")
    public void setHandle(String handle){
        this.handle = handle;
    }

    @PropertyName("name")
    public void setName(String name){
        this.name = name;
    }
}
