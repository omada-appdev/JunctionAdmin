package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.repository.DataRepositoryAccessIdentifier;
import com.omada.junctionadmin.data.repository.MainDataRepository;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import javax.annotation.OverridingMethodsMustInvokeSuper;


public abstract class BaseViewModel extends ViewModel {

    protected final DataRepositoryAccessIdentifier dataRepositoryAccessIdentifier = MainDataRepository.registerForDataRepositoryAccess();
    protected final DataValidator dataValidator = new DataValidator();
    protected final MutableLiveData<LiveEvent<DataValidator.DataValidationInformation>> dataValidationAction = new MutableLiveData<>();

    protected BaseViewModel(){
    }

    protected DataRepositoryAccessIdentifier getDataRepositoryAccessIdentifier() {
        return dataRepositoryAccessIdentifier;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onCleared() {
        super.onCleared();
        MainDataRepository.removeDataRepositoryAccessRegistration(dataRepositoryAccessIdentifier);
    }

    protected DataValidator getDataValidator() {
        return dataValidator;
    }

    public LiveData<LiveEvent<DataValidator.DataValidationInformation>> getDataValidationAction() {
        return dataValidationAction;
    }

    protected void notifyValidity(DataValidator.DataValidationInformation dataValidationInformation){
        dataValidationAction.setValue(new LiveEvent<>(dataValidationInformation));
    }

    public final String getUserId() {
        return MainDataRepository.getInstance().getUserDataHandler().getCurrentUserModel().getId();
    }

    public final OrganizationModel getUserModel() {
        return MainDataRepository.getInstance().getUserDataHandler().getCurrentUserModel();
    }
}
