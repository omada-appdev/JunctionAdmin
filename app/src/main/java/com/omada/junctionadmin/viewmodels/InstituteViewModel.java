package com.omada.junctionadmin.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.InstituteDataHandler;
import com.omada.junctionadmin.data.models.external.BookingModel;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;


public class InstituteViewModel extends BaseViewModel {

    private final InstituteUpdater instituteUpdater=new InstituteUpdater();

    protected String instituteId;
    protected InstituteModel instituteModel;

    private MediatorLiveData<List<PostModel>> loadedInstituteHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<OrganizationModel>> loadedInstituteOrganizations = new MediatorLiveData<>();
    private MediatorLiveData<List<VenueModel>> loadedInstituteVenues = new MediatorLiveData<>();

    private boolean editingDetails = false;
    private boolean updatingDetails = false;

    public InstituteViewModel() {
        initializeDataLoaders();
        distributeLoadedData();


        instituteModel =DataRepository.getInstance()
                .getInstituteDataHandler()
                .getInstituteModel();

        instituteId=instituteModel.getId();
        instituteUpdater.setValues(instituteModel);
    }


    public void detailsEntryDone(){
        updatingDetails =true;

        InstituteDataHandler.MutableUserInstituteModel userInstituteModel =
                new InstituteDataHandler.MutableUserInstituteModel();

        MediatorLiveData<DataValidator.DataValidationInformation> anyDetailsEntryInvalid
                = new MediatorLiveData<>();


        ValidationAggregator validationAggregator =ValidationAggregator
                .build(anyDetailsEntryInvalid)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_NAME)
                .get();

        if(instituteUpdater.newProfilePicture.getValue() !=null){
            userInstituteModel.setProfilePicturePath(instituteUpdater.newProfilePicture.getValue());
        }

        dataValidator.validateName(instituteUpdater.name.getValue(),dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                userInstituteModel.setName(
                        instituteUpdater.name.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_NAME,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

    }

    public void exitEditDetails() {
        editingDetails = false;
    }


    public void resetInstituteUpdater(){
        instituteUpdater.setValues(instituteModel);
    }



    public InstituteModel getInstituteDetails(){
        return this.instituteModel;
    }

    public InstituteUpdater getInstituteUpdater() {
        return instituteUpdater;
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
                        existingData.addAll(highlights);
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
                        existingData.addAll(organizationModels);
                        loadedInstituteOrganizations.setValue(existingData);
                    }
                }
        );

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

    public MediatorLiveData<List<OrganizationModel>> getLoadedInstituteOrganizations() {
        return loadedInstituteOrganizations;
    }

    public MediatorLiveData<List<PostModel>> getLoadedInstituteHighlights() {
        return loadedInstituteHighlights;
    }

    public MediatorLiveData<List<VenueModel>> getLoadedInstituteVenues() {
        return loadedInstituteVenues;
    }

    public void updateVenues(List<VenueModel> added, List<VenueModel> removed) {

    }

    public void updateInstituteDetails(@Nonnull InstituteModel instituteModel) {

    }

    public final boolean checkInstituteContentLoaded() {
        return loadedInstituteHighlights.getValue() != null && loadedInstituteHighlights.getValue().size() != 0
                && loadedInstituteOrganizations.getValue() != null && loadedInstituteOrganizations.getValue().size() != 0;
    }


    public static final class InstituteUpdater {
        public final MutableLiveData<String> name=new MutableLiveData<String>();
        public final MutableLiveData<String> handle=new MutableLiveData<String>();
        public final MutableLiveData<Uri> newProfilePicture=new MutableLiveData<Uri>();
        private String profilePicture;

        private InstituteUpdater(){}

        private void setValues(InstituteModel model){


            name.setValue(model.getName());
            handle.setValue(model.getHandle());
            LiveData<LiveEvent<String>> handleLiveData = DataRepository.getInstance()
                    .getInstituteDataHandler()
                    .getInstituteHandle(model.getId());
            setProfilePicture(model.getProfilePicture());


        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }


    }




}
