package com.omada.junctionadmin.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.databinding.UserProfileFragmentLayoutBinding;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private UserProfileFragmentLayoutBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        UserProfileFragmentLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_profile_fragment_layout, container, false);
        this.binding = binding;

        binding.setViewModel(
                new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class)
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.getViewModel().getUserUpdateAction()
                .observe(getViewLifecycleOwner(), organizationModel -> {
                    if(organizationModel == null) {
                        return;
                    }
                    CustomBindings.loadImageUrl(binding.userProfileImage, organizationModel.getProfilePicture());
                });

        binding.userProfileUpcomingEventsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)
        );

        binding.userProfileAchievementsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)
        );

        binding.toolbar.setNavigationOnClickListener(v -> {
            binding.drawerLayout.openDrawer(binding.navigationView, true);
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.sign_out_button:
                    binding.getViewModel().signOutUser();
                    break;
                default:
                    break;
            }
            item.setChecked(true);
            binding.drawerLayout.closeDrawers();
            return true;
        });

        binding.appbar.addOnOffsetChangedListener(this);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (-verticalOffset >= binding.profileDetails.getHeight()) {
            binding.toolbar.setTitle("Your Profile");
        }
        else {
            binding.toolbar.setTitle("");
        }
    }
}
