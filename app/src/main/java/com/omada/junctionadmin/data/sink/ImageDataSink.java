package com.omada.junctionadmin.data.sink;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

public interface ImageDataSink {

    Task<String> uploadPostImage(Uri path, String postId, String uid);

    Task<String> uploadInstituteImage(Uri path, String instituteId);

    Task<Uri> uploadProfilePicture(Uri path, String uid);
}