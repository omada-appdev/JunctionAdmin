package com.omada.junctionadmin.ui.posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.ui.uicomponents.binders.eventcard.EventCardSmallBinder;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;
import com.omada.junctionadmin.viewmodels.OrganizationProfileViewModel;

import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;

public class OrganizationPostsFragment extends Fragment {

    private OrganizationProfileViewModel organizationProfileViewModel;
    private FeedContentViewModel feedContentViewModel;
    private ListSection<PostModel> postModelListSection;
    private MultiViewAdapter adapter;

    public static OrganizationPostsFragment newInstance(OrganizationProfileViewModel organizationProfileViewModel) {

        Bundle args = new Bundle();

        OrganizationPostsFragment fragment = new OrganizationPostsFragment();
        fragment.setOrganizationProfileViewModel(organizationProfileViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organization_posts_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        adapter = new MultiViewAdapter();
        adapter.registerItemBinders(new EventCardSmallBinder(feedContentViewModel));

        recyclerView.setAdapter(adapter);

        postModelListSection = new ListSection<>();

        adapter.addSection(postModelListSection);
    }

    private void setOrganizationProfileViewModel(OrganizationProfileViewModel viewModel) {
        this.organizationProfileViewModel = viewModel;
    }
}
