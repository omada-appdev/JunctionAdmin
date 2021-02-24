package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.application.JunctionAdminApplication;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.utils.FileUtilities;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import me.shouheng.utils.UtilsApp;


public class SplashViewModel extends BaseViewModel {

    private final LiveData<LiveEvent<UserDataRepository.AuthStatus>> authResultAction;
    private final LiveData<LiveEvent<OrganizationModel>> signedInUserAction;

    public SplashViewModel() {

        // clear all files on startup
        UtilsApp.init(JunctionAdminApplication.getInstance());
        FileUtilities.Companion.clearTemporaryFiles();

        authResultAction = Transformations.map(
                MainDataRepository.getInstance()
                        .getUserDataRepository()
                        .getAuthResponseNotifier(),

                authResponse->{
                    if(authResponse == null){
                        return null;
                    }

                    UserDataRepository.AuthStatus receivedAuthResponse = authResponse.getDataOnceAndReset();
                    if(receivedAuthResponse == null) {
                        return null;
                    }
                    switch (receivedAuthResponse){
                        case CURRENT_USER_SUCCESS:
                        case CURRENT_USER_FAILURE:
                        case LOGIN_SUCCESS:
                        case LOGIN_FAILURE:
                            break;
                    }
                    return new LiveEvent<>(receivedAuthResponse);
                });

        signedInUserAction = Transformations.map(
                MainDataRepository.getInstance()
                        .getUserDataRepository()
                        .getSignedInUserNotifier(),

                userModelLiveEvent->{
                    if(userModelLiveEvent == null){
                        return null;
                    }

                    OrganizationModel signedInUser = userModelLiveEvent.getDataOnceAndReset();
                    if(signedInUser == null) return null;
                    else return new LiveEvent<>(signedInUser);
                });
    }

    public void getCurrentUser(){
        MainDataRepository.getInstance()
                .getUserDataRepository()
                .getCurrentUserDetails();
    }

    public LiveData<LiveEvent<UserDataRepository.AuthStatus>> getAuthResultAction() {
        return authResultAction;
    }

    public LiveData<LiveEvent<OrganizationModel>> getSignedInUserAction() {
        return signedInUserAction;
    }
}