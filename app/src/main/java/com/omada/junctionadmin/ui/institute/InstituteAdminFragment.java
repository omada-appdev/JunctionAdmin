package com.omada.junctionadmin.ui.institute;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.databinding.InstituteAdminLayoutBinding;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.ui.uicomponents.binders.notifications.InstituteJoinRequestNotificationItemBinder;
import com.omada.junctionadmin.ui.venue.InstituteVenuesFragment;
import com.omada.junctionadmin.utils.ImageUtilities;
import com.omada.junctionadmin.utils.image.GlideApp;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;
import mva3.adapter.util.Mode;

public class InstituteAdminFragment extends Fragment {

    private static final int REQUEST_CODE_PROFILE_PICTURE_CHOOSER = 3;
    private final AtomicBoolean compressingImage = new AtomicBoolean(false);


    private final ActivityResultLauncher<String> storagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startFilePicker();
                }
            });

    private MultiViewAdapter adapter;
    private ListSection<NotificationModel> notificationsListSection;

    private InstituteAdminLayoutBinding binding;
    private InstituteViewModel instituteViewModel;
    private AtomicBoolean filePickerOpened = new AtomicBoolean(false);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        InstituteAdminLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.institute_admin_layout, container, false);

        this.binding = binding;

        // ViewModel is tied to the fragment lifecycle
        instituteViewModel = new ViewModelProvider(requireActivity()).get(InstituteViewModel.class);
        binding.setViewModel(instituteViewModel);

        if (savedInstanceState == null
                || instituteViewModel.getLoadedInstituteNotifications().getValue() == null
                || instituteViewModel.getLoadedInstituteNotifications().getValue().size() == 0) {
            instituteViewModel.loadInstituteNotifications();
        }

        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        adapter = new MultiViewAdapter();
        notificationsListSection = new ListSection<>();

        notificationsListSection.setSelectionMode(Mode.SINGLE);

        adapter.registerItemBinders(
                new InstituteJoinRequestNotificationItemBinder(instituteViewModel, getViewLifecycleOwner())
        );

        notificationsListSection.setOnSelectionChangedListener((item, isSelected, selectedItems) -> {
            int index = notificationsListSection.getData().indexOf(item);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                notificationsListSection.remove(index);
            }, 200);
            notificationsListSection.clearSelections();
        });

        instituteViewModel
                .getLoadedInstituteNotifications()
                .observe(getViewLifecycleOwner(), notificationModels -> {
                    if (notificationModels == null) {
                        return;
                    }
                    notificationsListSection.addAll(notificationModels.subList(notificationsListSection.size(), notificationModels.size()));
                });

        instituteViewModel.getInstituteDetails().observe(getViewLifecycleOwner(), instituteModelLiveEvent -> {
            if (instituteModelLiveEvent == null) {
                return;
            }
            InstituteModel model = instituteModelLiveEvent.getDataOnceAndReset();
            if (model != null) {
                Log.e("Institute", "Non null model");
                Log.e("Institute", "Image path: " + model.getImage());
                if (instituteViewModel.getInstituteUpdater().getImagePath() == null && model.getImage() != null) {
                    CustomBindings.loadImageGs(binding.profilePictureImage, model.getImage());
                }
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);

        adapter.addSection(notificationsListSection);

        binding.doneButton.setOnClickListener(v -> {
            if (!v.isEnabled()) {
                return;
            }
            v.setEnabled(false);
            instituteViewModel
                    .detailsEntryDone()
                    .observe(getViewLifecycleOwner(), booleanLiveEvent -> {
                        if (booleanLiveEvent == null) {
                            return;
                        }
                        Boolean result = booleanLiveEvent.getDataOnceAndReset();
                        if (result != null) {
                            if (result) {
                                Toast.makeText(requireActivity(), "Updated details successfully", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(requireActivity(), "Error updating details", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        if (instituteViewModel.getUserModel().isInstituteAdmin()) {
            binding.editVenuesButton.setOnClickListener(v -> {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.institute_content_placeholder, InstituteVenuesFragment.newInstance())
                        .addToBackStack("venues")
                        .commit();
            });
        }

        binding.profilePictureImage.setOnClickListener(v -> {
            if (compressingImage.get()) {
                Toast.makeText(requireContext(), "Please Wait", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!v.isEnabled()) {
                return;
            }
            v.setEnabled(false);
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startFilePicker();
            } else {
                storagePermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                );
            }
        });

        binding.nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.nameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.handleInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.handleLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        instituteViewModel
                .getDataValidationAction()
                .observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent -> {
                    if (dataValidationInformationLiveEvent == null) {
                        return;
                    }

                    DataValidator.DataValidationInformation
                            dataValidationInformation =
                            dataValidationInformationLiveEvent.
                                    getDataOnceAndReset();
                    if (dataValidationInformation != null) {
                        switch (dataValidationInformation.getValidationPoint()) {
                            case VALIDATION_POINT_NAME:
                                if (dataValidationInformation.getDataValidationResult() !=
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                    binding.nameLayout.setError("Invalid name");
                                }
                                break;
                            case VALIDATION_POINT_INSTITUTE_HANDLE:
                                if (dataValidationInformation.getDataValidationResult() !=
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                    binding.handleLayout.setError("Invalid institute");
                                }
                                break;
                            case VALIDATION_POINT_ALL:
                                if (dataValidationInformation.getDataValidationResult() ==
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                    binding.doneButton.setEnabled(false);
                                } else {
                                    binding.doneButton.setEnabled(true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void startFilePicker() {
        filePickerOpened.set(true);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PROFILE_PICTURE_CHOOSER);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        binding.profilePictureImage.setEnabled(true);
        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_PROFILE_PICTURE_CHOOSER) {

            filePickerOpened.set(true);
            compressingImage.set(true);

            Uri selectedImage = data.getData();

            if (selectedImage == null) {
                return;
            }

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.e("Details", "Compressing image...." + bitmap.getWidth() + " " + bitmap.getHeight());
            ImageUtilities
                    .scaleToInstituteImageGetBitmap(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), bitmapLiveEvent -> {
                        if (bitmapLiveEvent != null) {
                            Bitmap picture = bitmapLiveEvent.getDataOnceAndReset();
                            if (picture != null) {
                                binding.profilePictureImage
                                        .setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
                                binding.profilePictureImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                binding.profilePictureImage.setImageBitmap(picture);
                            } else {

                            }
                        } else {
                            Log.e("Institute", "Error  ");
                        }

                    });

            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
            } catch (IOException io) {
                io.printStackTrace();
            }

            ImageUtilities
                    .scaleToInstituteImageGetFile(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), fileLiveEvent -> {
                        compressingImage.set(false);
                        if (fileLiveEvent != null) {
                            File file = fileLiveEvent.getDataOnceAndReset();
                            if (file != null) {
                                instituteViewModel.getInstituteUpdater()
                                        .setImagePath(Uri.fromFile(file));
                            } else {
                            }
                        } else {
                            Log.e("Institute", "Error : the provided fileLiveEvent was null");
                        }
                    });
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if (instituteViewModel.getInstituteUpdater().getImagePath() != null && !filePickerOpened.get()) {
            Log.e("Create", "Image path is non null. Preparing to set image");
            binding.profilePictureImage.setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
            binding.profilePictureImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Uri uri = instituteViewModel.getInstituteUpdater().getImagePath();
            GlideApp
                    .with(this)
                    .load(uri)
                    .into(binding.profilePictureImage);

        } else if (instituteViewModel.getInstituteUpdater().getImage() != null && !filePickerOpened.get()) {
            binding.profilePictureImage.setColorFilter(getResources().getColor(R.color.transparent, requireActivity().getTheme()));
            binding.profilePictureImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            CustomBindings.loadImageGs(binding.profilePictureImage, instituteViewModel.getInstituteUpdater().getImage());
        } else {
            filePickerOpened.set(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instituteViewModel.exitAdminConsole();
    }
}
