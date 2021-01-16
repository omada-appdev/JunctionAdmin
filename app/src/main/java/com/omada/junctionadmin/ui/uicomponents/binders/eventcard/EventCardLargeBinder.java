package com.omada.junctionadmin.ui.uicomponents.binders.eventcard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.databinding.EventCardLargeLayoutBinding;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class EventCardLargeBinder extends ItemBinder<EventModel, EventCardLargeBinder.EventCardViewHolder> {

    private final FeedContentViewModel viewModel;

    public EventCardLargeBinder(FeedContentViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public EventCardViewHolder createViewHolder(ViewGroup parent) {
        EventCardLargeLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.event_card_large_layout, parent, false);
        return new EventCardViewHolder(binding);
    }

    @Override
    public void bindViewHolder(final EventCardViewHolder holder, EventModel item) {

        holder.getBinding().setEventModel(item);
        holder.getBinding().setViewModel(viewModel);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof EventModel;
    }

    public static class EventCardViewHolder extends ItemViewHolder<EventModel>{

        EventCardLargeLayoutBinding binding;

        public EventCardViewHolder (EventCardLargeLayoutBinding containerBinding) {
            super(containerBinding.getRoot());
            binding = containerBinding;
        }

        public EventCardLargeLayoutBinding getBinding() {
            return binding;
        }
    }

}
