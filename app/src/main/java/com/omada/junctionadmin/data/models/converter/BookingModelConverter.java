package com.omada.junctionadmin.data.models.converter;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.internal.remote.BookingModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableBookingModel;
import com.omada.junctionadmin.utils.TransformUtilities;

import java.util.HashMap;
import java.util.Map;

public class BookingModelConverter extends BaseConverter <BookingModel, BookingModelRemoteDB, Void> {

    @Override
    public BookingModel convertLocalDBToExternalModel(Void localModel) {
        return null;
    }

    @Override
    public BookingModel convertRemoteDBToExternalModel(BookingModelRemoteDB remoteModel) {

        MutableBookingModel model = new MutableBookingModel();

        model.setId(remoteModel.getId());
        model.setEvent(remoteModel.getEvent());
        model.setEventName(remoteModel.getEventName());
        model.setImage(remoteModel.getImage());

        model.setVenue(remoteModel.getVenue());
        model.setStartTime(TransformUtilities.convertTimestampToLocalDateTime(remoteModel.getStartTime()));
        model.setEndTime(TransformUtilities.convertTimestampToLocalDateTime(remoteModel.getEndTime()));
        model.setTimeCreated(remoteModel.getTimeCreated());

        model.setCreator(remoteModel.getCreator());

        Map<String, String> creatorData = remoteModel.getCreatorCache();
        model.setCreatorName(creatorData.get("name"));
        model.setCreatorName(creatorData.get("profilePicture"));
        model.setCreatorName(creatorData.get("mail"));
        model.setCreatorName(creatorData.get("phone"));

        Map<String, String> venueData = remoteModel.getVenueCache();
        model.setVenueName(venueData.get("name"));
        model.setVenueAddress(venueData.get("address"));
        model.setVenueInstitute(venueData.get("institute"));

        return model;
        
    }

    @Override
    public BookingModelRemoteDB convertExternalToRemoteDBModel(BookingModel externalModel) {

        BookingModelRemoteDB model = new BookingModelRemoteDB();

        model.setId(externalModel.getId());
        model.setVenue(externalModel.getVenue());
        model.setEvent(externalModel.getEvent());
        model.setEventName(externalModel.getEventName());
        model.setTimeCreated(externalModel.getTimeCreated());
        model.setStartTime(TransformUtilities.convertUtcLocalDateTimeToTimestamp(externalModel.getStartTime()));
        model.setEndTime(TransformUtilities.convertUtcLocalDateTimeToTimestamp(externalModel.getEndTime()));
        model.setCreator(externalModel.getCreator());
        model.setImage(externalModel.getImage());

        Map<String, String> creatorCache = new HashMap<>();
        creatorCache.put("name", externalModel.getCreatorName());
        creatorCache.put("mail", externalModel.getCreatorMail());
        creatorCache.put("phone", externalModel.getCreatorPhone());
        creatorCache.put("profilePicture", externalModel.getCreatorProfilePicture());
        model.setCreatorCache(creatorCache);

        Map<String, String> venueCache = new HashMap<>();
        venueCache.put("name", externalModel.getVenueName());
        venueCache.put("address", externalModel.getVenueAddress());
        venueCache.put("institute", externalModel.getVenueInstitute());
        model.setVenueCache(venueCache);

        return model;

    }

    @Override
    public Void convertExternalToLocalDBModel(BookingModel externalModel) {
        return null;
    }

    @Override
    public Map<String, Object> convertExternalToMapObject(BookingModel externalModel) {
        return null;
    }

    @Override
    public BookingModel convertMapObjectToExternal(Map<String, Object> externalModel) {
        return null;
    }
}
