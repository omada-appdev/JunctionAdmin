package com.omada.junctionadmin.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.common.collect.ImmutableList;
import com.omada.junctionadmin.data.models.testdummy.TestVenueModel;
import com.omada.junctionadmin.data.repository.MainDataRepository;
import com.omada.junctionadmin.data.handler.PostDataHandler;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.StringUtilities;
import com.omada.junctionadmin.utils.TransformUtilities;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

/*
    For all dates and times all OUTPUTS are in system zone, all INPUTS are in UTC zone
    Everything that the View layer directly interacts with must be in system zone by default
 */
public class CreatePostViewModel extends BaseViewModel {

    public enum CurrentState {
        CURRENT_STATE_IDLE,
        CURRENT_STATE_UPLOADING,
        CURRENT_STATE_UPLOAD_SUCCESS;
    }

    private CurrentState currentState = CurrentState.CURRENT_STATE_IDLE;

    private final EventCreator eventCreator = new EventCreator();

    private final ArticleCreator articleCreator = new ArticleCreator();
    private final MutableLiveData<LiveEvent<Boolean>> createEventTrigger = new MutableLiveData<>();

    private final MutableLiveData<LiveEvent<Boolean>> createArticleTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Boolean>> createBookingTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Boolean>> createFormTrigger = new MutableLiveData<>();

    private final MutableLiveData<LiveEvent<Boolean>> bookingValidationTrigger = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Boolean>> formValidationTrigger = new MutableLiveData<>();

    public CreatePostViewModel() {
        initCalendar();
    }

    public void goToCreateBooking() {
        createBookingTrigger.setValue(new LiveEvent<>(true));
    }

    public void goToCreateForm() {
        createFormTrigger.setValue(new LiveEvent<>(true));
    }

    public void goToCreateEvent() {
        createEventTrigger.setValue(new LiveEvent<>(true));
    }

    public void goToCreateArticle() {
        createArticleTrigger.setValue(new LiveEvent<>(true));
    }


    public LiveData<LiveEvent<Boolean>> getCreateBookingTrigger() {
        return createBookingTrigger;
    }

    public LiveData<LiveEvent<Boolean>> getCreateFormTrigger() {
        return createFormTrigger;
    }

    public LiveData<LiveEvent<Boolean>> getCreateEventTrigger() {
        return createEventTrigger;
    }

    public MutableLiveData<LiveEvent<Boolean>> getBookingValidationTrigger() {
        return bookingValidationTrigger;
    }

    public MutableLiveData<LiveEvent<Boolean>> getFormValidationTrigger() {
        return formValidationTrigger;
    }

    public MutableLiveData<LiveEvent<Boolean>> getCreateArticleTrigger() {
        return createArticleTrigger;
    }

