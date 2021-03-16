package com.omada.junctionadmin.ui.venue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.ui.BaseFragment;
import com.omada.junctionadmin.ui.uicomponents.binders.VenueEditItemBinder;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;

public class InstituteVenuesFragment extends BaseFragment {

    private InstituteViewModel instituteViewModel;

    private ListSection<VenueModel> venueModelListSection;

    public static InstituteVenuesFragment newInstance() {

        Bundle args = new Bundle();

        InstituteVenuesFragment fragment = new InstituteVenuesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        instituteViewModel = new ViewModelProvider(requireActivity()).get(InstituteViewModel.class);
        if (instituteViewModel.getLoadedInstituteVenues().getValue() == null) {
            instituteViewModel.loadAllVenues();
        }

        return inflater.inflate(R.layout.institute_venues_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        ImageButton doneButton = view.findViewById(R.id.done_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        doneButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        venueModelListSection = new ListSection<>();
        instituteViewModel.getLoadedInstituteVenues().observe(getViewLifecycleOwner(), venueModels -> {
            venueModelListSection.set(venueModels);
        });

        MultiViewAdapter adapter = new MultiViewAdapter();
        adapter.registerItemBinders(
                new VenueEditItemBinder(instituteViewModel)
        );

        adapter.addSection(venueModelListSection);

        recyclerView.setAdapter(adapter);
    }


}
