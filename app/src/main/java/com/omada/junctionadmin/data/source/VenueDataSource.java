package com.omada.junctionadmin.data.source;

import com.google.android.gms.tasks.Task;
import com.omada.junctionadmin.data.models.external.VenueModel;

import java.util.List;

public interface VenueDataSource extends DataSource {
    Task<VenueModel> getVenueDetails(String venueId);
    Task<List<VenueModel>> getInstituteVenues(String venueId);
}
