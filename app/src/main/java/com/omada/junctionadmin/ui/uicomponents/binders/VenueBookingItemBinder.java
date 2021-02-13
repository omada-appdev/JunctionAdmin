package com.omada.junctionadmin.ui.uicomponents.binders;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.textview.MaterialTextView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.utils.ColorUtilities;
import com.omada.junctionadmin.utils.TransformUtilities;
import com.omada.junctionadmin.viewmodels.BookingViewModel;

import java.time.ZonedDateTime;
import java.util.List;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;

public class VenueBookingItemBinder extends ItemBinder<VenueModel, VenueBookingItemBinder.VenueBookingItemViewHolder> {

    private final BookingViewModel bookingViewModel;
    private final LifecycleOwner lifecycleOwner;

    public VenueBookingItemBinder(BookingViewModel bookingViewModel, LifecycleOwner lifecycleOwner){
        this.bookingViewModel = bookingViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }
    
    @Override
    public VenueBookingItemViewHolder createViewHolder(ViewGroup parent) {
        parent.setBackgroundColor(parent.getContext().getResources().getColor(R.color.white, parent.getContext().getTheme()));
        View view = inflate(parent, R.layout.book_venue_item_layout);
        return new VenueBookingItemViewHolder(view, bookingViewModel, lifecycleOwner);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof VenueModel;
    }

    @Override
    public void bindViewHolder(VenueBookingItemViewHolder holder, VenueModel item) {
        holder.bindViewHolder(item);
    }

    public static class VenueBookingItemViewHolder extends ItemViewHolder<VenueModel> {

        private final BookingViewModel bookingViewModel;
        private final LifecycleOwner lifecycleOwner;

        private final MaterialTextView venueName;
        private final MaterialTextView venueAddress;
        private final ImageButton expandButton;

        private final LinearLayout bookingsLayout;

        private boolean handledBookingsEvent = false;

        public VenueBookingItemViewHolder(View itemView, BookingViewModel bookingViewModel, LifecycleOwner lifecycleOwner) {
            super(itemView);
            this.bookingViewModel = bookingViewModel;
            this.lifecycleOwner = lifecycleOwner;

            venueName = itemView.findViewById(R.id.name_text);
            venueAddress = itemView.findViewById(R.id.address_text);
            expandButton = itemView.findViewById(R.id.expand_button);
            bookingsLayout = itemView.findViewById(R.id.bookings_layout);
        }

        public void bindViewHolder(VenueModel model) {

            handledBookingsEvent = false;

            if(model == null) {
                venueName.setText("null");
                venueAddress.setText("null");
            }
            venueName.setText(model.getName());
            venueAddress.setText(model.getAddress());

            bookingsLayout.setVisibility(isItemExpanded() ? View.VISIBLE : View.GONE);
            expandButton.setImageResource(isItemExpanded() ? R.drawable.ic_downwards_arrow_key : R.drawable.ic_right_arrow_key);

            expandButton.setOnClickListener(v -> {
                if(!isItemExpanded()) {
                    bookingsLayout.removeAllViews();
                    bookingViewModel.getBookings(model.getId());
                }
                toggleItemExpansion();
            });

            // Don't want to load all at once. Only on demand or if already cached
            if(isItemExpanded() || bookingViewModel.checkIfBookingCached(model.getId())){
                observeBookings(model);
            }
        }

        private void observeBookings(VenueModel model) {

            LiveData<List<Pair<ZonedDateTime, ZonedDateTime>>> liveData =
                    bookingViewModel.getBookings(model.getId());

            liveData.observe(lifecycleOwner, new Observer<List<Pair<ZonedDateTime, ZonedDateTime>>>() {
                @Override
                public void onChanged(List<Pair<ZonedDateTime, ZonedDateTime>> pairs) {
                    if(handledBookingsEvent) {
                        liveData.removeObserver(this);
                        return;
                    }
                    Log.e("Bookings", "Collected : "+pairs.size()+" bookings");
                    handledBookingsEvent = true;

                    LayoutInflater inflater = LayoutInflater.from(bookingsLayout.getContext());
                    for (Pair<ZonedDateTime, ZonedDateTime> pair : pairs) {
                        MaterialTextView textView = (MaterialTextView) inflater.inflate(R.layout.booking_layout, bookingsLayout, false);
                        textView.setText(
                                String.format(
                                        "%s   %s",
                                        TransformUtilities.convertSystemZoneDateTimeToHHMM(pair.first),
                                        TransformUtilities.convertSystemZoneDateTimeToHHMM(pair.second)
                                )
                        );
                        textView.setBackgroundColor((int) ColorUtilities.randomColor());
                        bookingsLayout.addView(textView);
                    }

                    liveData.removeObserver(this);
                }
            });
        }


    }
}
