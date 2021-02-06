package com.omada.junctionadmin.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.InstituteDataHandler;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.models.external.InterestModel;
import com.omada.junctionadmin.ui.login.LoginActivity;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveDataAggregator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.omada.junctionadmin.data.DataRepository.getInstance;


public class LoginViewModel extends BaseViewModel {

    private final static int MAX_INTERESTS = 5;

    //action fields (setting values triggers events)
    private final LiveData<LiveEvent<UserDataHandler.AuthStatus>> authResultAction;
    private final MutableLiveData<LiveEvent<LoginActivity.FragmentIdentifier>> fragmentChangeAction = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Boolean>> goToFeedAction = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<String>> toastMessageAction = new MutableLiveData<>();

    //data fields from UI
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public final MutableLiveData<String> password = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> institute = new MutableLiveData<>();
    public final MutableLiveData<Uri> profilePicture = new MutableLiveData<>();

    private final List<InterestModel> selectedInterests = new ArrayList<>();
    private final List<InterestModel> allInterests = new ArrayList<>();


    public LoginViewModel() {

        /*
        Observe this field in UI level to provide feedback to user
         */

        initCalendar();

        authResultAction = Transformations.map(
                getInstance().getUserDataHandler().getAuthResponseNotifier(),
                authResponse -> {

                    UserDataHandler.AuthStatus receivedAuthResponse = authResponse.getDataOnceAndReset();
                    if(receivedAuthResponse==null){
                        return authResponse;
                    }
                    switch (receivedAuthResponse){
                        case AUTHENTICATION_SUCCESS:
                            break;
                        case AUTHENTICATION_FAILURE:
                            break;
                        case LOGIN_SUCCESS:
                            goToFeedAction.setValue(new LiveEvent<>(true));
                            break;
                        case LOGIN_FAILURE:
                            email.setValue("");
                            password.setValue("");
                            break;
                        case SIGNUP_SUCCESS:
                            Log.e("SIGNUP", "success");
                            break;
                        case SIGNUP_FAILURE:
                            Log.e("SIGNUP", "failure");
                            email.setValue(null);
                            password.setValue(null);

                            break;
                        case ADD_EXTRA_DETAILS_SUCCESS:
                            Log.e("DETAILS", "success");
                            //add code to tell user to verify mail

                            getInstance()
                                    .getUserDataHandler()
                                    .authenticateUser(email.getValue(), password.getValue());
                            break;
                        case ADD_EXTRA_DETAILS_FAILURE:
                            Log.e("DETAILS", "failure");
                            break;
                    }
                    /*
                    done so that an old event is not triggered after attaching an observer later in another place
                    during the UI workflow
                    */
                    return new LiveEvent<>(receivedAuthResponse);
                }
        );
    }

