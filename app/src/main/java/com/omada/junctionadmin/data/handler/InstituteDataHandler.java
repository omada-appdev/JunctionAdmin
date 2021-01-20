package com.omada.junctionadmin.data.handler;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.models.converter.InstituteModelConverter;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.internal.remote.InstituteModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.Map;

public class InstituteDataHandler extends BaseDataHandler {

    private final InstituteModelConverter instituteModelConverter = new InstituteModelConverter();

    public LiveData<LiveEvent<InstituteModel>> getInstituteDetails(String instituteID) {

        MutableLiveData<LiveEvent<InstituteModel>> instituteModelLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("institutes")
                .document(instituteID)
                .get()
                .addOnSuccessListener(snapshot -> {

                    InstituteModelRemoteDB modelRemoteDB = snapshot.toObject(InstituteModelRemoteDB.class);

                    if(modelRemoteDB == null) {
                        instituteModelLiveData.setValue(null);
                    }
                    else {
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

    public LiveData<LiveEvent<Boolean>> updateInstituteDetails(InstituteModel changedInstituteModel) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        String instituteId = DataRepository
                .getInstance()
                .getUserDataHandler()
                .getCurrentUserModel()
                .getInstitute();

        FirebaseFirestore
                .getInstance()
                .collection("institutes")
                .document(instituteId)
                .set(instituteModelConverter.convertExternalToRemoteDBModel(changedInstituteModel), SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    resultLiveData.setValue(new LiveEvent<>(false));
                    Log.e("Institute", "Error updating institute details");
                });

        return resultLiveData;
    }
}
