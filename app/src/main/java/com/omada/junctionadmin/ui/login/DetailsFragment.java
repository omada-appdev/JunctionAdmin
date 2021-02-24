package com.omada.junctionadmin.ui.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.databinding.LoginDetailsFragmentLayoutBinding;
import com.omada.junctionadmin.utils.ImageUtilities;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.LoginViewModel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DetailsFragment extends Fragment {

    // Some arbitrary identifier
    private static final int REQUEST_CODE_PROFILE_PICTURE_CHOOSER = 4;
    private final AtomicBoolean compressingImage = new AtomicBoolean(false);

    private final ActivityResultLauncher<String[]> storagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), stringBooleanMap -> {
                boolean res = true;
                for (boolean result : stringBooleanMap.values()) {
                    res = res & result;
                } if(res) {
                    startFilePicker();
                }
            });

    private LoginDetailsFragmentLayoutBinding binding;

    public static DetailsFragment newInstance() {

        Bundle args = new Bundle();

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.login_details_fragment_layout, container, false);

        LoginViewModel loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        binding.setViewModel(loginViewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.getViewModel()
                .getDataValidationAction()
                .observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent -> {

                    if (dataValidationInformationLiveEvent == null) {
                        return;
                    }

                    DataValidator.DataValidationInformation dataValidationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
                    if (dataValidationInformation == null) {
                        return;
                    }

                    switch (dataValidationInformation.getValidationPoint()) {
                        case VALIDATION_POINT_EMAIL:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.emailLayout.setError("Invalid email");
                            }
                            break;
                        case VALIDATION_POINT_PHONE:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.phoneLayout.setError("Invalid phone number");
                            }
                            break;
                        case VALIDATION_POINT_PASSWORD:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.passwordLayout.setError("Invalid password");
                            }
                            break;
                        case VALIDATION_POINT_NAME:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.nameLayout.setError("Invalid name");
                            }
                            break;
                        case VALIDATION_POINT_INSTITUTE_HANDLE:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.instituteLayout.setError("Invalid institute");
                            }
                            break;
                        case VALIDATION_POINT_PROFILE_PICTURE:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.profilePictureImage.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_error, requireActivity().getTheme())));
                                binding.profilePictureImage.setStrokeWidth(5);
                            }
                            break;
                        case VALIDATION_POINT_INTERESTS:
                            break;
                        case VALIDATION_POINT_ALL:
                            Log.e("Details", "Validated " + dataValidationInformation.getValidationPoint().name() + " " + dataValidationInformation.getDataValidationResult().name());
                            if (dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                createJoinRequestSentDialog()
                                        .setPositiveButton("Continue", (dialog, which) -> {
                                            Log.e("Login", "Sending join request and adding details");
                                            binding.getViewModel().validateDetailsInputAndCreateAccount();
                                        })
                                        .setNegativeButton("Cancel", (dialog, which) -> {
                                            Log.e("Login", "Cancelled sending join request and adding details");
                                            binding.doneButton.setEnabled(true);
                                        })
                                        .show();

                                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(binding.doneButton.getWindowToken(), 0);
                                binding.doneButton.setEnabled(false);
                            } else {
                                binding.doneButton.setEnabled(true);
                            }
                            break;
                    }

                });

        binding.getViewModel()
                .getAuthResultAction()
                .observe(getViewLifecycleOwner(), authStatusLiveEvent -> {

                    if (authStatusLiveEvent == null) {
                        return;
                    }

                    UserDataHandler.AuthStatus authStatus = authStatusLiveEvent.getDataOnceAndReset();
                    if (authStatus == null) {
                        return;
                    }
                    switch (authStatus) {
                        case SIGNUP_FAILURE:
                        case ADD_EXTRA_DETAILS_FAILURE:
                            Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_LONG).show();
                            binding.doneButton.setEnabled(true);
                            break;
                        default:
                            break;
                    }
                });

        binding.profilePictureImage.setOnClickListener(v -> {

            if (compressingImage.get()) {
                Toast.makeText(requireContext(), "Please wait", Toast.LENGTH_SHORT).show();
                return;
            }

            ((ShapeableImageView) v).setStrokeColor(null);
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                startFilePicker();
            } else {
                storagePermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            }

        });

        binding.doneButton.setOnClickListener(v -> {
            binding.doneButton.setEnabled(false);
            binding.getViewModel().validateDetailsInputGetResult();
        });

        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.emailLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.phoneLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.nameLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.instituteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.instituteLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void startFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PROFILE_PICTURE_CHOOSER);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_PROFILE_PICTURE_CHOOSER) {

            compressingImage.set(true);
            Uri selectedImage = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("Details", "Compressing image... " + bitmap.getWidth() + "  " + bitmap.getHeight());
            ImageUtilities
                    .scaleToProfilePictureGetBitmap(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), bitmapLiveEvent -> {
                        if (bitmapLiveEvent != null) {
                            Bitmap picture = bitmapLiveEvent.getDataOnceAndReset();
                            if (picture != null) {
                                binding.profilePictureImage.setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
                                binding.profilePictureImage.setImageBitmap(picture);
                            } else {
                                // Handle null case
                            }
                        } else {
                            Log.e("Details", "Error ");
                        }
                    });

            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ImageUtilities
                    .scaleToProfilePictureGetFile(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), fileLiveEvent -> {
                        compressingImage.set(false);
                        if (fileLiveEvent != null) {
                            File file = fileLiveEvent.getDataOnceAndReset();
                            if (file != null) {
                                binding.getViewModel().profilePicture.setValue(Uri.fromFile(file));
                            } else {
                                // Handle null case
                            }
                        } else {
                            Log.e("Details", "Error ");
                        }
                    });

        }
    }

    private AlertDialog.Builder createJoinRequestSentDialog() {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Join " + binding.getViewModel().institute.getValue())
                .setMessage("You can view the institute temporarily but cannot post to it until your join request is accepted by the administrator. Your institute cannot be changed later. Proceed?");
    }

}
