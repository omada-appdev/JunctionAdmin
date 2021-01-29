package com.omada.junctionadmin.data.handler;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.Batch;
import com.google.common.collect.ImmutableList;
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
import com.omada.junctionadmin.data.models.mutable.MutableArticleModel;
import com.omada.junctionadmin.data.models.mutable.MutableBookingModel;
import com.omada.junctionadmin.data.models.mutable.MutableEventModel;
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
    // TODO changesInPostModel must be made immutable

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

        // creating batch to write booking and upload event in same batch
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // Generate a new random Id to correlate booking and event
        String generatedId = FirebaseFirestore.getInstance()
                .collection("posts").document().getId();


        Uri imagePath = null;
        Object data = null;
        Class<? extends PostModel> postClass = postModel.getClass();

        if (EventCreatorModel.class.equals(postClass)) {

            EventCreatorModel eventModel = (EventCreatorModel) postModel;
            imagePath = eventModel.getImagePath();

            // id needed for creating booking
            eventModel.setId(generatedId);
            data = eventModelConverter.convertExternalToRemoteDBModel(eventModel);

            /*
             Creating a new booking in the write batch
             fixme not checking the success of database operation might have bad consequences
            */
            DataRepository
                    .getInstance()
                    .getVenueDataHandler()
                    .createNewBooking(MutableBookingModel.fromEventModel(eventModel), batch);
        }
        else if (ArticleCreatorModel.class.equals(postClass)) {
            imagePath = ((ArticleCreatorModel)postModel).getImagePath();
            data = articleModelConverter.convertExternalToRemoteDBModel((ArticleModel) postModel);
        }
        else {
            throw new UnsupportedOperationException(
                    "Attempt to upload a post object that does not extend the required classes. Please ensure your post model extends ArticleCreatorModel or EventCreatorModel"
            );
        }

        if(data == null){
            resultLiveData.setValue(new LiveEvent<>(false));
            return resultLiveData;
        }

        DocumentReference postDocRef = FirebaseFirestore.getInstance()
                .collection("posts")
                .document(generatedId);

        // for lambda
        Object finalData = data;
        DataRepository.getInstance()
                .getImageUploadHandler()
                .uploadPostImage(imagePath, generatedId, postModel.getCreator())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {

                        String path = task.getResult().getMetadata().getReference().toString();

                        setImagePath(finalData, path);
                        batch.set(postDocRef, finalData);
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    resultLiveData.setValue(new LiveEvent<>(true));
                                    Log.e("Post", "Create post success");

                                    DataRepository
                                            .getInstance()
                                            .getUserDataHandler()
                                            .incrementHeldEventsNumber();
                                })
                                .addOnFailureListener(e -> {
                                    resultLiveData.setValue(new LiveEvent<>(false));
                                    Log.e("Post", "Error creating post");
                                });
                    }
                    else {
                        resultLiveData.setValue(new LiveEvent<>(false));
                        Log.e("Post", "Error uploading post image");
                    }

                });

        return resultLiveData;

    }

    private void setImagePath(Object data, String image) {
        if(data instanceof EventModelRemoteDB) {
            ((EventModelRemoteDB)data).setImage(image);
        } else if (data instanceof ArticleModelRemoteDB) {
            ((ArticleModelRemoteDB)data).setImage(image);
        } else {
            throw new UnsupportedOperationException("Provided object does not meet requirements for being a post");
        }
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

    // Will fail if any id does not exist so make sure the ids exist through the viewModel
    public LiveData<LiveEvent<Boolean>> updateOrganizationHighlights(String organizationId, ImmutableList<String> added, ImmutableList<String> removed) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        WriteBatch batch = FirebaseFirestore
                .getInstance()
                .batch();

        for(String add : added) {
            DocumentReference documentReference = FirebaseFirestore
                    .getInstance()
                    .collection("posts")
                    .document(add);
            batch.update(documentReference, "organizationHighlight", true);
        }

        for(String remove : removed) {
            DocumentReference documentReference = FirebaseFirestore
                    .getInstance()
                    .collection("posts")
                    .document(remove);
            batch.update(documentReference, "organizationHighlight", false);
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    resultLiveData.setValue(new LiveEvent<>(false));
                    Log.e("Posts", "Failed to update organization highlights");
                });

        return resultLiveData;
    }

    public LiveData<LiveEvent<Boolean>> updateOrganizationShowcase (String showcaseId, ImmutableList<String> added, ImmutableList<String> removed) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        WriteBatch batch = FirebaseFirestore
                .getInstance()
                .batch();

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

    public static class EventCreatorModel extends MutableEventModel {

        private Uri imagePath;

        public Uri getImagePath() {
            return imagePath;
        }

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }
    }

    public static class ArticleCreatorModel extends MutableArticleModel {

        private Uri imagePath;

        public Uri getImagePath() {
            return imagePath;
        }

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }
    }
}
