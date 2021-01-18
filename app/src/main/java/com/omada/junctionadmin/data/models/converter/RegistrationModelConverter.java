package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.RegistrationModel;
import com.omada.junctionadmin.data.models.internal.remote.RegistrationModelRemoteDB;

import java.util.Map;

public class RegistrationModelConverter extends BaseConverter<RegistrationModel, RegistrationModelRemoteDB, Void> {

    @Override
    public RegistrationModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public RegistrationModel convertRemoteDBToExternalModel(RegistrationModelRemoteDB remoteModel) {
        return null;
    }

    @Override
    public RegistrationModelRemoteDB convertExternalToRemoteDBModel(RegistrationModel externalModel) {
        return null;
    }

    @Override
    public Void convertExternalToLocalDBModel(RegistrationModel externalModel) {
        return null;
    }

    @Override
    public Map<String, Object> convertExternalToMapObject(RegistrationModel externalModel) {
        return null;
    }

    @Override
    public RegistrationModel convertMapObjectToExternal(Map<String, Object> externalModel) {
        return null;
    }
}
