package com.omada.junctionadmin.viewmodels.content;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import javax.annotation.Nonnull;

public class OrganizationViewHandler {

    private final MutableLiveData<LiveEvent<OrganizationModel>> organizationModelDetailsTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<ShowcaseModel>> organizationShowcaseDetailsTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<String>> organizationDetailsTrigger = new MutableLiveData<>();

    private final MutableLiveData<LiveEvent<String>> callOrganizationTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Pair<String, String>>> mailOrganizationTrigger = new MutableLiveData<>();

    public LiveData<LiveEvent<String>> getCallOrganizationTrigger() {
        return callOrganizationTrigger;
    }

    public MutableLiveData<LiveEvent<Pair<String, String>>> getMailOrganizationTrigger() {
        return mailOrganizationTrigger;
    }

    public void goToOrganization(String organizationID){
        organizationDetailsTrigger.setValue(new LiveEvent<>(organizationID));
    }

    public void goToOrganization(@Nonnull OrganizationModel organizationModel){
        organizationModelDetailsTrigger.setValue(new LiveEvent<>(organizationModel));
    }

    public void goToOrganizationShowcase(ShowcaseModel showcaseModel){
        organizationShowcaseDetailsTrigger.setValue(new LiveEvent<>(showcaseModel));
    }

    public LiveData<LiveEvent<String>> getOrganizationDetailsTrigger() {
        return organizationDetailsTrigger;
    }

    public LiveData<LiveEvent<OrganizationModel>> getOrganizationModelDetailsTrigger() {
        return organizationModelDetailsTrigger;
    }

    public LiveData<LiveEvent<ShowcaseModel>> getOrganizationShowcaseDetailsTrigger() {
        return organizationShowcaseDetailsTrigger;
    }

    public void callOrganization(String organizerNumber){
        callOrganizationTrigger.setValue(new LiveEvent<>(organizerNumber));
    }

    public void mailOrganization(String eventName, String organizerEmail){
        mailOrganizationTrigger.setValue(new LiveEvent<>(new Pair<>(eventName, organizerEmail)));
    }
}
