package com.omada.junctionadmin.ui.uicomponents.binders.notifications;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.textview.MaterialTextView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class InstituteJoinResponseNotificationItemBinder extends ItemBinder<NotificationModel, InstituteJoinResponseNotificationItemBinder.NotificationItemViewHolder> {

    private final UserProfileViewModel viewModel;

    public InstituteJoinResponseNotificationItemBinder(LifecycleOwner lifecycleOwner, UserProfileViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public NotificationItemViewHolder createViewHolder(ViewGroup parent) {
        return new NotificationItemViewHolder(inflate(parent, R.layout.institute_message_item_layout));
    }

    @Override
    public void bindViewHolder(final NotificationItemViewHolder holder, NotificationModel item) {
        holder.bind(item);
        holder.getCloseButton().setOnClickListener(v -> {
            viewModel.handleJoinResponseNotification(item);
            holder.toggleItemSelection();
        });
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof NotificationModel && ((NotificationModel) item).getNotificationType().equals("instituteJoinResponse");
    }

    public static class NotificationItemViewHolder extends ItemViewHolder<NotificationModel> {

        private ConstraintLayout constraintLayout;
        private MaterialTextView titleText;
        private MaterialTextView messageText;
        private AppCompatImageButton closeButton;

        public NotificationItemViewHolder(View v) {
            super(v);
            closeButton = v.findViewById(R.id.close_button);
            titleText = v.findViewById(R.id.title_text);
            messageText = v.findViewById(R.id.message_text);
            constraintLayout = v.findViewById(R.id.constraint_layout);
        }

        public void bind(NotificationModel model) {

            Resources resources = constraintLayout.getResources();
            Resources.Theme theme = constraintLayout.getContext().getTheme();

            Boolean accepted = model.getData().get("response").equals("accepted");

            if (accepted) {
                constraintLayout.setBackgroundColor(resources.getColor(R.color.green, theme));
                titleText.setText("Your join request has been accepted");
                messageText.setText("You can now post events and reach the users of your requested institute");
            } else {
                constraintLayout.setBackgroundColor(resources.getColor(R.color.design_default_color_error, theme));
                titleText.setText("Your join request has been denied");
                messageText.setText("You cannot view your requested institute any longer");

            }
        }

        public AppCompatImageButton getCloseButton() {
            return closeButton;
        }
    }
}
