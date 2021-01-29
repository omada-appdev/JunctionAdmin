package com.omada.junctionadmin.data.handler;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.converter.BookingModelConverter;
import com.omada.junctionadmin.data.models.converter.VenueModelConverter;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.internal.remote.BookingModelRemoteDB;
import com.omada.junctionadmin.data.models.internal.remote.VenueModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VenueDataHandler extends BaseDataHandler {

    private final VenueModelConverter venueModelConverter = new VenueModelConverter();
    private final BookingModelConverter bookingModelConverter = new BookingModelConverter();

    // Gets all venues possible for registering an event
    // TODO a more efficient query that orders venues by number of bookings they have on a given day

    public LiveData<LiveEvent<List<VenueModel>>> getAllVenues(
            DataRepository.DataRepositoryAccessIdentifier identifier, String instituteID) {

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

    // Gets all bookings between the start and end of a given day
    public LiveData<LiveEvent<List<Pair<LocalTime, LocalTime>>>> getVenueBookingsOn(
            DataRepository.DataRepositoryAccessIdentifier identifier, Date date, String venueId) {

        MutableLiveData<LiveEvent<List<Pair<LocalTime, LocalTime>>>> venueBookingsLiveData = new MutableLiveData<>();

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("bookings")
                .child(venueId)
                .child(LocalDate.from(date.toInstant()).format(DateTimeFormatter.ISO_DATE))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot == null){
                            return;
                        }
                        List<Pair<LocalTime, LocalTime>> bookingsList = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {

                            Long startTime = (Long) child.child("startTime").getValue();
                            Long endTime = (Long) child.child("endTime").getValue();

                            bookingsList.add(new Pair<>(
                                    LocalTime.from(Instant.ofEpochSecond(startTime)),
                                    LocalTime.from(Instant.ofEpochSecond(endTime))
                            ));
                        }
                        venueBookingsLiveData.setValue(new LiveEvent<>(bookingsList));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Booking", "Error creating booking in realtime database");
                        venueBookingsLiveData.setValue(new LiveEvent<>(null));
                    }
                });

        return venueBookingsLiveData;

    }

    // package private because EventDataHandler needs access to this method but preferably
    // no other packages should be able to access it
    Task<Void> createNewBooking(BookingModel bookingModel, WriteBatch batch) {

        DocumentReference docRef = FirebaseFirestore
                .getInstance()
                .collection("geography")
                .document(bookingModel.getVenue())
                .collection("bookings")
                .document();

        batch.set(docRef, bookingModelConverter.convertExternalToRemoteDBModel(bookingModel));

        LocalDate date =
                bookingModel.getStartTime().toDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();

        Map<String, Long> bookingData = new HashMap<>();
        bookingData.put("startTime", bookingModel.getStartTime().toDate().toInstant().getEpochSecond());
        bookingData.put("endTime", bookingModel.getEndTime().toDate().toInstant().getEpochSecond());

        // Writing booking into JSON database for fast and cheap querying
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child("bookings")
                .child(bookingModel.getVenue())
                .child(date.format(DateTimeFormatter.ISO_DATE))
                .push()
                .setValue(bookingData);

    }

}
