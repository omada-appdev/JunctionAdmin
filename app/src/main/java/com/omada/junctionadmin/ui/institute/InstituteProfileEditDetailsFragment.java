package com.omada.junctionadmin.ui.institute;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.databinding.InstituteProfileEditDetailsBinding;
import com.omada.junctionadmin.utils.ImageUtilities;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class InstituteProfileEditDetailsFragment extends Fragment {


    private static final int REQUEST_CODE_PROFILE_PICTURE_CHOOSER=3;
    private final AtomicBoolean compressingImage =new AtomicBoolean(false);


    private final ActivityResultLauncher<String> storagePermissionLauncher=
        registerForActivityResult(new
                ActivityResultContracts.RequestPermission(), isGranted ->
        {
            if(isGranted){
                startFilePicker();
            }
        });

    private InstituteProfileEditDetailsBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InstituteProfileEditDetailsBinding binding = DataBindingUtil.inflate(inflater,R.layout.institute_profile_edit_details,container,false);


        this.binding = binding;

        binding.setViewModel(new ViewModelProvider(requireActivity()).get(InstituteViewModel.class));


        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.doneButton.setOnClickListener(v->{
            binding.getViewModel().detailsEntryDone();
        });


        binding.profilePictureImage.setOnClickListener(v->{
            if(compressingImage.get()){
                Toast.makeText(requireContext(),"Please Wait",Toast.LENGTH_SHORT).show();
                return;
            }


            if(ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
                startFilePicker();
            }
            else{
                storagePermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

        binding.handleInput.addTextChangedListener(new TextWatcher(){

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


        binding.getViewModel()
                .getDataValidationAction()
                .observe(getViewLifecycleOwner(),
                        dataValidationInformationLiveEvent -> {
                            if (dataValidationInformationLiveEvent == null) {
                                return;
                            }

                            DataValidator.DataValidationInformation
                                    dataValidationInformation =
                                    dataValidationInformationLiveEvent.
                                            getDataOnceAndReset();
                            if(dataValidationInformation != null) {
                                switch (dataValidationInformation.getValidationPoint()){
                                    case VALIDATION_POINT_NAME:
                                        if(dataValidationInformation.getDataValidationResult()!=
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                                            binding.nameLayout.setError("Invalid name");
                                        }
                                        break;
                                    case VALIDATION_POINT_INSTITUTE_HANDLE:
                                        if(dataValidationInformation.getDataValidationResult()!=
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                                            binding.handleLayout.setError("Invalid institute");
                                        }
                                        break;
                                    case VALIDATION_POINT_ALL:
                                        if(dataValidationInformation.getDataValidationResult()==
                                        DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                                            binding.doneButton.setEnabled(false);
                                        }
                                        break;
                                    default:
                                        break;
                            }
                        }
                }
                );
    }

    private void startFilePicker(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),REQUEST_CODE_PROFILE_PICTURE_CHOOSER);
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data==null){
            return;
        }

        if(requestCode==REQUEST_CODE_PROFILE_PICTURE_CHOOSER)
        {
            compressingImage.set(true);

            Uri selectedImage =data.getData();

            Bitmap bitmap =null;

            try{
                bitmap= MediaStore.Images.Media.
                        getBitmap(requireActivity().getContentResolver(),selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.e("Details","Compressing image...."+ bitmap.getWidth()+" "+bitmap.getHeight());
            ImageUtilities
                    .scaleToProfilePictureGetBitmap(requireActivity(),bitmap)
                    .observe(getViewLifecycleOwner(),bitmapLiveEvent -> {
                        if(bitmapLiveEvent!=null){
                            Bitmap picture = bitmapLiveEvent.getDataOnceAndReset();
                            if(picture!=null){
                                binding.profilePictureImage.
                                        setColorFilter(getResources().getColor(R.color.transparent,requireActivity().getTheme()));
                                binding.profilePictureImage.setImageBitmap(picture);
                            }
                            else{

                            }
                        }
                        else{
                            Log.e("Institute","Error  ");
                        }

                    });

            try{
                bitmap =MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),selectedImage);
            }catch (IOException io){
                io.printStackTrace();
            }

            ImageUtilities
                    .scaleToProfilePictureGetFile(requireActivity(), bitmap)
                    .observe(getViewLifecycleOwner(), fileLiveEvent -> {
                        compressingImage.set(false);
                        if(fileLiveEvent != null){
                            File file = fileLiveEvent.getDataOnceAndReset();
                            if(file != null) {
                                MutableLiveData<Uri> profilePictureLiveData =
                                        binding.getViewModel().
                                                getInstituteUpdater().
                                                newProfilePicture;
                                profilePictureLiveData.
                                        setValue(Uri.fromFile(file));
                            }
                            else {
                            }
                        }
                        else{
                            Log.e("Institute",
                                    "Error :" +
                                            " the provided " +
                                            "fileLiveEvent was null");
                        }
                    });
        }
    }


    @Override
    public void onDestroyView() {
        binding.getViewModel().exitEditDetails();
        super.onDestroyView();
    }
}
