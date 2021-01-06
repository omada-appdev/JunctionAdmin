package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.BaseModel;
import com.omada.junctionadmin.data.models.OrganizationModel;
import com.omada.junctionadmin.data.models.ShowcaseModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.List;

public class OrganizationProfileViewModel extends ViewModel {

    private String organizationID;
    private OrganizationModel organizationModel;

    private final MediatorLiveData<List<BaseModel>> organizationHighlights = new MediatorLiveData<>();
    private final MediatorLiveData<List<ShowcaseModel>> organizationShowcases = new MediatorLiveData<>();

    public LiveData<List<BaseModel>> getOrganizationHighlights(){
        return organizationHighlights;
    }
    public LiveData<List<ShowcaseModel>> getOrganizationShowcases(){
        return organizationShowcases;
    }

    public LiveData<LiveEvent<OrganizationModel>> getOrganizationDetails(){

        organizationID = organizationID == null ? (organizationModel == null ? null : organizationModel.getOrganizationID()) : organizationID;

        return DataRepository
                .getInstance()
                .getOrganizationDataHandler()
                .getOrganizationDetails(organizationID);
    }

}
