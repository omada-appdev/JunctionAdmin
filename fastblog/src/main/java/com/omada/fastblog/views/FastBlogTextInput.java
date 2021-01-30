package com.omada.fastblog.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.omada.fastblog.R;
import com.omada.fastblog.utils.parser.FastBlogComponent;
import com.omada.fastblog.utils.text.FastBlogSpanManager;

import java.util.HashMap;
import java.util.Map;

public class FastBlogTextInput extends ConstraintLayout implements FastBlogComponent {

    private TextInputEditText textInput;

    public FastBlogTextInput(@NonNull Context context) {
        super(context);
        initView();
    }

    public FastBlogTextInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FastBlogTextInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.fastblog_edit_text_layout, this);
        this.textInput = findViewById(R.id.text_input);

        ((MaterialButton)findViewById(R.id.bold)).setOnClickListener(v -> {

            Spannable spannable = textInput.getEditableText();
            int start = textInput.getSelectionStart();
            int end = textInput.getSelectionEnd();

            boolean alreadySpanned = false;

            final Object[] spans = textInput.getText().getSpans(start, end, Object.class);
            for (final Object span : spans) {
                if (span instanceof StyleSpan && ((StyleSpan)span).getStyle() == Typeface.BOLD) {

                    if (spannable.getSpanStart(span) > start && spannable.getSpanEnd(span) < end) {
                        continue;
                    }
                    FastBlogSpanManager.removeStyle(spannable, start, end, Typeface.BOLD);
                    alreadySpanned = true;
                }
            }

            if(!alreadySpanned) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        });

        ((MaterialButton)findViewById(R.id.italic)).setOnClickListener(v -> {

            Spannable spannable = textInput.getEditableText();
            int start = textInput.getSelectionStart();
            int end = textInput.getSelectionEnd();

            boolean alreadySpanned = false;

            final Object[] spans = textInput.getText().getSpans(start, end, Object.class);
            for (final Object span : spans) {
                if (span instanceof StyleSpan && ((StyleSpan)span).getStyle() == Typeface.ITALIC) {

                    if (spannable.getSpanStart(span) > start && spannable.getSpanEnd(span) < end) {
                        continue;
                    }
                    FastBlogSpanManager.removeStyle(spannable, start, end, Typeface.ITALIC);
                    alreadySpanned = true;
                }
            }

            if(!alreadySpanned) {
                spannable.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        });

        ((MaterialButton)findViewById(R.id.underline)).setOnClickListener(v -> {

            Spannable spannable = textInput.getEditableText();
            int start = textInput.getSelectionStart();
            int end = textInput.getSelectionEnd();

            if(start == end) return;

            boolean alreadySpanned = false;

            final Object[] spans = textInput.getText().getSpans(start, end, UnderlineSpan.class);
            for (final Object span : spans) {
                if (span instanceof UnderlineSpan) {

                    if (spannable.getSpanStart(span) > start && spannable.getSpanEnd(span) < end) {
                        continue;
                    }
                    FastBlogSpanManager.removeOne(spannable, start, end, UnderlineSpan.class);
                    alreadySpanned = true;
                }
            }

            if(!alreadySpanned) {
                spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        });

        ((MaterialButton)findViewById(R.id.reorder_up)).setOnClickListener(v -> {
            ((MaterialTextView)findViewById(R.id.serialized_text)).setText(onSerialize().toString());
        });

        ((MaterialButton)findViewById(R.id.reorder_down)).setOnClickListener(v -> {

        });
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.FAST_BLOG_TEXT;
    }

    @Override
    public Map<String, Object> onSerialize() {
        Map<String, Object> serializedData = new HashMap<>();

        serializedData.put("text", Html.toHtml(textInput.getText()));

        Log.e("Serial", (String) serializedData.get("text"));

        return serializedData;
    }

    @Override
    public void onParse(Map<String, Object> data) {
    }
}
