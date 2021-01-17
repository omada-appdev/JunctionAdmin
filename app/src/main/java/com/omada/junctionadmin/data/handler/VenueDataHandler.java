package com.omada.junctionadmin.data.handler;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omada.junctionadmin.data.models.converter.BookingModelConverter;
import com.omada.junctionadmin.data.models.converter.VenueModelConverter;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.internal.remote.BookingModelRemoteDB;
import com.omada.junctionadmin.data.models.internal.remote.VenueModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class VenueDataHandler {

    private final VenueModelConverter venueModelConverter = new VenueModelConverter();
    private final BookingModelConverter bookingModelConverter = new BookingModelConverter();

    public LiveData<LiveEvent<List<VenueModel>>> getAllVenues(String instituteID) {

        MutableLiveData<LiveEvent<List<VenueModel>>> venueModelsLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("geography")
                .whereEqualTo("institute", instituteID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<VenueModel> venueModels = new ArrayList<>();
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                        VenueModel model = venueModelConverter.convertRemoteDBToExternalModel(snapshot.toObject(VenueModelRemoteDB.class));
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

    public LiveData<LiveEvent<List<BookingModel>>> getVenueBookingsOn(Date date, String venueId) {

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
                        bookingModels.add(
                                bookingModelConverter.convertRemoteDBToExternalModel(
                                        documentSnapshot.toObject(BookingModelRemoteDB.class)
                                )
                        );
                    }

                    venueBookingsLiveData.setValue(new LiveEvent<>(bookingModels));

                })
                .addOnFailureListener(e -> {

                });

        return venueBookingsLiveData;

    }

    public void createNewBooking(EventModel eventModel, VenueModel venueModel) {

    }
}
