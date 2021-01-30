package com.omada.junctionadmin.viewmodels.content;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

public class ArticleViewHandler {

    private MutableLiveData<LiveEvent<ArticleModel>> articleCardDetailsTrigger = new MutableLiveData<>();

    public void goToArticleCardDetails(ArticleModel articleModel){
        articleCardDetailsTrigger.setValue(new LiveEvent<>(articleModel));
    }

    public LiveData<LiveEvent<ArticleModel>> getArticleCardDetailsTrigger(){
        return articleCardDetailsTrigger;
    }
}
