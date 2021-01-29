package com.omada.junctionadmin.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.common.collect.ImmutableList;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.PostDataHandler;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.models.external.InterestModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.data.models.mutable.MutableArticleModel;
import com.omada.junctionadmin.data.models.mutable.MutableEventModel;
import com.omada.junctionadmin.data.models.testdummy.TestVenueModel;
import com.omada.junctionadmin.ui.login.LoginActivity;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveDataAggregator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;
import com.omada.junctionadmin.utils.transform.TransformUtilities;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.omada.junctionadmin.data.DataRepository.getInstance;


public class CreatePostViewModel extends BaseViewModel {

    private enum CurrentState {
        CURRENT_STATE_IDLE,
        CURRENT_STATE_UPLOADING,
        CURRENT_STATE_UPLOAD_SUCCESS,
        CURRENT_STATE_UPLOAD_FAILURE
    }

    private CurrentState currentState = CurrentState.CURRENT_STATE_IDLE;
    private final EventCreator eventCreator = new TestEventCreator();
    private final ArticleCreator articleCreator = new ArticleCreator();

    private final MutableLiveData<LiveEvent<Boolean>> createEventTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Boolean>> createArticleTrigger = new MutableLiveData<>();

    public CreatePostViewModel() {
        initCalendar();
    }

    public void goToCreateEvent() {
        createEventTrigger.setValue(new LiveEvent<>(true));
    }

    public void goToCreateArticle() {
        createArticleTrigger.setValue(new LiveEvent<>(true));
    }

    public LiveData<LiveEvent<Boolean>> getCreateEventTrigger() {
        return createEventTrigger;
    }

    public MutableLiveData<LiveEvent<Boolean>> getCreateArticleTrigger() {
        return createArticleTrigger;
    }

    public LiveData<LiveEvent<Boolean>> createEvent() {

        PostDataHandler.EventCreatorModel eventModel = new PostDataHandler.EventCreatorModel();

        AtomicBoolean anyDetailsEntryInvalid = new AtomicBoolean(false);

        dataValidator.validateEventTitle(eventCreator.title.getValue(), dataValidationInformation -> {
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                eventModel.setTitle(eventCreator.title.getValue());
            }
            else {
                anyDetailsEntryInvalid.set(true);
            }
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateImage(eventCreator.getImagePath(), dataValidationInformation -> {
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                eventModel.setImagePath(eventCreator.getImagePath());
            }
            else {
                anyDetailsEntryInvalid.set(true);
            }
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateEventDescription(eventCreator.description.getValue(), dataValidationInformation -> {
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                eventModel.setDescription(eventCreator.description.getValue());
            } else {
                anyDetailsEntryInvalid.set(true);
            }
            notifyValidity(dataValidationInformation);
        });


        /*
         In case of invalidity we return a constant live data (dummy) that indicates
         unsuccessful transaction
        */
        if(anyDetailsEntryInvalid.get()) {
            notifyValidity(
                    new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                    )
            );
            return new MutableLiveData<>(new LiveEvent<>(false));
        }
        else {
            notifyValidity(
                    new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
                    )
            );
        }


        OrganizationModel organizationModel = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel();

        // All other attributes that are guaranteed to be valid
        synchronized (eventCreator) {
            eventModel.setCreator(organizationModel.getId());
            eventModel.setCreatorInstitute(organizationModel.getInstitute());
            eventModel.setCreatorName(organizationModel.getName());
            eventModel.setCreatorMail(organizationModel.getMail());
            eventModel.setCreatorPhone(organizationModel.getPhone());
            eventModel.setCreatorProfilePicture(organizationModel.getProfilePicture());

            // TODO check how exactly parsing is done
            eventModel.setStartTime(TransformUtilities.utcDateFromLocalDateTime(
                    LocalDateTime.parse(eventCreator.startTime.getValue()))
            );
            eventModel.setEndTime(TransformUtilities.utcDateFromLocalDateTime(
                    LocalDateTime.parse(eventCreator.endTime.getValue()))
            );

            //VenueModel venueModel = eventCreator.getVenueModel();
            VenueModel venueModel = new TestVenueModel();
            eventModel.setVenue(venueModel.getId());
            eventModel.setVenueName(venueModel.getName());
            eventModel.setVenueAddress(venueModel.getAddress());
            eventModel.setVenueInstitute(venueModel.getInstitute());

            eventModel.setForm(eventCreator.getForm());
            eventModel.setTags(eventCreator.getTags());

        }

