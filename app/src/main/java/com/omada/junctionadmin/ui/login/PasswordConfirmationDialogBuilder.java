package com.omada.junctionadmin.ui.login;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;


public class PasswordConfirmationDialogBuilder {

    public interface ConfirmationClickListener {
        // returns whether to close or not
        boolean onClick(View v);
    }

    private Context context;

    private ConfirmationClickListener positiveButtonListener;
    private ConfirmationClickListener negativeButtonListener;
    private View view;

    public PasswordConfirmationDialogBuilder(Context context) {
    }

    public PasswordConfirmationDialogBuilder withView(View v) {
        view = v;
        return this;
    }

    public PasswordConfirmationDialogBuilder setPositiveButtonListener(ConfirmationClickListener listener) {
        positiveButtonListener = listener;
        return this;
    }

    public PasswordConfirmationDialogBuilder setNegativeButtonListener(ConfirmationClickListener listener) {
        negativeButtonListener = listener;
        return this;
    }

    public AlertDialog build() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setMessage("Please confirm your password");

        if (view == null) {
            view = new TextInputEditText(context);
        }
        builder.setView(view);

        if(positiveButtonListener != null) {
            builder.setPositiveButton("Done", (dialog, which) -> {
                boolean shouldClose = positiveButtonListener.onClick(view);
                if (shouldClose) {
                    dialog.dismiss();
                }
            });
        }
        if(negativeButtonListener != null) {
            builder.setNegativeButton("Done", (dialog, which) -> {
                boolean shouldClose = negativeButtonListener.onClick(view);
                if (shouldClose) {
                    dialog.dismiss();
                }
            });
        }
        return builder.create();
    }

}