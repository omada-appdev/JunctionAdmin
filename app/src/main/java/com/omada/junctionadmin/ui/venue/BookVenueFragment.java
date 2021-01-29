package com.omada.junctionadmin.ui.venue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.ui.uicomponents.binders.VenueBookingItemBinder;
import com.omada.junctionadmin.viewmodels.BookingViewModel;
import com.omada.junctionadmin.viewmodels.CreatePostViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import mva3.adapter.HeaderSection;
import mva3.adapter.MultiViewAdapter;

public class BookVenueFragment extends Fragment {

    private CreatePostViewModel createPostViewModel;
    private BookingViewModel bookingViewModel;
    private RecyclerView recyclerView;

    private MultiViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);
        createPostViewModel = viewModelProvider.get(CreatePostViewModel.class);

        return inflater.inflate(R.layout.book_venue_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        adapter = new MultiViewAdapter();
        recyclerView = view.findViewById(R.id.recycler_view);

        adapter.registerItemBinders(new VenueBookingItemBinder(bookingViewModel));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
