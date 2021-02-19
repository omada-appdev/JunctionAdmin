package com.omada.junctionadmin.data.models.internal.remote;

import com.google.firebase.database.IgnoreExtraProperties;
import com.omada.junctionadmin.data.models.internal.BaseModelInternal;

import java.time.Instant;
import java.util.Map;

public class NotificationModelRemoteDB extends BaseModelInternal {

    protected String notificationType;
    protected String sourceType;
    protected String source;
    protected String title;
    protected String text;
    protected String status;
    protected Map<String , Object> data;

    protected Long timeUpdated;

    // set default values here
    public NotificationModelRemoteDB() {
        timeUpdated = Instant.now().toEpochMilli();
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getStatus() {
        return status;
    }

    public Long getTimeUpdated() {
        return timeUpdated;
    }

    public Map<String, Object> getData() {
        return data;
    }


    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setTimeUpdated(Long timeUpdated) {
        this.timeUpdated = timeUpdated;
    }
}
