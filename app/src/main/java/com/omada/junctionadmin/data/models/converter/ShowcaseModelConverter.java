package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.data.models.internal.remote.ShowcaseModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableShowcaseModel;

import java.util.Map;

public class ShowcaseModelConverter extends BaseConverter <ShowcaseModel, ShowcaseModelRemoteDB, Void> {

    @Override
    public ShowcaseModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public ShowcaseModel convertRemoteDBToExternalModel(ShowcaseModelRemoteDB remoteModel) {
        
        MutableShowcaseModel model = new MutableShowcaseModel();
        
        model.setTitle(remoteModel.getTitle());
        model.setCreator(remoteModel.getCreator());
        model.setCreatorType(remoteModel.getCreatorType());
        model.setPhoto(remoteModel.getPhoto());
        
        return model;
    }

    @Override
    public ShowcaseModelRemoteDB convertExternalToRemoteDBModel(ShowcaseModel externalModel) {

        ShowcaseModelRemoteDB model = new ShowcaseModelRemoteDB();

        model.setTitle(externalModel.getTitle());
        model.setCreator(externalModel.getCreator());
        model.setCreatorType(externalModel.getCreatorType());
        model.setPhoto(externalModel.getPhoto());

        return model;
        
    }
 

    @Override
    public Void convertExternalToLocalDBModel(ShowcaseModel externalModel) {
        return null;
    }

    @Override
    public Map<String, Object> convertExternalToMapObject(ShowcaseModel externalModel) {
        return null;
    }

    @Override
    public ShowcaseModel convertMapObjectToExternal(Map<String, Object> externalModel) {
        return null;
    }
}
