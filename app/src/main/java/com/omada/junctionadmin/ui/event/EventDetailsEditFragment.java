package com.omada.junctionadmin.ui.event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.CircularArray;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.databinding.EventDetailsEditFragmentLayoutBinding;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

public class EventDetailsEditFragment extends Fragment {

    private EventModel eventModel;
    private EventDetailsEditFragmentLayoutBinding binding;

    private UserProfileViewModel userProfileViewModel;
    private UserProfileViewModel.EventUpdater eventUpdater;

    public static EventDetailsEditFragment newInstance(EventModel eventModel) {

        Bundle args = new Bundle();
        args.putParcelable("eventModel", eventModel);

        EventDetailsEditFragment fragment = new EventDetailsEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() == null) return;
            eventModel = getArguments().getParcelable("eventModel");
        } else {
            eventModel = savedInstanceState.getParcelable("eventModel");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.event_details_edit_fragment_layout, container, false);


        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        userProfileViewModel = viewModelProvider.get(UserProfileViewModel.class);
        FeedContentViewModel feedContentViewModel = viewModelProvider.get(FeedContentViewModel.class);

        binding.setFeedContentViewModel(feedContentViewModel);
        binding.setUserProfileViewModel(userProfileViewModel);
        binding.setEventDetails(eventModel);

        eventUpdater = UserProfileViewModel.getNewEventUpdaterInstance(eventModel);
        binding.setEventUpdater(eventUpdater);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfileViewModel.getDataValidationAction()
                .observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent -> {
                    if (dataValidationInformationLiveEvent == null) {
                        return;
                    }
                    DataValidator.DataValidationInformation dataValidationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
                    if (dataValidationInformation == null) {
                        return;
                    }
                    switch (dataValidationInformation.getValidationPoint()) {
                        case VALIDATION_POINT_EVENT_DESCRIPTION:
                            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW) {
                                binding.eventDescriptionLayout.setError("Maximum character limit is " + DataValidator.EVENT_DESCRIPTION_MAX_SIZE + " characters");
                            }
                            break;
                        case VALIDATION_POINT_ALL:
                            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.updateButton.setEnabled(false);
                            }
                            break;
                        default:
                            break;
                    }

                });

        binding.updateButton.setOnClickListener(v -> {
            userProfileViewModel.updateEvent(eventUpdater).observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                binding.updateButton.setEnabled(true);
                if (booleanLiveEvent == null) {
                    return;
                }
                Boolean result = booleanLiveEvent.getDataOnceAndReset();
                if (result == null) {
                    return;
                }
                if (result) {
                    Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Update failed please try again", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.deleteButton.setOnClickListener(v -> {
            if (!v.isEnabled()) {
                return;
            }
            v.setEnabled(false);
            buildDeleteConfirmationDialog();
        });
    }

    private void buildDeleteConfirmationDialog() {

        AlertDialog progressDialog = new MaterialAlertDialogBuilder(requireContext())
                .setCancelable(false)
                .setView(R.layout.deleting_alert_layout)
                .create();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm deletion")
                .setMessage("This action cannot be undone. Are you sure you want to delete?")
                .setPositiveButton("Yes, delete", (dialog, which) -> {
                    if (which != dialog.BUTTON_POSITIVE) return;

                    binding.deleteButton.setText("Deleting");
                    LiveData<Boolean> resultLiveData = userProfileViewModel.deletePost(eventModel);

                    progressDialog.show();

                    resultLiveData
                            .observe(getViewLifecycleOwner(), deleted -> {
                                if (deleted != null) {
                                    progressDialog.dismiss();
                                    if (deleted) {
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    } else {
                                        Log.e("Event", "Error deleting post");
                                        binding.deleteButton.setEnabled(true);
                                        binding.deleteButton.setText("Delete");
                                    }
                                }
                            });

                    /*
                     This is observed from activity scope so that the transformation in the viewModel
                     carries out and deletes the post from loaded highlights in case fragment is exited
                    */
                    resultLiveData.observe(requireActivity(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean result) {
                            resultLiveData.removeObserver(this);
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    if (which != dialog.BUTTON_NEGATIVE) return;
                    binding.deleteButton.setEnabled(true);
                    binding.deleteButton.setText("Delete");
                })
                .setOnCancelListener(dialog -> {
                    binding.deleteButton.setEnabled(true);
                    binding.deleteButton.setText("Delete");
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("eventModel", eventModel);
    }
}
