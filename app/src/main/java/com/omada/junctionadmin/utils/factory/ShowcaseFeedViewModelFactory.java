package com.omada.junctionadmin.utils.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.viewmodels.ShowcaseFeedViewModel;

public class ShowcaseFeedViewModelFactory implements ViewModelProvider.Factory {

    private final ShowcaseModel showcaseModel;


    public ShowcaseFeedViewModelFactory(ShowcaseModel showcaseModel) {
        this.showcaseModel = showcaseModel;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ShowcaseFeedViewModel(showcaseModel);
    }
}