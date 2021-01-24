package com.omada.junctionadmin.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.databinding.LoginDetailsFragmentLayoutBinding;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.utils.transform.TransformUtilities;
import com.omada.junctionadmin.viewmodels.LoginViewModel;

public class DetailsFragment extends Fragment {
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

                    if(dataValidationInformationLiveEvent == null) {
                        return;
                    }

                    DataValidator.DataValidationInformation dataValidationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
                    if(dataValidationInformation == null){
                        return;
                    }

                    switch (dataValidationInformation.getValidationPoint()){
                        case VALIDATION_POINT_EMAIL:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                                binding.emailLayout.setError("Invalid email");
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
                        case VALIDATION_POINT_INSTITUTE:
                            if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                                binding.instituteLayout.setError("Invalid institute");
                            }
                            break;
                        case VALIDATION_POINT_INTERESTS:
                            break;
                        case VALIDATION_POINT_ALL:
                            if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID){
                                binding.nextButton.setEnabled(false);

                                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(binding.nextButton.getWindowToken(), 0);
                                binding.nextButton.setEnabled(false);

                            }
                            break;
                    }

                });

        binding.getViewModel()
                .getAuthResultAction()
                .observe(getViewLifecycleOwner(), authStatusLiveEvent -> {

                    if(authStatusLiveEvent == null){
                        return;
                    }

                    UserDataHandler.AuthStatus authStatus = authStatusLiveEvent.getDataOnceAndReset();
                    if(authStatus == null){
                        return;
                    }
                    switch (authStatus){
                        case SIGNUP_FAILURE:
                        case ADD_EXTRA_DETAILS_FAILURE:
                            Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_LONG).show();
                            binding.nextButton.setEnabled(true);
                            break;
                        default:
                            break;
                    }
                });

        binding.nextButton.setOnClickListener(v->{

            binding.emailLayout.setError("");
            binding.passwordLayout.setError("");
            binding.nameLayout.setError("");
            binding.getViewModel().detailsEntryDone();
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

}
