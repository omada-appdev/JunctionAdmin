package com.omada.junctionadmin.viewmodels;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.models.BaseModel;
import com.omada.junctionadmin.data.models.BookingModel;
import com.omada.junctionadmin.data.models.EventModel;
import com.omada.junctionadmin.data.models.InstituteModel;
import com.omada.junctionadmin.data.models.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class InstituteViewModel extends ViewModel {


    private LiveData<LiveEvent<List<BaseModel>>> loadedInstituteHighlights;

    public LiveData<LiveEvent<List<BaseModel>>> getLoadedInstituteHighlights() {
        return loadedInstituteHighlights;
    }
    public Map<String,List<Pair<Time,Time>>> getBookedVenues(@Nonnull Date date){
        //get all the booking(collection) of the particular date
        //over all venue

        //*venue id
        //returns a map of string(venueid) list of <pair time(start time), time(end time)>.
    }
    public void bookVenue(BookingModel bookingModel){

    }
    public void getInstituteHighlights(){
        //Junction reference.
        //getting the institute highlights
    }
    public void getInstituteOrganisations(){

    }

    public void getAllVenues(){
        //getting all the available venues
    }
    public void updateVenues(List<VenueModel> Added, List<VenueModel> Removed){
        //
    }
    public void updateInstituteDetails(@Nonnull InstituteModel instituteModel){
    }


}
