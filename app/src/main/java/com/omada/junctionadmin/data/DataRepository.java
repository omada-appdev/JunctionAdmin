package com.omada.junctionadmin.data;

import com.omada.junctionadmin.data.handler.AppDataHandler;
import com.omada.junctionadmin.data.handler.ArticleDataHandler;
import com.omada.junctionadmin.data.handler.EventDataHandler;
import com.omada.junctionadmin.data.handler.OrganizationDataHandler;
import com.omada.junctionadmin.data.handler.PostDataHandler;
import com.omada.junctionadmin.data.handler.ShowcaseDataHandler;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.handler.VenueDataHandler;

public class DataRepository {

    private static DataRepository dataRepository;

    private final AppDataHandler appDataHandler = new AppDataHandler();
    private final UserDataHandler userDataHandler = new UserDataHandler();
    private final OrganizationDataHandler organizationDataHandler = new OrganizationDataHandler();
    private final ArticleDataHandler articleDataHandler = new ArticleDataHandler();
    private final ShowcaseDataHandler showcaseDataHandler = new ShowcaseDataHandler();
    private final PostDataHandler postDataHandler = new PostDataHandler();
    private final VenueDataHandler venueDataHandler = new VenueDataHandler();

    //this is only for events
    private final EventDataHandler eventDataHandler = new EventDataHandler();


    private DataRepository() {
        //class constructor init firestore and local db etc here if required
    }

    public static DataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new DataRepository();
        }
        return dataRepository;
    }

    public AppDataHandler getAppDataHandler() {
        return appDataHandler;
    }

    public UserDataHandler getUserDataHandler() {
        return userDataHandler;
    }

    public EventDataHandler getEventDataHandler() {
        return eventDataHandler;
    }

    public ArticleDataHandler getArticleDataHandler() {
        return articleDataHandler;
    }

    public OrganizationDataHandler getOrganizationDataHandler() {
        return organizationDataHandler;
    }

    public ShowcaseDataHandler getShowcaseDataHandler() {
        return showcaseDataHandler;
    }

    public PostDataHandler getPostDataHandler() {
        return postDataHandler;
    }

    public VenueDataHandler getVenueDataHandler() {
        return venueDataHandler;
    }
}

