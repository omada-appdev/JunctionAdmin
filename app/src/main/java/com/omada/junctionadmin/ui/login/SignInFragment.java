package com.omada.junctionadmin.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.databinding.LoginSigninFragmentLayoutBinding;
import com.omada.junctionadmin.utils.taskhandler.DataValidator;
import com.omada.junctionadmin.viewmodels.LoginViewModel;

public class SignInFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private LoginSigninFragmentLayoutBinding binding;

    public static Fragment getInstance() {
        return new SignInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.binding = DataBindingUtil.inflate(
                inflater, R.layout.login_signin_fragment_layout, container, false);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        binding.setViewModel(loginViewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        loginViewModel.getAuthResultAction().observe(getViewLifecycleOwner(), authResult -> {
            UserDataRepository.AuthStatus res = authResult.getDataOnceAndReset();
            binding.passwordLayout.clearFocus();
            if(res==null){
                return;
            }
            switch (res){
                case AUTHENTICATION_SUCCESS:
                    break;
                case USER_TOKEN_EXPIRED:
                case AUTHENTICATION_FAILURE:
                case LOGIN_FAILURE:
                    binding.loginButton.setEnabled(true);
                    binding.passwordInput.setText("");
                    binding.emailInput.setText("");
                    binding.emailLayout.setError("invalid account details");
                    break;
                case LOGIN_SUCCESS:
                    break;
            }
        });

        /*
        first param in pair is what was invalid and second param is why it is invalid
         */
        loginViewModel.getDataValidationAction().observe(getViewLifecycleOwner(), dataValidationInformationLiveEvent->{

            if(dataValidationInformationLiveEvent == null){
                return;
            }
            DataValidator.DataValidationInformation dataValidationInformation = dataValidationInformationLiveEvent.getDataOnceAndReset();
            if(dataValidationInformation == null || dataValidationInformation.getValidationPoint() == null){
                return;
            }

            switch (dataValidationInformation.getValidationPoint()){
                case VALIDATION_POINT_EMAIL:
                    if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                        binding.emailLayout.setError("invalid email");
                    }
                    break;
                case VALIDATION_POINT_PASSWORD:
                    if (dataValidationInformation.getDataValidationResult() != DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                        binding.passwordLayout.setError("invalid password");
                    }
                    break;
                case VALIDATION_POINT_ALL:
                    if(dataValidationInformation.getDataValidationResult() == DataValidator.DataValidationResult.VALIDATION_RESULT_VALID) {
                        binding.emailLayout.clearFocus();
                        binding.passwordLayout.clearFocus();

                        binding.emailLayout.setErrorEnabled(false);
                        binding.passwordLayout.setErrorEnabled(false);

                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(binding.loginButton.getWindowToken(), 0);
                        binding.loginButton.setEnabled(false);
                    }
            }
        });

        binding.passwordInput.setText("");
        binding.emailInput.setText("");

        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();
        binding.passwordInput.setTransformationMethod(new PasswordTransformationMethod());
    }

}
