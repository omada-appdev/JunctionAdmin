package com.omada.junctionadmin.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.ui.create.CreateActivity;
import com.omada.junctionadmin.ui.institute.InstituteActivity;
import com.omada.junctionadmin.ui.login.LoginActivity;
import com.omada.junctionadmin.ui.metrics.MetricsActivity;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    UserProfileViewModel userProfileViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_activity_layout);

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.more_content_placeholder, new ProfileFragment())
                    .commit();
        }

        setupBottomNavigation();
        setupTriggers();
    }

    private void setupBottomNavigation(){

        BottomNavigationView bottomMenu = findViewById(R.id.more_bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.profile_button).setChecked(true);
        bottomMenu.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            Intent i = null;

            if (itemId == R.id.create_button){
                i = new Intent(ProfileActivity.this, CreateActivity.class);
            }
            else if (itemId == R.id.metrics_button){
                i = new Intent(ProfileActivity.this, MetricsActivity.class);
            }
            else if (itemId == R.id.institute_button){
                i = new Intent(ProfileActivity.this, InstituteActivity.class);
            }

            if (i != null) {
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                return true;

            } else {
                return false;
            }

        });

    }

    private void setupTriggers(){

        userProfileViewModel.getSignOutTrigger()
                .observe(this, authStatusLiveEvent -> {

                    if(authStatusLiveEvent != null){

                        UserDataHandler.AuthStatus authStatus = authStatusLiveEvent.getDataOnceAndReset();
                        if(authStatus == UserDataHandler.AuthStatus.USER_SIGNED_OUT) {
                            Intent i = new Intent(this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                            finish();
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomMenu = findViewById(R.id.more_bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.profile_button).setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
