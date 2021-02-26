package com.omada.junctionadmin.ui.uicomponents;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.omada.junctionadmin.R;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeDurationInputCreator {

    AlertDialog createdDialog;

    MaterialTextView errorTextView;

    TextInputEditText startHourText1;
    TextInputEditText startHourText2;
    TextInputEditText startMinuteText1;
    TextInputEditText startMinuteText2;

    TextInputEditText endHourText1;
    TextInputEditText endHourText2;
    TextInputEditText endMinuteText1;
    TextInputEditText endMinuteText2;

    public TimeDurationInputCreator(Context context) {

        createdDialog = new MaterialAlertDialogBuilder(context)
                .setCancelable(false)
                .setTitle("Enter your timings")
                .setMessage("Please ensure 24 hour format")
                .setView(R.layout.time_duration_picker_layout)
                .setPositiveButton("Ok", (dialog, which) -> {

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .create();

        createdDialog.show();

        errorTextView = createdDialog.findViewById(R.id.alert_error_text);

        startHourText1 = createdDialog.findViewById(R.id.hour_text_1_start);
        startHourText2 = createdDialog.findViewById(R.id.hour_text_2_start);
        startMinuteText1 = createdDialog.findViewById(R.id.minute_text_1_start);
        startMinuteText2 = createdDialog.findViewById(R.id.minute_text_2_start);

        endHourText1 = createdDialog.findViewById(R.id.hour_text_1_end);
        endHourText2 = createdDialog.findViewById(R.id.hour_text_2_end);
        endMinuteText1 = createdDialog.findViewById(R.id.minute_text_1_end);
        endMinuteText2 = createdDialog.findViewById(R.id.minute_text_2_end);

        startHourText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        startHourText2.setInputType(InputType.TYPE_CLASS_NUMBER);
        startMinuteText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        startMinuteText2.setInputType(InputType.TYPE_CLASS_NUMBER);

        endHourText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        endHourText2.setInputType(InputType.TYPE_CLASS_NUMBER);
        endMinuteText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        endMinuteText2.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public AlertDialog getCreatedDialog() {
        return createdDialog;
    }

    public AlertDialog initializeDefaultKeyListeners() {

        return createdDialog;
    }

    public AlertDialog initializeDefaultTextWatchers() {

        startHourText1.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    startHourText1.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-2]")) {
                    startHourText1.setText("2");
                } else {
                    startHourText2.requestFocus();
                }
            }
        });

        startHourText2.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    startHourText2.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-9]")) {
                    startHourText2.setText("0");
                } else if (startHourText1.getText().toString().equals("2")
                        && !s.toString().matches("[0-3]")) {

                    startHourText2.setText("3");
                } else {
                    startMinuteText1.requestFocus();
                }
            }
        });

        startMinuteText1.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    startMinuteText1.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-5]")) {
                    startMinuteText1.setText("5");
                } else {
                    startMinuteText2.requestFocus();
                }
            }
        });

        startMinuteText2.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    startMinuteText2.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-9]")) {
                    startMinuteText2.setText("0");
                } else {
                    endHourText1.requestFocus();
                }
            }
        });

        endHourText1.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    endHourText1.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-2]")) {
                    endHourText1.setText("2");
                } else {
                    endHourText2.requestFocus();
                }
            }
        });

        endHourText2.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    endHourText2.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-9]")) {
                    endHourText2.setText("");
                } else if (endHourText1.getText().toString().equals("2")
                        && !s.toString().matches("[0-3]")) {

                    endHourText2.setText("3");
                } else {
                    endMinuteText1.requestFocus();
                }
            }
        });

        endMinuteText1.addTextChangedListener(new TextWatcher() {
            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    endMinuteText1.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-5]")) {
                    endMinuteText1.setText("5");
                } else {
                    endMinuteText2.requestFocus();
                }
            }
        });

        endMinuteText2.addTextChangedListener(new TextWatcher() {

            int editedAt = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editedAt = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString())) {
                    return;
                } else if (s.length() > 1) {
                    endMinuteText2.setText(Character.toString(s.charAt(editedAt)));
                } else if (!s.toString().matches("[0-9]")) {
                    endMinuteText2.setText("0");
                }
            }
        });

        return this.createdDialog;
    }

    public void setError(@Nullable String errorText) {
        if (errorText == null || errorText.equals("")) {
            errorTextView.setText(null);
        } else {
            errorTextView.setText(errorText);
        }
    }

    public LocalTime getEnteredStartTime() {
        String h1, h2, m1, m2;

        h1 = startHourText1.getText().toString();
        h2 = startHourText2.getText().toString();
        m1 = startMinuteText1.getText().toString();
        m2 = startMinuteText2.getText().toString();

        boolean invalid1 = h1 == null || h1.equals("");
        boolean invalid2 = h2 == null || h2.equals("") || invalid1;
        boolean invalid3 = m1 == null || m1.equals("") || invalid2;
        boolean invalid4 = m2 == null || m2.equals("") || invalid3;

        if (invalid4) {
            setError("Please enter all values");
            return null;
        }

        int hours = Integer.parseInt(h1 + h2);
        int minutes = Integer.parseInt(m1 + m2);

        return LocalTime.of(hours, minutes);
    }

    public LocalTime getEnteredEndTime() {
        String h1, h2, m1, m2;

        h1 = endHourText1.getText().toString();
        h2 = endHourText2.getText().toString();
        m1 = endMinuteText1.getText().toString();
        m2 = endMinuteText2.getText().toString();

        boolean invalid1 = h1 == null || h1.equals("");
        boolean invalid2 = h2 == null || h2.equals("") || invalid1;
        boolean invalid3 = m1 == null || m1.equals("") || invalid2;
        boolean invalid4 = m2 == null || m2.equals("") || invalid3;

        if (invalid4) {
            setError("Please enter all values");
            return null;
        }

        int hours = Integer.parseInt(h1 + h2);
        int minutes = Integer.parseInt(m1 + m2);

        return LocalTime.of(hours, minutes);
    }

    public TextInputEditText getStartHourText1() {
        return startHourText1;
    }

    public TextInputEditText getStartHourText2() {
        return startHourText2;
    }

    public TextInputEditText getStartMinuteText1() {
        return startMinuteText1;
    }

    public TextInputEditText getStartMinuteText2() {
        return startMinuteText2;
    }

    public TextInputEditText getEndHourText1() {
        return endHourText1;
    }

    public TextInputEditText getEndHourText2() {
        return endHourText2;
    }

    public TextInputEditText getEndMinuteText1() {
        return endMinuteText1;
    }

    public TextInputEditText getEndMinuteText2() {
        return endMinuteText2;
    }
}
