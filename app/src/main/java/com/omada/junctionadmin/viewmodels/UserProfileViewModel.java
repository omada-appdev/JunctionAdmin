package com.omada.junctionadmin.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.InstituteDataHandler;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveDataAggregator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/*
    This is purposely made to not inherit from OrganizationProfileViewModel
    because of possible misuse, as they have completely different patterns of accessing data
    One is through network request, other is through cached data

    Also, even though they share a lot in common, the two situations are very distinct
 */

public class UserProfileViewModel extends BaseViewModel {

    private final OrganizationUpdater organizationUpdater = new OrganizationUpdater();
    private OrganizationModel organizationModel;
    private final String organizationId;

    private LiveData<LiveEvent<UserDataHandler.AuthStatus>> authStatusTrigger;

    // No live event because the changes should always be subscribed to by all observers
    private LiveData<OrganizationModel> userUpdateAction;

    private MutableLiveData<LiveEvent<Boolean>> editDetailsTrigger;

    private MediatorLiveData<List<PostModel>> loadedOrganizationHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<ShowcaseModel>> loadedOrganizationShowcases = new MediatorLiveData<>();


    public UserProfileViewModel() {

        authStatusTrigger = Transformations.map(
                DataRepository.getInstance()
                        .getUserDataHandler()
                        .getAuthResponseNotifier(),

                authStatusLiveEvent -> authStatusLiveEvent
        );

        // TODO since multiple possible observers, remember to make it thread safe just in case
        userUpdateAction = Transformations.map(
                DataRepository.getInstance()
                .getUserDataHandler()
                .getSignedInUserNotifier(),

                organizationModelLiveEvent -> {
                    if(organizationModelLiveEvent == null) {
                        return null;
                    }
                    OrganizationModel temp = organizationModelLiveEvent.getDataOnceAndReset();
                    if(temp != null) {
                        organizationModel = temp;
                        organizationUpdater.setValues(organizationModel);
                        return organizationModel;
                    }
                    return null;
                }
        );

        editDetailsTrigger = new MutableLiveData<>();

        organizationModel = DataRepository.getInstance()
                .getUserDataHandler()
                .getCurrentUserModel();
        organizationId = organizationModel.getId();
        organizationUpdater.setValues(organizationModel);
    }

    public OrganizationModel getOrganizationDetails() {
        return organizationModel;
    }

    public LiveData<LiveEvent<Boolean>> updateDetails() {
        return Transformations.map(null, null);
    }

