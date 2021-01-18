package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.internal.remote.ArticleModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableArticleModel;

import java.util.HashMap;
import java.util.Map;

public class ArticleModelConverter extends BaseConverter <ArticleModel, ArticleModelRemoteDB, Void>{

    @Override
    public ArticleModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public ArticleModel convertRemoteDBToExternalModel(ArticleModelRemoteDB remoteModel) {

        MutableArticleModel model = new MutableArticleModel();

        model.setId(remoteModel.getId());
        model.setTitle(remoteModel.getTitle());
        model.setText(remoteModel.getText());
        model.setCreator(remoteModel.getCreator());
        model.setCreatorName(remoteModel.getCreatorCache().get("name"));
        model.setCreatorMail(remoteModel.getCreatorCache().get("profilePicture"));
        model.setCreatorPhone(remoteModel.getCreatorCache().get("profilePicture"));
        model.setCreatorProfilePicture(remoteModel.getCreatorCache().get("profilePicture"));
        model.setAuthor(remoteModel.getAuthor());
        model.setImage(remoteModel.getImage());
        model.setTimeCreated(remoteModel.getTimeCreated());

        return model;
    }

    @Override
    public ArticleModelRemoteDB convertExternalToRemoteDBModel(ArticleModel externalModel) {
        ArticleModelRemoteDB modelRemoteDB = new ArticleModelRemoteDB();

        modelRemoteDB.setId(externalModel.getId());
        modelRemoteDB.setCreator(externalModel.getCreator());
        modelRemoteDB.setAuthor(externalModel.getAuthor());
        modelRemoteDB.setImage(externalModel.getImage());
        modelRemoteDB.setTitle(externalModel.getTitle());
        modelRemoteDB.setText(externalModel.getText());
        modelRemoteDB.setTimeCreated(externalModel.getTimeCreated());

        Map<String, String> creatorCache = new HashMap<>();
        creatorCache.put("name", externalModel.getCreatorName());
        creatorCache.put("phone", externalModel.getCreatorPhone());
        creatorCache.put("mail", externalModel.getCreatorMail());
        creatorCache.put("profilePicture", externalModel.getCreatorProfilePicture());
        modelRemoteDB.setCreatorCache(creatorCache);

        return modelRemoteDB;
    }

    @Override
    public Void convertExternalToLocalDBModel(ArticleModel externalModel) {
        return null;
    }

    @Override
    public Map<String, Object> convertExternalToMapObject(ArticleModel externalModel) {
        return null;
    }

    @Override
    public ArticleModel convertMapObjectToExternal(Map<String, Object> externalModel) {
        return null;
    }
}
