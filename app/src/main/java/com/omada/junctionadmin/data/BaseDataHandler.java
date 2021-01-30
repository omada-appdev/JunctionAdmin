package com.omada.junctionadmin.data;

import com.omada.junctionadmin.data.DataRepository;

public abstract class BaseDataHandler {

    // Used to get data related to an accessor of this handler through data repository
    protected final DataRepository.DataRepositoryHandlerIdentifier handlerIdentifier = DataRepository.registerDataHandler();

    protected BaseDataHandler(){}

}
