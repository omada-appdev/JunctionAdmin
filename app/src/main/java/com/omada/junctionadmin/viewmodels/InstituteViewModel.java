package com.omada.junctionadmin.viewmodels;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.models.EventModel;

import java.sql.Time;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;

public class InstituteViewModel extends ViewModel {

    public Map<String, Pair<Time,Time>> getAvailableVenues(@Nonnull Date date){}
    public void bookVenue(EventModel eventModel){}
    public void getInstituteHighlights(){}
    public void getAllVenues(){}
    public void updateVenues(List<VenueModel> Added,List<VenueModel> Removed){}
    public void updateInstituteDetails(@Nonnull InstituteModel instituteModel){}



}
