package com.omada.junctionadmin.data.models;

public class InstituteModel {

    private final String ID;

    private String handle;
    private String name;

    public InstituteModel(InstituteModelRemoteDB modelRemoteDB){
        ID = modelRemoteDB.getId();
        this.handle = modelRemoteDB.getHandle();
        this.name = modelRemoteDB.getName();
    }

    public String getID() {
        return ID;
    }

    public String getHandle(){
        return handle;
    }

    public String getName(){
        return name;
    }

}