    public LiveData<LiveEvent<Boolean>> addEventForm(String formLink) {

        MutableLiveData<LiveEvent<Boolean>> result = new MutableLiveData<>();
        dataValidator.validateFormLink(formLink, dataValidationInformation -> {
            if (dataValidationInformation == null) {
                return;
            }
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                Map<String, Map<String, Map<String, String>>> form = new HashMap<>();
                form.put("links", new HashMap<>());
                form.get("links").put("linkId", new HashMap<>());
                form.get("links").get("linkId").put("url", formLink);
                eventCreator.setForm(form);
                result.setValue(new LiveEvent<>(true));
            } else {
                result.setValue(new LiveEvent<>(false));
            }
        });
        return result;
    }

    public LiveData<LiveEvent<Boolean>> createEvent() {

        PostDataHandler.EventCreatorModel eventModel = new PostDataHandler.EventCreatorModel();

        AtomicBoolean anyDetailsEntryInvalid = new AtomicBoolean(false);

        if (eventCreator.startTime.getValue() == null || eventCreator.endTime.getValue() == null) {
            anyDetailsEntryInvalid.set(true);
            Log.e("Create", "Null start time or end time");
            bookingValidationTrigger.setValue(new LiveEvent<>(false));
        }
        if (eventCreator.getVenueModel() == null) {
            anyDetailsEntryInvalid.set(true);
            Log.e("Create", "Null venue");
            bookingValidationTrigger.setValue(new LiveEvent<>(false));
        }

        if (eventCreator.getForm() == null) {
            anyDetailsEntryInvalid.set(true);
            Log.e("Create", "Null form");
            formValidationTrigger.setValue(new LiveEvent<>(false));
        }

        dataValidator.validateEventTitle(eventCreator.title.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                eventModel.setTitle(eventCreator.title.getValue());
            } else {
                anyDetailsEntryInvalid.set(true);
            }
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateImage(eventCreator.getImagePath(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                eventModel.setImagePath(eventCreator.getImagePath());
            } else {
                anyDetailsEntryInvalid.set(true);
            }
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateEventDescription(eventCreator.description.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
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
        if (anyDetailsEntryInvalid.get()) {
            notifyValidity(
                    new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                    )
            );
            return new MutableLiveData<>(new LiveEvent<>(false));
        } else {
            notifyValidity(
                    new DataValidator.DataValidationInformation(
                            DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                            DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
                    )
            );
        }


        OrganizationModel organizationModel = MainDataRepository
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

            eventModel.setStartTime(
                    TransformUtilities.convertSystemZoneLocalDateTimeToUtc(
                            eventCreator.startTime.getValue()
                    )
            );
            eventModel.setEndTime(
                    TransformUtilities.convertSystemZoneLocalDateTimeToUtc(
                            eventCreator.endTime.getValue()
                    )
            );

            VenueModel venueModel = eventCreator.getVenueModel();
            eventModel.setVenue(venueModel.getId());
            eventModel.setVenueName(venueModel.getName());
            eventModel.setVenueAddress(venueModel.getAddress());
            eventModel.setVenueInstitute(venueModel.getInstitute());

            eventModel.setForm(eventCreator.getForm());
            eventModel.setTags(eventCreator.getTags());

        }

        currentState = CurrentState.CURRENT_STATE_UPLOADING;
        return Transformations.map(
                MainDataRepository
                        .getInstance()
                        .getPostDataHandler()
                        .createPost(eventModel),

                input -> {
                    if (input == null) {
                        return null;
                    }
                    Boolean result = input.getDataOnceAndReset();
                    if (result) {
                        currentState = CurrentState.CURRENT_STATE_UPLOAD_SUCCESS;
                    } else {
                        currentState = CurrentState.CURRENT_STATE_IDLE;
                    }
                    return new LiveEvent<>(result);
                }
        );
    }

    public LiveData<LiveEvent<Boolean>> createArticle() {

        OrganizationModel organizationModel = MainDataRepository
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

        if (!valid) {
            validityResult.setValue(new LiveEvent<>(false));
            return validityResult;
        }

        return Transformations.map(
                MainDataRepository
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

    public CurrentState getCurrentState() {
        return currentState;
    }

    public void resetCreators() {
        currentState = CurrentState.CURRENT_STATE_IDLE;
        eventCreator.resetData();
        articleCreator.resetData();
    }


    private long invalidUpTo;
    private long endTime;
    private long startTime;

    private void initCalendar() {

        long today = ZonedDateTime.now(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.clear();
        calendar.setTimeInMillis(today);

        startTime = calendar.getTimeInMillis();

        invalidUpTo = startTime;

        calendar.roll(Calendar.YEAR, 2);
        endTime = calendar.getTimeInMillis();

    }

    public MaterialDatePicker.Builder<?> setupDateSelectorBuilder() {

        int inputMode = MaterialDatePicker.INPUT_MODE_CALENDAR;

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(startTime);
        builder.setInputMode(inputMode);

        return builder;
    }

    public CalendarConstraints.Builder setupConstraintsBuilder() {

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        constraintsBuilder.setStart(startTime);
        constraintsBuilder.setEnd(endTime);
        constraintsBuilder.setOpenAt(startTime);
        constraintsBuilder.setValidator(DateValidatorPointForward.from(invalidUpTo));

        return constraintsBuilder;
    }


    public static class EventCreator {

        private EventCreator() {
        }

        public final MutableLiveData<String> title = new MutableLiveData<>();
        public final MutableLiveData<String> description = new MutableLiveData<>();

        protected Uri imagePath;

        // in system zone
        public final MutableLiveData<ZonedDateTime> startTime = new MutableLiveData<>();
        // in system zone
        public final MutableLiveData<ZonedDateTime> endTime = new MutableLiveData<>();

        protected VenueModel venueModel;
        protected ImmutableList<String> tags;
        protected Map<String, Map<String, Map<String, String>>> form;

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

        private Map<String, Map<String, Map<String, String>>> getForm() {
            return form;
        }

        public String getFormLink() {
            if (form == null) {
                return null;
            }
            String link = null;
            try {
                link = form.get("links").get("linkId").get("url");
            } catch (Exception e) {
                return null;
            }
            return link;
        }

        protected void setForm(Map<String, Map<String, Map<String, String>>> form) {
            this.form = form;
        }

        public void resetData() {
            resetData(true, true);
        }

        public void resetData(boolean resetForm, boolean resetVenueModel) {

            Log.e("Create", "Resetting event");
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
}
