package com.omada.junctionadmin.ui.uicomponents.binders;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
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

public class VenueSelectionItemBinder extends ItemBinder<VenueModel, VenueSelectionItemBinder.VenueSelectionItemViewHolder> {

    VenueModel defaultSelectedVenue;

    public VenueSelectionItemBinder(VenueModel defaultSelectedVenue) {
        this.defaultSelectedVenue = defaultSelectedVenue;
    }

    @Override
    public VenueSelectionItemViewHolder createViewHolder(ViewGroup parent) {
        parent.setBackgroundColor(parent.getContext().getResources().getColor(R.color.white, parent.getContext().getTheme()));
        View view = inflate(parent, R.layout.book_venue_item_layout);
        return new VenueSelectionItemViewHolder(view);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof VenueModel;
    }

    @Override
    public void bindViewHolder(VenueSelectionItemViewHolder holder, VenueModel item) {
        Log.e("Venue", "bindViewHolder for" + item.getName());
        if (item.equals(defaultSelectedVenue)) {
            defaultSelectedVenue = null;
            holder.bindViewHolder(item, true);
        } else {
            holder.bindViewHolder(item);
        }
    }

    public static class VenueSelectionItemViewHolder extends ItemViewHolder<VenueModel> {

        private final ConstraintLayout layout;
        private final MaterialTextView venueName;
        private final MaterialTextView venueAddress;
        private final ImageButton selectButton;

        public VenueSelectionItemViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.constraint_layout);
            venueName = itemView.findViewById(R.id.name_text);
            venueAddress = itemView.findViewById(R.id.address_text);
            selectButton = itemView.findViewById(R.id.expand_button);
        }

        public void bindViewHolder(VenueModel model, boolean defaultState) {
            bindViewHolder(model);
            selectButton.setImageResource(defaultState ? R.drawable.selected_badge : R.drawable.deselected_badge);
        }

        public void bindViewHolder(VenueModel model) {

            if (model == null) {
                return;
            }
            venueName.setText(model.getName());
            venueAddress.setText(model.getAddress());

            selectButton.setImageResource(isItemSelected() ? R.drawable.selected_badge : R.drawable.deselected_badge);

            // null listener not working
            selectButton.setOnClickListener(v -> {
                toggleItemSelection();
            });

            layout.setOnClickListener(v -> {
                toggleItemSelection();
            });
        }
    }
}
