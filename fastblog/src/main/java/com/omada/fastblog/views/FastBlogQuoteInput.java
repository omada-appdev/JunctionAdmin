package com.omada.fastblog.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.omada.fastblog.R;
import com.omada.fastblog.utils.parser.FastBlogComponent;

import java.util.Map;

public class FastBlogQuoteInput extends ConstraintLayout implements FastBlogComponent {

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

    private void initView() {
        inflate(getContext(), R.layout.fastblog_edit_quote_layout, this);
    }

    @Override
    public ComponentType getComponentType() {
        return null;
    }

    @Override
    public Map<String, Object> onSerialize() {
        return null;
    }

    @Override
    public void onParse(Map<String, Object> data) {

    }
}
