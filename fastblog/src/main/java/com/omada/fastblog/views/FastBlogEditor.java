package com.omada.fastblog.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.omada.fastblog.R;
import com.omada.fastblog.utils.image.ImageDownloader;
import com.omada.fastblog.utils.image.ImageUploader;
import com.omada.fastblog.utils.parser.FastBlogComponent;
import com.omada.fastblog.utils.text.StringUtilities;

import java.util.HashMap;
import java.util.Map;


public class FastBlogEditor extends ConstraintLayout {

    private LinearLayout sectionsLayout;

    private ImageDownloader<String> imageDownloader;
    private ImageUploader<String> imageUploader;

    public FastBlogEditor(@NonNull Context context) {
        super(context);
        initView();
    }

    public FastBlogEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FastBlogEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public FastBlogEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View v = inflate(getContext(), R.layout.fastblog_editor_layout, this);
        sectionsLayout = v.findViewById(R.id.sections_layout);
    }

    private Map<String, Object> serialize() {

        Map<String, Object> serializedFastBlog = new HashMap<>();

        int bound = sectionsLayout.getChildCount();
        int componentCounter = 0;

        for (int i = 0; i < bound; i++) {

            View child = sectionsLayout.getChildAt(i);
            if(child instanceof FastBlogComponent){

                Map<String, Object> serializedComponent = new HashMap<>();

                Map<String, Object> serializedComponentData = ((FastBlogComponent) child).onSerialize();

                componentCounter++;
                serializedComponent.put("position", componentCounter);
                serializedComponent.put("data", serializedComponentData);
                serializedComponent.put("type", ((FastBlogComponent)child).getComponentType().toString());

                serializedFastBlog.put(
                        StringUtilities
                                .randomStringGenerator(8, true, true, ""),
                        serializedComponent
                );
            }

        }

        return serializedFastBlog;
    }

    public void setImageUploader(ImageUploader<String> imageUploader) {
        this.imageUploader = imageUploader;
    }

    public void setImageDownloader(ImageDownloader<String> imageDownloader) {
        this.imageDownloader = imageDownloader;
    }
}
