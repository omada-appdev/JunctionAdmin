package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

public class UserProfileViewModel extends OrganizationProfileViewModel {

    public LiveData<LiveEvent<Boolean>> updateDetails() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> createEvent() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> createArticle() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> updateHighlights() {
        return Transformations.map(null, null);
    }

    // Update both showcase details and showcase items
    public LiveData<LiveEvent<Boolean>> updateShowcase() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> updateEvent() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> updateArticle() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> deletePost() {
        return Transformations.map(null, null);
    }

}
