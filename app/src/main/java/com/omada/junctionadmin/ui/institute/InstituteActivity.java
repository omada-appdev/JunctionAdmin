package com.omada.junctionadmin.ui.institute;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.ui.create.CreateActivity;
import com.omada.junctionadmin.ui.event.EventDetailsFragment;
import com.omada.junctionadmin.ui.metrics.MetricsActivity;
import com.omada.junctionadmin.ui.profile.ProfileActivity;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

public class InstituteActivity extends AppCompatActivity {

    private InstituteViewModel instituteViewModel;
    private FeedContentViewModel feedContentViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.institute_activity_layout);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);

        instituteViewModel = viewModelProvider.get(InstituteViewModel.class);
        feedContentViewModel = viewModelProvider.get(FeedContentViewModel.class);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.institute_content_placeholder, new InstituteFeedFragment())
                    .commit();

        } else if(!instituteViewModel.checkInstituteContentLoaded()){
            instituteViewModel.loadInstituteOrganizations();
            instituteViewModel.loadInstituteHighlights();
        }

        setupBottomNavigation();
        setupTriggers();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomMenu = findViewById(R.id.bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.institute_button).setChecked(true);
        bottomMenu.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            Intent i = null;

            if (itemId == R.id.profile_button){
                i = new Intent(InstituteActivity.this, ProfileActivity.class);
            }
            else if (itemId == R.id.metrics_button){
                i = new Intent(InstituteActivity.this, MetricsActivity.class);
            }
            else if (itemId == R.id.create_button){
                i = new Intent(InstituteActivity.this, CreateActivity.class);
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

    private void setupTriggers() {

        FeedContentViewModel feedContentViewModel = new ViewModelProvider(this).get(FeedContentViewModel.class);

        /*
                TODO
        feedContentViewModel.getOrganizationViewHandler()
                .getOrganizationModelDetailsTrigger()
                .observe(this, organizationModelLiveEvent -> {

                    if(organizationModelLiveEvent != null){
                        OrganizationModel organizationModel = organizationModelLiveEvent.getDataOnceAndReset();

                        if(organizationModel == null) {
                            return;
                        }
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.institute_content_placeholder, OrganizationProfileFragment.newInstance(organizationModel))
                                .addToBackStack("stack")
                                .commit();
                    }

                });

         */

        feedContentViewModel.getEventViewHandler()
                .getEventCardDetailsTrigger()
                .observe(this, eventModelLiveEvent -> {
                    if(eventModelLiveEvent != null){
                        EventModel model = eventModelLiveEvent.getDataOnceAndReset();

                        if(model == null) {
                            return;
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.institute_content_placeholder, EventDetailsFragment.newInstance(model))
                                .addToBackStack("stack")
                                .commit();
                    }
                });


        /*
                TODO
        feedContentViewModel.getOrganizationViewHandler().getOrganizationDetailsTrigger()
                .observe(this, organizationIDLiveEvent -> {
                    if(organizationIDLiveEvent != null){

                        String organizationID = organizationIDLiveEvent.getDataOnceAndReset();
                        if(organizationID == null) {
                            return;
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.institute_content_placeholder, OrganizationProfileFragment.newInstance(organizationID))
                                .addToBackStack("stack")
                                .commit();
                    }
                });


                TODO
        feedContentViewModel
                .getOrganizationViewHandler()
                .getOrganizationShowcaseDetailsTrigger()
                .observe(this, showcaseModelLiveEvent -> {

                    if(showcaseModelLiveEvent != null){
                        ShowcaseModel model = showcaseModelLiveEvent.getDataOnceAndReset();
                        if(model == null) {
                            return;
                        }
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.institute_content_placeholder, OrganizationShowcaseFragment.newInstance(model.getCreator(), model.getShowcaseID()))
                                .addToBackStack("stack")
                                .commit();

                    }

                });

         */
    }



    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomMenu = findViewById(R.id.bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.institute_button).setChecked(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
