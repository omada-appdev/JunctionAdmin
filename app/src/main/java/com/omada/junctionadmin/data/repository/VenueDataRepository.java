package com.omada.junctionadmin.data.repository;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.repositorytemp.DataRepositoryAccessIdentifier;
import com.omada.junctionadmin.data.models.converter.BookingModelConverter;
import com.omada.junctionadmin.data.models.converter.VenueModelConverter;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.internal.remote.VenueModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveDataAggregator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VenueDataRepository extends BaseDataHandler {

    private final VenueModelConverter venueModelConverter = new VenueModelConverter();
    private final BookingModelConverter bookingModelConverter = new BookingModelConverter();

    // Gets all venues possible for registering an event
    // TODO a more efficient query that orders venues by number of bookings they have on a given day

    public LiveData<LiveEvent<List<VenueModel>>> getAllVenues(
            DataRepositoryAccessIdentifier identifier, String instituteID) {

        MutableLiveData<LiveEvent<List<VenueModel>>> venueModelsLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("geography")
                .whereEqualTo("institute", instituteID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<VenueModel> venueModels = new ArrayList<>();
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                        VenueModelRemoteDB modelRemoteDB = snapshot.toObject(VenueModelRemoteDB.class);
                        if(modelRemoteDB == null) {
                            continue;
                        }
                        modelRemoteDB.setId(snapshot.getId());
                        VenueModel model = venueModelConverter.convertRemoteDBToExternalModel(modelRemoteDB);
                        venueModels.add(model);
                    }

                    venueModelsLiveData.setValue(new LiveEvent<>(venueModels));
                })
                .addOnFailureListener(e -> {
                    Log.e("Venues", "Error retrieving all venues in institute");
                    venueModelsLiveData.setValue(null);
                });

        return venueModelsLiveData;

    }

    // Gets all bookings on a given day and all bookings one day before and after it
    public LiveData<LiveEvent<List<Pair<LocalDateTime, LocalDateTime>>>> getVenueBookingsOn(
            DataRepositoryAccessIdentifier identifier, LocalDateTime date, String venueId) {

        MediatorLiveData<LiveEvent<List<Pair<LocalDateTime, LocalDateTime>>>> venueBookingsLiveData = new MediatorLiveData<>();
        BookingsAggregator aggregator = new BookingsAggregator(venueBookingsLiveData);

        LocalDateTime providedDate = LocalDateTime.from(date);

        LocalDateTime beforeDate = providedDate.minusDays(1);
        LocalDateTime afterDate = providedDate.plusDays(1);

        getVenueBookingsOn(BookingDayType.BOOKING_DAY_BEFORE, beforeDate, venueId, aggregator);
        getVenueBookingsOn(BookingDayType.BOOKING_DAY_PROVIDED, providedDate, venueId, aggregator);
        getVenueBookingsOn(BookingDayType.BOOKING_DAY_AFTER, afterDate, venueId, aggregator);

        return venueBookingsLiveData;

    }

    private void getVenueBookingsOn(BookingDayType bookingDayType, LocalDateTime date, String venueId, BookingsAggregator aggregator) {

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("bookings")
                .child(venueId)
                .child(date.format(DateTimeFormatter.ISO_DATE))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot == null){
                            return;
                        }
                        List<Pair<LocalDateTime, LocalDateTime>> bookingsList = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {

                            Long startTime = (Long) child.child("startTime").getValue();
                            Long endTime = (Long) child.child("endTime").getValue();

                            bookingsList.add(new Pair<>(
                                    Instant.ofEpochSecond(startTime).atZone(ZoneId.of("UTC")).toLocalDateTime(),
                                    Instant.ofEpochSecond(endTime).atZone(ZoneId.of("UTC")).toLocalDateTime()
                            ));
                        }
                        aggregator.holdData(bookingDayType, bookingsList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Booking", "Error reading booking for "
                                + bookingDayType.name() + " from realtime database");
                    }
                });

    }

    /*
     package private because EventDataHandler needs access to this method but preferably
     no other packages should be able to access it
    */
    Task<Void> createNewBooking(BookingModel bookingModel, WriteBatch batch) {

        DocumentReference docRef = FirebaseFirestore
                .getInstance()
                .collection("geography")
                .document(bookingModel.getVenue())
                .collection("bookings")
                .document(bookingModel.getEvent());

        // getting only date because that is how it will be stored
        LocalDate date =
                bookingModel.getStartTime().atZone(ZoneId.of("UTC")).toLocalDate();

        batch.set(docRef, bookingModelConverter.convertExternalToRemoteDBModel(bookingModel));

        Map<String, Long> bookingData = new HashMap<>();
        bookingData.put("startTime", bookingModel.getStartTime().atZone(ZoneId.of("UTC")).toInstant().getEpochSecond());
        bookingData.put("endTime", bookingModel.getEndTime().atZone(ZoneId.of("UTC")).toInstant().getEpochSecond());

        // Writing booking into JSON database for fast and cheap querying
        // TODO add listeners to handle database failures
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child("bookings")
                .child(date.format(DateTimeFormatter.ISO_DATE))
                .child(bookingModel.getVenue())
                .child(bookingModel.getId())
                .setValue(bookingData);

    }

    void deleteBooking(EventModel eventModel, WriteBatch batch) {

        DocumentReference documentReference =  FirebaseFirestore
                .getInstance()
                .collection("geography")
                .document(eventModel.getVenue())
                .collection("bookings")
                .document(eventModel.getId());

        LocalDate date =
                eventModel.getStartTime().atZone(ZoneId.of("UTC")).toLocalDate();

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("bookings")
                .child(date.format(DateTimeFormatter.ISO_DATE))
                .child(eventModel.getVenue())
                .child(eventModel.getId())
                .removeValue()
                .addOnCompleteListener(aVoid -> Log.e("Booking", "Removed booking from Realtime database"));

        batch.delete(documentReference);
    }

    public LiveData<LiveEvent<Boolean>> updateVenues(ArrayList<VenueModel> added, ArrayList<VenueModel> removed) {

        if(added.size() == 0 && removed.size() == 0) {
            return new MutableLiveData<>(new LiveEvent<>(true));
        }

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        CollectionReference venuesCollection = FirebaseFirestore.getInstance()
                .collection("geography");

        for(VenueModel model : added) {
            batch.set(venuesCollection.document(), venueModelConverter.convertExternalToRemoteDBModel(model));
        }
        for (VenueModel model: removed) {
            batch.delete(venuesCollection.document(model.getId()));
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.e("Venues", "Updated venues successfully");
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    Log.e("Venues", "Failed to update venues");
                    resultLiveData.setValue(new LiveEvent<>(false));
                });

        return resultLiveData;
    }

    private enum BookingDayType {
        BOOKING_DAY_BEFORE,
        BOOKING_DAY_PROVIDED,
        BOOKING_DAY_AFTER
    }

    private static class BookingsAggregator extends LiveDataAggregator
            <BookingDayType, List<Pair<LocalDateTime, LocalDateTime>>, LiveEvent<List<Pair<LocalDateTime, LocalDateTime>>>> {


        public BookingsAggregator(MediatorLiveData<LiveEvent<List<Pair<LocalDateTime, LocalDateTime>>>> destination) {
            super(destination);
        }

        @Override
        protected List<Pair<LocalDateTime, LocalDateTime>> mergeWithExistingData(BookingDayType typeofData, List<Pair<LocalDateTime, LocalDateTime>> oldData, List<Pair<LocalDateTime, LocalDateTime>> newData) {
            oldData.addAll(newData);
            return oldData;
        }

        @Override
        protected boolean checkDataForAggregability() {
            return
                    dataOnHold.get(BookingDayType.BOOKING_DAY_AFTER) != null &&
                    dataOnHold.get(BookingDayType.BOOKING_DAY_PROVIDED) != null &&
                    dataOnHold.get(BookingDayType.BOOKING_DAY_BEFORE) != null;
        }

        @Override
        protected void aggregateData() {

            if(!checkDataForAggregability()) {
                return;
            }

            List<Pair<LocalDateTime, LocalDateTime>> aggregatedBookings = new ArrayList<>();
            aggregatedBookings.addAll(dataOnHold.get(BookingDayType.BOOKING_DAY_BEFORE));
            aggregatedBookings.addAll(dataOnHold.get(BookingDayType.BOOKING_DAY_PROVIDED));
            aggregatedBookings.addAll(dataOnHold.get(BookingDayType.BOOKING_DAY_AFTER));

            destinationLiveData.setValue(new LiveEvent<>(aggregatedBookings));
        }
    }

}
