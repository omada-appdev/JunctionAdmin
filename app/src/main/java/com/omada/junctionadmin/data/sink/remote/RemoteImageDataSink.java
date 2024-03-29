package com.omada.junctionadmin.data.sink.remote;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.sink.ImageDataSink;
import com.omada.junctionadmin.utils.StringUtilities;

public class RemoteImageDataSink extends BaseDataHandler implements ImageDataSink {

    public enum ImageUrlScheme {
        IMAGE_URL_GS,
        IMAGE_URL_HTTP
    }

    public void uploadImage(String uri) {

    }

    public Task<String> uploadPostImage(Uri path, String postId, String uid) {

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("organizationFiles")
                .child(uid)
                .child("posts")
                .child(postId);

        return reference.putFile(path)
                .continueWithTask(task -> {
                    if(task.isSuccessful()) {
                        return Tasks.forResult(task.getResult().getMetadata().getReference().toString());
                    } return Tasks.forException(task.getException());
                });
    }

    public Task<String> uploadInstituteImage(Uri path, String instituteId) {

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("instituteFiles")
                .child(instituteId)
                .child("images")
                .child(StringUtilities.randomStringGenerator(9, true, true, null));

        return reference
                .putFile(path)
                .continueWithTask(task -> {
                    if(task.isSuccessful()) {
                        return Tasks.forResult(task.getResult().getMetadata().getReference().toString());
                    } return Tasks.forException(task.getException());
                });
    }

    public Task<Uri> uploadProfilePicture(Uri path, String uid) {

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("organizationFiles")
                .child(uid)
                .child("profilePicture");

        return reference.putFile(path).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return reference.getDownloadUrl();
        });
    }
}
