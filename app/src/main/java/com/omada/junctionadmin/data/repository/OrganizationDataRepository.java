package com.omada.junctionadmin.data.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.repositorytemp.DataRepositoryAccessIdentifier;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.data.models.converter.OrganizationModelConverter;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.internal.remote.OrganizationModelRemoteDB;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.util.ArrayList;
import java.util.List;

public class OrganizationDataRepository extends BaseDataHandler {

    private MutableLiveData<LiveEvent<List<OrganizationModel>>> loadedInstituteOrganizationsNotifier = new MutableLiveData<>();

    private final OrganizationModelConverter organizationModelConverter = new OrganizationModelConverter();

    public LiveData<LiveEvent<OrganizationModel>> getOrganizationDetails(String organizationID) {

        MutableLiveData<LiveEvent<OrganizationModel>> detailsLiveData = new MutableLiveData<>();

        FirebaseFirestore
                .getInstance()
                .collection("organizations")
                .document(organizationID)
                .get()
                .addOnSuccessListener(snapshot -> {

                    OrganizationModelRemoteDB modelRemoteDB = snapshot.toObject(OrganizationModelRemoteDB.class);

                    if(modelRemoteDB == null){
                        detailsLiveData.setValue(null);
                    }
                    else {
                        modelRemoteDB.setId(snapshot.getId());
                        detailsLiveData.setValue(new LiveEvent<>(
                                organizationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB))
                        );
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Organizations", "Error retrieving organization details");
                    detailsLiveData.setValue(null);
                });

        return detailsLiveData;
    }


    public void getInstituteOrganizations(DataRepositoryAccessIdentifier identifier) {

        String instituteId = MainDataRepository.getInstance()
                .getUserDataRepository()
                .getCurrentUserModel()
                .getInstitute();

        FirebaseFirestore
                .getInstance()
                .collection("organizations")
                .whereEqualTo("institute", instituteId)
                .whereEqualTo("instituteVerified", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<OrganizationModel> organizationModels = new ArrayList<>();

                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        OrganizationModelRemoteDB modelRemoteDB = documentSnapshot.toObject(OrganizationModelRemoteDB.class);

                        if(modelRemoteDB != null){
                            modelRemoteDB.setId(documentSnapshot.getId());
                            organizationModels.add(
                                    organizationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB)
                            );
                        }

                    }
                    loadedInstituteOrganizationsNotifier.setValue(new LiveEvent<>(organizationModels));
                })
                .addOnFailureListener(e -> {
                    Log.e("Organizations", "Error retrieving institute organizations");
                    loadedInstituteOrganizationsNotifier.setValue(null);
                });

    }

    public MutableLiveData<LiveEvent<List<OrganizationModel>>> getLoadedInstituteOrganizationsNotifier() {
        return loadedInstituteOrganizationsNotifier;
    }
}
