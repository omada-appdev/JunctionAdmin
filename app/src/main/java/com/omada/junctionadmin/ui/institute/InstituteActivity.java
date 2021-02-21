package com.omada.junctionadmin.ui.institute;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.ui.article.ArticleDetailsEditFragment;
import com.omada.junctionadmin.ui.article.ArticleDetailsFragment;
import com.omada.junctionadmin.ui.create.CreateActivity;
import com.omada.junctionadmin.ui.event.EventDetailsEditFragment;
import com.omada.junctionadmin.ui.event.EventDetailsFragment;
import com.omada.junctionadmin.ui.metrics.MetricsActivity;
import com.omada.junctionadmin.ui.organization.OrganizationFragment;
import com.omada.junctionadmin.ui.organization.OrganizationShowcaseFragment;
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
            } else if (itemId == R.id.institute_button) {
                getSupportFragmentManager().popBackStack(null, 0);
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

        feedContentViewModel.getOrganizationViewHandler()
                .getOrganizationModelDetailsTrigger()
                .observe(this, organizationModelLiveEvent -> {

                    if(organizationModelLiveEvent != null){
                        OrganizationModel organizationModel = organizationModelLiveEvent.getDataOnceAndReset();

                        if(organizationModel == null) {
                            return;
                        }
                        if(organizationModel.getId().equals(instituteViewModel.getUserId())){
                            Intent i = new Intent(this, ProfileActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(i);
                        }
                        else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.institute_content_placeholder, OrganizationFragment.newInstance(organizationModel))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }

                });


        feedContentViewModel.getEventViewHandler()
                .getEventCardDetailsTrigger()
                .observe(this, eventModelLiveEvent -> {
                    if(eventModelLiveEvent != null){
                        EventModel model = eventModelLiveEvent.getDataOnceAndReset();

                        if(model == null) {
                            return;
                        }
                        if(model.getCreator().equals(instituteViewModel.getUserId())) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.institute_content_placeholder, EventDetailsEditFragment.newInstance(model))
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.institute_content_placeholder, EventDetailsFragment.newInstance(model))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });

        feedContentViewModel.getArticleViewHandler()
                .getArticleCardDetailsTrigger()
                .observe(this, articleModelLiveEvent -> {
                    if(articleModelLiveEvent != null){
                        ArticleModel model = articleModelLiveEvent.getDataOnceAndReset();

                        if(model == null) {
                            return;
                        }
                        if(model.getCreator().equals(instituteViewModel.getUserId())) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.institute_content_placeholder, ArticleDetailsEditFragment.newInstance(model))
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.institute_content_placeholder, ArticleDetailsFragment.newInstance(model))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });

        feedContentViewModel.getOrganizationViewHandler().getOrganizationDetailsTrigger()
                .observe(this, organizationIDLiveEvent -> {
                    if(organizationIDLiveEvent != null){

                        String organizationID = organizationIDLiveEvent.getDataOnceAndReset();
                        if(organizationID == null) {
                            return;
                        }
                        if(organizationID.equals(instituteViewModel.getUserId())){
                            Intent i = new Intent(this, ProfileActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(i);
                        }
                        else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.institute_content_placeholder, OrganizationFragment.newInstance(organizationID))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });


        feedContentViewModel.getOrganizationViewHandler()
                .getOrganizationShowcaseDetailsTrigger()
                .observe(this, showcaseModelLiveEvent -> {
                    if(showcaseModelLiveEvent == null) {
                        return;
                    }
                    ShowcaseModel showcaseModel = showcaseModelLiveEvent.getDataOnceAndReset();
                    if(showcaseModel == null) {
                        return;
                    }
                    if (showcaseModel.getCreator().equals(instituteViewModel.getUserId())) {
                        // TODO open edit details fragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.institute_content_placeholder, OrganizationShowcaseFragment.newInstance(showcaseModel))
                                .addToBackStack(null)
                                .commit();
                    }
                    else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.institute_content_placeholder, OrganizationShowcaseFragment.newInstance(showcaseModel))
                                .addToBackStack(null)
                                .commit();
                    }
                });
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
