package com.omada.junctionadmin.ui.event;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.databinding.EventCreateFragmentLayoutBinding;
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
    private final AtomicBoolean formLinkDialogCreated = new AtomicBoolean(false);
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

        view.setClickable(false);

        binding.getViewModel()
                .getBookingValidationTrigger()
                .observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                    if (booleanLiveEvent == null) {
                        return;
                    }
                    Boolean result = booleanLiveEvent.getDataOnceAndReset();
                    if (result == null) {
                        return;
                    } else if (!result) {
                        binding.bookVenueButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_error, requireContext().getTheme()));
                        binding.bookVenueButton.setTextColor(getResources().getColor(R.color.white, requireContext().getTheme()));
                    } else {

                    }
                });

        binding.getViewModel()
                .getFormValidationTrigger()
                .observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                    if (booleanLiveEvent == null) {
                        return;
                    }
                    Boolean result = booleanLiveEvent.getDataOnceAndReset();
                    if (result == null) {
                        return;
                    } else if (!result) {
                        binding.createFormButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_error, requireContext().getTheme()));
                        binding.createFormButton.setTextColor(getResources().getColor(R.color.white, requireContext().getTheme()));
                    } else {

                    }
                });

        binding.getViewModel()
                .getDataValidationAction()
                .observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent -> {
                    if (dataValidationInformationLiveEvent == null) {
                        return;
                    }
                    DataValidator.DataValidationInformation dataValidationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
                    if (dataValidationInformation != null) {
                        DataValidator.DataValidationResult validationResult = dataValidationInformation.getDataValidationResult();
                        switch (dataValidationInformation.getValidationPoint()) {
                            case VALIDATION_POINT_EVENT_DESCRIPTION:
                                if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_BLANK_VALUE) {
                                    binding.eventDescriptionLayout.setError("Please provide a description");
                                } else if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW) {
                                    binding.eventDescriptionLayout.setError("Character limit is " + DataValidator.EVENT_DESCRIPTION_MAX_SIZE);
                                }
                                break;
                            case VALIDATION_POINT_EVENT_TITLE:
                                if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_BLANK_VALUE) {
                                    binding.titleLayout.setError("Please enter a title");
                                } else if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_OVERFLOW) {
                                    binding.titleLayout.setError("Character limit is " + DataValidator.EVENT_TITLE_MAX_SIZE + " characters");
                                } else if (validationResult == DataValidator.DataValidationResult.VALIDATION_RESULT_UNDERFLOW) {
                                    binding.titleLayout.setError("Title needs at least " + DataValidator.EVENT_TITLE_MIN_SIZE + " characters");
                                }
                                break;
                            case VALIDATION_POINT_IMAGE:
                                if (validationResult != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
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

        binding.createFormButton.setOnClickListener(v -> {
            if (formLinkDialogCreated.get()) {
                return;
            }
            formLinkDialogCreated.set(true);
            enableDisableNavigationActions(false);
            createFormLinkInputDialog();
        });

        binding.postButton.setOnClickListener(v -> {
            binding.postButton.setEnabled(false);
            androidx.appcompat.app.AlertDialog dialog = createPostProgressDialog();
            dialog.show();
            createPostViewModel.createEvent().observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                if (booleanLiveEvent == null) {
                    return;
                }
                Boolean result = booleanLiveEvent.getDataOnceAndReset();
                if (result == null) {
                    return;
                }
                if (result) {
                    Toast.makeText(requireContext(), "Uploaded post successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(requireContext(), "Could not upload. Please try again", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        });

        binding.eventPosterImage.setOnClickListener(v -> {
            if (filePickerOpened.get()) {
                return;
            }
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
                binding.titleLayout.setErrorEnabled(false);
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

    /*
        There is delay when showing alert dialog, opening file picker, etc. So, disable all actions until safe and then enable it back
     */
    private void enableDisableNavigationActions(boolean enabled) {
        binding.createFormButton.setClickable(enabled);
        binding.bookVenueButton.setClickable(enabled);
        binding.postButton.setClickable(enabled);
        binding.eventPosterImage.setClickable(enabled);
    }

    private void createFormLinkInputDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                .setCancelable(false)
                .setTitle("Enter a link for your form")
                .setMessage("Ensure this link is valid because it cannot be changed later")
                .setView(R.layout.alert_text_input_layout)
                .setPositiveButton("ok", (dialog, which) -> {
                })
                .setNeutralButton("verify", (dialog, which) -> {
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setOnDismissListener(dialog -> {
                    formLinkDialogCreated.set(false);
                    enableDisableNavigationActions(true);
                })
                .create();

        alertDialog.show();

        TextInputEditText editText = alertDialog.findViewById(R.id.alert_text_input);
        TextInputLayout inputLayout = alertDialog.findViewById(R.id.alert_text_layout);

        editText.setText(createPostViewModel.getEventCreator().getFormLink());
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        editText.setHint("Your link");

        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
            if (editText.getText() != null) {

                String url = editText.getText().toString();

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                } else if (url.startsWith("http://")) {
                    url = url.replaceFirst("http", "https");
                }

                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    editText.setError("invalid url scheme");
                }
            }
        });

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            Editable input = editText.getText();
            if (input != null) {
                String link = input.toString();
                createPostViewModel.addEventForm(link)
                        .observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                            if (booleanLiveEvent == null) {
                                return;
                            } else if (Boolean.TRUE.equals(booleanLiveEvent.getDataOnceAndReset())) {
                                binding.createFormButton.setBackgroundColor(getResources().getColor(R.color.white, requireContext().getTheme()));
                                binding.createFormButton.setTextColor(getResources().getColor(R.color.colorPrimary, requireContext().getTheme()));
                                alertDialog.dismiss();
                            } else {
                                inputLayout.setError("Invalid link");
                            }
                        });
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private androidx.appcompat.app.AlertDialog createPostProgressDialog() {
        return new MaterialAlertDialogBuilder(requireContext())
                .setCancelable(false)
                .setView(R.layout.posting_alert_layout)
                .create();
    }

    private void startFilePicker() {
        filePickerOpened.set(true);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE_CHOOSER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (createPostViewModel.getEventCreator().getImagePath() != null && !filePickerOpened.get()) {

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

        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_IMAGE_CHOOSER) {

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
                        if (bitmapLiveEvent != null) {
                            Bitmap picture = bitmapLiveEvent.getDataOnceAndReset();
                            if (picture != null) {
                                binding.eventPosterImage.setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
                                binding.eventPosterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                binding.eventPosterImage.setImageBitmap(picture);
                            } else {
                                // Handle null case
                            }
                        } else {
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
                        if (fileLiveEvent != null) {
                            File file = fileLiveEvent.getDataOnceAndReset();
                            if (file != null) {
                                binding.getEventCreator().setImagePath(Uri.fromFile(file));
                            } else {
                                // Handle null case
                            }
                        } else {
                            Log.e("Profile", "Error: fileLiveEvent was null");
                        }
                    });

        }
    }
}
