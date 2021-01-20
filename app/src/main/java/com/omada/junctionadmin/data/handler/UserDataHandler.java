package com.omada.junctionadmin.data.handler;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.models.converter.OrganizationModelConverter;
import com.omada.junctionadmin.data.models.external.BaseModel;
import com.omada.junctionadmin.data.models.external.InterestModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.internal.remote.OrganizationModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableOrganizationModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    public UserDataHandler(){

        /*
        this is only to check for sign outs and token expiration (if needed)
         */
        FirebaseAuth.getInstance()
                .addAuthStateListener(firebaseAuth -> {

                    if(firebaseAuth.getCurrentUser() == null){
                        signedInUser = null;
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.USER_SIGNED_OUT));
                    }

                });
    }

    public void createNewUserWithEmailAndPassword(String email, String password, MutableOrganizationModel details){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        /*
        signing out so that all tasks related to sign out can be handled by the auth state listeners
        TODO register auth state listeners in a suitable place
        */
        auth.signOut();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("AUTH", "create new user successful");
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.SIGNUP_SUCCESS));
                        addCreatedUserDetails(organizationModelConverter.convertExternalToRemoteDBModel(details));
                    }
                    else{
                        Log.d("AUTH", "create new user unsuccessful");
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.SIGNUP_FAILURE));
                    }
                });

    }

    //to be called in createNewUser when it is successful and not anywhere else
    private void addCreatedUserDetails(OrganizationModelRemoteDB details){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            /*
            do not use the local cached variable named signed in user because it might not yet be updated by the
            auth state listener and do not update it from here because the auth state listener will take care of it
            */
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .set(details)
                    .addOnSuccessListener(task -> {
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.ADD_EXTRA_DETAILS_SUCCESS));
                    })
                    .addOnFailureListener(task-> authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.ADD_EXTRA_DETAILS_FAILURE)));
        }
    }

    /*
    use it through authResponse LiveData by attaching a transformation or an observer and use the
    sign in data in auth state listener
    */
    public void authenticateUser(final String email, final String password){

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.AUTHENTICATION_SUCCESS));
                        getAuthenticatedUserDetails();
                    } else {
                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.AUTHENTICATION_FAILURE));
                    }
                });
    }

    /*
    This method is for getting details of newly authenticated user
     */
    private void getAuthenticatedUserDetails(){

        signedInUser = null;

        FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();

        if(newUser == null){
            signedInUserNotifier.setValue(new LiveEvent<>(null));
        }
        else{

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(newUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        signedInUser = new MutableOrganizationModel(
                                organizationModelConverter.convertRemoteDBToExternalModel(
                                        documentSnapshot.toObject(OrganizationModelRemoteDB.class)
                                )
                        );

                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_SUCCESS));
                        signedInUserNotifier.setValue(new LiveEvent<>(signedInUser));


                    })
                    .addOnFailureListener(e -> authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_FAILURE)));
        }
    }

    public OrganizationModel getCurrentUserModel(){
        return signedInUser;
    }

    /*
    this method triggers a get user through firebase auth that changes the value in signed in user
    notifier live data through the auth state listener callback
     */
    public void getCurrentUserDetails(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && !user.getUid().equals("")) {
            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_SUCCESS));
            getUserDetailsFromRemote(user.getUid());
        }
        else {
            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_FAILURE));
        }
    }

    /*
    This method is called after login and getting details is done. ie, when firebase already has a current user.
    It sets details into the signed in user notifier live data
     */
    private void getUserDetailsFromRemote(String uid){

        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals("")){
            //TODO get data from local and then remote if that fails

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        OrganizationModelRemoteDB modelRemoteDB = documentSnapshot.toObject(OrganizationModelRemoteDB.class);
                        if(modelRemoteDB != null) {
                            modelRemoteDB.setId(documentSnapshot.getId());
                        }
                        signedInUser = new MutableOrganizationModel(
                                organizationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB)
                        );

                        authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_LOGIN_SUCCESS));
                        signedInUserNotifier.setValue(new LiveEvent<>(signedInUser));

                    })
                    .addOnFailureListener(e -> authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.LOGIN_FAILURE)));

        }
        else{
            authResponseNotifier.setValue(new LiveEvent<>(AuthStatus.CURRENT_USER_LOGIN_FAILURE));
        }

    }

    public void signOutCurrentUser(){
        FirebaseAuth.getInstance().signOut();
        signedInUser = null;
    }

    public void updateCurrentUserDetails(OrganizationModel updatedUserModel){

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(getCurrentUserModel().getId())
                .set(organizationModelConverter.convertExternalToRemoteDBModel(updatedUserModel))
                .addOnSuccessListener(aVoid -> {
                    Log.e("Update", "success");
                    signedInUser = new MutableOrganizationModel(updatedUserModel);
                })
                .addOnFailureListener(e -> Log.e("Update", e.getMessage()));

    }

    /*
    shares if auth request was success or failure
    */
    public LiveData<LiveEvent<AuthStatus>> getAuthResponseNotifier(){
        return authResponseNotifier;
    }

    /*
    to be used when the signed in user changes
    this is distinct to auth response as that only shares status of request
    this shares result of request ie, current user or null
    */
    public LiveData<LiveEvent<OrganizationModel>> getSignedInUserNotifier(){
        return signedInUserNotifier;
    }
}
