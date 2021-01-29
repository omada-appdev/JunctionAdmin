package com.omada.junctionadmin.data.models.mutable;

import com.google.common.collect.ImmutableList;
import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.external.ArticleModel;

import java.util.ArrayList;
import java.util.Date;

public class MutableArticleModel extends ArticleModel {

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTags(ImmutableList<String> tags) {
        this.tags = tags;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setCreatorPhone(String creatorPhone) {
        this.creatorPhone = creatorPhone;
    }

    public void setCreatorProfilePicture(String creatorProfilePicture) {
        this.creatorProfilePicture = creatorProfilePicture;
    }

    public void setCreatorMail(String creatorMail) {
        this.creatorMail = creatorMail;
    }

    public void setCreatorInstitute(String institute) {
        this.creatorInstitute = institute;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public void setTimeCreated(Date timeCreated){
        this.timeCreated = timeCreated;
    }
}
