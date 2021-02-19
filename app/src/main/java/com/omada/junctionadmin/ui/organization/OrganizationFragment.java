package com.omada.junctionadmin.ui.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.OrganizationModel;
import com.omada.junctionadmin.databinding.OrganizationProfileFragmentLayoutBinding;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.utils.taskhandler.LiveEvent;
import com.omada.junctionadmin.viewmodels.OrganizationProfileViewModel;

import java.io.Serializable;

public class OrganizationFragment extends Fragment {

    private OrganizationProfileFragmentLayoutBinding binding;
    private OrganizationProfileViewModel organizationProfileViewModel;


    public static OrganizationFragment newInstance(OrganizationModel organizationModel) {

        Bundle args = new Bundle();
        args.putString("organizationID", organizationModel.getId());
        args.putParcelable("organizationModel", organizationModel);

        OrganizationFragment fragment = new OrganizationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static OrganizationFragment newInstance(String organizationID) {

        Bundle args = new Bundle();
        args.putString("organizationID", organizationID);
        args.putParcelable("organizationModel", null);

        OrganizationFragment fragment = new OrganizationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private OrganizationFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        organizationProfileViewModel = new ViewModelProvider(this).get(OrganizationProfileViewModel.class);

        if(savedInstanceState == null) {
            // created first time

            if (getArguments() != null) {
                organizationProfileViewModel.setOrganizationModel(getArguments().getParcelable("organizationModel"));
                organizationProfileViewModel.setOrganizationID(getArguments().getString("organizationID"));
            } else {
                throw new RuntimeException("Arguments were not set");
            }

            organizationProfileViewModel.loadOrganizationHighlights();
            organizationProfileViewModel.loadOrganizationShowcases();
        }
        else{
            if(organizationProfileViewModel.getOrganizationID() == null){
                if(organizationProfileViewModel.getOrganizationModel() != null){
                    organizationProfileViewModel.setOrganizationID(
                            organizationProfileViewModel.getOrganizationModel().getId()
                    );
                }
                else {
                    organizationProfileViewModel.setOrganizationID(savedInstanceState.getString("organizationID"));
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.organization_profile_fragment_layout, container, false);
        binding.setViewModel(organizationProfileViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(organizationProfileViewModel.getOrganizationModel() == null) {

            LiveData<LiveEvent<OrganizationModel>> orgLiveData = organizationProfileViewModel.getOrganizationDetails();

            orgLiveData.observe(getViewLifecycleOwner(), orgModelLiveEvent->{
                if (orgModelLiveEvent != null){
                    OrganizationModel model =  orgModelLiveEvent.getDataOnceAndReset();
                    if(model == null) {
                        return;
                    }
                    organizationProfileViewModel.setOrganizationModel(model);
                    populateViews();
                }
            });
        }
        else{
            populateViews();
        }

        ViewPager2 pager = binding.organizationProfilePager;

        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new OrganizationProfilePagerAdapter(getChildFragmentManager(), getLifecycle()));
        pager.setCurrentItem(0);

        pager.setUserInputEnabled(false);

        new TabLayoutMediator(binding.organizationProfileTabs, pager, (tab, position) -> {
            switch(position){
                case 0:
                    tab.setText("Content");
                    break;
                case 1:
                    tab.setText("About");
                    break;
            }
        }).attach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("organizationID", organizationProfileViewModel.getOrganizationID());
    }

    private void populateViews(){
        binding.organizationNameText.setText(organizationProfileViewModel.getOrganizationModel().getName());
        CustomBindings.loadImageUrl(
                binding.organizationProfilePictureImage,
                organizationProfileViewModel.getOrganizationModel().getProfilePicture()
        );
    }

    public static class OrganizationProfilePagerAdapter extends FragmentStateAdapter {

        public OrganizationProfilePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position){
                case 0:
                    return new OrganizationContentFragment();
                case 1:
                    return new OrganizationAboutFragment();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