    // Update both showcase details and showcase items
    public LiveData<LiveEvent<Boolean>> updateShowcase() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> updateEvent(EventUpdater updater) {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> updateArticle(ArticleUpdater updater) {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> deletePost(String postId) {

        return Transformations.map(DataRepository
                        .getInstance()
                        .getPostDataHandler()
                        .deletePost(postId),
                resultLiveEvent -> {
                    if (resultLiveEvent == null) {
                        return new LiveEvent<>(false);
                    }
                    boolean res = resultLiveEvent.getDataOnceAndReset();
                    // Do something and pass it on to view
                    return new LiveEvent<>(res);
                }
        );
    }


    public void loadOrganizationHighlights() {

        LiveData<LiveEvent<List<PostModel>>> source =
                DataRepository
                        .getInstance()
                        .getPostDataHandler()
                        .getOrganizationHighlights(getDataRepositoryAccessIdentifier(), organizationId);

        loadedOrganizationHighlights.addSource(
                source,
                postModelsLiveEvent -> {

                    // Multiple calls or faulty pagination implementations might result in data contention
                    synchronized (this) {
                        if (postModelsLiveEvent != null) {
                            List<PostModel> postModels = postModelsLiveEvent.getDataOnceAndReset();
                            if (postModels != null) {

                                // If some data already exists, append this to the existing data
                                if (loadedOrganizationHighlights.getValue() != null) {
                                    loadedOrganizationHighlights.getValue().addAll(postModels);
                                    postModels = loadedOrganizationHighlights.getValue();
                                }
                                loadedOrganizationHighlights.setValue(postModels);
                            } else {
                            }
                        } else {
                        }
                        loadedOrganizationHighlights.removeSource(source);
                    }

                }
        );

    }

    public void loadOrganizationShowcases() {

        LiveData<LiveEvent<List<ShowcaseModel>>> source =
                DataRepository
                        .getInstance()
                        .getShowcaseDataHandler()
                        .getOrganizationShowcases(getDataRepositoryAccessIdentifier(), organizationId);

        loadedOrganizationHighlights.addSource(
                source,
                showcaseModelsLiveEvent -> {

                    // Multiple calls or faulty pagination implementations might result in data contention
                    synchronized (this) {
                        if (showcaseModelsLiveEvent != null) {
                            List<ShowcaseModel> showcaseModels = showcaseModelsLiveEvent.getDataOnceAndReset();
                            if (showcaseModels != null) {

                                // If some data already exists, append this to the existing data
                                if (loadedOrganizationShowcases.getValue() != null) {
                                    loadedOrganizationShowcases.getValue().addAll(showcaseModels);
                                    showcaseModels = loadedOrganizationShowcases.getValue();
                                }
                                loadedOrganizationShowcases.setValue(showcaseModels);
                            } else {
                            }
                        } else {
                        }
                        loadedOrganizationShowcases.removeSource(source);
                    }

                }
        );

    }

    public LiveData<List<PostModel>> getLoadedOrganizationHighlights() {
        return loadedOrganizationHighlights;
    }

    public LiveData<List<ShowcaseModel>> getLoadedOrganizationShowcases() {
        return loadedOrganizationShowcases;
    }

    public void signOutUser() {
        DataRepository.getInstance().getUserDataHandler().signOutCurrentUser();
    }

    public void goToEditDetails() {
        editDetailsTrigger.setValue(new LiveEvent<>(true));
    }

    public void detailsEntryDone() {

        UserDataHandler.MutableUserOrganizationModel userOrganizationModel =
                new UserDataHandler.MutableUserOrganizationModel();

        MediatorLiveData<DataValidator.DataValidationInformation> anyDetailsEntryInvalid = new MediatorLiveData<>();

        ValidationAggregator validationAggregator = new ValidationAggregator(anyDetailsEntryInvalid);

        if(organizationUpdater.newProfilePicture.getValue() != null) {
            userOrganizationModel.setProfilePicturePath(organizationUpdater.newProfilePicture.getValue());
        }
        dataValidator.validateInstitute(organizationUpdater.instituteHandle.getValue(), dataValidationInformation -> {

            boolean reFetching = false;
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                String id = InstituteDataHandler.getCachedInstituteId(organizationUpdater.instituteHandle.getValue());
                if (id == null || id.equals("")) {

                    reFetching = true;
                    LiveData<LiveEvent<String>> reFetchedId = DataRepository
                            .getInstance()
                            .getInstituteDataHandler()
                            .getInstituteId(organizationUpdater.instituteHandle.getValue());

                    reFetchedId.observeForever(new Observer<LiveEvent<String>>() {
                        @Override
                        public void onChanged(LiveEvent<String> stringLiveEvent) {
                            if(stringLiveEvent == null) {
                                return;
                            }
                            String result = stringLiveEvent.getDataOnceAndReset();
                            DataValidator.DataValidationInformation newValidationInformation;

                            if(result != null && !result.equals("notFound")){
                                userOrganizationModel.setInstitute(result);
                                newValidationInformation = new DataValidator.DataValidationInformation(
                                        DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
                                );
                            }
                            else {
                                newValidationInformation = new DataValidator.DataValidationInformation(
                                        DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                                );
                            }

                            validationAggregator.holdData(
                                    DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                    newValidationInformation
                            );
                            notifyValidity(newValidationInformation);
                            reFetchedId.removeObserver(this);
                        }});

                } else {
                    userOrganizationModel.setInstitute(id);
                }
            }
            if(!reFetching) {
                validationAggregator.holdData(
                        DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                        dataValidationInformation
                );
                notifyValidity(dataValidationInformation);
            }
        });


        dataValidator.validateName(organizationUpdater.name.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                userOrganizationModel.setName(
                        organizationUpdater.name.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_NAME,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        anyDetailsEntryInvalid.observeForever(new Observer<DataValidator.DataValidationInformation>() {
            @Override
            public void onChanged(DataValidator.DataValidationInformation dataValidationInformation) {
                if (dataValidationInformation != null) {
                    notifyValidity(dataValidationInformation);
                    if (dataValidationInformation.getValidationPoint() == DataValidator.DataValidationPoint.VALIDATION_POINT_ALL
                            && dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {

                        DataRepository.getInstance()
                                .getUserDataHandler()
                                .updateCurrentUserDetails(userOrganizationModel);
                    }
                }
                anyDetailsEntryInvalid.removeObserver(this);
            }
        });

    }

    /**
     * No possibility of exceptions because {@link OrganizationUpdater} gets set automatically after
     * setting model or loading model from {@link DataRepository}
     */

    public OrganizationUpdater getOrganizationUpdater() {
        return organizationUpdater;
    }

    /**
     * static methods because these should not access instance variables of this {@link ViewModel}. They are not in
     * separate files because the constructor should be accessible only from {@link UserProfileViewModel}
     */

    public static EventUpdater getNewEventUpdaterInstance(EventModel eventModel) {
        return new EventUpdater(eventModel);
    }

    public static ArticleUpdater getNewArticleUpdaterInstance(ArticleModel articleModel) {
        return new ArticleUpdater(articleModel);
    }

    public static ShowcaseUpdater getNewShowcaseUpdaterInstance(ShowcaseModel showcaseModel) {
        return new ShowcaseUpdater(showcaseModel);
    }

    public LiveData<LiveEvent<UserDataHandler.AuthStatus>> getAuthStatusTrigger() {
        return authStatusTrigger;
    }

    public MutableLiveData<LiveEvent<Boolean>> getEditDetailsTrigger() {
        return editDetailsTrigger;
    }

    public void resetUpdater() {
        organizationUpdater.setValues(organizationModel);
    }

    public LiveData<OrganizationModel> getUserUpdateAction() {
        return userUpdateAction;
    }

    public static final class OrganizationUpdater {

        public final MutableLiveData<String> name = new MutableLiveData<>();
        public final MutableLiveData<String> phone = new MutableLiveData<>();
        public final MutableLiveData<Uri> newProfilePicture = new MutableLiveData<>();
        public final MutableLiveData<String> instituteHandle = new MutableLiveData<>();
        private String profilePicture;

        private OrganizationUpdater() {
        }

        private void setValues(OrganizationModel model) {
            name.setValue(model.getName());
            phone.setValue(model.getPhone());

            LiveData<LiveEvent<String>> handleLiveData = DataRepository.getInstance()
                    .getInstituteDataHandler()
                    .getInstituteHandle(model.getInstitute());

            handleLiveData.observeForever(new Observer<LiveEvent<String>>() {
                @Override
                public void onChanged(LiveEvent<String> stringLiveEvent) {
                    if (stringLiveEvent == null) {
                        return;
                    }
                    String handle = stringLiveEvent.getDataOnceAndReset();
                    if (handle == null || handle.equals("notFound")) {
                        return;
                    }
                    instituteHandle.postValue(handle);
                    handleLiveData.removeObserver(this);
                }
            });
            setProfilePicture(model.getProfilePicture());
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }
    }

    public static class EventUpdater {

        private EventUpdater(EventModel eventModel) {

            description = new MutableLiveData<>(eventModel.getDescription());
            image = new MutableLiveData<>(eventModel.getImage());
            form = new MutableLiveData<>(eventModel.getForm());

            startTime = new MutableLiveData<>(eventModel.getStartTime());
            endTime = new MutableLiveData<>(eventModel.getEndTime());
            tags = new MutableLiveData<>(eventModel.getTags());

            venueModel = new MutableLiveData<>();
        }

        public final MutableLiveData<String> description;
        public final MutableLiveData<String> image;

        public final MutableLiveData<Map<String, Map<String, Map<String, String>>>> form;
        public final MutableLiveData<LocalDateTime> startTime;
        public final MutableLiveData<LocalDateTime> endTime;
        public final MutableLiveData<VenueModel> venueModel;

        public final MutableLiveData<List<String>> tags;

    }

    public static class ArticleUpdater {

        private ArticleUpdater(ArticleModel articleModel) {

            title = new MutableLiveData<>(articleModel.getTitle());
            image = new MutableLiveData<>(articleModel.getImage());

            text = new MutableLiveData<>(articleModel.getText());
            author = new MutableLiveData<>(articleModel.getAuthor());

            tags = new MutableLiveData<>(articleModel.getTags());
        }

        public final MutableLiveData<String> title;
        public final MutableLiveData<String> image;

        public final MutableLiveData<String> text;
        public final MutableLiveData<String> author;

        public final MutableLiveData<List<String>> tags;

    }

    public static class ShowcaseUpdater {

        private ShowcaseUpdater(ShowcaseModel showcaseModel) {
            title = new MutableLiveData<>(showcaseModel.getTitle());
            photo = new MutableLiveData<>(showcaseModel.getPhoto());
        }

        public final MutableLiveData<String> title;
        public final MutableLiveData<String> photo;

    }

    private static class ValidationAggregator extends LiveDataAggregator<DataValidator.DataValidationPoint, DataValidator.DataValidationInformation, DataValidator.DataValidationInformation> {

        private static final Set<DataValidator.DataValidationPoint> allValidationPoints = new HashSet<>();

        static {
            allValidationPoints.add(DataValidator.DataValidationPoint.VALIDATION_POINT_NAME);
            allValidationPoints.add(DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE);
        }

        public ValidationAggregator(MediatorLiveData<DataValidator.DataValidationInformation> destination) {
            super(destination);
        }

        @Override
        protected DataValidator.DataValidationInformation mergeWithExistingData(DataValidator.DataValidationPoint typeofData, DataValidator.DataValidationInformation oldData, DataValidator.DataValidationInformation newData) {
            throw new UnsupportedOperationException("Attempt to set same type of validation parameter twice");
        }

        @Override
        protected boolean checkDataForAggregability() {
            return dataOnHold.keySet().containsAll(allValidationPoints);
        }

        @Override
        protected void aggregateData() {

            for (DataValidator.DataValidationInformation dataValidationInformation : dataOnHold.values()) {
                if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                    destinationLiveData.postValue(new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                    ));
                    return;
                }
            }
            destinationLiveData.postValue(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
            ));
        }
    }

}
