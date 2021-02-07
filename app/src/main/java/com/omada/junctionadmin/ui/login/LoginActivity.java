package com.omada.junctionadmin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.ui.profile.ProfileActivity;
import com.omada.junctionadmin.viewmodels.LoginViewModel;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    //public enum identifying fragments
    public enum FragmentIdentifier {
        LOGIN_START_FRAGMENT,
        LOGIN_SIGNIN_FRAGMENT,
        LOGIN_DETAILS_FRAGMENT,
        LOGIN_INTERESTS_FRAGMENT,
        LOGIN_FORGOTPASSWORD_FRAGMENT
    }

    private FragmentIdentifier currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        currentFragment = FragmentIdentifier.LOGIN_START_FRAGMENT;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_activity_placeholder, StartFragment.newInstance())
                .commit();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getAuthResultAction().observe(this, authStatusLiveEvent -> {});

        loginViewModel.getFragmentChangeAction().observe(this, fragId -> {

            FragmentIdentifier id = fragId.getDataOnceAndReset();
            if(id == null){
                return;
            }
            currentFragment = id;
            switch(currentFragment){
                case LOGIN_SIGNIN_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.login_activity_placeholder, SignInFragment.getInstance())
                            .addToBackStack("signin")
                            .commit();
                    break;
                case LOGIN_FORGOTPASSWORD_FRAGMENT:
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
            if(goToFeed.getDataOnceAndReset()){
                Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginViewModel.getToastMessageAction().observe(this, stringLiveEvent -> {
            if(stringLiveEvent == null) {
                return;
            }
            String data = stringLiveEvent.getDataOnceAndReset();
            if(data == null) {
                return;
            }
            Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (currentFragment){
            case LOGIN_SIGNIN_FRAGMENT:
                currentFragment = FragmentIdentifier.LOGIN_START_FRAGMENT;
                loginViewModel.exitSignInScreen();
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