        currentState = CurrentState.CURRENT_STATE_UPLOADING;
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
                    if(result) {
                        currentState = CurrentState.CURRENT_STATE_UPLOAD_SUCCESS;
                    }
                    else {
                        currentState = CurrentState.CURRENT_STATE_UPLOAD_FAILURE;
                    }
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

        PostDataHandler.ArticleCreatorModel articleModel = new PostDataHandler.ArticleCreatorModel();

        // accessing and setting atomically
        synchronized (articleCreator) {
            articleModel.setCreator(organizationModel.getId());
            articleModel.setCreatorName(organizationModel.getName());
            articleModel.setCreatorMail(organizationModel.getMail());
            articleModel.setCreatorPhone(organizationModel.getPhone());
            articleModel.setCreatorProfilePicture(organizationModel.getProfilePicture());

            articleModel.setText(articleCreator.text.getValue());
            articleModel.setTitle(articleCreator.title.getValue());
            articleModel.setImagePath(articleCreator.getImagePath());
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


    public EventCreator getEventCreator() {
        return eventCreator;
    }

    public ArticleCreator getArticleCreator() {
        return articleCreator;
    }

    public void resetCreators() {
        eventCreator.resetData();
        articleCreator.resetData();
    }


    private long endTime;
    private long startTime;

    private void initCalendar() {

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.setTimeInMillis(today);

        calendar.roll(Calendar.YEAR, -5);
        endTime = calendar.getTimeInMillis();

        calendar.roll(Calendar.YEAR, -70);
        startTime = calendar.getTimeInMillis();
    }

    public MaterialDatePicker.Builder<?> setupDateSelectorBuilder() {

        int inputMode = MaterialDatePicker.INPUT_MODE_CALENDAR;

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(endTime);
        builder.setInputMode(inputMode);

        return builder;
    }

    public CalendarConstraints.Builder setupConstraintsBuilder() {

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        constraintsBuilder.setStart(startTime);
        constraintsBuilder.setEnd(endTime);
        constraintsBuilder.setOpenAt(endTime);

        return constraintsBuilder;
    }

    public static class EventCreator {

        private EventCreator(){
        }

        public final MutableLiveData<String> title = new MutableLiveData<>();
        public final MutableLiveData<String> description = new MutableLiveData<>();
        protected Uri imagePath;


        public final MutableLiveData<String> startTime = new MutableLiveData<>();
        public final MutableLiveData<String> endTime = new MutableLiveData<>();

        protected VenueModel venueModel;
        protected ImmutableList<String> tags;
        protected Map <String, Map <String, Map<String, String>>> form;

        public VenueModel getVenueModel() {
            return venueModel;
        }

        public void setVenueModel(VenueModel venueModel) {
            this.venueModel = venueModel;
        }

        public ImmutableList<String> getTags() {
            return tags != null ? tags : ImmutableList.copyOf(new ArrayList<>());
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
            startTime.setValue(null);
            endTime.setValue(null);
            imagePath = null;
            tags = null;

            if (resetForm) {
                form = null;
            }
            if (resetVenueModel) {
                venueModel = null;
            }

        }

        public Uri getImagePath() {
            return imagePath;
        }

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }
    }

    public static final class ArticleCreator {

        public final MutableLiveData<String> title = new MutableLiveData<>();
        public final MutableLiveData<String> text = new MutableLiveData<>();
        public final MutableLiveData<String> author = new MutableLiveData<>();
        protected Uri imagePath;

        protected ImmutableList<String> tags;

        private ArticleCreator() {
            setTags(new ArrayList<>());
        }

        public ImmutableList<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = ImmutableList.copyOf(tags);
        }

        public void resetData() {

            title.setValue(null);
            text.setValue(null);
            author.setValue(null);
            imagePath = null;
            tags = null;

        }

        public Uri getImagePath() {
            return imagePath;
        }

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }
    }

    private static class TestEventCreator extends EventCreator {

        public TestEventCreator() {
            setVenueModel(new TestVenueModel());
            startTime.setValue(LocalDateTime.of(2021, 2, 5, 12, 0, 0).format(
                    DateTimeFormatter.ISO_DATE_TIME
            ));
            endTime.setValue(LocalDateTime.of(2021, 2, 5, 12, 30, 0).format(
                    DateTimeFormatter.ISO_DATE_TIME
            ));
        }

        @Override
        public void resetData() {
            resetData(true, true);
        }

        @Override
        public void resetData(boolean resetForm, boolean resetVenueModel) {

            title.setValue(null);
            description.setValue(null);
            imagePath = null;
            tags = null;

            if (resetForm) {
                form = null;
            }
            if (resetVenueModel) {
                venueModel = null;
            }
        }
    }
}
