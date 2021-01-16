package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.internal.remote.InstituteModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableInstituteModel;


public class InstituteModelConverter extends BaseConverter <InstituteModel, InstituteModelRemoteDB, Void> {

    @Override
    public InstituteModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public InstituteModel convertRemoteDBToExternalModel(InstituteModelRemoteDB remoteModel) {

        MutableInstituteModel model = new MutableInstituteModel();

        model.setId(remoteModel.getId());
        model.setName(remoteModel.getName());
        model.setHandle(remoteModel.getHandle());

        return model;
    }

    @Override
    public InstituteModelRemoteDB convertExternalToRemoteDBModel(InstituteModel externalModel) {

        InstituteModelRemoteDB model = new InstituteModelRemoteDB();

        model.setId(externalModel.getId());
        model.setName(externalModel.getName());
        model.setHandle(externalModel.getHandle());

        return model;
    }

    @Override
    public Void convertExternalToLocalDBModel(InstituteModel externalModel) {
        return null;
    }
}
