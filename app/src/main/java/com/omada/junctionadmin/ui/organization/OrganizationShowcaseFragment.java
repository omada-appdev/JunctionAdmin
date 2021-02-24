package com.omada.junctionadmin.ui.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.databinding.OrganizationShowcaseFragmentLayoutBinding;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardSmallNoTitleBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.eventcard.EventCardSmallNoTitleBinder;
import com.omada.junctionadmin.utils.factory.ShowcaseFeedViewModelFactory;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.ShowcaseFeedViewModel;

import java.util.List;

import javax.annotation.Nonnull;

import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;

public class OrganizationShowcaseFragment extends Fragment {

    private ShowcaseModel showcaseModel;

    private ShowcaseFeedViewModel showcaseFeedViewModel;

    private final MultiViewAdapter adapter = new MultiViewAdapter();
    private final ListSection<PostModel> showcaseItemsListSection = new ListSection<>();
    private boolean refreshContents = true;
    private OrganizationShowcaseFragmentLayoutBinding binding;


    public static com.omada.junctionadmin.ui.organization.OrganizationShowcaseFragment newInstance(@Nonnull ShowcaseModel model) {

        Bundle args = new Bundle();
        args.putParcelable("showcaseModel", model);

        com.omada.junctionadmin.ui.organization.OrganizationShowcaseFragment fragment = new com.omada.junctionadmin.ui.organization.OrganizationShowcaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showcaseModel = getArguments().getParcelable("showcaseModel");

        adapter.addSection(showcaseItemsListSection);

        showcaseFeedViewModel = new ViewModelProvider(
                this,
                new ShowcaseFeedViewModelFactory(showcaseModel))
                .get(ShowcaseFeedViewModel.class);

        if(savedInstanceState == null
                || showcaseFeedViewModel.getLoadedShowcaseItems().getValue() == null
                || showcaseFeedViewModel.getLoadedShowcaseItems().getValue().size() == 0) {

            showcaseFeedViewModel.loadShowcaseItems();
        }

        FeedContentViewModel feedContentViewModel = new ViewModelProvider(requireActivity()).get(FeedContentViewModel.class);

        adapter.registerItemBinders(
                new EventCardSmallNoTitleBinder(feedContentViewModel),
                new ArticleCardSmallNoTitleBinder(feedContentViewModel)
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.organization_showcase_fragment_layout, container, false);
        binding.setViewModel(showcaseFeedViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getView() == null) return;
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);

        showcaseFeedViewModel.getLoadedShowcaseItems()
                .observe(getViewLifecycleOwner(), this::onShowcaseItemsLoaded);

        recyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false)
        );

        recyclerView.setAdapter(adapter);
    }

    private void onShowcaseItemsLoaded(List<PostModel> items){
        if(refreshContents || showcaseItemsListSection.size() == 0){
            showcaseItemsListSection.addAll(items);
            refreshContents = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
