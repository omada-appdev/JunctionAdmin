package com.omada.junctionadmin.viewmodels.content;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omada.junctionadmin.data.models.ArticleModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

public class ArticleViewHandler {

    private MutableLiveData<LiveEvent<ArticleModel>> articleDetailsTrigger = new MutableLiveData<>();

    public void goToArticleDetails(ArticleModel articleModel){
        articleDetailsTrigger.setValue(new LiveEvent<>(articleModel));
    }

    public LiveData<LiveEvent<ArticleModel>> getArticleDetailsTrigger(){
        return articleDetailsTrigger;
    }
}
