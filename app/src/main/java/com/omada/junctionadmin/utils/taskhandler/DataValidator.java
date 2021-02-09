package com.omada.junctionadmin.utils.taskhandler;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.omada.junctionadmin.data.DataRepository;

import java.time.LocalDateTime;


/*
*
* NOTE
* This class has all the code for data validation so use this class wherever required.
* There are plans to convert it into a static singleton, seeing how this class has no state and all the methods
* can be treated to be functional self enclosed entities
*
* Remember to always assume the validation completion listener is invoked asynchronously and not immediately. This is because sometimes
* validation involves network calls and database queries.
*
* TODO make all methods static and use a builder for batch validation
 */
public class DataValidator {


    public static final int EVENT_DESCRIPTION_MAX_SIZE = 200;
    public static final int EVENT_TITLE_MAX_SIZE = 30;
    public static final int EVENT_TITLE_MIN_SIZE = 5;
    private static final String EMAIL_VERIFICATION_REGEX =
            "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";

    private static final String NAME_VERIFICATION_REGEX =
            "[a-zA-Z ]*";

    private static final String PHONE_VERIFICATION_REGEX =
            "[0-9]{6,}";


    public enum DataValidationPoint {

        VALIDATION_POINT_ALL,

        VALIDATION_POINT_NAME,
        VALIDATION_POINT_EMAIL,
        VALIDATION_POINT_PHONE,
        VALIDATION_POINT_INSTITUTE_HANDLE,
        VALIDATION_POINT_PASSWORD,
        VALIDATION_POINT_INTERESTS,

        VALIDATION_POINT_PROFILE_PICTURE,
        VALIDATION_POINT_IMAGE,

        VALIDATION_POINT_EVENT_TITLE,
        VALIDATION_POINT_EVENT_DESCRIPTION,

        VALIDATION_POINT_EVENT_TIMINGS,

        VALIDATION_POINT_ARTICLE_TITLE,
        VALIDATION_POINT_ARTICLE_AUTHOR,

        VALIDATION_POINT_TAGS
    }

    public enum DataValidationResult {

        VALIDATION_RESULT_VALID,
        VALIDATION_RESULT_INVALID,

        VALIDATION_RESULT_BLANK_VALUE,
        VALIDATION_RESULT_NO_INFO,

        VALIDATION_RESULT_UNEXPECTED_VALUE,
        VALIDATION_RESULT_ILLEGAL_FORMAT,
        VALIDATION_RESULT_PARSE_ERROR,
        VALIDATION_RESULT_DOES_NOT_EXIST,
        VALIDATION_RESULT_OVERFLOW,
        VALIDATION_RESULT_UNDERFLOW,

    }


    public void validateEventTimings(LocalDateTime startTime, LocalDateTime endTime) {

    }

