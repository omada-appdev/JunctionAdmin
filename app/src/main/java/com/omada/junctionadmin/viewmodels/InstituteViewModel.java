package com.omada.junctionadmin.viewmodels;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.VenueModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class InstituteViewModel extends ViewModel {

    public Map<String, Pair<Date,Date>> getAvailableVenues(@Nonnull Date date){
        return null;
    }
    public void bookVenue(EventModel eventModel){}
    public void getInstituteHighlights(){}
    public void getAllVenues(){}
    public void updateVenues(List<VenueModel> Added, List<VenueModel> Removed){}
    public void updateInstituteDetails(@Nonnull InstituteModel instituteModel){}



}
