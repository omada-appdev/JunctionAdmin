package com.omada.junctionadmin.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omada.junctionadmin.BuildConfig;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.ui.article.ArticleDetailsEditFragment;
import com.omada.junctionadmin.ui.article.ArticleDetailsFragment;
import com.omada.junctionadmin.ui.create.CreateActivity;
import com.omada.junctionadmin.ui.event.EventDetailsEditFragment;
import com.omada.junctionadmin.ui.event.EventDetailsFragment;
import com.omada.junctionadmin.ui.institute.InstituteActivity;
import com.omada.junctionadmin.ui.login.LoginActivity;
import com.omada.junctionadmin.ui.metrics.MetricsActivity;
import com.omada.junctionadmin.ui.organization.OrganizationShowcaseEditFragment;
import com.omada.junctionadmin.ui.organization.OrganizationShowcaseFragment;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

import java.util.Map;


public class ProfileActivity extends AppCompatActivity {

    private UserProfileViewModel userProfileViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity_layout);

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        if (savedInstanceState == null) {
            userProfileViewModel.loadOrganizationHighlights();
            userProfileViewModel.loadOrganizationShowcases();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_content_placeholder, new ProfileFragment())
                    .commit();
        }

        setupBottomNavigation();
        setupTriggers();
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomMenu = findViewById(R.id.bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.profile_button).setChecked(true);
        bottomMenu.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            Intent i = null;

            if (itemId == R.id.create_button) {
                i = new Intent(ProfileActivity.this, CreateActivity.class);
            } else if (itemId == R.id.metrics_button) {
                i = new Intent(ProfileActivity.this, MetricsActivity.class);
            } else if (itemId == R.id.institute_button) {
                i = new Intent(ProfileActivity.this, InstituteActivity.class);
            } else if (itemId == R.id.profile_button) {
                if(getSupportFragmentManager().getBackStackEntryCount() != 0 ) {
                    getSupportFragmentManager().popBackStack(null, 0);
                } else {
                    userProfileViewModel.reloadOrganizationHighlights();
                }
            }

            if (i != null) {
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                return true;

            } else {
                return false;
            }

        });

    }

    private void setupTriggers() {

        userProfileViewModel.getAuthStatusTrigger()
                .observe(this, authStatusLiveEvent -> {

                    if (authStatusLiveEvent != null) {
                        UserDataRepository.AuthStatus authStatus = authStatusLiveEvent.getDataOnceAndReset();
                        if (authStatus == null) {
                            return;
                        }
                        switch (authStatus) {
                            case USER_SIGNED_OUT:
                                Intent i = new Intent(this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                                break;
                            case UPDATE_USER_DETAILS_SUCCESS:
                                Toast.makeText(this, "Updated details successfully", Toast.LENGTH_SHORT).show();
                                getSupportFragmentManager().popBackStack();
                                break;
                            case CURRENT_USER_FAILURE:
                                Toast.makeText(this, "Please try again", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }

                    }
                });

        userProfileViewModel.getEditDetailsTrigger()
                .observe(this, booleanLiveEvent -> {

                    if (booleanLiveEvent == null) {
                        return;
                    }
                    boolean trigger = booleanLiveEvent.getDataOnceAndReset();
                    if (trigger) {

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, new ProfileEditDetailsFragment())
                                .addToBackStack(null)
                                .commit();

                    } else {

                    }
                });

        FeedContentViewModel feedContentViewModel = new ViewModelProvider(this).get(FeedContentViewModel.class);

        feedContentViewModel
                .getOrganizationViewHandler()
                .getCallOrganizationTrigger().observe(this, stringLiveEvent -> {

            if (stringLiveEvent != null) {
                String data = stringLiveEvent.getDataOnceAndReset();
                if (data == null) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + data));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

        });

        feedContentViewModel
                .getOrganizationViewHandler()
                .getMailOrganizationTrigger().observe(this, pairLiveEvent -> {

            if (pairLiveEvent != null) {

                Pair<String, String> data = pairLiveEvent.getDataOnceAndReset();
                if (data == null) {
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{data.second});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding " + data.first);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

        });

        feedContentViewModel.getEventViewHandler()
                .getEventFormTrigger()
                .observe(this, eventModelLiveEvent -> {
                    if (eventModelLiveEvent == null) {
                        return;
                    }
                    EventModel eventModel = eventModelLiveEvent.getDataOnceAndReset();
                    if (eventModel == null) {
                        return;
                    }
                    Map<String, Map<String, Map<String, String>>> form = eventModel.getForm();
                    String url;
                    try {
                        url = form.get("links").get("linkId").get("url");
                    } catch (Exception e) {
                        url = null;
                    }

                    if (url != null) {
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;

                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        } catch (Exception e) {
                            Log.e("Event", "Invalid form URL");
                        }
                    }
                });

        feedContentViewModel.getEventViewHandler()
                .getEventCardDetailsTrigger()
                .observe(this, eventModelLiveEvent -> {
                    if (eventModelLiveEvent == null) {
                        return;
                    }
                    EventModel eventModel = eventModelLiveEvent.getDataOnceAndReset();
                    if (eventModel == null) {
                        return;
                    }
                    if (eventModel.getCreator().equals(userProfileViewModel.getUserId())) {
                        // TODO open edit details fragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, EventDetailsEditFragment.newInstance(eventModel))
                                .addToBackStack(null)
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, EventDetailsFragment.newInstance(eventModel))
                                .addToBackStack(null)
                                .commit();
                    }
                });

        feedContentViewModel.getArticleViewHandler()
                .getArticleCardDetailsTrigger()
                .observe(this, articleModelLiveEvent -> {
                    if (articleModelLiveEvent == null) {
                        return;
                    }
                    ArticleModel articleModel = articleModelLiveEvent.getDataOnceAndReset();
                    if (articleModel == null) {
                        return;
                    }
                    if (articleModel.getCreator().equals(userProfileViewModel.getUserId())) {
                        // TODO open edit details fragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, ArticleDetailsEditFragment.newInstance(articleModel))
                                .addToBackStack(null)
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, ArticleDetailsFragment.newInstance(articleModel))
                                .addToBackStack(null)
                                .commit();
                    }
                });

        feedContentViewModel.getOrganizationViewHandler()
                .getOrganizationShowcaseDetailsTrigger()
                .observe(this, showcaseModelLiveEvent -> {
                    if (showcaseModelLiveEvent == null) {
                        return;
                    }
                    ShowcaseModel showcaseModel = showcaseModelLiveEvent.getDataOnceAndReset();
                    if (showcaseModel == null) {
                        return;
                    }
                    if (showcaseModel.getCreator().equals(userProfileViewModel.getUserId())) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, OrganizationShowcaseEditFragment.newInstance(showcaseModel))
                                .addToBackStack(null)
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profile_content_placeholder, OrganizationShowcaseFragment.newInstance(showcaseModel))
                                .addToBackStack(null)
                                .commit();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        userProfileViewModel.resetOrganizationUpdater();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomMenu = findViewById(R.id.bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.profile_button).setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
