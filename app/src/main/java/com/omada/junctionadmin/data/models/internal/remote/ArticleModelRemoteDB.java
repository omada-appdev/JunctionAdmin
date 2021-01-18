package com.omada.junctionadmin.data.models.internal.remote;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.omada.junctionadmin.data.models.external.BaseModel;
import com.omada.junctionadmin.data.models.internal.BaseModelInternal;

import java.util.Map;

public class ArticleModelRemoteDB extends BaseModelInternal {

    private final String type = "article";

    private String title;
    private String creator;
    private String text;
    private String author;

    private Timestamp timeCreated;

    private Map<String, String> creatorCache;

    private String image;

    public ArticleModelRemoteDB(){
        super();
    }

    public ArticleModelRemoteDB(String id) {
        super(id);
    }

    @PropertyName("creator")
    public String getCreator() {
        return creator;
    }

    @PropertyName("text")
    public String getText() {
        return text;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    @PropertyName("author")
    public String getAuthor() {
        return author;
    }

    @PropertyName("image")
    public String getImage() {
        return image;
    }

    @PropertyName("creatorCache")
    public Map<String, String> getCreatorCache() {
        return creatorCache;
    }

    @PropertyName("creator")
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @PropertyName("text")
    public void setText(String text) {
        this.text = text;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @PropertyName("image")
    public void setImage(String image) {
        this.image = image;
    }

    @PropertyName("creatorCache")
    public void setCreatorCache(Map<String, String> creatorCache) {
        this.creatorCache = creatorCache;
    }

    @PropertyName("timeCreated")
    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    @PropertyName("timeCreated")
    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @PropertyName("type")
    public String getType() {
        return type;
    }
}