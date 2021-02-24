package com.omada.junctionadmin.data.source;

import com.google.android.gms.tasks.Task;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;

import java.util.List;

public interface ShowcaseDataSource extends DataSource {
    Task<ShowcaseModel> getShowcaseDetails(String showcaseId);
    Task<List<ShowcaseModel>> getOrganizationShowcases(String organizationId);
}
