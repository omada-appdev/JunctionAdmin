package com.omada.junctionadmin.ui.create;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.ui.article.ArticleCreateFragment;
import com.omada.junctionadmin.ui.event.EventCreateFragment;
import com.omada.junctionadmin.ui.form.CreateFormFragment;
import com.omada.junctionadmin.ui.institute.InstituteActivity;
import com.omada.junctionadmin.ui.metrics.MetricsActivity;
import com.omada.junctionadmin.ui.profile.ProfileActivity;
import com.omada.junctionadmin.ui.profile.ProfileFragment;
import com.omada.junctionadmin.ui.venue.BookVenueFragment;
import com.omada.junctionadmin.viewmodels.BookingViewModel;
import com.omada.junctionadmin.viewmodels.CreatePostViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

public class CreateActivity extends AppCompatActivity {

    private CreatePostViewModel createPostViewModel;
    private BookingViewModel bookingViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity_layout);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);
        createPostViewModel = viewModelProvider.get(CreatePostViewModel.class);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.create_content_placeholder, new CreateFragment())
                    .commit();
        }

        setupBottomNavigation();
        setUpTriggers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!createPostViewModel.getUserModel().isInstituteVerified()) {
            findViewById(R.id.institute_warning_constraint_layout).setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomMenu = findViewById(R.id.bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.create_button).setChecked(true);
        bottomMenu.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            Intent i = null;

            if (itemId == R.id.profile_button){
                i = new Intent(CreateActivity.this, ProfileActivity.class);
            }
            else if (itemId == R.id.metrics_button){
                i = new Intent(CreateActivity.this, MetricsActivity.class);
            }
            else if (itemId == R.id.institute_button){
                i = new Intent(CreateActivity.this, InstituteActivity.class);
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

    private void setUpTriggers() {

        createPostViewModel.getCreateEventTrigger().observe(this, booleanLiveEvent -> {

            if(booleanLiveEvent == null) {
                return;
            }

            Boolean result = booleanLiveEvent.getDataOnceAndReset();
            if(result != null && result) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.create_content_placeholder, new EventCreateFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        createPostViewModel.getCreateArticleTrigger().observe(this, booleanLiveEvent -> {

            if(booleanLiveEvent == null) {
                return;
            }

            Boolean result = booleanLiveEvent.getDataOnceAndReset();
            if(result != null && result) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.create_content_placeholder, new ArticleCreateFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        createPostViewModel.getCreateBookingTrigger().observe(this, booleanLiveEvent -> {

            if(booleanLiveEvent == null) {
                return;
            }

            Boolean result = booleanLiveEvent.getDataOnceAndReset();
            if(result != null && result) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.create_content_placeholder, new BookVenueFragment())
                        .addToBackStack(null)
                        .commit();
            }

        });

        createPostViewModel.getCreateFormTrigger().observe(this, booleanLiveEvent -> {

            if(booleanLiveEvent == null) {
                return;
            }

            Boolean result = booleanLiveEvent.getDataOnceAndReset();
            if(result != null && result) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.create_content_placeholder, new CreateFormFragment())
                        .addToBackStack(null)
                        .commit();
            }

        });
    }

    @Override
    public void onBackPressed() {

        int stackCount = getSupportFragmentManager().getBackStackEntryCount();
        if(stackCount == 1) {
            if(createPostViewModel.getCurrentState() == CreatePostViewModel.CurrentState.CURRENT_STATE_IDLE) {
                new MaterialAlertDialogBuilder(this)
                        .setCancelable(false)
                        .setTitle("Discard changes?")
                        .setMessage("Any changes you made will be lost")
                        .setPositiveButton("DISCARD", (dialog, which) -> {
                            createPostViewModel.resetCreators();
                            bookingViewModel.resetBookingDate();
                            super.onBackPressed();
                        })
                        .setNegativeButton("Keep editing", (dialog, which) -> {
                        }).show();
            } else {
                createPostViewModel.resetCreators();
                bookingViewModel.resetBookingDate();
                super.onBackPressed();
            }
            Log.e("Create", "Attempt to exit before posting");
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomMenu = findViewById(R.id.bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.create_button).setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
