package com.omada.fastblog.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.omada.fastblog.utils.image.ImageDownloader;
import com.omada.fastblog.utils.image.ImageUploader;
import com.omada.fastblog.utils.parser.FastBlogComponent;

import java.util.HashMap;
import java.util.Map;

public class FastBlogImageInput extends ConstraintLayout implements FastBlogComponent {

    private String imagePath = "";
    private ImageUploader<String> imageUploader;
    private ImageDownloader<String> imageDownloader;

    public FastBlogImageInput(@NonNull Context context) {
        super(context);
    }

    public FastBlogImageInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FastBlogImageInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FastBlogImageInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageTaskHandlers(ImageUploader<String> imageUploader, ImageDownloader<String> imageDownloader){
        this.imageUploader = imageUploader;
        this.imageDownloader = imageDownloader;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.FAST_BLOG_IMAGE;
    }

    @Override
    public Map<String, Object> onSerialize() {

        Map<String, Object> serializedData = new HashMap<>();

        serializedData.put("imageUrl", imageUploader.uploadImage(imagePath));

        return serializedData;
    }

    @Override
    public void onParse(Map<String, Object> data) {
    }
}
