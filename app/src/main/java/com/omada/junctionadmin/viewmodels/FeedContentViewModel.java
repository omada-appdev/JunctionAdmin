package com.omada.junctionadmin.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.utils.taskhandler.LiveEvent;
import com.omada.junctionadmin.viewmodels.content.ArticleViewHandler;
import com.omada.junctionadmin.viewmodels.content.EventViewHandler;
import com.omada.junctionadmin.viewmodels.content.OrganizationViewHandler;

public class FeedContentViewModel extends BaseViewModel {

    private final EventViewHandler eventViewHandler = new EventViewHandler();
    private final OrganizationViewHandler organizationViewHandler = new OrganizationViewHandler();
    private final ArticleViewHandler articleViewHandler= new ArticleViewHandler();

    public OrganizationViewHandler getOrganizationViewHandler(){
        return organizationViewHandler;
    }
    public ArticleViewHandler getArticleViewHandler() {
        return articleViewHandler;
    }

    public EventViewHandler getEventViewHandler() {
        return eventViewHandler;
    }

    public void goToOrganisation(@NonNull String organizationID){

    }

}
