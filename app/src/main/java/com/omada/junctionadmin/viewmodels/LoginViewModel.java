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
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.data.models.external.InterestModel;
import com.omada.junctionadmin.ui.login.LoginActivity;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.omada.junctionadmin.data.repositorytemp.MainDataRepository.getInstance;


public class LoginViewModel extends BaseViewModel {

    private final static int MAX_INTERESTS = 5;

    //action fields (setting values triggers events)
    private final LiveData<LiveEvent<UserDataRepository.AuthStatus>> authResultAction;
    private final MutableLiveData<LiveEvent<LoginActivity.FragmentIdentifier>> fragmentChangeAction = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<Boolean>> goToFeedAction = new MutableLiveData<>();
    private final MutableLiveData<LiveEvent<String>> toastMessageAction = new MutableLiveData<>();

    //data fields from UI
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public final MutableLiveData<String> password = new MutableLiveData<>();
    public final MutableLiveData<String> email = new MutableLiveData<>();
    public final MutableLiveData<String> institute = new MutableLiveData<>();
    public final MutableLiveData<String> phone = new MutableLiveData<>();
    public final MutableLiveData<Uri> profilePicture = new MutableLiveData<>();

    private final List<InterestModel> selectedInterests = new ArrayList<>();
    private final List<InterestModel> allInterests = new ArrayList<>();


    public LoginViewModel() {

        /*
        Observe this field in UI level to provide feedback to user
         */

        initCalendar();

        authResultAction = Transformations.map(
                getInstance().getUserDataRepository().getAuthResponseNotifier(),
                authResponse -> {

                    UserDataRepository.AuthStatus receivedAuthResponse = authResponse.getDataOnceAndReset();
                    if (receivedAuthResponse == null) {
                        return authResponse;
                    }
                    switch (receivedAuthResponse) {
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
                            Log.e("Login", "Signed up successfully");
                            break;
                        case SIGNUP_FAILURE:
                            Log.e("Login", "Sign up failure");
                            email.setValue(null);
                            password.setValue(null);
                            toastMessageAction.setValue(new LiveEvent<>("Signed up successfully"));

                            break;
                        case ADD_EXTRA_DETAILS_SUCCESS:
                            Log.e("Login", "Added extra details successfully");
                            //add code to tell user to verify mail

                            getInstance()
                                    .getUserDataRepository()
                                    .authenticateUser(email.getValue(), password.getValue());
                            break;
                        case ADD_EXTRA_DETAILS_FAILURE:
                            Log.e("Login", "Error adding user details");
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

    public void startSignIn() {
        fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_SIGN_IN_FRAGMENT));
    }

    public void startSignUp() {
        fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_INTERESTS_FRAGMENT));
    }

