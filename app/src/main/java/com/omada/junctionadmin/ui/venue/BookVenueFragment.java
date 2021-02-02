package com.omada.junctionadmin.ui.venue;

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

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.ui.uicomponents.animations.VenueItemAnimator;
import com.omada.junctionadmin.ui.uicomponents.binders.VenueBookingItemBinder;
import com.omada.junctionadmin.viewmodels.BookingViewModel;
import com.omada.junctionadmin.viewmodels.CreatePostViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;
import mva3.adapter.util.Mode;

public class BookVenueFragment extends Fragment {

    private CreatePostViewModel createPostViewModel;
    private BookingViewModel bookingViewModel;
    private RecyclerView recyclerView;
    private TextInputLayout dateLayout;
    private TextInputEditText dateInput;

    private final ListSection<VenueModel> venueModelListSection = new ListSection<>();
    private MultiViewAdapter adapter;

    private boolean refreshVenues = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);
        createPostViewModel = viewModelProvider.get(CreatePostViewModel.class);

        if(savedInstanceState == null
                && (bookingViewModel.getLoadedInstituteVenues().getValue() == null
                || bookingViewModel.getLoadedInstituteVenues().getValue().size() == 0)) {
            refreshVenues = true;
            bookingViewModel.loadAllVenues();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        adapter = new MultiViewAdapter();
        adapter.addSection(venueModelListSection);
        adapter.registerItemBinders(new VenueBookingItemBinder(bookingViewModel, getViewLifecycleOwner()));

        return inflater.inflate(R.layout.book_venue_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dateLayout = view.findViewById(R.id.event_date_layout);
        dateInput = view.findViewById(R.id.event_date_input);
        recyclerView = view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setExpansionMode(Mode.SINGLE);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new VenueItemAnimator(0));
        recyclerView.addItemDecoration(adapter.getItemDecoration());

        bookingViewModel.getLoadedInstituteVenues().observe(getViewLifecycleOwner(), this::onVenuesLoaded);

        dateInput.setText(
                bookingViewModel.getZonedBookingDate().toLocalDate().format(DateTimeFormatter.ISO_DATE)
        );

        dateLayout.setEndIconOnClickListener(v -> {

            MaterialDatePicker.Builder<?> builder = createPostViewModel.setupDateSelectorBuilder();
            CalendarConstraints.Builder constraintsBuilder = createPostViewModel.setupConstraintsBuilder();
            builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
            builder.setTitleText("Choose A Date");

            try {
                builder.setCalendarConstraints(constraintsBuilder.build());
                MaterialDatePicker<?> picker = builder.build();

                picker.addOnPositiveButtonClickListener(selection -> {
                    bookingViewModel.setBookingDate(
                            Instant.ofEpochMilli((Long) selection)
                                    .atZone(ZoneId.systemDefault())
                                    .withZoneSameInstant(ZoneId.of("UTC"))
                                    .toLocalDateTime()
                    );
                    dateInput.setText(
                            Instant.ofEpochMilli((Long) selection).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE)
                    );
                    adapter.collapseAllItems();
                });

                picker.show(getChildFragmentManager(), picker.toString());
            }
            catch (Exception e){
                e.printStackTrace();
                Log.e("Venue", "Calendar builder error");
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onVenuesLoaded(List<VenueModel> venueModels) {
        if(venueModels != null && (venueModelListSection.size() == 0 || refreshVenues) ) {
            venueModelListSection.addAll(venueModels.subList(venueModelListSection.size(), venueModels.size()));
            refreshVenues = false;
        }
    }
}
