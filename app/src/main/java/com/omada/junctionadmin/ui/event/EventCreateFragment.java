package com.omada.junctionadmin.ui.event;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.material.textfield.TextInputEditText;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.databinding.EventCreateFragmentLayoutBinding;
import com.omada.junctionadmin.utils.FileUtilities;
import com.omada.junctionadmin.utils.image.GlideApp;
import com.omada.junctionadmin.utils.ImageUtilities;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.CreatePostViewModel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventCreateFragment extends Fragment {

    private CreatePostViewModel createPostViewModel;
    private EventCreateFragmentLayoutBinding binding;

    private final AtomicBoolean compressingImage = new AtomicBoolean(false);
    private final AtomicBoolean filePickerOpened = new AtomicBoolean(false);
    private static final int REQUEST_CODE_IMAGE_CHOOSER = 2;

    private final ActivityResultLauncher<String> storagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startFilePicker();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        createPostViewModel = new ViewModelProvider(requireActivity()).get(CreatePostViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.event_create_fragment_layout, container, false);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(createPostViewModel);
        binding.setEventCreator(createPostViewModel.getEventCreator());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.getViewModel()
                .getDataValidationAction()
                .observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent -> {
                    if(dataValidationInformationLiveEvent == null) {
                        return;
                    }
                    DataValidator.DataValidationInformation dataValidationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
                    if(dataValidationInformation != null) {
                        DataValidator.DataValidationResult validationResult = dataValidationInformation.getDataValidationResult();
                        switch (dataValidationInformation.getValidationPoint()) {
                            case VALIDATION_POINT_EVENT_DESCRIPTION:
                                if(validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_BLANK_VALUE) {
                                    binding.eventDescriptionLayout.setError("Please provide a description");
                                } else if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW) {
                                    binding.eventDescriptionLayout.setError("Character limit is " + DataValidator.EVENT_DESCRIPTION_MAX_SIZE);
                                }
                                break;
                            case VALIDATION_POINT_EVENT_TITLE:
                                if(validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_BLANK_VALUE) {
                                    binding.titleLayout.setError("Please enter a title");
                                } else if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW) {
                                    binding.titleLayout.setError("Character limit is " + DataValidator.EVENT_TITLE_MAX_SIZE + " characters");
                                } else if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_UNDERFLOW) {
                                    binding.titleLayout.setError("Title needs at least " + DataValidator.EVENT_TITLE_MIN_SIZE + " characters");
                                }
                                break;
                            case VALIDATION_POINT_IMAGE:
                                if(validationResult != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                    binding.eventPosterImage.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_error, requireActivity().getTheme())));
                                    binding.eventPosterImage.setStrokeWidth(2);
                                }
                                break;
                            case VALIDATION_POINT_ALL:
                                if (validationResult != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                    binding.postButton.setEnabled(true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });

        binding.postButton.setOnClickListener(v -> {
            binding.postButton.setEnabled(false);
            createPostViewModel.createEvent().observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                if(booleanLiveEvent == null) {
                    return;
                }
                Boolean result = booleanLiveEvent.getDataOnceAndReset();
                if(result == null) {
                    return;
                }
                if(result){
                    Toast.makeText(requireContext(), "Uploaded post successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Could not upload. Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.eventPosterImage.setOnClickListener(v -> {

            if(filePickerOpened.get()) {
                return;
            }
            if(compressingImage.get()) {
                Toast.makeText(requireContext(), "Please wait", Toast.LENGTH_SHORT).show();
                return;
            }

            ((ShapeableImageView)v).setStrokeColor(null);
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                startFilePicker();
            }
            else {
                storagePermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

        });

        binding.titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.titleLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.eventDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.eventDescriptionLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void startFilePicker() {
        filePickerOpened.set(true);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE_CHOOSER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(createPostViewModel.getEventCreator().getImagePath() != null && !filePickerOpened.get()) {

            Log.e("Create", "Image path is non null. Preparing to set image");

            binding.eventPosterImage.setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
            binding.eventPosterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Uri uri = createPostViewModel.getEventCreator().getImagePath();
            GlideApp
                    .with(this)
                    .load(uri)
                    .into(binding.eventPosterImage);

        } else {
            filePickerOpened.set(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(data == null) {
            return;
        }

        if( requestCode == REQUEST_CODE_IMAGE_CHOOSER) {

            Log.e("CreateEvent", "Entered onActivityResult");

            filePickerOpened.set(true);
            compressingImage.set(true);
            Uri selectedImage = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("CreateEvent", "Compressing image... " + bitmap.getWidth() + "  " + bitmap.getHeight());
            ImageUtilities
                    .scaleToPostImageGetBitmap(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), bitmapLiveEvent -> {
                        if(bitmapLiveEvent != null){
                            Bitmap picture = bitmapLiveEvent.getDataOnceAndReset();
                            if(picture != null) {
                                binding.eventPosterImage.setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
                                binding.eventPosterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                binding.eventPosterImage.setImageBitmap(picture);
                            }
                            else {
                                // Handle null case
                            }
                        }
                        else{
                        }
                    });

            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageUtilities
                    .scaleToPostImageGetFile(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), fileLiveEvent -> {
                        compressingImage.set(false);
                        if(fileLiveEvent != null){
                            File file = fileLiveEvent.getDataOnceAndReset();
                            if(file != null) {
                                binding.getEventCreator().setImagePath(Uri.fromFile(file));
                            }
                            else {
                                // Handle null case
                            }
                        }
                        else{
                            Log.e("Profile", "Error: fileLiveEvent was null");
                        }
                    });

        }
    }
}
