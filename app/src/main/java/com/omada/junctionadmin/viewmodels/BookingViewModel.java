package com.omada.junctionadmin.viewmodels;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;
import com.omada.junctionadmin.utils.TransformUtilities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class BookingViewModel extends BaseViewModel {

    // In UTC
    String instituteId;
    Boolean isInstituteAdmin;
    private LocalDateTime bookingDate;
    private MediatorLiveData<List<VenueModel>> loadedInstituteVenues = new MediatorLiveData<>();

    public BookingViewModel() {

        OrganizationModel model = MainDataRepository.getInstance().getUserDataRepository().getCurrentUserModel();
        instituteId = model.getInstitute();
        isInstituteAdmin = Boolean.TRUE.equals(model.isInstituteAdmin());
    }

    // Mapping from booking date (as EpochSecond) to venues to bookings list
    private Map<Long, Map<String, LiveData<List<Pair<ZonedDateTime, ZonedDateTime>>>>> cachedBookings = new HashMap<>();

    public ZonedDateTime getZonedBookingDate() {
        if(bookingDate == null) {
            return null;
        }
        return TransformUtilities.convertUtcLocalDateTimeToSystemZone(bookingDate);
    }

    public final void setBookingDate(@Nonnull LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
        if(cachedBookings.get(this.bookingDate.atZone(ZoneId.of("UTC")).toEpochSecond()) != null) {
            return;
        }
        cachedBookings.put(this.bookingDate.atZone(ZoneId.of("UTC")).toEpochSecond(), new HashMap<>());
    }

    public void resetBookingDate() {
        bookingDate = null;
    }

    public LiveData<List<VenueModel>> getLoadedInstituteVenues() {
        return loadedInstituteVenues;
    }

    public void loadAllVenues() {

        LiveData<LiveEvent<List<VenueModel>>> source = MainDataRepository
                .getInstance()
                .getVenueDataRepository()
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

    public String getInstituteId() {
        return instituteId;
    }

    public Boolean isInstituteAdmin() {
        return isInstituteAdmin;
    }

    // TODO write an efficient query and design a system to count number of bookings
    public void loadAllVenuesSortedByNumberOfBookings() {

    }

    public boolean checkIfBookingCached(String venue) {

        LiveData<List<Pair<ZonedDateTime, ZonedDateTime>>>  bookingsAtDate;
        try {
            bookingsAtDate = cachedBookings
                    .get(bookingDate.atZone(ZoneId.of("UTC")).toEpochSecond())
                    .get(venue);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return bookingsAtDate != null;
    }
    /*
    Take all bookings and return pairs of FREE time slots after performing some filtering and manipulation
    CONVERT ALL TIMES TO LOCAL BECAUSE THEY ARE STORED AS UTC IN THE SERVER
     */
    public LiveData<List<Pair<ZonedDateTime, ZonedDateTime>>> getBookings(@Nonnull String venue){

        try {
            LiveData<List<Pair<ZonedDateTime, ZonedDateTime>>> bookingsAtDate = cachedBookings
                    .get(bookingDate.atZone(ZoneId.of("UTC")).toEpochSecond())
                    .get(venue);
            if (bookingsAtDate == null) {
                throw new NullPointerException();
            }
            else {
                return bookingsAtDate;
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("Venues", "Bookings were not cached, retrieving from database");
        }

        LiveData<LiveEvent<List<Pair<LocalDateTime, LocalDateTime>>>> allBookingsOnDate =
                MainDataRepository.getInstance()
                .getVenueDataRepository()
                .getVenueBookingsOn(getDataRepositoryAccessIdentifier(), bookingDate, venue);


        // bookingDate variable is in UTC
        LiveData<List<Pair<ZonedDateTime, ZonedDateTime>>> bookingTransformation =
                Transformations.map(
                allBookingsOnDate,

                input -> {
                    if(input == null) {
                        return null;
                    }
                    List<Pair<LocalDateTime, LocalDateTime>> bookingsList = input.getDataOnceAndReset();
                    if(bookingsList != null) {

                        LocalDateTime startBookingDay = bookingDate.toLocalDate().atStartOfDay();
                        LocalDateTime endBookingDay = bookingDate.toLocalDate().plusDays(1).atStartOfDay();

                        List<Pair<ZonedDateTime, ZonedDateTime>> freeSlots = new ArrayList<>();

                        for (Pair<LocalDateTime, LocalDateTime> booking : bookingsList) {
                            Pair<ZonedDateTime, ZonedDateTime> bookingZoned = trimToCurrentDay(booking, startBookingDay, endBookingDay);
                            if(bookingZoned != null) {
                                freeSlots.add(bookingZoned);
                            }
                        }

                        Collections.sort(freeSlots, (o1, o2) -> {
                            int res = 0;
                            res = o1.first.isBefore(o2.first) ? -1 : res;
                            res = o1.first.isAfter(o2.first) ? 1 : res;
                            return res;
                        });
                        return freeSlots;
                    }
                    return null;
                }

        );

        try {
            cachedBookings
                    .get(bookingDate.atZone(ZoneId.of("UTC")).toEpochSecond())
                    .put(venue, bookingTransformation);
        } catch (NullPointerException e) {
            Log.e("Venues", "Booking date was not stored in cache please ensure you used the setter");
            throw new RuntimeException();
        }

        return bookingTransformation;
    }

    /*
     returns null if completely out of bounds
     returns trimmed date if partially out of bounds
     returns booking unchanged if valid
    */
    private static Pair<ZonedDateTime, ZonedDateTime> trimToCurrentDay(
            Pair<LocalDateTime, LocalDateTime> bookedSlot,
            LocalDateTime startBookingDay, LocalDateTime endBookingDay) {

        LocalDateTime first = bookedSlot.first;
        LocalDateTime second = bookedSlot.second;

        // checking if both dates are on same side of bounds
        if ((first.isBefore(startBookingDay) && second.isBefore(startBookingDay))
            || (first.isAfter(endBookingDay) && second.isAfter(endBookingDay))) {

            return null;
        }
        // checking if both are inside bounds
        else if (first.isAfter(startBookingDay) && second.isBefore(endBookingDay)) {
            return new Pair<>(
                    TransformUtilities.convertUtcLocalDateTimeToSystemZone(first),
                    TransformUtilities.convertUtcLocalDateTimeToSystemZone(second)
            );
        }
        else {
            if(first.isBefore(startBookingDay)) {
                first = LocalDateTime.from(startBookingDay);
            }
            if (second.isAfter(endBookingDay)) {
                second = LocalDateTime.from(endBookingDay);
            }

            return new Pair<>(
                    TransformUtilities.convertUtcLocalDateTimeToSystemZone(first),
                    TransformUtilities.convertUtcLocalDateTimeToSystemZone(second)
            );
        }

    }

}
