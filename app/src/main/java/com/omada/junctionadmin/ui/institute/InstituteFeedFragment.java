package com.omada.junctionadmin.ui.institute;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.InstituteModel;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.mutable.MutableOrganizationModel;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardLargeBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.eventcard.EventCardLargeBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.institutefeed.OrganizationThumbnailListBinder;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

import java.util.List;

import mva3.adapter.ItemSection;
import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;


public class InstituteFeedFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private InstituteViewModel instituteViewModel;
    private UserProfileViewModel userProfileViewModel;

    private final MultiViewAdapter adapter = new MultiViewAdapter();
    private final ListSection<PostModel> highlightSection = new ListSection<>();
    private final ItemSection<ListSection<OrganizationModel>> organizationSection = new ItemSection<>();

    private boolean refreshOrganizations = true;
    private boolean refreshHighlights = true;
    private ExtendedFloatingActionButton adminButton;

    boolean isAdmin = false;

    public InstituteFeedFragment() {
        ListSection<OrganizationModel> organizationThumbnailSection = new ListSection<>();
        organizationThumbnailSection.add(new MutableOrganizationModel());
        organizationSection.setItem(organizationThumbnailSection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());

        instituteViewModel = viewModelProvider.get(InstituteViewModel.class);
        userProfileViewModel = viewModelProvider.get(UserProfileViewModel.class);
        FeedContentViewModel feedContentViewModel = viewModelProvider.get(FeedContentViewModel.class);

        if (savedInstanceState == null) {
            instituteViewModel.loadInstituteOrganizations();
            instituteViewModel.loadInstituteHighlights();
        }

        refreshHighlights = true;
        refreshOrganizations = true;

        adapter.addSection(organizationSection);
        adapter.addSection(highlightSection);
        adapter.registerItemBinders(
                new OrganizationThumbnailListBinder(feedContentViewModel),
                new EventCardLargeBinder(feedContentViewModel),
                new ArticleCardLargeBinder(feedContentViewModel)
        );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.institute_feed_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        isAdmin = userProfileViewModel.getOrganizationDetails().isInstituteAdmin();

        RecyclerView recyclerView = view.findViewById(R.id.institute_feed_recycler);
        MaterialSearchBar searchBar = view.findViewById(R.id.institute_search_bar);
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);

        MaterialTextView instituteName = view.findViewById(R.id.institute_name);
        ImageView instituteImage = view.findViewById(R.id.institute_image);
        adminButton = view.findViewById(R.id.institute_admin_button);

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(enabled) {
                    appBarLayout.setExpanded(false, true);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        if (isAdmin) {
            appBarLayout.addOnOffsetChangedListener(this);
            adminButton.setVisibility(View.VISIBLE);
            adminButton.setOnClickListener(v -> {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.institute_content_placeholder, new InstituteAdminFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        instituteViewModel.getInstituteDetails()
                .observe(getViewLifecycleOwner(), instituteModelLiveEvent -> {
                    if (instituteModelLiveEvent == null) {
                        return;
                    }
                    InstituteModel model = instituteModelLiveEvent.getDataOnceAndReset();
                    if (model == null) {
                        return;
                    }
                    if (model.getId() == null || model.getId().equals("")) {
                        createNoInstituteAlertDialog().show();
                    } else {
                        instituteName.setText(model.getName());
                        Log.e("Institute", model.getImage() == null ? "null" : model.getImage());
                        CustomBindings.loadImageGs(instituteImage, model.getImage());
                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

        instituteViewModel.getLoadedInstituteHighlights()
                .observe(getViewLifecycleOwner(), postModels -> {
                    onHighlightsLoaded(postModels);
                    Log.e("Institute", "Highlights loaded :" + postModels.size());
                });

        instituteViewModel.getLoadedInstituteOrganizations()
                .observe(getViewLifecycleOwner(), this::onOrganizationsLoaded);

    }

    private void onOrganizationsLoaded(List<OrganizationModel> organizationModels) {

        if (organizationModels != null && organizationModels.size() > 0 && refreshOrganizations) {

            organizationSection.getItem().addAll(organizationModels);

            if (organizationSection.getItem() != null &&
                    organizationSection.getItem().size() > 0 &&
                    organizationSection.getItem().get(0).getId() == null) {

                organizationSection.getItem().remove(0);
            }

            refreshOrganizations = false;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (appBarLayout.getHeight() + verticalOffset < adminButton.getHeight() * 2.5) {
            adminButton.setVisibility(View.GONE);
        } else {
            adminButton.setVisibility(View.VISIBLE);
        }
    }

    private void onHighlightsLoaded(List<PostModel> highlights) {
        if (highlights != null && (refreshHighlights || highlightSection.size() == 0 || highlights.size() != highlightSection.size())) {
            highlightSection.set(highlights);
            refreshHighlights = false;
        }
    }

    private Dialog createNoInstituteAlertDialog() {
        return new MaterialAlertDialogBuilder(requireContext())
                .setCancelable(false)
                .setTitle("Please try later")
                .setMessage("You are currently not part of any institute")
                .setPositiveButton("OK", (dialog, which) -> {
                    requireActivity().finish();
                })
                .create();
    }
}