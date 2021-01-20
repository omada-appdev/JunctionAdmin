package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;

import javax.annotation.OverridingMethodsMustInvokeSuper;


public abstract class BaseViewModel extends ViewModel {

    protected final DataRepository.DataRepositoryAccessIdentifier dataRepositoryAccessIdentifier = DataRepository.registerForDataRepositoryAccess();

    protected BaseViewModel(){}

    protected DataRepository.DataRepositoryAccessIdentifier getDataRepositoryAccessIdentifier() {
        return dataRepositoryAccessIdentifier;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onCleared() {
        super.onCleared();
        DataRepository.removeDataRepositoryAccessRegistration(dataRepositoryAccessIdentifier);
    }
}
