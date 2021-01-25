package com.omada.junctionadmin.data.handler;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omada.junctionadmin.data.BaseDataHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageUploadHandler extends BaseDataHandler {

    public String uploadProfilePicture(Uri path, String organizationId) {

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("organizationFiles")
                .child(organizationId)
                .child("profilePicture");

        reference.putFile(path)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.e("Image", "Successfully uploaded image");
                    }
                    Log.e("Image", "Image upload failed");
                });

        return reference.toString();
    }

    public void uploadImage(String uri) {

    }

}
