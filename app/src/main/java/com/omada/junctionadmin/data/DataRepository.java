package com.omada.junctionadmin.data;

import com.omada.junctionadmin.data.repository.AppDataRepository;
import com.omada.junctionadmin.data.repository.ArticleDataRepository;
import com.omada.junctionadmin.data.repository.EventDataRepository;
import com.omada.junctionadmin.data.sink.remote.RemoteImageDataSink;
import com.omada.junctionadmin.data.repository.InstituteDataRepository;
import com.omada.junctionadmin.data.repository.OrganizationDataRepository;
import com.omada.junctionadmin.data.repository.PostDataRepository;
import com.omada.junctionadmin.data.repository.ShowcaseDataRepository;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.data.repository.VenueDataRepository;

public interface DataRepository {

    AppDataRepository getAppDataRepository();

    UserDataRepository getUserDataRepository();

    EventDataRepository getEventDataRepository();

    ArticleDataRepository getArticleDataRepository();

    OrganizationDataRepository getOrganizationDataRepository();

    ShowcaseDataRepository getShowcaseDataRepository();

    PostDataRepository getPostDataRepository();

    VenueDataRepository getVenueDataRepository();

    InstituteDataRepository getInstituteDataRepository();

    RemoteImageDataSink getRemoteImageDataSink();
}
