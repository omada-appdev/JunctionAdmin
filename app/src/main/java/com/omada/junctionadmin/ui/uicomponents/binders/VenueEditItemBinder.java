package com.omada.junctionadmin.ui.uicomponents.binders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.google.android.material.textview.MaterialTextView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.VenueModel;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;

public class VenueEditItemBinder extends ItemBinder<VenueModel, VenueEditItemBinder.VenueBookingItemViewHolder> {

    InstituteViewModel instituteViewModel;

    public VenueEditItemBinder(InstituteViewModel instituteViewModel) {
        this.instituteViewModel = instituteViewModel;
    }

    @Override
    public VenueBookingItemViewHolder createViewHolder(ViewGroup parent) {
        parent.setBackgroundColor(parent.getContext().getResources().getColor(R.color.white, parent.getContext().getTheme()));
        View view = inflate(parent, R.layout.venue_item_layout);
        return new VenueBookingItemViewHolder(view);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof VenueModel;
    }

    @Override
    public void bindViewHolder(VenueBookingItemViewHolder holder, VenueModel item) {
        holder.bindViewHolder(item);
        holder.removeButton.setOnClickListener(v -> {
            if (holder.removeButton.isChecked()) {
                instituteViewModel.getInstituteVenuesUpdater().remove(item);
            } else {
                instituteViewModel.getInstituteVenuesUpdater().undoRemove(item);
            }
        });
    }

    public static class VenueBookingItemViewHolder extends ItemViewHolder<VenueModel> {

        private MaterialTextView venueName;
        private MaterialTextView venueAddress;
        private ToggleButton removeButton;

        public VenueBookingItemViewHolder(View itemView) {
            super(itemView);
            venueName = itemView.findViewById(R.id.name_text);
            venueAddress = itemView.findViewById(R.id.address_text);
            removeButton = itemView.findViewById(R.id.remove_button);
        }

        public void bindViewHolder(VenueModel model) {
            venueName.setText(model.getName());
            venueAddress.setText(model.getAddress());
        }

    }
}
