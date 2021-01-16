package com.omada.junctionadmin.data.models.external;

import com.google.firebase.Timestamp;

public abstract class PostModel extends BaseModel {

    protected String type;

    protected String title;
    protected String image;

    protected Timestamp timeCreated;

    protected String creator;
    protected String creatorName;
    protected String creatorProfilePicture;
    protected String creatorMail;
    protected String creatorPhone;

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public String getCreator() {
        return creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorPhone() {
        return creatorPhone;
    }

    public String getCreatorMail() {
        return creatorMail;
    }

    public String getCreatorProfilePicture() {
        return creatorProfilePicture;
    }
}
