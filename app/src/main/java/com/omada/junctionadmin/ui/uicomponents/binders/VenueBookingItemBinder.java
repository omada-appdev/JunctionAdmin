package com.omada.junctionadmin.ui.uicomponents.binders;

import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.viewmodels.BookingViewModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;

public class VenueBookingItemBinder extends ItemBinder<VenueModel, VenueBookingItemBinder.VenueBookingItemViewHolder> {

    private final BookingViewModel bookingViewModel;

    public VenueBookingItemBinder(BookingViewModel bookingViewModel){
        this.bookingViewModel = bookingViewModel;
    }
    
    @Override
    public VenueBookingItemViewHolder createViewHolder(ViewGroup parent) {
        View view = inflate(parent, R.layout.book_venue_item_layout);
        return new VenueBookingItemViewHolder(view, bookingViewModel);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof VenueModel;
    }

    @Override
    public void bindViewHolder(VenueBookingItemViewHolder holder, VenueModel item) {

    }

    public static class VenueBookingItemViewHolder extends ItemViewHolder<VenueModel> {

        private final BookingViewModel bookingViewModel;

        private final MaterialTextView venueName;
        private final MaterialTextView venueAddress;
        private final ToggleButton expandButton;

        public VenueBookingItemViewHolder(View itemView, BookingViewModel bookingViewModel) {
            super(itemView);
            this.bookingViewModel = bookingViewModel;
            venueName = itemView.findViewById(R.id.name_text);
            venueAddress = itemView.findViewById(R.id.address_text);
            expandButton = itemView.findViewById(R.id.expand_button);
        }

        public void bindViewHolder(VenueModel model) {

            if(model == null) {
                venueName.setText("null");
                venueAddress.setText("null");
            }
            venueName.setText(model.getName());
            venueAddress.setText(model.getAddress());

            expandButton.setOnClickListener(v -> {
                if (expandButton.isChecked()) {
                    expandButton.setBackgroundDrawable(
                            ContextCompat.getDrawable(expandButton.getContext(), R.drawable.ic_downwards_arrow_key));
                    // TODO get bookings and display
                    bookingViewModel.getBookings(model.getId());
                }
                else {
                    expandButton.setBackgroundDrawable(
                            ContextCompat.getDrawable(expandButton.getContext(), R.drawable.ic_right_arrow_key));
                }
                toggleItemExpansion();
            });
        }
    }
}
