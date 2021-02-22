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

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.databinding.OrganizationProfileAboutFragmentLayoutBinding;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.OrganizationProfileViewModel;

public class OrganizationAboutFragment extends Fragment {

    public static OrganizationAboutFragment newInstance() {

        Bundle args = new Bundle();

        OrganizationAboutFragment fragment = new OrganizationAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OrganizationProfileAboutFragmentLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.organization_profile_about_fragment_layout, container, false);
        binding.setViewModel(
                new ViewModelProvider(requireParentFragment().requireActivity()).get(FeedContentViewModel.class)
        );
        OrganizationProfileViewModel viewModel = new ViewModelProvider(requireParentFragment()).get(OrganizationProfileViewModel.class);
        viewModel.getOrganizationDetails().observe(getViewLifecycleOwner(), organizationModelLiveEvent -> {
            if(organizationModelLiveEvent == null) {
                return;
            }
            OrganizationModel model = organizationModelLiveEvent.getDataOnceAndReset();
            if(model == null) {
                return;
            }
            binding.setModel(model);
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
