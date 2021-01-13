package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.models.BaseModel;
import com.omada.junctionadmin.data.models.EventModel;
import com.omada.junctionadmin.data.models.OrganizationModel;
import com.omada.junctionadmin.data.models.ShowcaseModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;


// NOTE this class is not supposed to be used at the activity scope because it is stateful

public class OrganizationProfileViewModel extends ViewModel {

    private String organizationID;
    private OrganizationModel organizationModel;

    private final MediatorLiveData<List<BaseModel>> loadedOrganizationHighlights = new MediatorLiveData<>();
    private final MediatorLiveData<List<ShowcaseModel>> loadedOrganizationShowcases = new MediatorLiveData<>();

    public void setOrganizationID(String orgID){
        organizationID = orgID;
    }

    public void setOrganizationModel(OrganizationModel organizationModel) {
        this.organizationModel = organizationModel;
    }

    public OrganizationModel getOrganizationModel(){
        return organizationModel;
    }

    public String getOrganizationID(){
        return organizationID;
    }

    public LiveData<LiveEvent<OrganizationModel>> getOrganizationDetails(){
    }

    public void loadOrganizationHighlights(){
    }

    public void loadOrganizationShowcases(){
    }

    public LiveData<List<BaseModel>> getLoadedOrganizationHighlights(){
        return loadedOrganizationHighlights;
    }

    public LiveData<List<ShowcaseModel>> getLoadedOrganizationShowcases(){
        return loadedOrganizationShowcases;
    }

    public boolean getFollowingStatus(){

    }

    public void updateFollowingStatus(boolean following){
    }

}
