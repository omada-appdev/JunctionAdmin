package com.omada.junctionadmin.data.models.immutable;

import com.omada.junctionadmin.data.models.external.OrganizationModel;


public final class ImmutableOrganizationModel extends OrganizationModel {

    private ImmutableOrganizationModel(OrganizationModel organizationModel){
        super(organizationModel);
    }

    public static ImmutableOrganizationModel from(OrganizationModel organizationModel) {
        return new ImmutableOrganizationModel(organizationModel);
    }

}