    public void validateName(String name, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validateName(name));
    }

    public void validateEmail(String email, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validateEmail(email));
    }

    public void validatePhone(String phone, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validatePhone(phone));
    }

    public void validateInstitute(String institute, OnValidationCompleteListener listener) {

        LiveData<LiveEvent<DataValidationInformation>> dataValidationLiveData = validateInstitute(institute);
        dataValidationLiveData.observeForever(new Observer<LiveEvent<DataValidationInformation>>() {
            @Override
            public void onChanged(LiveEvent<DataValidationInformation> dataValidationInformationLiveEvent) {
                listener.onValidationComplete(dataValidationLiveData.getValue().getDataOnceAndReset());
                dataValidationLiveData.removeObserver(this);
            }
        });
    }

    public void validatePassword(String password, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validatePassword(password));
    }

    public void validateEventTitle(String title, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validateEventTitle(title));
    }

    public void validateEventDescription(String description, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validateEventDescription(description));
    }

    /*
      STUB
     */
    // TODO add validation when needed
    public void validateProfilePicture (Uri uri, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validateProfilePicture(uri));
    }

    public void validateImage (Uri uri, OnValidationCompleteListener listener) {
        listener.onValidationComplete(validateImage(uri));
    }



    private DataValidationInformation validateName(String name) {

        if (name == null) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_NAME,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        }

        name = name.trim();

        if (name.equals("")) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_NAME,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        } else if (!name.matches(NAME_VERIFICATION_REGEX)) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_NAME,
                    DataValidationResult.VALIDATION_RESULT_ILLEGAL_FORMAT
            );
        } else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_NAME,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }

    }

    private DataValidationInformation validateEmail(String email) {

        if (email == null) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EMAIL,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        }

        email = email.trim();

        if (email.equals("")) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EMAIL,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        } else if (!email.matches(EMAIL_VERIFICATION_REGEX)) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EMAIL,
                    DataValidationResult.VALIDATION_RESULT_ILLEGAL_FORMAT
            );
        } else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EMAIL,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }
    }

    private DataValidationInformation validatePhone(String phone) {

        if (phone == null) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PHONE,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        }

        phone = phone.trim();

        if (phone.equals("")) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PHONE,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        } else if (!phone.matches(PHONE_VERIFICATION_REGEX)) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PHONE,
                    DataValidationResult.VALIDATION_RESULT_ILLEGAL_FORMAT
            );
        } else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PHONE,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }
    }


    private LiveData<LiveEvent<DataValidationInformation>> validateInstitute(String institute) {

        if(institute == null || institute.equals("")){
            return new MutableLiveData<>(new LiveEvent<>(new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                    DataValidationResult.VALIDATION_RESULT_INVALID
            )));
        }

        return Transformations.map(
                DataRepository
                        .getInstance()
                        .getInstituteDataHandler()
                        .checkInstituteCodeValidity(institute),

                input -> {
                    Boolean result = input.getDataOnceAndReset();
                    if (result != null && result) {
                        return new LiveEvent<>(new DataValidationInformation(
                                DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                DataValidationResult.VALIDATION_RESULT_VALID
                        ));
                    } else {
                        return new LiveEvent<>(new DataValidationInformation(
                                DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                DataValidationResult.VALIDATION_RESULT_INVALID
                        ));
                    }
                }
        );
    }

    private DataValidationInformation validateProfilePicture(Uri uri) {
        if (uri == null) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PROFILE_PICTURE,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        } else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PROFILE_PICTURE,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }
    }

    private DataValidationInformation validateEventDescription(String description) {

        if(description == null || description.equals("")) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_DESCRIPTION,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        }
        else if (description.length() > EVENT_DESCRIPTION_MAX_SIZE) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_DESCRIPTION,
                    DataValidationResult.VALIDATION_RESULT_OVERFLOW
            );
        }
        else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_DESCRIPTION,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }

    }

    private DataValidationInformation validateEventTitle(String title) {

        if(title == null || title.equals("")) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_TITLE,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        }
        else if (title.length() > EVENT_TITLE_MAX_SIZE) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_TITLE,
                    DataValidationResult.VALIDATION_RESULT_OVERFLOW
            );
        }
        else if (title.length() < EVENT_TITLE_MIN_SIZE) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_TITLE,
                    DataValidationResult.VALIDATION_RESULT_UNDERFLOW
            );
        }
        else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_EVENT_TITLE,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }

    }

    private DataValidationInformation validateImage(Uri uri) {
        if (uri == null) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_IMAGE,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        } else {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_IMAGE,
                    DataValidationResult.VALIDATION_RESULT_VALID
            );
        }
    }

    private DataValidationInformation validatePassword(String password) {
        if (password == null || password.equals("")) {
            return new DataValidationInformation(
                    DataValidationPoint.VALIDATION_POINT_PASSWORD,
                    DataValidationResult.VALIDATION_RESULT_BLANK_VALUE
            );
        }
        return new DataValidationInformation(
                DataValidationPoint.VALIDATION_POINT_PASSWORD,
                DataValidationResult.VALIDATION_RESULT_VALID
        );
    }

    public static final class DataValidationInformation {

        private final DataValidationPoint dataValidationPoint;
        private final DataValidationResult dataValidationResult;

        public DataValidationInformation(DataValidationPoint dataValidationPoint, DataValidationResult dataValidationResult) {
            this.dataValidationPoint = dataValidationPoint;
            this.dataValidationResult = dataValidationResult;
        }

        public DataValidationPoint getValidationPoint() {
            return dataValidationPoint;
        }

        public DataValidationResult getDataValidationResult() {
            return dataValidationResult;
        }
    }

    public interface OnValidationCompleteListener {
        void onValidationComplete(DataValidationInformation dataValidationInformation);
    }

}
