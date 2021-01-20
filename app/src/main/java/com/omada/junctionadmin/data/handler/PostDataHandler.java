package com.omada.junctionadmin.data.handler;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.Batch;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.converter.ArticleModelConverter;
import com.omada.junctionadmin.data.models.converter.EventModelConverter;
import com.omada.junctionadmin.data.models.converter.RegistrationModelConverter;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.RegistrationModel;
import com.omada.junctionadmin.data.models.internal.remote.ArticleModelRemoteDB;
import com.omada.junctionadmin.data.models.internal.remote.EventModelRemoteDB;
import com.omada.junctionadmin.data.models.internal.remote.RegistrationModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableBookingModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostDataHandler extends BaseDataHandler {

    private MutableLiveData<LiveEvent<List<PostModel>>> loadedInstituteHighlightsNotifier = new MutableLiveData<>();
    private MutableLiveData<LiveEvent<List<PostModel>>> loadedOrganizationHighlightsNotifier = new MutableLiveData<>();
    private MutableLiveData<LiveEvent<List<PostModel>>> loadedAllInstitutePostsNotifier = new MutableLiveData<>();
    private MutableLiveData<LiveEvent<List<PostModel>>> loadedAllOrganizationPostsNotifier = new MutableLiveData<>();


    public LiveData<LiveEvent<List<PostModel>>> getOrganizationHighlights(
            DataRepository.DataRepositoryAccessIdentifier accessIdentifier, String organizationID){

        MutableLiveData<LiveEvent<List<PostModel>>> loadedOrganizationHighlights = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("creator", organizationID)
                .whereEqualTo("organizationHighlight", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<PostModel> postModels = new ArrayList<>();

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                        PostModel postModel = convertSnapshotToPostModel(snapshot);
                        if(postModel != null){
                            postModels.add(postModel);
                        }
                    }

                    loadedOrganizationHighlights.setValue(new LiveEvent<>(postModels));

                })
                .addOnFailureListener(e -> {
                    Log.e("Posts", "Error retrieving organization highlights");
                    loadedOrganizationHighlights.setValue(null);
                });

        return loadedOrganizationHighlights;
    }

    public void getInstituteHighlights(
            DataRepository.DataRepositoryAccessIdentifier identifier){


        String instituteId = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();


        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("creatorCache.institute", instituteId)
                .whereEqualTo("instituteHighlight", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<PostModel> postModels = new ArrayList<>();

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                        PostModel postModel = convertSnapshotToPostModel(snapshot);
                        if(postModel != null){
                            postModels.add(postModel);
                        }
                    }

                    loadedInstituteHighlightsNotifier.setValue(new LiveEvent<>(postModels));

                })
                .addOnFailureListener(e -> {
                    Log.e("Posts", "Error retrieving organization highlights");
                    loadedAllOrganizationPostsNotifier.setValue(null);
                });


    }

    public void getAllInstitutePosts(
            DataRepository.DataRepositoryAccessIdentifier identifier){


        String instituteId = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();


        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("creatorCache.institute", instituteId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<PostModel> postModels = new ArrayList<>();

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                        PostModel postModel = convertSnapshotToPostModel(snapshot);
                        if(postModel != null){
                            postModels.add(postModel);
                        }
                    }

                    loadedAllInstitutePostsNotifier.setValue(new LiveEvent<>(postModels));

                })
                .addOnFailureListener(e -> {
                    Log.e("Posts", "Error retrieving all organization posts");
                    loadedAllOrganizationPostsNotifier.setValue(null);
                });

    }

    public void getAllOrganizationPosts(
            DataRepository.DataRepositoryAccessIdentifier identifier, String organizationID){

        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("creator", organizationID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<PostModel> postModels = new ArrayList<>();

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                        PostModel postModel = convertSnapshotToPostModel(snapshot);
                        if(postModel != null){
                            postModels.add(postModel);
                        }
                    }

                    loadedAllOrganizationPostsNotifier.setValue(new LiveEvent<>(postModels));

                })
                .addOnFailureListener(e -> {
                    Log.e("Posts", "Error retrieving organization highlights");
                    loadedAllOrganizationPostsNotifier.setValue(null);
                });

    }

    public LiveData<LiveEvent<List<PostModel>>> getShowcasePosts(
            DataRepository.DataRepositoryAccessIdentifier identifier, String showcaseId) {

        MutableLiveData<LiveEvent<List<PostModel>>> loadedShowcasePostsLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("showcase", showcaseId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<PostModel> postModels = new ArrayList<>();

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                        PostModel postModel = convertSnapshotToPostModel(snapshot);
                        if(postModel != null){
                            postModels.add(postModel);
                        }
                    }

                    loadedShowcasePostsLiveData.setValue(new LiveEvent<>(postModels));

                })
                .addOnFailureListener(e -> {
                    Log.e("Posts", "Failed to retrieve showcase posts");
                    loadedShowcasePostsLiveData.setValue(null);
                });

        return loadedShowcasePostsLiveData;
    }

    /*
    NOTE
    The change or the diff between old and new post data is to be calculated at the view model
    and given here. This does not break the M V V M pattern because the repository should not take care
    of this possibly complicated validation
     */
    public LiveData<LiveEvent<Boolean>> updatePost(String postId, Map<String, Object> changesInPostModel){

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postId)
                .update(changesInPostModel)
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    resultLiveData.setValue(new LiveEvent<>(false));
                    Log.e("Post", "Error creating post");
                });

        return resultLiveData;

    }

    public LiveData<LiveEvent<Boolean>> createPost(PostModel postModel){

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // Generate a new random Id to correlate booking and event
        String randomId = FirebaseFirestore.getInstance()
                .collection("posts").document().getId();


        Object data = null;
        Class<? extends PostModel> postClass = postModel.getClass();

        if (EventModel.class.equals(postClass)) {

            EventModel eventModel = (EventModel) postModel;
            data = eventModelConverter.convertExternalToRemoteDBModel(eventModel);

            eventModel.setId(randomId);

            // Creating a new booking in the write batch
            DataRepository
                    .getInstance()
                    .getVenueDataHandler()
                    .createNewBooking(MutableBookingModel.fromEventModel(eventModel), batch);
        }
        else if (ArticleModel.class.equals(postClass)) {
            data = articleModelConverter.convertExternalToRemoteDBModel((ArticleModel) postModel);
        }

        if(data == null){
            resultLiveData.setValue(new LiveEvent<>(false));
            return resultLiveData;
        }

        DocumentReference eventDocRef = FirebaseFirestore.getInstance()
                .collection("posts")
                .document(randomId);

        batch.set(eventDocRef, data);

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    resultLiveData.setValue(new LiveEvent<>(false));
                    Log.e("Post", "Error creating post");
                });

        return resultLiveData;

    }

    public LiveData<LiveEvent<Boolean>> deletePost(String postID){

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    resultLiveData.setValue(new LiveEvent<>(false));
                    Log.e("Post", "Error creating post");
                });

        return resultLiveData;

    }

    public LiveData<LiveEvent<List<RegistrationModel>>> getEventRegistrations(
            DataRepository.DataRepositoryAccessIdentifier identifier, String eventId){

        MutableLiveData<LiveEvent<List<RegistrationModel>>> registrationsLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .document(eventId)
                .collection("registrations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<RegistrationModel> registrationModels = new ArrayList<>();

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        RegistrationModel model = convertSnapshotToRegistrationModel(snapshot);
                        if(model != null) {
                            registrationModels.add(model);
                        }
                    }
                    registrationsLiveData.setValue(new LiveEvent<>(registrationModels));
                })
                .addOnFailureListener(e -> {
                    Log.e("Registrations", "Error retrieving registrations");
                    registrationsLiveData.setValue(null);
                });

        return registrationsLiveData;
    }

    public void updateOrganizationHighlights(String organizationId, List<String> _added, List<String> _removed) {
        // TODO
    }

    public LiveData<LiveEvent<Boolean>> updateOrganizationShowcase (String showcaseId, List<String> _added, List<String> _removed) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        WriteBatch batch = FirebaseFirestore
                .getInstance()
                .batch();

        List<String> added = new ArrayList<>(_added);
        List<String> removed = new ArrayList<>(_removed);

        for(String id : added){
            DocumentReference reference = FirebaseFirestore
                    .getInstance()
                    .collection("posts")
                    .document(id);
            batch.update(reference, "showcase", showcaseId);
        }
        for(String id : removed){
            DocumentReference reference = FirebaseFirestore
                    .getInstance()
                    .collection("posts")
                    .document(id);
            batch.update(reference, "showcase", FieldValue.delete());
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.e("Post", "Added items to showcase successfully");
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    Log.e("Post", "Error adding items to showcase");
                    resultLiveData.setValue(new LiveEvent<>(false));
                });

        return resultLiveData;

    }

    public MutableLiveData<LiveEvent<List<PostModel>>> getLoadedInstituteHighlightsNotifier() {
        return loadedInstituteHighlightsNotifier;
    }

    public MutableLiveData<LiveEvent<List<PostModel>>> getLoadedAllInstitutePostsNotifier() {
        return loadedAllInstitutePostsNotifier;
    }

    public MutableLiveData<LiveEvent<List<PostModel>>> getLoadedOrganizationHighlightsNotifier() {
        return loadedOrganizationHighlightsNotifier;
    }

    public MutableLiveData<LiveEvent<List<PostModel>>> getLoadedAllOrganizationPostsNotifier() {
        return loadedAllOrganizationPostsNotifier;
    }

    /*
    Helper Fields
    If required, will be moved into a separate singleton class for use across handlers
     */

    private static final EventModelConverter eventModelConverter = new EventModelConverter();
    private static final ArticleModelConverter articleModelConverter = new ArticleModelConverter();
    private static final RegistrationModelConverter registrationModelConverter = new RegistrationModelConverter();

    private static PostModel convertSnapshotToPostModel(DocumentSnapshot snapshot){

        String type = snapshot.getString("type");

        if(type != null) {
            switch (type) {
                case "event":
                    return convertSnapshotToEventModel(snapshot);
                case "article":
                    return convertSnapshotToArticleModel(snapshot);
                default:
                    return null;
            }
        }
        return null;
    }

    private static EventModel convertSnapshotToEventModel(DocumentSnapshot snapshot){
        EventModelRemoteDB modelRemoteDB = snapshot.toObject(EventModelRemoteDB.class);
        if (modelRemoteDB == null){
            return null;
        }
        modelRemoteDB.setId(snapshot.getId());
        return eventModelConverter.convertRemoteDBToExternalModel(modelRemoteDB);
    }

    private static ArticleModel convertSnapshotToArticleModel(DocumentSnapshot snapshot){
        ArticleModelRemoteDB modelRemoteDB = snapshot.toObject(ArticleModelRemoteDB.class);
        if (modelRemoteDB == null){
            return null;
        }
        modelRemoteDB.setId(snapshot.getId());
        return articleModelConverter.convertRemoteDBToExternalModel(modelRemoteDB);
    }

    public static RegistrationModel convertSnapshotToRegistrationModel(DocumentSnapshot snapshot){
        RegistrationModelRemoteDB modelRemoteDB = snapshot.toObject(RegistrationModelRemoteDB.class);
        if (modelRemoteDB == null){
            return null;
        }
        modelRemoteDB.setId(snapshot.getId());
        return registrationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB);
    }



}
