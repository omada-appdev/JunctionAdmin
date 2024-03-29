package com.omada.junctionadmin.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.common.collect.ImmutableList;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.data.models.mutable.MutableArticleModel;
import com.omada.junctionadmin.data.models.mutable.MutableEventModel;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


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

    private LiveData<LiveEvent<UserDataRepository.AuthStatus>> authStatusTrigger;

    // No live event because the changes should always be subscribed to by all observers
    private LiveData<OrganizationModel> userUpdateAction;

    private MutableLiveData<LiveEvent<Boolean>> editDetailsTrigger;

    private MediatorLiveData<List<PostModel>> loadedOrganizationHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<ShowcaseModel>> loadedOrganizationShowcases = new MediatorLiveData<>();

    private boolean editingDetails = false;
    private boolean updatingDetails = false;

    public UserProfileViewModel() {

        authStatusTrigger = Transformations.map(
                MainDataRepository.getInstance()
                        .getUserDataRepository()
                        .getAuthResponseNotifier(),

                authStatusLiveEvent -> authStatusLiveEvent
        );

        // TODO since multiple possible observers, remember to make it thread safe just in case
        userUpdateAction = Transformations.map(
                MainDataRepository.getInstance()
                        .getUserDataRepository()
                        .getSignedInUserNotifier(),

                organizationModelLiveEvent -> {
                    if (organizationModelLiveEvent == null) {
                        return null;
                    }
                    OrganizationModel temp = organizationModelLiveEvent.getDataOnceAndReset();
                    if (temp != null) {
                        /*
                         update happened from somewhere else
                         TODO use a repository and query that
                        */
                        if(!temp.getHeldEventsNumber().equals(organizationModel.getHeldEventsNumber())) {
                            loadOrganizationHighlights();
                        }
                        organizationModel = temp;
                        organizationUpdater.setValues(organizationModel);
                        return organizationModel;
                    }
                    return null;
                }
        );

        editDetailsTrigger = new MutableLiveData<>();

        organizationModel = MainDataRepository.getInstance()
                .getUserDataRepository()
                .getCurrentUserModel();
        organizationId = organizationModel.getId();
        organizationUpdater.setValues(organizationModel);
    }

    public OrganizationModel getOrganizationDetails() {
        return organizationModel;
    }

    public LiveData<List<NotificationModel>> getOrganizationNotifications() {
        return Transformations.map(
                MainDataRepository.getInstance()
                        .getNotificationDataRepository()
                        .getPendingNotifications(organizationId),
                input -> {
                    if (input == null) {
                        return null;
                    }
                    List<NotificationModel> notificationModels = input.getDataOnceAndReset();
                    if (notificationModels == null) {
                        return null;
                    }
                    notificationModels.removeIf(model -> model == null
                            || !Arrays.asList("instituteJoinResponse", "feedbackResponse", "instituteAdminResponse").contains(model.getNotificationType()));
                    if (notificationModels.size() > 0) {
                        // Because institute data is changed
                        MainDataRepository.getInstance()
                                .getUserDataRepository()
                                .getCurrentUserDetailsFromRemote();
                    }
                    return notificationModels;
                }
        );
    }

    public LiveData<LiveEvent<Boolean>> handleJoinResponseNotification(NotificationModel model) {
        return Transformations.map(
                MainDataRepository.getInstance()
                        .getNotificationDataRepository()
                        .handleNotification(model, null),
                input -> {
                    if (input == null) {
                        return null;
                    }
                    Boolean response = input.getDataOnceAndReset();
                    if (response == null) return null;
                    return new LiveEvent<>(response);
                }
        );
    }

    // Update both showcase details and showcase items
    public LiveData<LiveEvent<Boolean>> updateShowcase() {
        return Transformations.map(null, null);
    }

    public LiveData<LiveEvent<Boolean>> updateEvent(EventUpdater updater) {

        DataValidator dataValidator = getDataValidator();
        AtomicBoolean detailsInvalid = new AtomicBoolean(false);

        MutableEventModel mutableEventModel = new MutableEventModel();
        mutableEventModel.setTags(ImmutableList.copyOf(updater.tags.getValue()));

        /*
         It is known that this validation routine is synchronous so adding a ValidationAggregator
         will only complicate things. If later use requires asynchronous routines, use a
         ValidationAggregator
        */
        dataValidator.validateEventDescription(updater.description.getValue(), dataValidationInformation -> {
            notifyValidity(dataValidationInformation);
            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                detailsInvalid.set(true);
            }
            mutableEventModel.setDescription(updater.description.getValue());
        });

        if (detailsInvalid.get()) {
            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
            ));
            return new MutableLiveData<>(new LiveEvent<>(false));
        }
        notifyValidity(new DataValidator.DataValidationInformation(
                DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
        ));
        return Transformations.map(
                MainDataRepository.getInstance()
                        .getPostDataRepository()
                        .updatePost(updater.getId(), mutableEventModel),

                booleanLiveEvent -> {
                    if (booleanLiveEvent == null) {
                        return null;
                    }
                    Boolean result = booleanLiveEvent.getDataOnceAndReset();
                    if (result == null) {
                        return null;
                    }
                    return new LiveEvent<>(result);
                }
        );
    }

    public LiveData<LiveEvent<Boolean>> updateArticle(ArticleUpdater updater) {

        MutableArticleModel mutableArticleModel = new MutableArticleModel();
        mutableArticleModel.setText(updater.text.getValue());
        mutableArticleModel.setAuthor(updater.author.getValue());
        mutableArticleModel.setTags(ImmutableList.copyOf(updater.tags.getValue()));

        return Transformations.map(
                MainDataRepository.getInstance()
                        .getPostDataRepository()
                        .updatePost(updater.getId(), mutableArticleModel),

                booleanLiveEvent -> {
                    if (booleanLiveEvent == null) {
                        return null;
                    }
                    Boolean result = booleanLiveEvent.getDataOnceAndReset();
                    if (result == null) {
                        return null;
                    }
                    return new LiveEvent<>(result);
                }
        );
    }

    public LiveData<Boolean> deletePost(EventModel eventModel) {

        return Transformations.map(MainDataRepository
                        .getInstance()
                        .getPostDataRepository()
                        .deletePost(eventModel),

                resultLiveEvent -> {
                    if (resultLiveEvent == null) {
                        return null;
                    }
                    Boolean deleted = resultLiveEvent.getDataOnceAndReset();
                    if (deleted == null) {
                        return null;
                    }
                    if (deleted) {
                        List<PostModel> highlights = loadedOrganizationHighlights.getValue();
                        if (highlights != null) {
                            highlights.removeIf(postModel -> postModel.getId().equals(eventModel.getId()));
                            loadedOrganizationHighlights.setValue(highlights);
                        }
                    } else {
                        Log.e("Post", "Error deleting " + eventModel.getId());
                    }
                    return deleted;
                }
        );
    }

    public void reloadOrganizationHighlights() {
        if(loadedOrganizationHighlights != null) {
            if(loadedOrganizationHighlights.getValue() != null) {
                loadedOrganizationHighlights.getValue().clear();
            }
        } else {
            loadedOrganizationHighlights = new MediatorLiveData<>();
        }
        loadOrganizationHighlights();
    }

    public void loadOrganizationHighlights() {

        Log.e("Profile", "Loading highlights from remote");
        loadedOrganizationHighlights.postValue(null);

        LiveData<LiveEvent<List<PostModel>>> source =
                MainDataRepository
                        .getInstance()
                        .getPostDataRepository()
                        .getOrganizationHighlights(getDataRepositoryAccessIdentifier(), organizationId);

        loadedOrganizationHighlights.addSource(
                source,
                postModelsLiveEvent -> {

                    // Multiple calls or faulty pagination implementations might result in data contention
                    synchronized (this) {
                        if (postModelsLiveEvent != null) {
                            List<PostModel> postModels = postModelsLiveEvent.getDataOnceAndReset();
                            if (postModels != null) {
                                loadedOrganizationHighlights.setValue(postModels);
                            } else {
                            }
                            loadedOrganizationHighlights.removeSource(source);
                        } else {
                        }
                    }

                }
        );

    }

    public void loadOrganizationShowcases() {

        loadedOrganizationShowcases.postValue(null);

        LiveData<LiveEvent<List<ShowcaseModel>>> source =
                MainDataRepository
                        .getInstance()
                        .getShowcaseDataRepository()
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
        MainDataRepository.getInstance().getUserDataRepository().signOutCurrentUser();
    }

    public void goToEditDetails() {
        editingDetails = true;
        editDetailsTrigger.setValue(new LiveEvent<>(true));
    }

    public void exitEditDetails() {
        editingDetails = false;
    }

    public void detailsEntryDone() {

        if (updatingDetails) {
            return;
        }

        updatingDetails = true;
        UserDataRepository.MutableUserOrganizationModel userOrganizationModel =
                new UserDataRepository.MutableUserOrganizationModel();

        MediatorLiveData<DataValidator.DataValidationInformation> anyDetailsEntryInvalid = new MediatorLiveData<>();

        ValidationAggregator validationAggregator = ValidationAggregator
                .build(anyDetailsEntryInvalid)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_NAME)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_PHONE)
                .get();

        if (organizationUpdater.newProfilePicture.getValue() != null) {
            userOrganizationModel.setProfilePicturePath(organizationUpdater.newProfilePicture.getValue());
        }

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

        dataValidator.validatePhone(organizationUpdater.phone.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                userOrganizationModel.setPhone(
                        organizationUpdater.phone.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_PHONE,
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
                        updatingDetails = false;
                        MainDataRepository.getInstance()
                                .getUserDataRepository()
                                .updateCurrentUserDetails(userOrganizationModel);
                    } else {
                    }
                }
                anyDetailsEntryInvalid.removeObserver(this);
            }
        });

    }

    /**
     * No possibility of exceptions because {@link OrganizationUpdater} gets set automatically after
     * setting model or loading model from {@link MainDataRepository}
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

    public LiveData<LiveEvent<UserDataRepository.AuthStatus>> getAuthStatusTrigger() {
        return authStatusTrigger;
    }

    public MutableLiveData<LiveEvent<Boolean>> getEditDetailsTrigger() {
        return editDetailsTrigger;
    }

    public void resetOrganizationUpdater() {
        organizationUpdater.setValues(organizationModel);
    }

    public LiveData<OrganizationModel> getUserUpdateAction() {
        return userUpdateAction;
    }

    public LiveData<LiveEvent<DataValidator.DataValidationInformation>> validateFeedback(String feedback) {
        MutableLiveData<LiveEvent<DataValidator.DataValidationInformation>> validationResultLiveData = new MutableLiveData<>();

        dataValidator.validateFeedback(feedback, dataValidationInformation -> {
            if (dataValidationInformation != null) {
                validationResultLiveData.setValue(new LiveEvent<>(dataValidationInformation));
            }
        });

        return validationResultLiveData;
    }

    public LiveData<LiveEvent<Boolean>> sendFeedback(String feedback, String feedbackType) {
        return Transformations.map(
                MainDataRepository
                        .getInstance()
                        .getNotificationDataRepository()
                        .sendFeedbackNotification(feedback, feedbackType),
                input -> input
        );
    }

    public static final class OrganizationUpdater {

        public final MutableLiveData<String> name = new MutableLiveData<>();
        public final MutableLiveData<String> phone = new MutableLiveData<>();
        public final MutableLiveData<Uri> newProfilePicture = new MutableLiveData<>();

        public final MutableLiveData<String> heldEventsNumber = new MutableLiveData<>();
        public final MutableLiveData<String> attendedUsersNumber = new MutableLiveData<>();

        private String profilePicture;

        private OrganizationUpdater() {
        }

        private void setValues(OrganizationModel model) {
            name.setValue(model.getName());
            phone.setValue(model.getPhone());

            attendedUsersNumber.setValue(String.valueOf(model.getAttendedUsersNumber()));
            heldEventsNumber.setValue(String.valueOf(model.getHeldEventsNumber()));
            Log.e("Profile", "stats string : " + attendedUsersNumber.getValue() + " " + heldEventsNumber.getValue());
            Log.e("Profile", "stats integer : " + model.getAttendedUsersNumber() + " " + model.getHeldEventsNumber()    );

            LiveData<LiveEvent<String>> handleLiveData = MainDataRepository.getInstance()
                    .getInstituteDataRepository()
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

    public static final class EventUpdater {

        private EventUpdater(EventModel eventModel) {
            id = eventModel.getId();
            description = new MutableLiveData<>(eventModel.getDescription());
            tags = new MutableLiveData<>(eventModel.getTags());
        }

        private final String id;

        private String getId() {
            return id;
        }

        public final MutableLiveData<String> description;
        public final MutableLiveData<List<String>> tags;

    }

    public static final class ArticleUpdater {

        private ArticleUpdater(ArticleModel articleModel) {
            id = articleModel.getId();
            text = new MutableLiveData<>(articleModel.getText());
            author = new MutableLiveData<>(articleModel.getAuthor());
            tags = new MutableLiveData<>(articleModel.getTags());
        }

        private final String id;

        private String getId() {
            return id;
        }

        public final MutableLiveData<String> text;
        public final MutableLiveData<String> author;
        public final MutableLiveData<List<String>> tags;
    }

    public static final class ShowcaseUpdater {

        private ShowcaseUpdater(ShowcaseModel showcaseModel) {
            id = showcaseModel.getId();
            title = new MutableLiveData<>(showcaseModel.getTitle());
            photo = new MutableLiveData<>(showcaseModel.getPhoto());
        }

        private final String id;

        private String getId() {
            return id;
        }

        public final MutableLiveData<String> title;
        public final MutableLiveData<String> photo;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
