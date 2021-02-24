package com.omada.junctionadmin.data.source;

import com.google.android.gms.tasks.Task;
import com.omada.junctionadmin.data.models.external.InstituteModel;

public interface InstituteDataSource extends DataSource {
    Task<InstituteModel> getInstituteDetails();
}
