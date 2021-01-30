package com.omada.fastblog.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.omada.fastblog.utils.parser.FastBlogComponent;

import java.util.Map;

public class FastBlogTitleInput extends ConstraintLayout implements FastBlogComponent {

    public FastBlogTitleInput(@NonNull Context context) {
        super(context);
    }

    public FastBlogTitleInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FastBlogTitleInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FastBlogTitleInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
