package com.omada.junctionadmin.data.models.mutable;

import com.omada.junctionadmin.data.models.external.InstituteModel;

public class MutableInstituteModel extends InstituteModel {

    public MutableInstituteModel() {
    }

    public MutableInstituteModel(InstituteModel instituteModel) {
        setImage(instituteModel.getImage());
        setName(instituteModel.getName());
        setHandle(instituteModel.getHandle());
        setId(instituteModel.getId());
    }

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
