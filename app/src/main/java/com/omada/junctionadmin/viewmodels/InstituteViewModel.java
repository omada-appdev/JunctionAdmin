package com.omada.junctionadmin.viewmodels;

import android.net.Uri;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.data.models.mutable.MutableVenueModel;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.repository.InstituteDataRepository;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.StringUtilities;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InstituteViewModel extends BaseViewModel {

    private static final int INSTITUTE_HANDLE_MIN_LENGTH = 4;
    private static final int INSTITUTE_HANDLE_MAX_LENGTH = 8;

    private final InstituteUpdater instituteUpdater = new InstituteUpdater();
    private InstituteVenuesUpdater instituteVenuesUpdater;

    private String instituteId;
    private InstituteModel instituteModel;

    private MediatorLiveData<List<PostModel>> loadedInstituteHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<OrganizationModel>> loadedInstituteOrganizations = new MediatorLiveData<>();
    private MediatorLiveData<List<VenueModel>> loadedInstituteVenues = new MediatorLiveData<>();
    private MediatorLiveData<List<VenueModel>> addedInstituteVenues = new MediatorLiveData<>();
    private MediatorLiveData<List<NotificationModel>> loadedInstituteNotifications = new MediatorLiveData<>();

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
                MainDataRepository.getInstance().getPostDataRepository().getLoadedInstituteHighlightsNotifier(),
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
                MainDataRepository.getInstance().getOrganizationDataRepository().getLoadedInstituteOrganizationsNotifier(),
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
        InstituteDataRepository.MutableUserInstituteModel mutableUserInstituteModel =
                new InstituteDataRepository.MutableUserInstituteModel();

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

        /*
            TODO MANAGE CACHE PROPERLY BECAUSE IT IS CAUSING CONFLICTS
         */
        if (instituteUpdater.handle.getValue().equals(instituteModel.getHandle())) {
            mutableUserInstituteModel.setHandle(instituteModel.getHandle());
            DataValidator.DataValidationInformation dataValidationInformation = new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
            );
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        } else {
            dataValidator.validateInstitute(instituteUpdater.handle.getValue(), validationInformation -> {

                if (validationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                    DataValidator.DataValidationInformation dataValidationInformation = new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                    );
                    validationAggregator.holdData(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                            dataValidationInformation
                    );
                    notifyValidity(dataValidationInformation);
                } else if (instituteUpdater.handle.getValue() != null && instituteUpdater.handle.getValue().length() >= INSTITUTE_HANDLE_MIN_LENGTH && instituteUpdater.handle.getValue().length() <= INSTITUTE_HANDLE_MAX_LENGTH) {
                    mutableUserInstituteModel.setHandle(
                            instituteUpdater.handle.getValue()
                    );
                    DataValidator.DataValidationInformation dataValidationInformation = new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
                    );
                    validationAggregator.holdData(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                            dataValidationInformation
                    );
                    notifyValidity(dataValidationInformation);
                } else {
                    DataValidator.DataValidationInformation dataValidationInformation = new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                    );
                    validationAggregator.holdData(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                            dataValidationInformation
                    );
                    notifyValidity(dataValidationInformation);
                }
            });
        }

        LiveData<LiveEvent<Boolean>> validationResultLiveData = Transformations.switchMap(
                anyDetailsEntryInvalid,
                input -> {
                    if (input == null) {
                        return null;
                    }
                    Log.e("Institute", "All details " + input.getDataValidationResult());
                    notifyValidity(input);
                    DataValidator.DataValidationResult dataValidationResult = input.getDataValidationResult();
                    if (dataValidationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                        return MainDataRepository.getInstance()
                                .getInstituteDataRepository()
                                .updateInstituteDetails(mutableUserInstituteModel);
                    }
                    return new MutableLiveData<>(new LiveEvent<>(false));
                }
        );

        return Transformations.map(
                validationResultLiveData,
                input ->

                {
                    if (input == null) {
                        return null;
                    }
                    updatingDetails = false;
                    Boolean result = input.getDataOnceAndReset();
                    if (result == null) {
                        return null;
                    }
                    if (result) {
                        instituteModel = null;
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

    public void exitAdminConsole() {
        instituteUpdater.setValues(instituteModel);
        loadedInstituteNotifications.setValue(null);
    }

    public LiveData<LiveEvent<InstituteModel>> getInstituteDetails() {

        instituteId = MainDataRepository
                .getInstance()
                .getUserDataRepository()
                .getCurrentUserModel()
                .getInstitute();

        if (instituteModel != null && instituteModel.getId().equals(instituteId)) {
            return new MutableLiveData<>(new LiveEvent<>(instituteModel));
        }

        return Transformations.map(
                MainDataRepository.getInstance()
                        .getInstituteDataRepository()
                        .getInstituteDetails(instituteId),

                input -> {
                    if (input == null) {
                        return null;
                    }
                    InstituteModel instituteModel = input.getDataOnceAndReset();
                    if (instituteModel == null) {
                        return null;
                    }
                    this.instituteModel = instituteModel;
                    instituteUpdater.setValues(instituteModel);
                    return new LiveEvent<>(instituteModel);
                }
        );
    }

    public InstituteVenuesUpdater getInstituteVenuesUpdater() {
        if (instituteVenuesUpdater == null) {
            instituteVenuesUpdater = new InstituteVenuesUpdater();
        }
        return instituteVenuesUpdater;
    }

    /*
    WARNING Use only from activity or during exit of edit venues screen. Can potentially
    cause unpredictable UI problems
     */
    public void resetInstituteVenuesUpdater() {
        addedInstituteVenues.setValue(null);
        instituteVenuesUpdater.clear();
    }

    public InstituteUpdater getInstituteUpdater() {
        return instituteUpdater;
    }

    public void reloadInstituteHighlights() {
        List<PostModel> highlights = loadedInstituteHighlights.getValue();
        if (!(highlights == null || highlights.size() == 0)) {
            highlights.clear();
        }
        loadInstituteHighlights();
    }

    public void loadInstituteHighlights() {
        MainDataRepository
                .getInstance()
                .getPostDataRepository()
                .getInstituteHighlights(getDataRepositoryAccessIdentifier());
    }

    public void loadInstituteOrganizations() {
        MainDataRepository
                .getInstance()
                .getOrganizationDataRepository()
                .getInstituteOrganizations(getDataRepositoryAccessIdentifier());
    }

    public void loadAllVenues() {

        String instituteId = MainDataRepository.getInstance()
                .getUserDataRepository()
                .getCurrentUserModel()
                .getInstitute();

        LiveData<LiveEvent<List<VenueModel>>> source = MainDataRepository
                .getInstance()
                .getVenueDataRepository()
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

    public LiveData<LiveEvent<Boolean>> updateInstituteVenues() {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        if (getInstituteVenuesUpdater().added.size() > 0 || getInstituteVenuesUpdater().removed.size() > 0) {
            return Transformations.map(
                    MainDataRepository
                            .getInstance()
                            .getVenueDataRepository()
                            .updateVenues(new ArrayList<>(instituteVenuesUpdater.added.values()), new ArrayList<>(instituteVenuesUpdater.removed.values())),

                    input -> {
                        if (input == null) {
                            return null;
                        }
                        Boolean res = input.getDataOnceAndReset();
                        if (Boolean.TRUE.equals(res)) {
                            List<VenueModel> loadedVenues = loadedInstituteVenues.getValue();
                            if (loadedVenues != null) {
                                loadedVenues.removeIf(model -> instituteVenuesUpdater.isRemoved(model));
                                loadedVenues.addAll(0, instituteVenuesUpdater.added.values());
                            }
                            resetInstituteVenuesUpdater();
                        }

                        return new LiveEvent<>(res);
                    }
            );
        }

        return resultLiveData;
    }

    // TODO write an efficient query and design a system to count number of bookings
    public void loadAllVenuesSortedByNumberOfBookings() {

    }

    public void loadInstituteNotifications() {

        String instituteId = MainDataRepository.getInstance()
                .getUserDataRepository()
                .getCurrentUserModel()
                .getInstitute();

        LiveData<LiveEvent<List<NotificationModel>>> source = MainDataRepository.getInstance()
                .getNotificationDataRepository()
                .getPendingNotifications(instituteId);

        loadedInstituteNotifications.addSource(
                source,
                listLiveEvent -> {
                    if (listLiveEvent == null) {
                        return;
                    }
                    List<NotificationModel> notifications = listLiveEvent.getDataOnceAndReset();
                    if (notifications == null) {
                        return;
                    }
                    loadedInstituteNotifications.setValue(notifications);
                    loadedInstituteNotifications.removeSource(source);
                }
        );
    }

    public LiveData<LiveEvent<Boolean>> handleJoinRequest(NotificationModel model, Boolean response) {
        return Transformations.map(
                MainDataRepository.getInstance()
                        .getNotificationDataRepository()
                        .handleNotification(model, response),
                input -> input
        );
    }

    public LiveData<List<NotificationModel>> getLoadedInstituteNotifications() {
        return loadedInstituteNotifications;
    }

    public LiveData<List<OrganizationModel>> getLoadedInstituteOrganizations() {
        return loadedInstituteOrganizations;
    }

    public LiveData<List<PostModel>> getLoadedInstituteHighlights() {
        return loadedInstituteHighlights;
    }

    public LiveData<List<VenueModel>> getLoadedInstituteVenues() {
        return loadedInstituteVenues;
    }

    public LiveData<List<VenueModel>> getAddedInstituteVenues() {
        return addedInstituteVenues;
    }

    public LiveData<LiveEvent<Boolean>> getEditInstituteTrigger() {
        return editInstituteTrigger;
    }

    public void updateVenues(List<VenueModel> added, List<VenueModel> removed) {

    }

    public final boolean checkInstituteContentLoaded() {
        return loadedInstituteHighlights.getValue() != null && loadedInstituteHighlights.getValue().size() != 0
                && loadedInstituteOrganizations.getValue() != null && loadedInstituteOrganizations.getValue().size() != 0;
    }

    // return value indicates validity
    // TODO replace with DataValidator
    public boolean addNewVenue(@Nullable String name, @Nullable String address) {

        if (name == null || address == null || name.length() < 8 || address.length() < 15) {
            return false;
        }

        MutableVenueModel venueModel = new MutableVenueModel();

        // This id is temporary. It is ignored during the final add
        venueModel.setId(StringUtilities.randomAlphaNumericGenerator(10));

        venueModel.setName(name);
        venueModel.setAddress(address);
        venueModel.setInstitute(instituteId);

        getInstituteVenuesUpdater().add(venueModel);
        if (addedInstituteVenues.getValue() != null) {
            addedInstituteVenues.getValue().add(0, venueModel);
            addedInstituteVenues.setValue(addedInstituteVenues.getValue());
        } else {
            addedInstituteVenues.setValue(new ArrayList<>(Collections.singletonList(venueModel)));
        }
        return true;
    }


    public static final class InstituteUpdater {
        public final MutableLiveData<String> name = new MutableLiveData<>();
        public final MutableLiveData<String> handle = new MutableLiveData<>();
        public Uri imagePath = null;
        private String image;

        private InstituteUpdater() {
        }

        private void setValues(InstituteModel model) {
            if (model == null) {
                name.setValue(null);
                handle.setValue(null);
                imagePath = null;
                image = null;
                return;
            }
            name.setValue(model.getName());
            handle.setValue(model.getHandle());
            setImage(model.getImage());
            imagePath = null;
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

    public static final class InstituteVenuesUpdater {

        private final Map<String, VenueModel> removed = new HashMap<>();
        private final Map<String, VenueModel> added = new HashMap<>();

        private void clear() {
            removed.clear();
            added.clear();
        }

        public synchronized void remove(VenueModel model) {
            if (model != null) {
                if (model.getId() == null || model.getId().equals("")) {
                    return;
                } else if (added.get(model.getId()) != null) {
                    added.remove(model.getId());
                } else {
                    removed.put(model.getId(), model);
                }
            }
        }

        public void undoRemove(VenueModel model) {
            if (!(model == null || model.getId() == null || model.getId().equals(""))) {
                if (removed.get(model.getId()) != null) {
                    removed.remove(model.getId());
                } else {
                    added.put(model.getId(), model);
                }
            }
        }

        private synchronized void add(VenueModel model) {
            if (model != null) {
                added.put(model.getId(), model);
            }
        }

        public boolean hasBeenModified() {
            return added.size() > 0 || removed.size() > 0;
        }

        public boolean isRemoved(VenueModel model) {
            return removed.get(model.getId()) != null;
        }

        public boolean isAdded(VenueModel model) {
            return added.get(model.getId()) != null;
        }

    }

}
