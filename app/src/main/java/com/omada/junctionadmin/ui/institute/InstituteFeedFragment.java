package com.omada.junctionadmin.ui.institute;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.mutable.MutableOrganizationModel;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardLargeBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.eventcard.EventCardLargeBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.institutefeed.OrganizationThumbnailListBinder;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import java.util.List;

import mva3.adapter.ItemSection;
import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;


public class InstituteFeedFragment extends Fragment {

    private InstituteViewModel instituteViewModel;

    private final MultiViewAdapter adapter = new MultiViewAdapter();
    private final ListSection<PostModel> highlightSection = new ListSection<>();
    private final ItemSection<ListSection<OrganizationModel>> organizationSection = new ItemSection<>();

    private boolean refreshOrganizations = true;
    private boolean refreshHighlights = true;

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
        FeedContentViewModel feedContentViewModel = viewModelProvider.get(FeedContentViewModel.class);

        if(savedInstanceState == null){
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

        RecyclerView recyclerView = view.findViewById(R.id.institute_feed_recycler);
        MaterialSearchBar searchBar = view.findViewById(R.id.institute_search_bar);
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);

        CustomBindings.loadImageGs(
                view.findViewById(R.id.institute_banner),
                "gs://junction-b7b44.appspot.com/instituteFiles/nitw.jpg"
        );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

        instituteViewModel.getLoadedInstituteHighlights()
                .observe(getViewLifecycleOwner(), postModels-> {
                    onHighlightsLoaded(postModels);
                    Log.e("highlights loaded", String.valueOf(postModels.size()));
                });

        instituteViewModel.getLoadedInstituteOrganizations()
                .observe(getViewLifecycleOwner(), this::onOrganizationsLoaded);

    }

    private void onOrganizationsLoaded(List<OrganizationModel> organizationModels) {

        if(organizationModels != null && organizationModels.size() > 0 && refreshOrganizations) {

            organizationSection.getItem().addAll(organizationModels);

            if (organizationSection.getItem() != null &&
                    organizationSection.getItem().size() > 0 &&
                    organizationSection.getItem().get(0).getId() == null) {

                organizationSection.getItem().remove(0);
            }

            refreshOrganizations = false;
        }
    }

    private void onHighlightsLoaded(List<PostModel> highlights) {

        if (highlights != null && (refreshHighlights || highlightSection.size() == 0)) {

            highlightSection.addAll(highlights);
            refreshHighlights = false;
        }
    }
}