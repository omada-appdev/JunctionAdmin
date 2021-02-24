package com.omada.junctionadmin.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.omada.junctionadmin.data.BaseDataHandler;
import com.omada.junctionadmin.data.models.converter.NotificationModelConverter;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.internal.remote.NotificationModelRemoteDB;
import com.omada.junctionadmin.data.repositorytemp.MainDataRepository;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDataRepository extends BaseDataHandler {

    NotificationModelConverter notificationModelConverter = new NotificationModelConverter();

    public enum NotificationType {

        NOTIFICATION_TYPE_INSTITUTE_JOIN_REQUEST("instituteJoinRequest"),
        NOTIFICATION_TYPE_INSTITUTE_JOIN_RESPONSE("instituteJoinResponse"),
        NOTIFICATION_TYPE_FEEDBACK_RESPONSE("feedbackResponse");

        private static final Map<String, NotificationType> nameMapping = new HashMap<>();

        static {
            for (NotificationType type : EnumSet.allOf(NotificationType.class))
                nameMapping.put(type.toString(), type);
        }

        private final String notificationTypeString;

        NotificationType(String notificationTypeString) {
            this.notificationTypeString = notificationTypeString;
        }

        @NonNull
        @Override
        public String toString() {
            return notificationTypeString;
        }

        public static NotificationType fromName(String name) {
            return nameMapping.get(name);
        }
    }

    public LiveData<LiveEvent<List<NotificationModel>>> getPendingNotifications(String destinationId) {

        MutableLiveData<LiveEvent<List<NotificationModel>>> notifications = new MutableLiveData<>();
        getPendingNotificationsTask(destinationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<NotificationModel> notificationModelList = new ArrayList<>((int) snapshot.getChildrenCount());
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            NotificationModelRemoteDB modelRemoteDB = dataSnapshot.getValue(NotificationModelRemoteDB.class);
                            if (modelRemoteDB == null) {
                                continue;
                            }
                            modelRemoteDB.setId(dataSnapshot.getKey());
                            NotificationModel model = notificationModelConverter.convertRemoteDBToExternalModel(modelRemoteDB);
                            notificationModelList.add(model);
                        }
                        Log.e("Notifications", "Loaded " + notificationModelList.size() + " notifications for " + destinationId);
                        notifications.setValue(new LiveEvent<>(notificationModelList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        notifications.setValue(new LiveEvent<>(new ArrayList<>()));
                    }
                });

        return notifications;
    }

    Query getPendingNotificationsTask(String destinationId) {

        return FirebaseDatabase.getInstance()
                .getReference()
                .child("notifications")
                .child(destinationId)
                .orderByChild("status")
                .equalTo("pending");
    }

    public LiveData<LiveEvent<Boolean>> sendInstituteJoinRequestNotification(String sourceId, String destinationId, OrganizationModel details) {

        MutableLiveData<LiveEvent<Boolean>> result = new MutableLiveData<>();
        sendInstituteJoinRequestNotificationGetTask(sourceId, destinationId, details)
                .addOnSuccessListener(aVoid -> {
                    result.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    Log.e("Notifications", "Error sending join request notification");
                    e.getMessage();
                    result.setValue(new LiveEvent<>(false));
                });
        return result;
    }

    Task<Void> sendInstituteJoinRequestNotificationGetTask(String sourceId, String destinationId, OrganizationModel details) {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("notifications")
                .child(destinationId)
                .push();

        Map<String, Object> notificationPayload = new HashMap<>();
        notificationPayload.put("notificationType", NotificationType.NOTIFICATION_TYPE_INSTITUTE_JOIN_REQUEST.toString());
        notificationPayload.put("sourceType", "organization");
        notificationPayload.put("source", sourceId);
        notificationPayload.put("timeUpdated", Instant.now().getEpochSecond());
        notificationPayload.put("title", "");

        Map<String, Object> data = new HashMap<>();
        data.put("name", details.getName());
        data.put("email", details.getMail());
        data.put("profilePicture", details.getProfilePicture());
        notificationPayload.put("data", data);
        notificationPayload.put("status", "pending");

        return reference.setValue(notificationPayload);
    }

    public LiveData<LiveEvent<Boolean>> handleNotification(NotificationModel model, Object responsePayload) {
        MutableLiveData<LiveEvent<Boolean>> result = new MutableLiveData<>();

        handleNotificationGetTask(model, responsePayload)
                .addOnSuccessListener(aVoid1 -> {
                    result.setValue(new LiveEvent<>(true));
                })
                .addOnFailureListener(e -> {
                    result.setValue(new LiveEvent<>(false));
                });

        return result;
    }

    Task<Void> handleNotificationGetTask(NotificationModel model, Object responsePayload) {

        if (!validateNotification(model.getNotificationType())) {
            return Tasks.forException(new Exception("Illegal notification type: " + model.getNotificationType()));
        }
        NotificationType notificationType = NotificationType.fromName(model.getNotificationType());

        switch (notificationType) {
            case NOTIFICATION_TYPE_INSTITUTE_JOIN_REQUEST:

                if (responsePayload instanceof Boolean) {

                    String instituteId = MainDataRepository.getInstance()
                            .getUserDataRepository()
                            .getCurrentUserModel()
                            .getInstitute();

                    Task<Void> updateTask;

                    WriteBatch batch = FirebaseFirestore.getInstance().batch();

                    if ((Boolean) responsePayload) {
                        batch.update(
                                FirebaseFirestore.getInstance()
                                        .collection("institutes")
                                        .document(instituteId)
                                        .collection("private")
                                        .document("config"),
                                "organizations", FieldValue.arrayUnion(model.getSource())
                        );
                        batch.update(
                                FirebaseFirestore.getInstance()
                                        .collection("organizations")
                                        .document(model.getSource()),
                                "institute", instituteId,
                                "instituteVerified", true
                        );
                    }
                    else {
                        batch.update(
                                FirebaseFirestore.getInstance()
                                        .collection("organizations")
                                        .document(model.getSource()),
                                "institute", null,
                                "instituteVerified", false
                        );
                    }
                    updateTask = batch.commit();

                    return updateTask
                            .continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("Notification", "Error verifying organization");
                                    return Tasks.forException(new Exception("Could not complete updating batch"));
                                }
                                return sendInstituteJoinConfirmation(instituteId, model.getSource(), (Boolean) responsePayload);
                            })
                            .continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("Notification", "Error sending response to organization");
                                    return Tasks.forException(new Exception("Could not complete updating batch"));
                                }
                                return FirebaseDatabase
                                        .getInstance()
                                        .getReference()
                                        .child("notifications")
                                        .child(instituteId)
                                        .child(model.getId())
                                        .child("status")
                                        .setValue("handled");
                            });

                } else {
                    Log.e("Institute", "Invalid response type");
                    return Tasks.forException(new Exception("Invalid response type for: " + notificationType.name()));
                }
            case NOTIFICATION_TYPE_INSTITUTE_JOIN_RESPONSE:
            case NOTIFICATION_TYPE_FEEDBACK_RESPONSE:

                String userId = MainDataRepository.getInstance()
                        .getUserDataRepository()
                        .getCurrentUserModel()
                        .getId();

                return FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("notifications")
                        .child(userId)
                        .child(model.getId())
                        .child("status")
                        .setValue("handled");

            default:
                Log.e("Institute", "Invalid notification type");
                return Tasks.forException(new Exception("Invalid notification type"));
        }

    }

    public LiveData<LiveEvent<Boolean>> sendFeedbackNotification(String feedback, String feedbackType) {
        MutableLiveData<LiveEvent<Boolean>> result  = new MutableLiveData<>();

        String userId = MainDataRepository
                .getInstance()
                .getUserDataRepository()
                .getCurrentUserModel()
                .getId();

        Map<String, Object> feedbackPayload = new HashMap<>();

        feedbackPayload.put("feedback", feedback);
        feedbackPayload.put("feedbackType", feedbackType);
        feedbackPayload.put("timeUpdated", Instant.now().getEpochSecond());

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("feedback")
                .child(userId)
                .setValue(feedbackPayload)
                .addOnSuccessListener(aVoid -> {
                    result.setValue(new LiveEvent<>(true));
                    Log.e("Notification", "Successfully sent feedback");
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e("Notification", "Error sending feedback");
                    result.setValue(new LiveEvent<>(false));
                });

        return result;
    }

    private Task<Void> sendInstituteJoinConfirmation(String sourceId, String destinationId, boolean response) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("notifications")
                .child(destinationId)
                .push();

        Map<String, Object> notificationPayload = new HashMap<>();
        notificationPayload.put("notificationType", NotificationType.NOTIFICATION_TYPE_INSTITUTE_JOIN_RESPONSE.toString());
        notificationPayload.put("sourceType", "institute");
        notificationPayload.put("source", sourceId);
        notificationPayload.put("timeUpdated", Instant.now().getEpochSecond());
        notificationPayload.put("title", "");

        Map<String, Object> data = new HashMap<>();
        data.put("response", response ? "accepted" : "declined");
        notificationPayload.put("data", data);
        notificationPayload.put("status", "pending");

        return reference.setValue(notificationPayload);
    }

    private boolean validateNotification(String notificationString) {
        return NotificationType.fromName(notificationString) != null;
    }
}
