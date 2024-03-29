package com.omada.junctionadmin.ui.organization;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.BaseModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardLargeBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardMediumNoTitleBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.eventcard.EventCardMediumNoTitleBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.misc.LargeBoldHeaderBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.organizationfeed.OrganizationShowcaseThumbnailListBinder;
import com.omada.junctionadmin.ui.uicomponents.models.LargeBoldHeaderModel;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.OrganizationProfileViewModel;

import java.util.List;

import mva3.adapter.HeaderSection;
import mva3.adapter.ItemSection;
import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;

public class OrganizationContentFragment extends Fragment {

    private final MultiViewAdapter adapter = new MultiViewAdapter();
    private final ListSection<BaseModel> highlightListSection = new ListSection<>();
    private final ItemSection<ListSection<ShowcaseModel>> showcaseSection = new ItemSection<>();

    private HeaderSection<LargeBoldHeaderModel> showcaseHeaderSection;
    private HeaderSection<LargeBoldHeaderModel> highlightHeaderSection;

    private RecyclerView recyclerView;

    private boolean refreshHighlights = true;
    private boolean refreshShowcases = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FeedContentViewModel feedContentViewModel = new ViewModelProvider(getParentFragment().requireActivity()).get(FeedContentViewModel.class);

        if(savedInstanceState != null){
            refreshHighlights = true;
            refreshShowcases = true;
        }

        ListSection<ShowcaseModel> showcaseThumbnailListSection = new ListSection<>();
        showcaseThumbnailListSection.add(new ShowcaseModel());
        showcaseSection.setItem(showcaseThumbnailListSection);

        showcaseHeaderSection = new HeaderSection<>(new LargeBoldHeaderModel("Showcase"));
        showcaseHeaderSection.addSection(showcaseSection);
        showcaseHeaderSection.hideSection();

        highlightHeaderSection = new HeaderSection<>(new LargeBoldHeaderModel("Highlights"));
        highlightHeaderSection.addSection(highlightListSection);
        highlightHeaderSection.hideSection();

        adapter.addSection(showcaseHeaderSection);
        adapter.addSection(highlightHeaderSection);

        adapter.registerItemBinders(
                new EventCardMediumNoTitleBinder(feedContentViewModel),
                new ArticleCardMediumNoTitleBinder(feedContentViewModel),
                new OrganizationShowcaseThumbnailListBinder(feedContentViewModel),
                new LargeBoldHeaderBinder()
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organization_profile_content_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        OrganizationProfileViewModel organizationProfileViewModel = new ViewModelProvider(getParentFragment())
                .get(OrganizationProfileViewModel.class);

        organizationProfileViewModel.getLoadedOrganizationShowcases().observe(getViewLifecycleOwner(),
                this::onShowcasesLoaded
        );

        organizationProfileViewModel.getLoadedOrganizationHighlights().observe(getViewLifecycleOwner(),
                this::onHighlightsLoaded
        );

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false)
        );

        recyclerView.setAdapter(adapter);
    }

    // apparently we are using post model here and in junction we are using BaseModel.
    private void onHighlightsLoaded(List<PostModel> postModels) {
        if(postModels != null && postModels.size() > 0 && refreshHighlights) {

            if(highlightHeaderSection.isSectionHidden()){
                highlightHeaderSection.showSection();
            }
            highlightListSection.addAll(postModels);

            refreshHighlights = false;
        }
    }

    private void onShowcasesLoaded(List<ShowcaseModel> showcaseModels) {

        if(showcaseModels != null && showcaseModels.size() > 0 && showcaseSection.getItem() != null && refreshShowcases) {

            if(showcaseHeaderSection.isSectionHidden()){
                showcaseHeaderSection.showSection();
            }
            if (showcaseSection.getItem() != null &&
                    showcaseSection.getItem().size() > 0 &&
                    showcaseSection.getItem().get(0).getId() == null) {

                showcaseSection.getItem().remove(0);

            }

            showcaseSection.getItem().addAll(showcaseModels);

            recyclerView.scrollToPosition(0);
            refreshShowcases = false;
        }
    }
}
