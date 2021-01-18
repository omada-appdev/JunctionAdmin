package com.omada.junctionadmin.viewmodels;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;


public class InstituteViewModel extends ViewModel {

    public LiveData<LiveEvent<List<BookingModel>>> getBookings(@Nonnull String venue, @Nonnull Date date){

        String instituteId = DataRepository.getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        return DataRepository.getInstance()
                .getVenueDataHandler()
                .getVenueBookingsOn(date, venue);

    }

    public void bookVenue(EventModel eventModel) {

    }

    public void getInstituteHighlights() {

    }

    public LiveData<LiveEvent<List<VenueModel>>> getAllVenues() {

        String instituteId = DataRepository.getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        return DataRepository.getInstance()
                .getVenueDataHandler()
                .getAllVenues(instituteId);

    }

    // TODO write an efficient query and design a system to count number of bookings
    public void getAllVenuesSortedByNumberOfBookings() {

    }

    public void updateVenues(List<VenueModel> added, List<VenueModel> removed) {

    }

    public void updateInstituteDetails(@Nonnull InstituteModel instituteModel) {

    }

}
