package com.omada.junctionadmin.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.ui.login.LoginActivity;
import com.omada.junctionadmin.ui.profile.ProfileActivity;
import com.omada.junctionadmin.viewmodels.SplashViewModel;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity_layout);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        splashViewModel.getCurrentUser();

        splashViewModel.getAuthResultAction().observe(this, authStatusLiveEvent -> {
            if(authStatusLiveEvent != null){
                UserDataRepository.AuthStatus authStatus = authStatusLiveEvent.getDataOnceAndReset();
                if(authStatus==null) return;
                Log.e("Splash", "AuthStatus : " + authStatus.toString());
                Intent i;
                Log.e("Splash", authStatus.toString());
                switch (authStatus){
                    case CURRENT_USER_SUCCESS:
                        break;
                    case LOGIN_SUCCESS:
                        i = new Intent(this, ProfileActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case CURRENT_USER_FAILURE:
                    case USER_SIGNED_OUT:
                    case USER_TOKEN_EXPIRED:
                    case LOGIN_FAILURE:
                        i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                        finish();
                        break;
                }
            }
        });

        // Observing for transformation to execute in LoginViewModel
        splashViewModel.getSignedInUserAction().observe(this, userModelLiveEvent -> {
        });
    }
}
