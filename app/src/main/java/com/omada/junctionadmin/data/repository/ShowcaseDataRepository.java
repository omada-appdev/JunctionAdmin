package com.omada.junctionadmin.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.repositorytemp.DataRepositoryAccessIdentifier;
import com.omada.junctionadmin.data.models.converter.ShowcaseModelConverter;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.data.models.internal.remote.ShowcaseModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseDataRepository extends BaseDataHandler {

    private final ShowcaseModelConverter showcaseModelConverter = new ShowcaseModelConverter();

    public LiveData<LiveEvent<List<ShowcaseModel>>> getOrganizationShowcases(
            DataRepositoryAccessIdentifier identifier, String organizationID) {

        MutableLiveData<LiveEvent<List<ShowcaseModel>>> showcaseModelLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("showcases")
                .whereEqualTo("creator", organizationID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<ShowcaseModel> showcaseModels = new ArrayList<>();
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        ShowcaseModelRemoteDB modelRemoteDB = documentSnapshot.toObject(ShowcaseModelRemoteDB.class);
                        if(modelRemoteDB != null){
                            modelRemoteDB.setId(documentSnapshot.getId());
                            showcaseModels.add(showcaseModelConverter.convertRemoteDBToExternalModel(modelRemoteDB));
                        }
                    }
                    showcaseModelLiveData.setValue(new LiveEvent<>(showcaseModels));

                })
                .addOnFailureListener(e -> {
                    Log.e("Showcases", "Error retrieving organization showcases");
                    showcaseModelLiveData.setValue(null);
                });

        return showcaseModelLiveData;
    }

    public LiveData<LiveEvent<Boolean>> updateShowcase(ShowcaseModel showcaseModel) {

        MutableLiveData<LiveEvent<Boolean>> resultLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("showcases")
                .document(showcaseModel.getId())
                .set(showcaseModelConverter.convertExternalToRemoteDBModel(showcaseModel), SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    Log.e("Showcases", "Failed updating showcase data");
                    e.printStackTrace();
                    resultLiveData.setValue(new LiveEvent<>(false));
                });

        return resultLiveData;
    }
}
