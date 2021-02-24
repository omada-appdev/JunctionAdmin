package com.omada.junctionadmin.data.source;

import com.google.android.gms.tasks.Task;
import com.omada.junctionadmin.data.models.external.OrganizationModel;

import java.util.List;

public interface OrganizationDataSource extends DataSource {
    Task<OrganizationModel> getOrganizationDetails();
    Task<List<OrganizationModel>> getInstituteOrganizations();
}
