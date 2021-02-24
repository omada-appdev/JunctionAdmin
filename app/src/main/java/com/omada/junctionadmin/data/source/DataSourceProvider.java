package com.omada.junctionadmin.data.source;

public interface DataSourceProvider {

    InstituteDataSource getInstituteDataSource();

    NotificationDataSource getNotificationDataSource();

    OrganizationDataSource getOrganizationDataSource();

    UserDataSource getUserDataSource();

    PostDataSource getPostDataSource();

    ShowcaseDataSource getShowcaseDataSource();

    VenueDataSource getVenueDataSource();
}