package com.omada.junctionadmin.ui.uicomponents.binders.eventcard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.EventModel;
import com.omada.junctionadmin.databinding.EventCardSmallNoTitleLayoutBinding;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class EventCardSmallNoTitleBinder extends ItemBinder<EventModel, EventCardSmallNoTitleBinder.EventCardViewHolder> {

    private final FeedContentViewModel viewModel;

    public EventCardSmallNoTitleBinder(FeedContentViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public EventCardViewHolder createViewHolder(ViewGroup parent) {
        EventCardSmallNoTitleLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.event_card_small_no_title_layout, parent, false);
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

        EventCardSmallNoTitleLayoutBinding binding;

        public EventCardViewHolder (EventCardSmallNoTitleLayoutBinding containerBinding) {
            super(containerBinding.getRoot());
            binding = containerBinding;
        }

        public EventCardSmallNoTitleLayoutBinding getBinding() {
            return binding;
        }
    }

}
