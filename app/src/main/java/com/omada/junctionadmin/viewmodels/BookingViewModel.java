package com.omada.junctionadmin.viewmodels;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

public class BookingViewModel extends BaseViewModel {

    private LocalDate bookingDate;
    private MediatorLiveData<List<VenueModel>> loadedInstituteVenues = new MediatorLiveData<>();

    public void loadAllVenues() {

        String instituteId = DataRepository.getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        LiveData<LiveEvent<List<VenueModel>>> source = DataRepository
                .getInstance()
                .getVenueDataHandler()
                .getAllVenues(getDataRepositoryAccessIdentifier(), instituteId);

        loadedInstituteVenues.addSource(source, venueModelsLiveEvent -> {

            if(venueModelsLiveEvent == null){
                loadedInstituteVenues.setValue(null);
            }
            else {
                List<VenueModel> venueModels = venueModelsLiveEvent.getDataOnceAndReset();
                if(loadedInstituteVenues.getValue() == null){
                    loadedInstituteVenues.setValue(venueModels);
                }
                else {
                    loadedInstituteVenues.getValue().addAll(venueModels);
                    loadedInstituteVenues.setValue(loadedInstituteVenues.getValue());
                }
            }
            loadedInstituteVenues.removeSource(source);

        });

    }

    // TODO write an efficient query and design a system to count number of bookings
    public void loadAllVenuesSortedByNumberOfBookings() {

    }

    /*
    Take all bookings and return pairs of FREE time slots after performing some manipulation
     */
    public LiveData<LiveEvent<List<Pair<LocalTime, LocalTime>>>> getBookings(@Nonnull String venue){

        return Transformations.map(
                DataRepository.getInstance()
                .getVenueDataHandler()
                .getVenueBookingsOn(getDataRepositoryAccessIdentifier(),
                        Date.from(bookingDate.atStartOfDay().toInstant(ZoneOffset.UTC)), venue),

                input -> input
        );

    }

}
