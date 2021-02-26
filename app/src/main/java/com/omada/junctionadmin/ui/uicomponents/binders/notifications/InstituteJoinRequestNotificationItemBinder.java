package com.omada.junctionadmin.ui.uicomponents.binders.notifications;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.databinding.InstituteJoinRequestItemLayoutBinding;
import com.omada.junctionadmin.ui.uicomponents.CustomBindings;
import com.omada.junctionadmin.viewmodels.InstituteViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class InstituteJoinRequestNotificationItemBinder extends ItemBinder<NotificationModel, InstituteJoinRequestNotificationItemBinder.NotificationItemViewHolder> {

    private final InstituteViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;

    public InstituteJoinRequestNotificationItemBinder(InstituteViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public NotificationItemViewHolder createViewHolder(ViewGroup parent) {
        InstituteJoinRequestItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.institute_join_request_item_layout, parent, false);
        return new NotificationItemViewHolder(binding);
    }

    @Override
    public void bindViewHolder(final NotificationItemViewHolder holder, NotificationModel item) {

        InstituteJoinRequestItemLayoutBinding binding = holder.getBinding();

        binding.setViewModel(viewModel);
        binding.acceptButton.setOnClickListener(v -> {
            if(!binding.acceptButton.isEnabled()) {
                return;
            }
            binding.declineButton.setEnabled(false);
            binding.acceptButton.setEnabled(false);
            viewModel.handleJoinRequest(item, true)
                    .observe(lifecycleOwner, booleanLiveEvent -> {
                        if (booleanLiveEvent == null) {
                            return;
                        }
                        Boolean result = booleanLiveEvent.getDataOnceAndReset();
                        if (Boolean.TRUE.equals(result)) {
                            holder.toggleItemSelection();
                        } else {
                            Toast.makeText(v.getContext(), "There was an error processing your request", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        binding.declineButton.setOnClickListener(v -> {
            if(!binding.declineButton.isEnabled()) {
                return;
            }
            binding.declineButton.setEnabled(false);
            binding.acceptButton.setEnabled(false);
            viewModel.handleJoinRequest(item, false)
                    .observe(lifecycleOwner, booleanLiveEvent -> {
                        if (booleanLiveEvent == null) {
                            return;
                        }
                        Boolean result = booleanLiveEvent.getDataOnceAndReset();
                        if (Boolean.TRUE.equals(result)) {
                            holder.toggleItemSelection();
                        } else {
                            Toast.makeText(v.getContext(), "There was an error processing your request", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        CustomBindings.loadImageUrl(
                binding.organizerProfileImage, (String) item.getData().get("profilePicture")
        );
        binding.titleText.setText((String) item.getData().get("name"));
        binding.detailsText.setText((String) item.getData().get("email"));
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof NotificationModel && ((NotificationModel) item).getNotificationType().equals("instituteJoinRequest");
    }

    public static class NotificationItemViewHolder extends ItemViewHolder<NotificationModel> {

        InstituteJoinRequestItemLayoutBinding binding;

        public NotificationItemViewHolder(InstituteJoinRequestItemLayoutBinding containerBinding) {
            super(containerBinding.getRoot());
            binding = containerBinding;
        }

        public InstituteJoinRequestItemLayoutBinding getBinding() {
            return binding;
        }
    }

}
