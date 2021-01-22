package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import javax.annotation.OverridingMethodsMustInvokeSuper;


public abstract class BaseViewModel extends ViewModel {

    protected final DataRepository.DataRepositoryAccessIdentifier dataRepositoryAccessIdentifier = DataRepository.registerForDataRepositoryAccess();
    protected final DataValidator dataValidator = new DataValidator();
    protected final MutableLiveData<LiveEvent<DataValidator.DataValidationInformation>> dataValidationAction = new MutableLiveData<>();

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

    protected DataValidator getDataValidator() {
        return dataValidator;
    }

    public LiveData<LiveEvent<DataValidator.DataValidationInformation>> getDataValidationAction() {
        return dataValidationAction;
    }
}