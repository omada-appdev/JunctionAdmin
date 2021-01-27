package com.omada.junctionadmin.data.handler;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omada.junctionadmin.data.BaseDataHandler;

public class ImageUploadHandler extends BaseDataHandler {

    public void uploadImage(String uri) {

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
