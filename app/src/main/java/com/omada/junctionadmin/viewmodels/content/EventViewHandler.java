package com.omada.junctionadmin.viewmodels.content;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.EventModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.Map;

public class EventViewHandler {


    private final MutableLiveData<LiveEvent<EventModel>> eventCardDetailsTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<EventModel>> eventFormTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<EventModel>> upcomingEventDetailsTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<EventModel>> attendedEventDetailsTrigger = new MutableLiveData<>();

    private final MutableLiveData<LiveEvent<String>> callOrganizerTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Pair<String, String>>> mailOrganizerTrigger = new MutableLiveData<>();


    public LiveData<LiveEvent<EventModel>> getEventCardDetailsTrigger() {
        return eventCardDetailsTrigger;
    }

    public LiveData<LiveEvent<EventModel>> getEventFormTrigger() {
        return eventFormTrigger;
    }

    public LiveData<LiveEvent<String>> getCallOrganizerTrigger() {
        return callOrganizerTrigger;
    }

    public MutableLiveData<LiveEvent<Pair<String, String>>> getMailOrganizerTrigger() {
        return mailOrganizerTrigger;
    }



    public void goToEventCardDetails(EventModel eventModel){
        eventCardDetailsTrigger.setValue(new LiveEvent<>(eventModel));
    }

    public void goToUpcomingEventDetails(EventModel eventModel){
        upcomingEventDetailsTrigger.setValue(new LiveEvent<>(eventModel));
    }

    public void goToAttendedEventDetails(EventModel eventModel){
        attendedEventDetailsTrigger.setValue(new LiveEvent<>(eventModel));
    }

    public void goToEventForm(EventModel eventModel){
        eventFormTrigger.setValue(new LiveEvent<>(eventModel));
    }

    public void callOrganizer(String organizerNumber){
        callOrganizerTrigger.setValue(new LiveEvent<>(organizerNumber));
    }

    public void mailOrganizer(String eventName, String organizerEmail){
        mailOrganizerTrigger.setValue(new LiveEvent<>(new Pair<>(eventName, organizerEmail)));
    }

    public void registerForEvent(EventModel eventModel, Map <String, Map <String, Map<String, String>>> responses){
    }

    public void goToEventFormResponses(EventModel eventModel){

    }
}