    public void startSignIn(){
        fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_SIGNIN_FRAGMENT));
    }

    public void startSignUp(){
        fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_INTERESTS_FRAGMENT));
    }

    public void doUserLogin(){

        AtomicBoolean invalidData = new AtomicBoolean(false);

        dataValidator.validateEmail(email.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                invalidData.set(true);
            }
            notifyValidity(dataValidationInformation);
        });
        dataValidator.validatePassword(password.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                invalidData.set(true);
            }
            notifyValidity(dataValidationInformation);
        });

        if(!invalidData.get()) {

            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
            ));
            getInstance()
                    .getUserDataHandler()
                    .authenticateUser(email.getValue().trim(), password.getValue().trim());
        }

    }

    public void forgotPassword(){
        password.setValue(null);
        email.setValue(null);
        fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_FORGOTPASSWORD_FRAGMENT));
    }

    /*
    Called after user clicks on button to reset password. The email ID of the user is reflected onto
    email live data. It is set to null when forgotPassword() is called
     */
    public void resetPassword() {

        //TODO add code to send password reset link
    }

    public void detailsEntryDone(){

        // TODO add code to verify email and go to home only if email is verified

        UserDataHandler.MutableUserOrganizationModel userModel = new UserDataHandler.MutableUserOrganizationModel();
        MediatorLiveData<DataValidator.DataValidationInformation> anyDetailsEntryInvalid = new MediatorLiveData<>();
        ValidationAggregator validationAggregator = ValidationAggregator
                .build(anyDetailsEntryInvalid)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_PROFILE_PICTURE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_EMAIL)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_NAME)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_PASSWORD)
                .get();

        dataValidator.validateProfilePicture(profilePicture.getValue(), dataValidationInformation -> {
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                userModel.setProfilePicturePath(
                        profilePicture.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_PROFILE_PICTURE,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateInstitute(institute.getValue(), dataValidationInformation -> {

            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                LiveData<LiveEvent<String>> instituteId = DataRepository
                        .getInstance()
                        .getInstituteDataHandler()
                        .getInstituteId(institute.getValue());

                instituteId.observeForever(new Observer<LiveEvent<String>>() {
                    @Override
                    public void onChanged(LiveEvent<String> stringLiveEvent) {
                        if (stringLiveEvent == null) {
                            return;
                        }
                        String result = stringLiveEvent.getDataOnceAndReset();
                        DataValidator.DataValidationInformation newValidationInformation;

                        if (result != null && !result.equals("notFound")) {
                            userModel.setInstitute(result);
                            newValidationInformation = new DataValidator.DataValidationInformation(
                                    DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                    DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
                            );
                        } else {
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
                        instituteId.removeObserver(this);
                    }
                });
            }
            else notifyValidity(dataValidationInformation);
        });

        dataValidator.validateName(name.getValue(), dataValidationInformation -> {
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                userModel.setName(
                        name.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_NAME,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validateEmail(email.getValue(), dataValidationInformation -> {
            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                userModel.setMail(
                        email.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_EMAIL,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validatePassword(password.getValue(), dataValidationInformation -> {
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_PASSWORD,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        if(selectedInterests.size()>0){
            List<String> interests = new ArrayList<>();
            for(InterestModel model : selectedInterests) {
                interests.add(model.interestString);
            }
            userModel.setInterests(interests);
        }

        anyDetailsEntryInvalid.observeForever(new Observer<DataValidator.DataValidationInformation>() {
            @Override
            public void onChanged(DataValidator.DataValidationInformation dataValidationInformation) {
                if(dataValidationInformation != null) {
                    notifyValidity(dataValidationInformation);
                    if (dataValidationInformation.getValidationPoint() == DataValidator.DataValidationPoint.VALIDATION_POINT_ALL
                        && dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {

                        getInstance()
                                .getUserDataHandler()
                                .createNewUserWithEmailAndPassword(email.getValue(), password.getValue(), userModel);

                    }
                }
                anyDetailsEntryInvalid.removeObserver(this);
            }
        });

    }

    public void interestsSelectionDone(List<InterestModel> interestListSection){

        if(interestListSection == null){
            return;
        }
        else if(interestListSection.size() == 0){
            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INTERESTS,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_UNDERFLOW
            ));
        }
        else if(interestListSection.size() > MAX_INTERESTS){
            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INTERESTS,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW
            ));
        }
        else {
            selectedInterests.clear();
            selectedInterests.addAll(interestListSection);
            fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_DETAILS_FRAGMENT));
        }
    }

    public void exitDetailsScreen(){
    }

    public void exitInterestsScreen(){
        name.setValue(null);
        institute.setValue(null);
        password.setValue(null);
        email.setValue(null);
    }

    public void exitSignInScreen(){
        email.setValue(null);
        password.setValue(null);
    }

    public LiveData<LiveEvent<UserDataHandler.AuthStatus>> getAuthResultAction(){
        return authResultAction;
    }

    public LiveData<LiveEvent<LoginActivity.FragmentIdentifier>> getFragmentChangeAction(){
        return fragmentChangeAction;
    }

    public LiveData<LiveEvent<Boolean>> getGoToFeedAction(){
        return goToFeedAction;
    }

    public LiveData<LiveEvent<String>> getToastMessageAction(){
        return toastMessageAction;
    }

    public List<InterestModel> getInterestsList(){
        if(allInterests.size()>0){
            return allInterests;
        }
        return getInstance().getAppDataHandler().getInterestsList();
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

}
