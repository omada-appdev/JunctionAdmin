package com.omada.junctionadmin.viewmodels;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    CONVERT ALL TIMES TO LOCAL BECAUSE THEY ARE STORED AS UTC IN THE SERVER
     */
    public LiveData<LiveEvent<List<Pair<LocalDateTime, LocalDateTime>>>> getBookings(@Nonnull String venue){

        return Transformations.map(
                DataRepository.getInstance()
                .getVenueDataHandler()
                .getVenueBookingsOn(getDataRepositoryAccessIdentifier(),
                        Date.from(bookingDate.atStartOfDay().toInstant(ZoneOffset.UTC)), venue),

                input -> {

                    if(input == null) {
                        return null;
                    }
                    List<Pair<LocalDateTime, LocalDateTime>> bookingsList = input.getDataOnceAndReset();
                    if(bookingsList != null) {

                        List<Pair<LocalDateTime, LocalDateTime>> freeSlots = new ArrayList<>();

                        if(bookingsList.size() == 0) {
                            freeSlots.add(new Pair<>(
                                    LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
                                    LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
                            ));
                            return new LiveEvent<>(freeSlots);
                        } else if (bookingsList.size() == 1) {

                            freeSlots.add(new Pair<>(
                                    LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
                                    bookingsList.get(0).first
                            ));
                            freeSlots.add(new Pair<>(
                                    bookingsList.get(0).second,
                                    LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
                            ));
                            return new LiveEvent<>(freeSlots);
                        }

                        // Sort by startTime for easy splitting in a linear pass
                        Collections.sort(bookingsList, (o1, o2) -> o1.first.compareTo(o2.first));
                        LocalDateTime timeSweep =
                                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));

                        for(Pair<LocalDateTime, LocalDateTime> occupied : bookingsList) {
                            freeSlots.add(new Pair<>(
                                    timeSweep,
                                    occupied.first
                            ));
                            timeSweep = occupied.second;
                        }
                        freeSlots.add(new Pair<>(
                                timeSweep,
                                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
                        ));

                        return new LiveEvent<>(freeSlots);
                    }
                    return new LiveEvent<>(null);
                }
        );

    }

}
