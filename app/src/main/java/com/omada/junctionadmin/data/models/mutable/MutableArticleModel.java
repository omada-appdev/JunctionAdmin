package com.omada.junctionadmin.data.models.mutable;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.external.ArticleModel;

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

    public void setImage(String image) {
        this.image = image;
    }

    public void setTimeCreated(Timestamp timeCreated){
        this.timeCreated = timeCreated;
    }
}
