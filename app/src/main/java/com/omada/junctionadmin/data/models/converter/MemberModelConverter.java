package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.MemberModel;
import com.omada.junctionadmin.data.models.internal.remote.MemberModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableMemberModel;


public class MemberModelConverter extends BaseConverter <MemberModel, MemberModelRemoteDB, Void> {

    @Override
    public MemberModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public MemberModel convertRemoteDBToExternalModel(MemberModelRemoteDB remoteModel) {

        MutableMemberModel model = new MutableMemberModel();
        
        model.setId(remoteModel.getId());

        model.setUser(remoteModel.getUser());
        model.setName(remoteModel.getName());
        model.setPhoto(remoteModel.getPhoto());

        model.setDateJoined(remoteModel.getDateJoined());
        model.setPosition(remoteModel.getPosition());

        return model;
        
    }

    @Override
    public MemberModelRemoteDB convertExternalToRemoteDBModel(MemberModel externalModel) {

        MemberModelRemoteDB model = new MemberModelRemoteDB();

        model.setId(externalModel.getId());
        model.setName(externalModel.getName());
        model.setPhoto(externalModel.getPhoto());
        model.setDateJoined(externalModel.getDateJoined());
        model.setPosition(externalModel.getPosition());
        model.setUser(externalModel.getUser());

        return model;

    }

    @Override
    public Void convertExternalToLocalDBModel(MemberModel externalModel) {
        return null;
    }
}
