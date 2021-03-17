package com.omada.junctionadmin.ui.venue;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.ui.uicomponents.TimeDurationInputCreator;
import com.omada.junctionadmin.ui.uicomponents.animations.VenueItemAnimator;
import com.omada.junctionadmin.ui.uicomponents.binders.VenueSelectionItemBinder;
import com.omada.junctionadmin.viewmodels.BookingViewModel;
import com.omada.junctionadmin.viewmodels.CreatePostViewModel;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;
import mva3.adapter.util.Mode;
import mva3.adapter.util.OnSelectionChangedListener;
import mva3.adapter.util.PayloadProvider;

public class BookVenueFragment extends Fragment {

    private CreatePostViewModel createPostViewModel;
    private BookingViewModel bookingViewModel;
    private RecyclerView recyclerView;
    private TextInputLayout dateLayout;
    private TextInputEditText dateInput;

    private VenueModel lastSelectedVenueModel;
    private final ListSection<VenueModel> venueModelListSection = new ListSection<>();
    private MultiViewAdapter adapter;

    private boolean refreshVenues = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        bookingViewModel = viewModelProvider.get(BookingViewModel.class);
        createPostViewModel = viewModelProvider.get(CreatePostViewModel.class);

        // To keep track of previous state and show it to the user
        lastSelectedVenueModel = createPostViewModel.getEventCreator().getVenueModel();

        if (savedInstanceState == null
                && bookingViewModel.getZonedBookingDate() == null && (bookingViewModel.getLoadedInstituteVenues().getValue() == null
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

        adapter.registerItemBinders(new VenueSelectionItemBinder(createPostViewModel.getEventCreator().getVenueModel()));

        return inflater.inflate(R.layout.book_venue_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dateLayout = view.findViewById(R.id.event_date_layout);
        dateInput = view.findViewById(R.id.event_date_input);
        recyclerView = view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setSelectionMode(Mode.SINGLE);

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new VenueItemAnimator(0));
        recyclerView.addItemDecoration(adapter.getItemDecoration());

        if (bookingViewModel.getZonedBookingDate() != null
                && createPostViewModel.getEventCreator().startTime.getValue() != null
                && createPostViewModel.getEventCreator().endTime.getValue() != null) {

            dateInput.setText(
                    bookingViewModel.getZonedBookingDate().toLocalDate().format(DateTimeFormatter.ISO_DATE)
            );
            if (bookingViewModel.getLoadedInstituteVenues() != null) {
                onVenuesLoaded(bookingViewModel.getLoadedInstituteVenues().getValue());
            }
        }

        /*
         All the mental gymnastics with lastSelectedVenueModel is because the adapter does not allow toggling
         selection before the recycler view layout is done and there is no way to access the ViewHolder either,
         to select a specific item from the Section or adapter
        */
        venueModelListSection.setOnSelectionChangedListener((item, isSelected, selectedItems) -> {
            if (item.equals(lastSelectedVenueModel)) {
                lastSelectedVenueModel = null;
                createPostViewModel.getEventCreator().setVenueModel(null);
                venueModelListSection.clearSelections();
            } else if (isSelected) {
                createPostViewModel.getEventCreator().setVenueModel(item);
                if (lastSelectedVenueModel != null) {
                    lastSelectedVenueModel = null;
                    adapter.notifyDataSetChanged();
                }
            } else {
                createPostViewModel.getEventCreator().setVenueModel(null);
            }
            if (item != null && createPostViewModel.getEventCreator().getVenueModel() != null) {
                new Handler(Looper.getMainLooper()).postDelayed(
                        () -> requireActivity().onBackPressed(), 200
                );
            }
        });

        disableDateInputText(dateInput);

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
                    createTimeInputDialog();
                });

                picker.show(getChildFragmentManager(), picker.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Venue", "Calendar builder error");
            }
        });

    }

    private void disableDateInputText(TextInputEditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }


    public void createTimeInputDialog() {

        TimeDurationInputCreator creator = new TimeDurationInputCreator(requireContext());
        AlertDialog dialog = creator.initializeDefaultTextWatchers();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {

            LocalTime startTime = creator.getEnteredStartTime();
            LocalTime endTime = creator.getEnteredEndTime();

            ZonedDateTime startDate = null;
            ZonedDateTime endDate = null;

            if (startTime != null && endTime != null) {
                startDate = startTime.atDate(bookingViewModel.getZonedBookingDate().toLocalDate()).atZone(ZoneId.systemDefault());
                endDate = endTime.atDate(bookingViewModel.getZonedBookingDate().toLocalDate()).atZone(ZoneId.systemDefault());
            } else {
                return;
            }

            if (!startTime.isBefore(endTime)) {
                creator.setError("Invalid timing");
            } else if (startDate != null && endDate != null) {
                creator.setError(null);
                createPostViewModel.getEventCreator().startTime.setValue(startDate);
                createPostViewModel.getEventCreator().endTime.setValue(endDate);

                bookingViewModel
                        .getLoadedInstituteVenues()
                        .observe(getViewLifecycleOwner(), this::onVenuesLoaded);

                dialog.dismiss();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog1, which) -> {
            bookingViewModel.resetBookingDate();
            createPostViewModel.getEventCreator().startTime.postValue(null);
            createPostViewModel.getEventCreator().endTime.postValue(null);
            dateInput.setText(null);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onVenuesLoaded(List<VenueModel> venueModels) {
        if (venueModels != null && (venueModelListSection.size() == 0 || refreshVenues)) {
            venueModelListSection.set(venueModels);
            refreshVenues = false;
        }
    }
}
