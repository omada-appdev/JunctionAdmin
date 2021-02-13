package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseFeedViewModel extends BaseViewModel {

    private MediatorLiveData<List<PostModel>> loadedShowcaseItems = new MediatorLiveData<>();

    private final ShowcaseModel showcaseModel;

    // To be instantiated through a factory
    public ShowcaseFeedViewModel(ShowcaseModel showcaseModel){
        this.showcaseModel = showcaseModel;
        initializeDataLoaders();
    }

    private void initializeDataLoaders(){
    }

    public void loadShowcaseItems() {

        LiveData<LiveEvent<List<PostModel>>> source =
                DataRepository
                .getInstance()
                .getPostDataHandler()
                .getShowcasePosts(getDataRepositoryAccessIdentifier(), showcaseModel.getId());

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

    public LiveData<List<PostModel>> getLoadedShowcaseItems(){
        return loadedShowcaseItems;
    }

    public ShowcaseModel getShowcaseModel() {
        return showcaseModel;
    }
}
