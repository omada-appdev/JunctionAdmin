package com.omada.junctionadmin.data.sink;

public interface DataSinkProvider {

    InstituteDataSink getInstituteDataSink();

    NotificationDataSink getNotificationDataSink();

    OrganizationDataSink getOrganizationDataSink();

    PostDataSink getPostDataSink();

    UserDataSink getUserDataSink();

    ShowcaseDataSink getShowcaseDataSink();

    VenueDataSink getVenueDataSink();
}
