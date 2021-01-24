package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.Date;
import java.util.List;
import java.util.Map;


/*
    This is purposely made to not inherit from OrganizationProfileViewModel
    because of possible misuse, as they have completely different patterns of accessing data
    One is through network request, other is through cached data

    Also, even though they share a lot in common, the two situations are very distinct
 */

public class UserProfileViewModel extends BaseViewModel{

    private final OrganizationUpdater organizationUpdater = new OrganizationUpdater();
    private final OrganizationModel organizationModel;
    private final String organizationId;

    private LiveData<LiveEvent<UserDataHandler.AuthStatus>> signOutTrigger;
    private MediatorLiveData<List<PostModel>> loadedOrganizationHighlights = new MediatorLiveData<>();
    private MediatorLiveData<List<ShowcaseModel>> loadedOrganizationShowcases = new MediatorLiveData<>();

    public UserProfileViewModel() {

        signOutTrigger = Transformations.map(
                DataRepository.getInstance()
                        .getUserDataHandler()
                        .getAuthResponseNotifier(),

                authStatusLiveEvent -> authStatusLiveEvent
        );

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
                    if(resultLiveEvent == null){
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
                            }
                            else {
                            }
                        }
                        else {
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
                            }
                            else {
                            }
                        }
                        else {
                        }
                        loadedOrganizationShowcases.removeSource(source);
                    }

                }
        );

    }

    public LiveData<List<PostModel>> getLoadedOrganizationHighlights(){
        return loadedOrganizationHighlights;
    }

    public LiveData<List<ShowcaseModel>> getLoadedOrganizationShowcases(){
        return loadedOrganizationShowcases;
    }

    public void signOutUser() {
        DataRepository.getInstance().getUserDataHandler().signOutCurrentUser();
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

    public LiveData<LiveEvent<UserDataHandler.AuthStatus>> getSignOutTrigger() {
        return signOutTrigger;
    }

    public static final class OrganizationUpdater {

        public final MutableLiveData<String> name = new MutableLiveData<>();
        public final MutableLiveData<String> mail = new MutableLiveData<>();
        public final MutableLiveData<String> phone = new MutableLiveData<>();
        public final MutableLiveData<String> profilePhoto = new MutableLiveData<>();

        public final MutableLiveData<List<String>> interests = new MutableLiveData<>();

        private OrganizationUpdater() {
        }

        private void setValues(OrganizationModel model) {
            name.setValue(model.getName());
            mail.setValue(model.getName());
            phone.setValue(model.getPhone());
            profilePhoto.setValue(model.getProfilePicture());
            interests.setValue(model.getInterests());
        }
    }

    public static class EventUpdater {

        private EventUpdater(EventModel eventModel){

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
        public final MutableLiveData<Date> startTime;
        public final MutableLiveData<Date> endTime;
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

}
