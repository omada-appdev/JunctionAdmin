package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.internal.remote.EventModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableEventModel;

import java.util.HashMap;
import java.util.Map;

public class EventModelConverter extends BaseConverter <EventModel, EventModelRemoteDB, Void> {

    @Override
    public EventModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public EventModel convertRemoteDBToExternalModel(EventModelRemoteDB remoteModel) {

        MutableEventModel model = new MutableEventModel();

        model.setId(remoteModel.getId());
        model.setTitle(remoteModel.getTitle());
        model.setDescription(remoteModel.getDescription());

        model.setImage(remoteModel.getImage());

        model.setCreator(remoteModel.getCreator());
        model.setCreatorName(remoteModel.getCreatorCache().get("name"));
        model.setCreatorPhone(remoteModel.getCreatorCache().get("phone"));
        model.setCreatorProfilePicture(remoteModel.getCreatorCache().get("profilePicture"));
        model.setCreatorMail(remoteModel.getCreatorCache().get("mail"));

        model.setForm(remoteModel.getForm());

        model.setStatus(remoteModel.getStatus());
        model.setStartTime(remoteModel.getStartTime());
        model.setEndTime(remoteModel.getEndTime());
        model.setTimeCreated(remoteModel.getTimeCreated());

        model.setVenue(remoteModel.getVenue());
        model.setVenueName(remoteModel.getVenueCache().get("name"));
        model.setVenueAddress(remoteModel.getVenueCache().get("address"));

        model.setTags(remoteModel.getTags());

        return model;
    }

    @Override
    public EventModelRemoteDB convertExternalToRemoteDBModel(EventModel externalModel) {
        
        EventModelRemoteDB model = new EventModelRemoteDB();

        model.setId(externalModel.getId());
        model.setTitle(externalModel.getTitle());
        model.setDescription(externalModel.getDescription());

        model.setImage(externalModel.getImage());

        model.setCreator(externalModel.getCreator());

        Map<String, String> creatorCache = new HashMap<>();
        creatorCache.put("name", externalModel.getCreatorName());
        creatorCache.put("phone", externalModel.getCreatorPhone());
        creatorCache.put("mail", externalModel.getCreatorMail());
        creatorCache.put("profilePicture", externalModel.getCreatorProfilePicture());

        model.setCreatorCache(creatorCache);

        model.setForm(externalModel.getForm());

        model.setStatus(externalModel.getStatus());
        model.setStartTime(externalModel.getStartTime());
        model.setEndTime(externalModel.getEndTime());
        model.setTimeCreated(externalModel.getTimeCreated());

        model.setVenue(externalModel.getVenue());


        Map<String, String> venueCache = new HashMap<>();
        venueCache.put("name", externalModel.getVenueName());
        venueCache.put("address", externalModel.getVenueAddress());
        model.setVenueCache(venueCache);

        model.setTags(externalModel.getTags());

        return model;
    }

    @Override
    public Void convertExternalToLocalDBModel(EventModel externalModel) {
        return null;
    }
}
