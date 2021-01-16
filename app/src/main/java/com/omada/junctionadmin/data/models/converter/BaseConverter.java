package com.omada.junctionadmin.data.models.converter;

public abstract class BaseConverter <E, R, L> {

    public abstract E convertLocalDBToExternalModel(L localModel);
    public abstract E convertRemoteDBToExternalModel(R remoteModel);
    public abstract R convertExternalToRemoteDBModel(E externalModel);
    public abstract L convertExternalToLocalDBModel(E externalModel);

}
