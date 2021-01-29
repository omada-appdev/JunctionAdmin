package com.omada.junctionadmin.data.handler;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.omada.junctionadmin.BuildConfig;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.converter.OrganizationModelConverter;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.internal.remote.OrganizationModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableOrganizationModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.HashMap;
import java.util.Map;

public class UserDataHandler extends BaseDataHandler {

    public enum AuthStatus {

        CURRENT_USER_SUCCESS,
        CURRENT_USER_LOGIN_SUCCESS,
        CURRENT_USER_FAILURE,
        CURRENT_USER_LOGIN_FAILURE,

        AUTHENTICATION_SUCCESS,
        AUTHENTICATION_FAILURE,
        LOGIN_SUCCESS,
        LOGIN_FAILURE,

        SIGNUP_SUCCESS,
        SIGNUP_FAILURE,
        ADD_EXTRA_DETAILS_SUCCESS,
        ADD_EXTRA_DETAILS_FAILURE,

        UPDATE_USER_DETAILS_SUCCESS,
        UPDATE_USER_DETAILS_FAILURE,

        USER_TOKEN_EXPIRED,
        USER_SIGNED_OUT
    }

    //to be used as a cache to check for changes
    private MutableOrganizationModel signedInUser = new MutableOrganizationModel();

    //output fields to view model or mid level layers
    MutableLiveData<LiveEvent<AuthStatus>> authResponseNotifier = new MutableLiveData<>();
    MutableLiveData<LiveEvent<OrganizationModel>> signedInUserNotifier = new MutableLiveData<>();

    //state fields go here
    private final OrganizationModelConverter organizationModelConverter = new OrganizationModelConverter();


