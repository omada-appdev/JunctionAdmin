package com.omada.junctionadmin.data.handler;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.UploadTask;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.models.converter.NotificationModelConverter;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.data.models.internal.remote.NotificationModelRemoteDB;
import com.omada.junctionadmin.data.repository.MainDataRepository;
import com.omada.junctionadmin.data.models.converter.InstituteModelConverter;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.internal.remote.InstituteModelRemoteDB;
import com.omada.junctionadmin.data.models.mutable.MutableInstituteModel;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class InstituteDataHandler extends BaseDataHandler {

    private final InstituteModelConverter instituteModelConverter = new InstituteModelConverter();
    private final NotificationModelConverter notificationModelConverter = new NotificationModelConverter();

    private static final Map<String, String> instituteHandleToIdCache = new HashMap<>();
    private static final Map<String, String> instituteIdToHandleCache = new HashMap<>();

    public LiveData<LiveEvent<InstituteModel>> getInstituteDetails(String instituteID) {

        if(instituteID == null) {
            return new MutableLiveData<>(new LiveEvent<>(new MutableInstituteModel()));
        }

        MutableLiveData<LiveEvent<InstituteModel>> instituteModelLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("institutes")
                .document(instituteID)
                .get()
                .addOnSuccessListener(snapshot -> {

                    InstituteModelRemoteDB modelRemoteDB = snapshot.toObject(InstituteModelRemoteDB.class);

                    if (modelRemoteDB == null) {
                        instituteModelLiveData.setValue(null);
                    } else {
                        addToCache(instituteID, modelRemoteDB.getHandle());
                        modelRemoteDB.setId(snapshot.getId());
                        instituteModelLiveData.setValue(new LiveEvent<>(
                                instituteModelConverter.convertRemoteDBToExternalModel(modelRemoteDB)
                        ));
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Institute", "Error retrieving institute details");
                    instituteModelLiveData.setValue(null);
                });

        return instituteModelLiveData;
    }

    public LiveData<LiveEvent<Boolean>> updateInstituteDetails(MutableUserInstituteModel changedInstituteModel) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        String instituteId = MainDataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        Map<String, Object> changes = new HashMap<>();
        changes.put("name", changedInstituteModel.getName());
        changes.put("handle", changedInstituteModel.getHandle());

        if (changedInstituteModel.getImagePath() != null) {
            UploadTask uploadTask = MainDataRepository.getInstance()
                    .getImageUploadHandler()
                    .uploadInstituteImage(changedInstituteModel.getImagePath(), instituteId);
            String imagePath = uploadTask.getSnapshot().getStorage().toString();
            changes.put("image", imagePath);
        }

        FirebaseFirestore
                .getInstance()
                .collection("institutes")
                .document(instituteId)
                .update(changes)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> updatedInstituteData = new HashMap<>();

                    updatedInstituteData.put("/instituteIds/" + instituteId, changedInstituteModel.getHandle());
                    updatedInstituteData.put("/instituteHandles/" + changedInstituteModel.getHandle(), instituteId);

                    if (!changedInstituteModel.getHandle().equals(getCachedInstituteHandle(instituteId))) {
                        updatedInstituteData.put("/instituteHandles/" + getCachedInstituteHandle(instituteId), null);
                    }
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .updateChildren(updatedInstituteData)
                            .addOnSuccessListener(aVoid1 -> {
                                addToCache(instituteId, changedInstituteModel.getHandle());
                                resultLiveData.setValue(new LiveEvent<>(true));
                            })
                            .addOnFailureListener(e -> {
                                resultLiveData.setValue(new LiveEvent<>(false));
                            });
                })
                .addOnFailureListener(e -> {
                    resultLiveData.setValue(new LiveEvent<>(false));
                    Log.e("Institute", "Database error updating institute details");
                });

        return resultLiveData;
    }

    public LiveData<LiveEvent<Boolean>> checkInstituteCodeValidity(String code) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        if (code != null) {

            String cachedId = getCachedInstituteId(code);
            if (cachedId != null && !cachedId.equals("notFound")) {
                return new MutableLiveData<>(new LiveEvent<>(true));
            }

            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("instituteHandles")
                    .child(code)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getValue() instanceof String) {
                                addToCache((String) snapshot.getValue(), snapshot.getKey());
                                resultLiveData.setValue(new LiveEvent<>(true));
                            } else {
                                resultLiveData.setValue(new LiveEvent<>(false));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            resultLiveData.setValue(new LiveEvent<>(false));
                            Log.e("Institute", "Error checking institute code validity");
                        }
                    });
        } else {
            resultLiveData.setValue(new LiveEvent<>(false));
        }
        return resultLiveData;
    }

    public LiveData<LiveEvent<String>> getInstituteId(String handle) {

        MutableLiveData<LiveEvent<String>> resultLiveData = new MutableLiveData<>();

        if (handle == null) {
            resultLiveData.setValue(new LiveEvent<>("notFound"));
        } else if (getCachedInstituteId(handle) != null) {
            resultLiveData.setValue(new LiveEvent<>(getCachedInstituteId(handle)));
        } else {
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("instituteHandles")
                    .child(handle)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getValue() instanceof String) {
                                addToCache((String) snapshot.getValue(), snapshot.getKey());
                                resultLiveData.setValue(new LiveEvent<>((String) snapshot.getValue()));
                            } else {
                                resultLiveData.setValue(new LiveEvent<>(null));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            resultLiveData.setValue(new LiveEvent<>("notFound"));
                            Log.e("Institute", "Error checking institute code validity");
                        }
                    });
        }

        return resultLiveData;
    }

    public LiveData<LiveEvent<String>> getInstituteHandle(String id) {

        MutableLiveData<LiveEvent<String>> resultLiveData = new MutableLiveData<>();

        if (id == null) {
            resultLiveData.setValue(new LiveEvent<>("notFound"));
        } else if (getCachedInstituteHandle(id) != null) {
            resultLiveData.setValue(new LiveEvent<>(getCachedInstituteHandle(id)));
        } else {
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("instituteIds")
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getValue() instanceof String) {
                                addToCache(snapshot.getKey(), (String) snapshot.getValue());
                                resultLiveData.setValue(new LiveEvent<>((String) snapshot.getValue()));
                            } else {
                                resultLiveData.setValue(new LiveEvent<>("notFound"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            resultLiveData.setValue(new LiveEvent<>("notFound"));
                            Log.e("Institute", "Error checking institute code validity");
                        }
                    });
        }
        return resultLiveData;
    }

    public static synchronized void addToCache(String id, String handle) {
        instituteHandleToIdCache.put(handle, id);
        instituteIdToHandleCache.put(id, handle);
    }

    public static synchronized String getCachedInstituteId(String handle) {
        return instituteHandleToIdCache.get(handle);
    }

    public static synchronized String getCachedInstituteHandle(String id) {
        return instituteIdToHandleCache.get(id);
    }

    public static final class MutableUserInstituteModel extends MutableInstituteModel {

        private Uri imagePath;
        private String oldHandle;

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }

        public Uri getImagePath() {
            return imagePath;
        }
    }
}
