package com.omada.junctionadmin.data.sink.providers;

import com.omada.junctionadmin.data.sink.InstituteDataSink;
import com.omada.junctionadmin.data.sink.NotificationDataSink;
import com.omada.junctionadmin.data.sink.OrganizationDataSink;
import com.omada.junctionadmin.data.sink.PostDataSink;
import com.omada.junctionadmin.data.sink.ShowcaseDataSink;
import com.omada.junctionadmin.data.sink.UserDataSink;
import com.omada.junctionadmin.data.sink.VenueDataSink;

public interface DataSinkProvider {

    InstituteDataSink getInstituteDataSink();

    NotificationDataSink getNotificationDataSink();

    OrganizationDataSink getOrganizationDataSink();

    PostDataSink getPostDataSink();

    UserDataSink getUserDataSink();

    ShowcaseDataSink getShowcaseDataSink();

    VenueDataSink getVenueDataSink();
}
