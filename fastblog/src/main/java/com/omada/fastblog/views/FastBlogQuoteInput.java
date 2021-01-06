package com.omada.fastblog.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.omada.fastblog.R;

public class FastBlogQuoteInput extends ConstraintLayout {

    public FastBlogQuoteInput(@NonNull Context context) {
        super(context);
        initView();
    }

    public FastBlogQuoteInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FastBlogQuoteInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        inflate(getContext(), R.layout.fastblog_edit_quote_layout, this);
    }

}
