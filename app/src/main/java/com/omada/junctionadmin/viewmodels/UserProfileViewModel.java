package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserProfileViewModel extends OrganizationProfileViewModel {

    private final OrganizationUpdater organizationUpdater = new OrganizationUpdater();

    @Override
    public void setOrganizationModel(OrganizationModel organizationModel) {
        super.setOrganizationModel(organizationModel);
        organizationUpdater.setValues(organizationModel);
    }

    // Sets the updater in the transformation
    @Override
    public LiveData<LiveEvent<OrganizationModel>> getOrganizationDetails() {

        if(organizationModel != null){
            MutableLiveData<LiveEvent<OrganizationModel>> organizationDetailsLiveData = new MutableLiveData<>();
            organizationDetailsLiveData.setValue(new LiveEvent<>(organizationModel));
            return organizationDetailsLiveData;
        }
        return Transformations.map(
                DataRepository.getInstance().getOrganizationDataHandler().getOrganizationDetails(organizationID),
                input -> {
                    if(input == null){
                        return null;
                    }
                    else {
                        OrganizationModel model = input.getDataOnceAndReset();
                        organizationModel = model;
                        organizationUpdater.setValues(model);

                        return new LiveEvent<>(model);
                    }
                }
        );

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

}
