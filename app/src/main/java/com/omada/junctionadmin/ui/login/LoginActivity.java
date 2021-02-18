package com.omada.junctionadmin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.ui.profile.ProfileActivity;
import com.omada.junctionadmin.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    //public enum identifying fragments
    public enum FragmentIdentifier {
        LOGIN_START_FRAGMENT,
        LOGIN_SIGN_IN_FRAGMENT,
        LOGIN_DETAILS_FRAGMENT,
        LOGIN_INTERESTS_FRAGMENT,
        LOGIN_FORGOT_PASSWORD_FRAGMENT
    }

    private FragmentIdentifier currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        currentFragment = FragmentIdentifier.LOGIN_START_FRAGMENT;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_activity_placeholder, StartFragment.newInstance())
                .commit();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getAuthResultAction().observe(this, authStatusLiveEvent -> {
        });

        loginViewModel.getFragmentChangeAction().observe(this, fragId -> {

            FragmentIdentifier id = fragId.getDataOnceAndReset();
            if (id == null) {
                return;
            }
            currentFragment = id;
            switch (currentFragment) {
                case LOGIN_SIGN_IN_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.login_activity_placeholder, SignInFragment.getInstance())
                            .addToBackStack("signin")
                            .commit();
                    break;
                case LOGIN_FORGOT_PASSWORD_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.login_activity_placeholder, ForgotPasswordFragment.newInstance())
                            .addToBackStack("forgot")
                            .commit();
                    break;
                case LOGIN_DETAILS_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.login_activity_placeholder, DetailsFragment.newInstance())
                            .addToBackStack("details")
                            .commit();
                    break;
                case LOGIN_INTERESTS_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.login_activity_placeholder, InterestsFragment.newInstance())
                            .addToBackStack("interests")
                            .commit();
                    break;
            }
        });

        loginViewModel.getGoToFeedAction().observe(this, goToFeed -> {
            if (goToFeed.getDataOnceAndReset() == true) {
                Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        loginViewModel.getToastMessageAction().observe(this, stringLiveEvent -> {
            if (stringLiveEvent == null) {
                return;
            }
            String data = stringLiveEvent.getDataOnceAndReset();
            if (data == null) {
                return;
            }
            Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (currentFragment) {
            case LOGIN_SIGN_IN_FRAGMENT:
                currentFragment = FragmentIdentifier.LOGIN_START_FRAGMENT;
                loginViewModel.exitSignInScreen();
                break;
            case LOGIN_FORGOT_PASSWORD_FRAGMENT:
                currentFragment = FragmentIdentifier.LOGIN_SIGN_IN_FRAGMENT;
                break;
            case LOGIN_DETAILS_FRAGMENT:
                currentFragment = FragmentIdentifier.LOGIN_INTERESTS_FRAGMENT;
                loginViewModel.exitDetailsScreen();
                break;
            case LOGIN_INTERESTS_FRAGMENT:
                currentFragment = FragmentIdentifier.LOGIN_START_FRAGMENT;
                loginViewModel.exitInterestsScreen();
                break;
        }
    }

}
