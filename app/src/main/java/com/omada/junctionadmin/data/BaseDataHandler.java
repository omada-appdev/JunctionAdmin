package com.omada.junctionadmin.data;

import com.omada.junctionadmin.data.repositorytemp.DataRepositoryHandlerIdentifier;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;

public abstract class BaseDataHandler {

    // Used to get data related to an accessor of this handler through data repository
    protected final DataRepositoryHandlerIdentifier handlerIdentifier = MainDataRepository.registerDataHandler();

    protected BaseDataHandler() {
    }
}
