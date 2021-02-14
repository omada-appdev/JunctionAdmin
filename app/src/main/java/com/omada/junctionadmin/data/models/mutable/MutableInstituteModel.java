package com.omada.junctionadmin.data.models.mutable;

import com.omada.junctionadmin.data.models.external.InstituteModel;

public class MutableInstituteModel extends InstituteModel {

    public void setHandle(String handle){
        this.handle = handle;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
