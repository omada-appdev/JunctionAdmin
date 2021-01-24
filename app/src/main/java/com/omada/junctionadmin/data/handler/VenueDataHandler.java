package com.omada.junctionadmin.data.handler;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.converter.BookingModelConverter;
import com.omada.junctionadmin.data.models.converter.VenueModelConverter;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.internal.remote.BookingModelRemoteDB;
import com.omada.junctionadmin.data.models.internal.remote.VenueModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;


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
    public LiveData<LiveEvent<List<BookingModel>>> getVenueBookingsOn(
            DataRepository.DataRepositoryAccessIdentifier identifier, Date date, String venueId) {

        MutableLiveData<LiveEvent<List<BookingModel>>> venueBookingsLiveData = new MutableLiveData<>();

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Timestamp lowerBoundDate = new Timestamp(calendar.getTime());

        calendar.roll(Calendar.DATE, 1);
        Timestamp upperBoundDate = new Timestamp(calendar.getTime());

        FirebaseFirestore
                .getInstance()
                .collection("geography")
                .document(venueId)
                .collection("bookings")
                .whereLessThan("startTime", upperBoundDate)
                .whereGreaterThan("startTime", lowerBoundDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<BookingModel> bookingModels = new ArrayList<>();

                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {

                        BookingModelRemoteDB modelRemoteDB = documentSnapshot.toObject(BookingModelRemoteDB.class);
                        if (modelRemoteDB == null) {
                            continue;
                        }
                        modelRemoteDB.setId(documentSnapshot.getId());
                        bookingModels.add(
                                bookingModelConverter.convertRemoteDBToExternalModel(modelRemoteDB)
                        );
                    }
                    venueBookingsLiveData.setValue(new LiveEvent<>(bookingModels));
                })
                .addOnFailureListener(e -> {
                    Log.e("Venue", "Failed to retrieve venue bookings on given date");
                    venueBookingsLiveData.setValue(null);
                });

        return venueBookingsLiveData;

    }

    // package private because EventDataHandler needs access to this method but preferably
    // no other packages should be able to access it
    void createNewBooking(BookingModel bookingModel, WriteBatch batch) {

        DocumentReference docRef = FirebaseFirestore
                .getInstance()
                .collection("venues")
                .document(bookingModel.getVenue())
                .collection("bookings")
                .document();

        batch.set(docRef, bookingModelConverter.convertExternalToRemoteDBModel(bookingModel));
        
    }

}
