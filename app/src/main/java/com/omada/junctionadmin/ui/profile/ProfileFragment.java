package com.omada.junctionadmin.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.databinding.UserProfileFragmentLayoutBinding;
import com.omada.junctionadmin.ui.institute.InstituteProfileEditDetailsFragment;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.utils.taskhandler.DefaultExecutorSupplier;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

import static com.omada.junctionadmin.R.*;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private UserProfileFragmentLayoutBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        UserProfileFragmentLayoutBinding binding = DataBindingUtil.inflate(inflater, layout.user_profile_fragment_layout, container, false);
        this.binding = binding;

        binding.setViewModel(
                new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class)
        );

        binding.getViewModel().loadOrganizationHighlights();
        binding.getViewModel().loadOrganizationShowcases();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Profile","onViewCreated");
        binding.getViewModel().getUserUpdateAction()
                .observe(getViewLifecycleOwner(), organizationModel -> {
                    if(organizationModel == null) {
                        return;
                    }
                    CustomBindings.loadImageUrl(binding.userProfileImage, organizationModel.getProfilePicture());
                });

        binding.toolbar.setNavigationOnClickListener(v -> {
            binding.drawerLayout.openDrawer(binding.navigationView, true);
        });

        Boolean isInstituteAdmin = binding.getViewModel().getOrganizationDetails().isInstituteAdmin();
        if(isInstituteAdmin == null || !isInstituteAdmin) {
            Log.e("Profile", "Not an admin");
            binding.navigationView.getMenu().removeItem(id.create_institute_button);
        }

        binding.drawerLayout.closeDrawer(binding.navigationView);

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            binding.drawerLayout.closeDrawer(binding.navigationView);
            Handler handler = new Handler(Looper.getMainLooper());

            // delay to let the drawer close
            handler.postDelayed(() -> {
                if (itemId == id.create_institute_button) {
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(id.profile_content_placeholder, new InstituteProfileEditDetailsFragment())
                            .addToBackStack(null)
                            .commit();
                } else if (itemId == id.members_button) {
                } else if (itemId == id.settings_button) {
                } else if (itemId == id.feedback_button) {
                } else {
                }
            }, 300);
            return true;
        });

        binding.appbar.addOnOffsetChangedListener(this);
    }

    /*
    All this mental gymnastics with a flag variable is because there is an infinite loop
    somewhere and it is probably a bug in the Android SDK
    TODO file an issue if non existent
     */
    private boolean titleSetFlag = false;
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (!titleSetFlag && -verticalOffset >= binding.profileDetails.getHeight()) {
            titleSetFlag = true;
            binding.toolbar.setTitle("Your Profile");
        }
        else if (titleSetFlag && -verticalOffset < binding.profileDetails.getHeight()) {
            titleSetFlag = false;
            binding.toolbar.setTitle("");
        }
    }
}
