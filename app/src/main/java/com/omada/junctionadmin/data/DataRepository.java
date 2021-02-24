package com.omada.junctionadmin.data;

import com.omada.junctionadmin.data.handler.AppDataHandler;
import com.omada.junctionadmin.data.handler.ArticleDataHandler;
import com.omada.junctionadmin.data.handler.EventDataHandler;
import com.omada.junctionadmin.data.handler.ImageUploadHandler;
import com.omada.junctionadmin.data.handler.InstituteDataHandler;
import com.omada.junctionadmin.data.handler.OrganizationDataHandler;
import com.omada.junctionadmin.data.handler.PostDataHandler;
import com.omada.junctionadmin.data.handler.ShowcaseDataHandler;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.handler.VenueDataHandler;

public interface DataRepository {

    AppDataHandler getAppDataHandler();

    UserDataHandler getUserDataHandler();

    EventDataHandler getEventDataHandler();

    ArticleDataHandler getArticleDataHandler();

    OrganizationDataHandler getOrganizationDataHandler();

    ShowcaseDataHandler getShowcaseDataHandler();

    PostDataHandler getPostDataHandler();

    VenueDataHandler getVenueDataHandler();

    InstituteDataHandler getInstituteDataHandler();

    ImageUploadHandler getImageUploadHandler();
}
