package com.omada.junctionadmin.data.handler;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.utils.FileUtilities;
import com.omada.junctionadmin.utils.StringUtilities;

public class ImageUploadHandler extends BaseDataHandler {

    public enum ImageUrlScheme {
        IMAGE_URL_GS,
        IMAGE_URL_HTTP
    }

    public void uploadImage(String uri) {

    }

    public UploadTask uploadPostImage(Uri path, String postId, String uid) {

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("organizationFiles")
                .child(uid)
                .child("posts")
                .child(postId);

        return reference.putFile(path);
    }

    public UploadTask uploadInstituteImage(Uri path, String instituteId) {

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("instituteFiles")
                .child(instituteId)
                .child("images")
                .child(StringUtilities.randomStringGenerator(9, true, true, null));

        return reference.putFile(path);
    }

    public Task<Uri> uploadProfilePictureWithTask(Uri path, String uid) {

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