    public UserDataHandler() {

        /*
        this is only to check for sign outs and token expiration (if needed)
         */
        FirebaseAuth.getInstance()
                .addAuthStateListener(firebaseAuth -> {

                    if (firebaseAuth.getCurrentUser() == null) {
                        signedInUser = null;
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.USER_SIGNED_OUT));
                    }

                });
    }

    public void createNewUserWithEmailAndPassword(String email, String password, MutableUserOrganizationModel details) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        /*
        signing out so that all tasks related to sign out can be handled by the auth state listeners
        TODO register auth state listeners in a suitable place
        */
        auth.signOut();

        details.setHeldEventsNumber(0);
        details.setAttendedUsersNumber(0);
        details.setType("club");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AUTH", "create new user successful");
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.SIGNUP_SUCCESS));
                        addCreatedUserDetails(details.getProfilePicturePath(), organizationModelConverter.convertExternalToRemoteDBModel(details));
                    } else {
                        Log.d("AUTH", "create new user unsuccessful");
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.SIGNUP_FAILURE));
                    }
                });

    }

    //to be called in createNewUser when it is successful and not anywhere else
    private void addCreatedUserDetails(Uri profilePicturePath, OrganizationModelRemoteDB details) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            /*
            do not use the local cached variable named signed in user because it might not yet be updated by the
            auth state listener and do not update it from here because the auth state listener will take care of it
            */

            DataRepository
                    .getInstance()
                    .getImageUploadHandler()
                    .uploadProfilePictureWithTask(profilePicturePath, user.getUid())
                    .addOnCompleteListener(uri -> {

                        details.setProfilePicture(uri.getResult().toString());
                        FirebaseFirestore.getInstance()
                                .collection("organizations")
                                .document(user.getUid())
                                .set(details)
                                .addOnSuccessListener(task -> {
                                    authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.ADD_EXTRA_DETAILS_SUCCESS));
                                })
                                .addOnFailureListener(task -> {
                                    authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.ADD_EXTRA_DETAILS_FAILURE));
                                });
                    });

        }
    }

    /*
    use it through authResponse LiveData by attaching a transformation or an observer and use the
    sign in data in auth state listener
    */
    public void authenticateUser(final String email, final String password) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.AUTHENTICATION_SUCCESS));
                        getNewlyAuthenticatedUserDetails();
                    } else {
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.AUTHENTICATION_FAILURE));
                    }
                });
    }

    /*
    This method is for getting details of newly authenticated user
     */
    private void getNewlyAuthenticatedUserDetails() {

        signedInUser = null;

        FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();

        if (newUser == null) {
            signedInUserNotifier.setValue(new LiveEvent<>(null));
        } else {

            FirebaseFirestore.getInstance()
                    .collection("organizations")
                    .document(newUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        OrganizationModelRemoteDB modelRemoteDB = documentSnapshot.toObject(OrganizationModelRemoteDB.class);
                        if (modelRemoteDB != null) {
                            modelRemoteDB.setId(documentSnapshot.getId());
                            signedInUser = new MutableOrganizationModel(
                                    organizationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB)
                            );

                            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_SUCCESS));
                            signedInUserNotifier.setValue(new LiveEvent<>(getCurrentUserModel()));
                        } else {
                            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_FAILURE));
                        }

                    })
                    .addOnFailureListener(e -> authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_FAILURE)));
        }
    }

    // Copy of user model. Changes from outside have no effect on it internally
    public OrganizationModel getCurrentUserModel() {
        return new MutableOrganizationModel(signedInUser);
    }

    /*
    this method triggers a get user through firebase auth that changes the value in signed in user
    notifier live data through the auth state listener callback
     */
    public void getCurrentUserDetails() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.getUid().equals("")) {
            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_SUCCESS));
            getUserDetailsFromRemote(user.getUid());
        } else {
            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_FAILURE));
        }
    }

    /*
    This method is called after login and getting details is done. ie, when firebase already has a current user.
    It sets details into the signed in user notifier live data
     */
    private void getUserDetailsFromRemote(String uid) {

        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals("")) {
            //TODO get data from local and then remote if that fails

            FirebaseFirestore.getInstance()
                    .collection("organizations")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        OrganizationModelRemoteDB modelRemoteDB = documentSnapshot.toObject(OrganizationModelRemoteDB.class);
                        if (modelRemoteDB != null) {
                            modelRemoteDB.setId(documentSnapshot.getId());
                            signedInUser = new MutableOrganizationModel(
                                    organizationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB)
                            );
                            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_LOGIN_SUCCESS));
                            signedInUserNotifier.setValue(new LiveEvent<>(getCurrentUserModel()));
                        } else {
                            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_FAILURE));
                        }

                    })
                    .addOnFailureListener(e -> authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_FAILURE)));

        } else {
            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_LOGIN_FAILURE));
        }

    }

    public void signOutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
        signedInUser = null;
    }

    public void updateCurrentUserDetails(MutableUserOrganizationModel updatedUserModel) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (BuildConfig.DEBUG && !(user != null && user.getUid() != null && !user.getUid().equals(""))) {
            throw new AssertionError("Assertion failed");
        }

        // To make sure we do not rewrite the entire document by mistake
        Map<String, Object> updates = new HashMap<>();

        updates.put("institute", signedInUser.getInstitute());
        updates.put("name", updatedUserModel.getName());

        // Filesystem path
        Uri newProfilePicture = updatedUserModel.getProfilePicturePath();
        if (newProfilePicture != null) {
            DataRepository.getInstance()
                    .getImageUploadHandler()
                    .uploadProfilePictureWithTask(newProfilePicture, user.getUid())
                    .addOnCompleteListener(uri -> {
                        String httpUrl = uri.getResult().toString();
                        updates.put("profilePicture", httpUrl);
                        updateDatabaseDetails(updates);
                    });
        }
        else {
            updateDatabaseDetails(updates);
        }

    }

    private void updateDatabaseDetails(Map<String, Object> updates) {

        FirebaseFirestore.getInstance()
                .collection("organizations")
                .document(getCurrentUserModel().getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {

                    Log.e("Update", "success");
                    Log.e("Update", "institute " + updates.get("institute"));
                    Log.e("Update", "success");
                    signedInUser.setInstitute((String) updates.get("institute"));
                    signedInUser.setName((String) updates.get("name"));

                    if(updates.get("profilePicture") != null) {
                        signedInUser.setProfilePicture((String) updates.get("profilePicture"));
                    }

                    signedInUserNotifier.setValue(new LiveEvent<>(signedInUser));
                    authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.UPDATE_USER_DETAILS_SUCCESS));
                })
                .addOnFailureListener(e -> {
                    Log.e("Update", e.getMessage());
                    authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.UPDATE_USER_DETAILS_FAILURE));
                });
    }

    /*
    shares if auth request was success or failure
    */

    public LiveData<LiveEvent<AuthStatus>> getAuthResponseNotifier() {
        return authResponseNotifier;
    }
    /*
    to be used when the signed in user changes
    this is distinct to auth response as that only shares status of request
    this shares result of request ie, current user or null
    */

    public LiveData<LiveEvent<OrganizationModel>> getSignedInUserNotifier() {
        return signedInUserNotifier;
    }


    public static final class MutableUserOrganizationModel extends MutableOrganizationModel {

        private Uri profilePicturePath;

        public Uri getProfilePicturePath() {
            return profilePicturePath;
        }

        public void setProfilePicturePath(Uri profilePicturePath) {
            this.profilePicturePath = profilePicturePath;
        }
    }
}
