package com.omada.junctionadmin.data.source.providers;

import com.omada.junctionadmin.data.source.InstituteDataSource;
import com.omada.junctionadmin.data.source.NotificationDataSource;
import com.omada.junctionadmin.data.source.OrganizationDataSource;
import com.omada.junctionadmin.data.source.PostDataSource;
import com.omada.junctionadmin.data.source.ShowcaseDataSource;
import com.omada.junctionadmin.data.source.UserDataSource;
import com.omada.junctionadmin.data.source.VenueDataSource;

public interface DataSourceProvider {

    InstituteDataSource getInstituteDataSource();

    NotificationDataSource getNotificationDataSource();

    OrganizationDataSource getOrganizationDataSource();

    UserDataSource getUserDataSource();

    PostDataSource getPostDataSource();

    ShowcaseDataSource getShowcaseDataSource();

    VenueDataSource getVenueDataSource();
}