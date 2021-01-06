package com.omada.junctionadmin.viewmodels.content;

import androidx.lifecycle.MutableLiveData;

import com.omada.junctionadmin.data.models.OrganizationModel;
import com.omada.junctionadmin.data.models.ShowcaseModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import javax.annotation.Nonnull;

public class OrganizationViewHandler {

    private final MutableLiveData<LiveEvent<OrganizationModel>> organizationModelDetailsTrigger = new MutableLiveData<>();

    public void goToOrganization(){
        //organizationModelDetailsTrigger.setValue(new LiveEvent<>(institue ID));
    }

}
