package com.omada.junctionadmin.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.InstituteDataHandler;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;


public class InstituteViewModel extends BaseViewModel {

    private final InstituteUpdater instituteUpdater = new InstituteUpdater();

    private String instituteId;
    private InstituteModel instituteModel;

    private MediatorLiveData<List<PostModel>> loadedInstituteHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<OrganizationModel>> loadedInstituteOrganizations = new MediatorLiveData<>();
    private MediatorLiveData<List<VenueModel>> loadedInstituteVenues = new MediatorLiveData<>();

    private final MutableLiveData<LiveEvent<Boolean>> editInstituteTrigger = new MutableLiveData<>();

    private boolean editingDetails = false;
    private boolean updatingDetails = false;

    public InstituteViewModel() {
        initializeDataLoaders();
        distributeLoadedData();
    }

    private void initializeDataLoaders() {
    }

    private void distributeLoadedData() {

        loadedInstituteHighlights.addSource(
                DataRepository.getInstance().getPostDataHandler().getLoadedInstituteHighlightsNotifier(),
                input -> {
                    if (input != null) {

                        List<PostModel> highlights = input.getDataOnceAndReset();
                        if (highlights == null) {
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
                    if (input != null) {

                        List<OrganizationModel> organizationModels = input.getDataOnceAndReset();
                        if (organizationModels == null) {
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

    public LiveData<LiveEvent<Boolean>> detailsEntryDone() {

        updatingDetails = true;
        InstituteDataHandler.MutableUserInstituteModel mutableUserInstituteModel =
                new InstituteDataHandler.MutableUserInstituteModel();

        MediatorLiveData<DataValidator.DataValidationInformation> anyDetailsEntryInvalid = new MediatorLiveData<>();

        ValidationAggregator validationAggregator = ValidationAggregator
                .build(anyDetailsEntryInvalid)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_NAME)
                .get();

        if (instituteUpdater.getImagePath() != null) {
            mutableUserInstituteModel.setImagePath(instituteUpdater.getImagePath());
        }

        dataValidator.validateName(instituteUpdater.name.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                mutableUserInstituteModel.setName(
                        instituteUpdater.name.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_NAME,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateInstitute(instituteUpdater.handle.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                mutableUserInstituteModel.setHandle(
                        instituteUpdater.handle.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        LiveData<LiveEvent<Boolean>> validationResultLiveData = Transformations.switchMap(
                anyDetailsEntryInvalid,
                input -> {
                    if(input == null) {
                        return null;
                    }
                    Log.e("Institute", "All details " + input.getDataValidationResult());
                    notifyValidity(input);
                    DataValidator.DataValidationResult dataValidationResult = input.getDataValidationResult();
                    if(dataValidationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                        return DataRepository.getInstance()
                                .getInstituteDataHandler()
                                .updateInstituteDetails(mutableUserInstituteModel);
                    }
                    return new MutableLiveData<>(new LiveEvent<>(false));
                }
        );

        return Transformations.map(
                validationResultLiveData,
                input -> {
                    if(input == null) {
                        return null;
                    }
                    updatingDetails = false;
                    Boolean result  = input.getDataOnceAndReset();
                    if(result == null) {
                        return null;
                    }
                    if(result) {
                        return new LiveEvent<>(true);
                    } else {
                        return new LiveEvent<>(false);
                    }
                }
        );
    }

    public void goToEditInstituteDetails() {
        editingDetails = true;
        editInstituteTrigger.setValue(new LiveEvent<>(true));
    }

    public void resetInstituteUpdater() {
        instituteUpdater.setValues(instituteModel);
    }

    public LiveData<LiveEvent<InstituteModel>> getInstituteDetails() {

        instituteId = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        if (instituteModel != null && instituteModel.getId().equals(instituteId)) {
            return new MutableLiveData<>(new LiveEvent<>(instituteModel));
        }

        return Transformations.map(
                DataRepository.getInstance()
                        .getInstituteDataHandler()
                        .getInstituteDetails(instituteId),

                input -> {
                    if (input == null) {
                        return null;
                    }
                    InstituteModel instituteModel = input.getDataOnceAndReset();
                    if (instituteModel == null) {
                        return null;
                    }
                    instituteUpdater.setValues(instituteModel);
                    return new LiveEvent<>(instituteModel);
                }
        );
    }

    public InstituteUpdater getInstituteUpdater() {
        return instituteUpdater;
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

            if (venueModelsLiveEvent == null) {
                loadedInstituteVenues.setValue(null);
            } else {
                List<VenueModel> venueModels = venueModelsLiveEvent.getDataOnceAndReset();
                if (loadedInstituteVenues.getValue() == null) {
                    loadedInstituteVenues.setValue(venueModels);
                } else {
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

    public MutableLiveData<LiveEvent<Boolean>> getEditInstituteTrigger() {
        return editInstituteTrigger;
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
        public final MutableLiveData<String> name = new MutableLiveData<>();
        public final MutableLiveData<String> handle = new MutableLiveData<>();
        public Uri imagePath = null;
        private String image;

        private InstituteUpdater() {
        }

        private void setValues(InstituteModel model) {
            name.setValue(model.getName());
            handle.setValue(model.getHandle());
            setImage(model.getImage());
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Uri getImagePath() {
            return imagePath;
        }

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }
    }

}
