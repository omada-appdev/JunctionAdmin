package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.BaseModel;

import java.util.List;

public class ShowcaseFeedViewModel extends ViewModel {

    private LiveData<List<BaseModel>> loadedShowcaseItems;

    private final String organizationID;
    private final String showcaseID;

    public ShowcaseFeedViewModel(String organizationID, String showcaseID){

        this.organizationID = organizationID;
        this.showcaseID = showcaseID;
    }

    private void initializeDataLoaders(){
    }

    public LiveData<List<BaseModel>> getLoadedShowcaseItems(){
        return loadedShowcaseItems;
    }

}
