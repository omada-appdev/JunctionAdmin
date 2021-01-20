package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.common.collect.ImmutableList;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;


public class InstituteViewModel extends BaseViewModel {

    private MediatorLiveData<List<PostModel>> loadedInstituteHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<OrganizationModel>> loadedInstituteOrganizations = new MediatorLiveData<>();
    private MediatorLiveData<List<VenueModel>> loadedInstituteVenues = new MediatorLiveData<>();

    public InstituteViewModel() {
        initializeDataLoaders();
        distributeLoadedData();
    }

    private void initializeDataLoaders() {
    }

    private void distributeLoadedData(){

        loadedInstituteHighlights.addSource(
                DataRepository.getInstance().getPostDataHandler().getLoadedInstituteHighlightsNotifier(),
                input -> {

                    if(input != null) {

                        List<PostModel> highlights = input.getDataOnceAndReset();
                        if(highlights == null){
                            return;
                        }

                        List<PostModel> existingData = loadedInstituteHighlights.getValue();
                        if (existingData == null) {
                            existingData = new ArrayList<>();
                        }
                        existingData.addAll(input.getDataOnceAndReset());
                        loadedInstituteHighlights.setValue(existingData);
                    }
                }
        );

        loadedInstituteOrganizations.addSource(
                DataRepository.getInstance().getOrganizationDataHandler().getLoadedInstituteOrganizationsNotifier(),
                input -> {

                    if(input != null) {

                        List<OrganizationModel> organizationModels = input.getDataOnceAndReset();
                        if(organizationModels == null){
                            return;
                        }

                        List<OrganizationModel> existingData = loadedInstituteOrganizations.getValue();
                        if (existingData == null) {
                            existingData = new ArrayList<>();
                        }
                        existingData.addAll(input.getDataOnceAndReset());
                        loadedInstituteOrganizations.setValue(existingData);
                    }
                }
        );

    }

    public LiveData<LiveEvent<List<BookingModel>>> getBookings(@Nonnull String venue, @Nonnull Date date){

        String instituteId = DataRepository.getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        return DataRepository.getInstance()
                .getVenueDataHandler()
                .getVenueBookingsOn(getDataRepositoryAccessIdentifier(), date, venue);

    }

    public void loadInstituteHighlights() {
        DataRepository
                .getInstance()
                .getPostDataHandler()
                .getInstituteHighlights(getDataRepositoryAccessIdentifier());

    }

    public void loadInstituteOrganizations() {
        DataRepository
                .getInstance()
                .getOrganizationDataHandler()
                .getInstituteOrganizations(getDataRepositoryAccessIdentifier());
    }

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

    public void updateVenues(List<VenueModel> added, List<VenueModel> removed) {

    }

    public void updateInstituteDetails(@Nonnull InstituteModel instituteModel) {

    }

}
