package com.omada.junctionadmin.ui.uicomponents.binders.institutefeed;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.OrganizationModel;
import com.omada.junctionadmin.databinding.OrganizationThumbnailLayoutBinding;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;

public class OrganizationThumbnailBinder extends ItemBinder<OrganizationModel, OrganizationThumbnailBinder.OrganizationThumbnailViewHolder> {

    FeedContentViewModel feedContentViewModel;

    public OrganizationThumbnailBinder(FeedContentViewModel viewModel){
        feedContentViewModel = viewModel;
    }

    @Override
    public OrganizationThumbnailViewHolder createViewHolder(ViewGroup parent) {
        OrganizationThumbnailLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.organization_thumbnail_layout, parent, false);
        return new OrganizationThumbnailViewHolder(binding);
    }

    @Override
    public void bindViewHolder(OrganizationThumbnailViewHolder holder, OrganizationModel item) {
        holder.binding.setOrganizationModel(item);
        holder.binding.setViewModel(feedContentViewModel);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof OrganizationModel;
    }

    public static class OrganizationThumbnailViewHolder extends ItemViewHolder<OrganizationModel>{

        private final OrganizationThumbnailLayoutBinding binding;

        public OrganizationThumbnailViewHolder(OrganizationThumbnailLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
