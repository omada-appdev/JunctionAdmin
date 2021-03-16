package com.omada.junctionadmin.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.databinding.UserProfileFragmentLayoutBinding;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private UserProfileFragmentLayoutBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        UserProfileFragmentLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_profile_fragment_layout, container, false);
        this.binding = binding;

        binding.setViewModel(
                new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class)
        );
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Profile", "onViewCreated");
        binding.getViewModel().getUserUpdateAction()
                .observe(getViewLifecycleOwner(), organizationModel -> {
                    if (organizationModel == null) {
                        return;
                    }
                    CustomBindings.loadImageUrl(binding.userProfileImage, organizationModel.getProfilePicture());
                });

        binding.toolbar.setNavigationOnClickListener(v -> {
            binding.drawerLayout.openDrawer(binding.navigationView, true);
        });

        binding.drawerLayout.closeDrawer(binding.navigationView);

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            binding.drawerLayout.closeDrawer(binding.navigationView);
            Handler handler = new Handler(Looper.getMainLooper());

            // delay to let the drawer close
            handler.postDelayed(() -> {
                if (itemId == R.id.members_button) {
                } else if (itemId == R.id.settings_button) {
                } else if (itemId == R.id.feedback_button) {
                    showFeedbackDialog();
                } else {
                }
            }, 300);
            return true;
        });

        binding.appbar.addOnOffsetChangedListener(this);
    }

    /*
    All this mental gymnastics with a flag variable is because there is an infinite loop
    somewhere and it is probably a bug in the Android SDK
    TODO file an issue if non existent
     */
    private boolean titleSetFlag = false;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (!titleSetFlag && -verticalOffset >= binding.profileDetails.getHeight()) {
            titleSetFlag = true;
            binding.toolbar.setTitle("Your Profile");
        } else if (titleSetFlag && -verticalOffset < binding.profileDetails.getHeight()) {
            titleSetFlag = false;
            binding.toolbar.setTitle("");
        }
    }

    private AlertDialog showFeedbackDialog() {

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Feedback")
                .setIcon(R.drawable.ic_favorite_red_24dp)
                .setMessage(R.string.feedback_message_text)
                .setView(R.layout.alert_text_input_layout)
                .setPositiveButton("Send", (dialog1, which) -> {
                })
                .setNegativeButton("Cancel", (dialog1, which) -> {
                })
                .create();

        dialog.show();

        TextInputLayout inputLayout = dialog.findViewById(R.id.alert_text_layout);
        inputLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        inputLayout.setErrorIconDrawable(null);

        TextInputEditText editText = dialog.findViewById(R.id.alert_text_input);
        editText.setGravity(Gravity.TOP);
        editText.setMinLines(5);
        editText.setHint("Say something");

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {

            binding.getViewModel().validateFeedback(editText.getText().toString())
                    .observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent -> {
                        if (dataValidationInformationLiveEvent == null) {
                            return;
                        }
                        DataValidator.DataValidationInformation validationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
                        if (validationInformation == null) {
                            return;
                        }
                        switch (validationInformation.getDataValidationResult()) {
                            case VALIDATION_RESULT_VALID:
                                binding.getViewModel().sendFeedback(editText.getText().toString(), "feedback")
                                        .observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                                            if (booleanLiveEvent == null) {
                                                return;
                                            }
                                            Boolean result = booleanLiveEvent.getDataOnceAndReset();
                                            if (Boolean.TRUE.equals(result)) {
                                                dialog.dismiss();
                                                Toast.makeText(requireContext(), R.string.send_feedback_success_message, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requireContext(), R.string.send_feedback_error_message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            case VALIDATION_RESULT_INVALID:
                                inputLayout.setError("Invalid input please retry");
                                break;
                            case VALIDATION_RESULT_UNDERFLOW:
                            case VALIDATION_RESULT_BLANK_VALUE:
                                inputLayout.setError("Enter at least " + DataValidator.FEEDBACK_MIN_SIZE + " characters");
                                break;
                            case VALIDATION_RESULT_OVERFLOW:
                                inputLayout.setError("Character limit is " + DataValidator.FEEDBACK_MAX_SIZE);
                            default:
                                break;
                        }
                    });
        });

        return dialog;
    }
}
