package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseFeedViewModel extends BaseViewModel {

    private MediatorLiveData<List<PostModel>> loadedShowcaseItems = new MediatorLiveData<>();

    private final OrganizationModel organizationModel;
    private final String showcaseID;

    // To be instantiated through a factory
    public ShowcaseFeedViewModel(OrganizationModel organizationModel, String showcaseID){
        this.organizationModel = organizationModel;
        this.showcaseID = showcaseID;
        initializeDataLoaders();
    }

    private void initializeDataLoaders(){
    }

    public void loadShowcaseItems() {

        LiveData<LiveEvent<List<PostModel>>> source =
                DataRepository
                .getInstance()
                .getPostDataHandler()
                .getShowcasePosts(getDataRepositoryAccessIdentifier(), showcaseID);

        loadedShowcaseItems.addSource(
                source,
                showcaseItems -> {
                    if(showcaseItems != null){
                        List<PostModel> postModels = showcaseItems.getDataOnceAndReset();
                        if(postModels != null){
                            List<PostModel> existingData = loadedShowcaseItems.getValue();
                            if(existingData == null){
                                existingData = new ArrayList<>();
                            }
                            existingData.addAll(postModels);
                            loadedShowcaseItems.setValue(existingData);
                        }
                    }
                    loadedShowcaseItems.removeSource(source);
                }
        );

    }

    public OrganizationModel getOrganizationModel() {
        return organizationModel;
    }

    public LiveData<List<PostModel>> getLoadedShowcaseItems(){
        return loadedShowcaseItems;
    }
}
