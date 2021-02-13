package com.omada.junctionadmin.viewmodels.content;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;


public class EventViewHandler {

    private final MutableLiveData<LiveEvent<EventModel>> eventCardDetailsTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<EventModel>> editEventDetailsTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<EventModel>> eventFormTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<EventModel>> upcomingEventDetailsTrigger = new MutableLiveData<>();


    public LiveData<LiveEvent<EventModel>> getEventCardDetailsTrigger() {
        return eventCardDetailsTrigger;
    }

    public MutableLiveData<LiveEvent<EventModel>> getEditEventDetailsTrigger() {
        return editEventDetailsTrigger;
    }

    public LiveData<LiveEvent<EventModel>> getEventFormTrigger() {
        return eventFormTrigger;
    }

    public void goToEventCardDetails(EventModel eventModel) {
        eventCardDetailsTrigger.setValue(new LiveEvent<>(eventModel));
    }

    public final void goToEditEventDetails(EventModel eventModel) {

        String userId = DataRepository.getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getId();
        if (eventModel.getCreator().equals(userId)) {
            editEventDetailsTrigger.setValue(new LiveEvent<>(eventModel));
        }
    }

    public void goToUpcomingEventDetails(EventModel eventModel){
        upcomingEventDetailsTrigger.setValue(new LiveEvent<>(eventModel));
    }

    public void goToEventForm(EventModel eventModel){
        eventFormTrigger.setValue(new LiveEvent<>(eventModel));
    }
}