    public void doUserLogin() {

        AtomicBoolean invalidData = new AtomicBoolean(false);

        dataValidator.validateEmail(email.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                invalidData.set(true);
            }
            notifyValidity(dataValidationInformation);
        });
        dataValidator.validatePassword(password.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                invalidData.set(true);
            }
            notifyValidity(dataValidationInformation);
        });

        if (!invalidData.get()) {

            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_ALL,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_VALID
            ));
            getInstance()
                    .getUserDataRepository()
                    .authenticateUser(email.getValue().trim(), password.getValue().trim());
        }

    }

    public void goToForgotPassword() {
        password.setValue(null);
        fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_FORGOT_PASSWORD_FRAGMENT));
    }

    public LiveData<LiveEvent<Boolean>> sendPasswordResetLink() {
        if (email.getValue() == null) {
            return new MutableLiveData<>(new LiveEvent<>(false));
        }
        return MainDataRepository.getInstance()
                .getUserDataRepository()
                .sendPasswordResetLink(email.getValue());
    }

    private UserDataRepository.MutableUserOrganizationModel validatedUserModel;

    private LiveData<DataValidator.DataValidationInformation> validateDetails() {

        validatedUserModel = new UserDataRepository.MutableUserOrganizationModel();
        MediatorLiveData<DataValidator.DataValidationInformation> anyDetailsEntryInvalid = new MediatorLiveData<>();

        ValidationAggregator validationAggregator = ValidationAggregator
                .build(anyDetailsEntryInvalid)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_PROFILE_PICTURE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_EMAIL)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_PHONE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_NAME)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE)
                .add(DataValidator.DataValidationPoint.VALIDATION_POINT_PASSWORD)
                .get();

        dataValidator.validateProfilePicture(profilePicture.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                validatedUserModel.setProfilePicturePath(
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

            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                LiveData<LiveEvent<String>> instituteId = MainDataRepository
                        .getInstance()
                        .getInstituteDataRepository()
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
                            validatedUserModel.setInstitute(result);
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
            } else {
                validationAggregator.holdData(
                        DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                        new DataValidator.DataValidationInformation(
                                DataValidator.DataValidationPoint.VALIDATION_POINT_INSTITUTE_HANDLE,
                                DataValidator.DataValidationResult.VALIDATION_RESULT_INVALID
                        )
                );
                notifyValidity(dataValidationInformation);
            }
        });

        dataValidator.validateName(name.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                validatedUserModel.setName(
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
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                validatedUserModel.setMail(
                        email.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_EMAIL,
                    dataValidationInformation
            );
            notifyValidity(dataValidationInformation);
        });

        dataValidator.validatePhone(phone.getValue(), dataValidationInformation -> {
            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                validatedUserModel.setPhone(
                        phone.getValue()
                );
            }
            validationAggregator.holdData(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_PHONE,
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

        if (selectedInterests.size() > 0) {
            List<String> interests = new ArrayList<>();
            for (InterestModel model : selectedInterests) {
                interests.add(model.interestString);
            }
            validatedUserModel.setInterests(interests);
        }

        return anyDetailsEntryInvalid;
    }

    public final void validateDetailsInputGetResult() {

        LiveData<DataValidator.DataValidationInformation> validationResultLiveData = validateDetails();

        validationResultLiveData.observeForever(new Observer<DataValidator.DataValidationInformation>() {
            @Override
            public void onChanged(DataValidator.DataValidationInformation dataValidationInformation) {
                if (dataValidationInformation != null) {
                    Log.e("Login", "Validated " + dataValidationInformation.getValidationPoint().name() + " " + dataValidationInformation.getDataValidationResult().name());
                    notifyValidity(dataValidationInformation);
                }
                validationResultLiveData.removeObserver(this);
            }
        });
    }

    public void validateDetailsInputAndCreateAccount() {

        // TODO add code to verify email and go to home only if email is verified

        LiveData<DataValidator.DataValidationInformation> validationResultLiveData = validateDetails();

        validationResultLiveData.observeForever(new Observer<DataValidator.DataValidationInformation>() {
            @Override
            public void onChanged(DataValidator.DataValidationInformation dataValidationInformation) {
                if (dataValidationInformation != null) {
                    if (dataValidationInformation.getValidationPoint() == DataValidator.DataValidationPoint.VALIDATION_POINT_ALL
                            && dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {

                        MainDataRepository
                                .getInstance()
                                .getUserDataRepository()
                                .createNewUserWithEmailAndPassword(email.getValue(), password.getValue(), validatedUserModel);
                    }
                }
                validationResultLiveData.removeObserver(this);
            }
        });

    }

    public void interestsSelectionDone(List<InterestModel> interestListSection) {

        if (interestListSection == null) {
            return;
        } else if (interestListSection.size() == 0) {
            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INTERESTS,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_UNDERFLOW
            ));
        } else if (interestListSection.size() > MAX_INTERESTS) {
            notifyValidity(new DataValidator.DataValidationInformation(
                    DataValidator.DataValidationPoint.VALIDATION_POINT_INTERESTS,
                    DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW
            ));
        } else {
            selectedInterests.clear();
            selectedInterests.addAll(interestListSection);
            fragmentChangeAction.setValue(new LiveEvent<>(LoginActivity.FragmentIdentifier.LOGIN_DETAILS_FRAGMENT));
        }
    }

    public void exitDetailsScreen() {
    }

    public void exitInterestsScreen() {
        name.setValue(null);
        institute.setValue(null);
        password.setValue(null);
        email.setValue(null);
    }

    public void exitSignInScreen() {
        email.setValue(null);
        password.setValue(null);
    }

    public LiveData<LiveEvent<UserDataRepository.AuthStatus>> getAuthResultAction() {
        return authResultAction;
    }

    public LiveData<LiveEvent<LoginActivity.FragmentIdentifier>> getFragmentChangeAction() {
        return fragmentChangeAction;
    }

    public LiveData<LiveEvent<Boolean>> getGoToFeedAction() {
        return goToFeedAction;
    }

    public LiveData<LiveEvent<String>> getToastMessageAction() {
        return toastMessageAction;
    }

    public List<InterestModel> getInterestsList() {
        if (allInterests.size() > 0) {
            return allInterests;
        }
        return getInstance().getAppDataRepository().getInterestsList();
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

    public static class DetailsDataValidationInformation extends DataValidator.DataValidationInformation {

        private final OrganizationModel validatedOrganizationModel;

        public DetailsDataValidationInformation(DataValidator.DataValidationPoint dataValidationPoint, DataValidator.DataValidationResult dataValidationResult, OrganizationModel validatedOrganizationModel) {
            super(dataValidationPoint, dataValidationResult);
            this.validatedOrganizationModel = validatedOrganizationModel;
        }

        private OrganizationModel getValidatedOrganizationModel() {
            return validatedOrganizationModel;
        }
    }

}
