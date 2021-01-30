package com.omada.fastblog.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.omada.fastblog.utils.parser.FastBlogComponent;

import java.util.Map;

public class FastBlogAuthorInput extends ConstraintLayout implements FastBlogComponent {

    public FastBlogAuthorInput(@NonNull Context context) {
        super(context);
    }

    public FastBlogAuthorInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FastBlogAuthorInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FastBlogAuthorInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
