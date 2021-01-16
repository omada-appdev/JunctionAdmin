package com.omada.junctionadmin.data.models.converter;

import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.internal.remote.BookingModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableBookingModel;

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
        model.setPhoto(remoteModel.getPhoto());

        model.setVenue(remoteModel.getVenue());
        model.setStartTime(remoteModel.getStartTime());
        model.setEndTime(remoteModel.getEndTime());
        model.setTimeCreated(remoteModel.getTimeCreated());

        model.setCreator(remoteModel.getCreator());

        Map<String, String> creatorData = remoteModel.getCreatorCache();
        model.setCreatorName(creatorData.get("name"));
        model.setCreatorName(creatorData.get("profilePicture"));
        model.setCreatorName(creatorData.get("mail"));
        model.setCreatorName(creatorData.get("phone"));

        return model;
        
    }

    @Override
    public BookingModelRemoteDB convertExternalToRemoteDBModel(BookingModel externalModel) {

        BookingModelRemoteDB model = new BookingModelRemoteDB();

        model.setId(externalModel.getId());
        model.setEvent(externalModel.getEvent());
        model.setEventName(externalModel.getEventName());
        model.setTimeCreated(externalModel.getTimeCreated());
        model.setStartTime(externalModel.getStartTime());
        model.setEndTime(externalModel.getEndTime());
        model.setCreator(externalModel.getCreator());
        model.setPhoto(externalModel.getPhoto());

        Map<String, String> creatorCache = new HashMap<>();
        creatorCache.put("name", externalModel.getCreatorName());
        creatorCache.put("mail", externalModel.getCreatorMail());
        creatorCache.put("phone", externalModel.getCreatorPhone());
        creatorCache.put("profilePicture", externalModel.getCreatorProfilePicture());
        model.setCreatorCache(creatorCache);

        return model;

    }

    @Override
    public Void convertExternalToLocalDBModel(BookingModel externalModel) {
        return null;
    }
}
