package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.List;

public class OrganizationProfileViewModel extends BaseViewModel {

    protected String organizationID;

    protected OrganizationModel organizationModel;

    protected final MediatorLiveData<List<PostModel>> loadedOrganizationHighlights = new MediatorLiveData<>();
    protected final MediatorLiveData<List<ShowcaseModel>> loadedOrganizationShowcases = new MediatorLiveData<>();

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationModel(OrganizationModel organizationModel) {
        this.organizationModel = organizationModel;
    }
    public OrganizationModel getOrganizationModel(){
        return this.organizationModel;
    }

    public LiveData<LiveEvent<OrganizationModel>> getOrganizationDetails(){

        if(organizationModel != null){
            MutableLiveData<LiveEvent<OrganizationModel>> organizationDetailsLiveData = new MutableLiveData<>();
            organizationDetailsLiveData.setValue(new LiveEvent<>(organizationModel));
            return organizationDetailsLiveData;
        }
        return Transformations.map(
                MainDataRepository.getInstance().getOrganizationDataRepository().getOrganizationDetails(organizationID),
                input -> {
                    if(input == null){
                        return null;
                    }
                    else {
                        OrganizationModel model = input.getDataOnceAndReset();
                        organizationModel = model;
                        return new LiveEvent<>(model);
                    }
                }
        );
    }

    public void loadOrganizationHighlights() {

        LiveData<LiveEvent<List<PostModel>>> source =
                MainDataRepository
                        .getInstance()
                        .getPostDataRepository()
                        .getOrganizationHighlights(getDataRepositoryAccessIdentifier(), organizationID);

        loadedOrganizationHighlights.addSource(
                source,
                postModelsLiveEvent -> {

                    // Multiple calls or faulty pagination implementations might result in data contention
                    synchronized (this) {
                        if (postModelsLiveEvent != null) {
                            List<PostModel> postModels = postModelsLiveEvent.getDataOnceAndReset();
                            if (postModels != null) {

                                // If some data already exists, append this to the existing data
                                if (loadedOrganizationHighlights.getValue() != null) {
                                    loadedOrganizationHighlights.getValue().addAll(postModels);
                                    postModels = loadedOrganizationHighlights.getValue();
                                }
                                loadedOrganizationHighlights.setValue(postModels);
                            }
                            else {
                            }
                        }
                        else {
                        }
                        loadedOrganizationHighlights.removeSource(source);
                    }

                }
        );

    }

    public void loadOrganizationShowcases() {

        LiveData<LiveEvent<List<ShowcaseModel>>> source =
                MainDataRepository
                        .getInstance()
                        .getShowcaseDataRepository()
                        .getOrganizationShowcases(getDataRepositoryAccessIdentifier(), organizationID);

        loadedOrganizationShowcases.addSource(
                source,
                showcaseModelsLiveEvent -> {

                    // Multiple calls or faulty pagination implementations might result in data contention
                    synchronized (this) {
                        if (showcaseModelsLiveEvent != null) {
                            List<ShowcaseModel> showcaseModels = showcaseModelsLiveEvent.getDataOnceAndReset();
                            if (showcaseModels != null) {

                                // If some data already exists, append this to the existing data
                                if (loadedOrganizationShowcases.getValue() != null) {
                                    loadedOrganizationShowcases.getValue().addAll(showcaseModels);
                                    showcaseModels = loadedOrganizationShowcases.getValue();
                                }
                                loadedOrganizationShowcases.setValue(showcaseModels);
                            }
                            else {
                            }
                        }
                        else {
                        }
                        //loadedOrganizationShowcases.removeSource(source);
                    }

                }
        );

    }

    public LiveData<List<PostModel>> getLoadedOrganizationHighlights(){
        return loadedOrganizationHighlights;
    }

    public LiveData<List<ShowcaseModel>> getLoadedOrganizationShowcases(){
        return loadedOrganizationShowcases;
    }
}
