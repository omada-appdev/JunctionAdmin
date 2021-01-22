package com.omada.junctionadmin.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.common.collect.ImmutableList;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.mutable.MutableArticleModel;
import com.omada.junctionadmin.data.models.mutable.MutableEventModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;
import com.omada.junctionadmin.utils.transform.TransformUtilities;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CreatePostViewModel extends BaseViewModel {

    private final EventCreator eventCreator = new EventCreator();
    private final ArticleCreator articleCreator = new ArticleCreator();

    public LiveData<LiveEvent<Boolean>> createEvent() {

        OrganizationModel organizationModel = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel();

        // TODO validate data
        MutableLiveData<LiveEvent<Boolean>> validityResult = new MutableLiveData<>();


        MutableEventModel eventModel = new MutableEventModel();

        synchronized (eventCreator) {
            eventModel.setCreator(organizationModel.getId());
            eventModel.setCreatorName(organizationModel.getName());
            eventModel.setCreatorMail(organizationModel.getMail());
            eventModel.setCreatorPhone(organizationModel.getPhone());
            eventModel.setCreatorProfilePicture(organizationModel.getProfilePicture());

            eventModel.setDescription(eventCreator.description.getValue());
            eventModel.setTitle(eventCreator.title.getValue());
            eventModel.setStartTime(TransformUtilities.utcDateFromLocalDateTime(
                    eventCreator.startTime.getValue())
            );
            eventModel.setEndTime(TransformUtilities.utcDateFromLocalDateTime(
                    eventCreator.endTime.getValue())
            );

            VenueModel venueModel = eventCreator.getVenueModel();
            eventModel.setVenue(venueModel.getId());
            eventModel.setVenueName(venueModel.getName());
            eventModel.setVenueAddress(venueModel.getAddress());
            eventModel.setVenueInstitute(venueModel.getInstitute());

            eventModel.setForm(eventCreator.getForm());
            eventModel.setTags(eventCreator.getTags());
        }

        boolean valid = true;

        // TODO validate

        if(!valid){
            validityResult.setValue(new LiveEvent<>(false));
            return validityResult;
        }

        return Transformations.map(
                DataRepository
                    .getInstance()
                    .getPostDataHandler()
                    .createPost(eventModel),

                input -> {
                    if (input == null) {
                        return null;
                    }
                    Boolean result = input.getDataOnceAndReset();
                    // TODO do something with result
                    return new LiveEvent<>(result);
                }
        );
    }

    public LiveData<LiveEvent<Boolean>> createArticle() {


        OrganizationModel organizationModel = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel();


        // TODO validate data
        MutableLiveData<LiveEvent<Boolean>> validityResult = new MutableLiveData<>();

        MutableArticleModel articleModel = new MutableArticleModel();

        // accessing and setting atomically
        synchronized (articleCreator) {
            articleModel.setCreator(organizationModel.getId());
            articleModel.setCreatorName(organizationModel.getName());
            articleModel.setCreatorMail(organizationModel.getMail());
            articleModel.setCreatorPhone(organizationModel.getPhone());
            articleModel.setCreatorProfilePicture(organizationModel.getProfilePicture());

            articleModel.setText(articleCreator.text.getValue());
            articleModel.setTitle(articleCreator.title.getValue());
            articleModel.setImage(articleCreator.image.getValue());
            articleModel.setAuthor(articleCreator.author.getValue());

            articleModel.setTags(articleCreator.getTags());
        }

        boolean valid = true;

        // TODO validate

        if(!valid){
            validityResult.setValue(new LiveEvent<>(false));
            return validityResult;
        }

        return Transformations.map(
                DataRepository
                        .getInstance()
                        .getPostDataHandler()
                        .createPost(articleModel),

                input -> {
                    if (input == null) {
                        return null;
                    }
                    Boolean result = input.getDataOnceAndReset();
                    // TODO do something with result
                    return new LiveEvent<>(result);
                }
        );
    }


    public void resetCreators() {
        eventCreator.resetData();
        articleCreator.resetData();
    }

    public EventCreator getEventCreator() {
        return eventCreator;
    }

    public ArticleCreator getArticleCreator() {
        return articleCreator;
    }


    public static final class EventCreator {

        private EventCreator(){
        }

        public final MutableLiveData<String> title = new MutableLiveData<>();
        public final MutableLiveData<String> description = new MutableLiveData<>();
        public final MutableLiveData<String> image = new MutableLiveData<>();


        public final MutableLiveData<LocalDateTime> startTime = new MutableLiveData<>();
        public final MutableLiveData<LocalDateTime> endTime = new MutableLiveData<>();

        private VenueModel venueModel;
        private ImmutableList<String> tags;
        private Map <String, Map <String, Map<String, String>>> form;

        public VenueModel getVenueModel() {
            return venueModel;
        }

        public void setVenueModel(VenueModel venueModel) {
            this.venueModel = venueModel;
        }

        public ImmutableList<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = ImmutableList.copyOf(tags);
        }

        public Map<String, Map<String, Map<String, String>>> getForm() {
            return form;
        }

        public void setForm(Map<String, Map<String, Map<String, String>>> form) {
            this.form = form;
        }

        public void resetData(){
            resetData(true, true);
        }

        public void resetData(boolean resetForm, boolean resetVenueModel) {

            title.setValue(null);
            description.setValue(null);
            image.setValue(null);
            startTime.setValue(null);
            endTime.setValue(null);

            if (resetForm) {
                form = null;
            }
            if (resetVenueModel) {
                venueModel = null;
            }

        }
    }

    public static final class ArticleCreator {

        public final MutableLiveData<String> title = new MutableLiveData<>();
        public final MutableLiveData<String> text = new MutableLiveData<>();
        public final MutableLiveData<String> author = new MutableLiveData<>();
        public final MutableLiveData<String> image = new MutableLiveData<>();

        private ImmutableList<String> tags;

        private ArticleCreator() {
        }

        public void setTags(List<String> tags) {
            this.tags = ImmutableList.copyOf(tags);
        }

        public ImmutableList<String> getTags() {
            return tags;
        }

        public void resetData() {

            title.setValue(null);
            text.setValue(null);
            image.setValue(null);
            author.setValue(null);

        }
    }

}
