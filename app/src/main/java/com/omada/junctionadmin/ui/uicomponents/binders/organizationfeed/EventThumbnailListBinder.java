package com.omada.junctionadmin.ui.uicomponents.binders.organizationfeed;

import android.view.ViewGroup;

import com.omada.junctionadmin.data.models.external.EventModel;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import java.util.List;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class EventThumbnailListBinder extends ItemBinder<List<EventModel>, EventThumbnailListBinder.EventThumbnailListViewHolder> {


    public EventThumbnailListBinder(FeedContentViewModel viewModel) {
    }

    @Override
    public EventThumbnailListViewHolder createViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void bindViewHolder(final EventThumbnailListViewHolder holder, List<EventModel> item) {
    }

    @Override
    public boolean canBindData(Object item) {
        return false;
    }

    public static class EventThumbnailListViewHolder extends ItemViewHolder<List<EventModel>> {

        public EventThumbnailListViewHolder() {
            super(null);
        }
    }
}