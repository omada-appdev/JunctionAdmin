package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.internal.remote.VenueModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableVenueModel;

public class VenueModelConverter extends BaseConverter <VenueModel, VenueModelRemoteDB, Void>{

    @Override
    public VenueModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public VenueModel convertRemoteDBToExternalModel(VenueModelRemoteDB remoteModel) {

        MutableVenueModel model = new MutableVenueModel();

        model.setId(remoteModel.getId());
        model.setName(remoteModel.getName());
        model.setAddress(remoteModel.getAddress());
        model.setInstitute(remoteModel.getInstitute());

        return model;
    }

    @Override
    public VenueModelRemoteDB convertExternalToRemoteDBModel(VenueModel externalModel) {

        VenueModelRemoteDB model = new VenueModelRemoteDB();

        model.setId(externalModel.getId());
        model.setName(externalModel.getName());
        model.setAddress(externalModel.getAddress());
        model.setInstitute(externalModel.getInstitute());

        return model;
        
    }

    @Override
    public Void convertExternalToLocalDBModel(VenueModel externalModel) {
        return null;
    }
}
