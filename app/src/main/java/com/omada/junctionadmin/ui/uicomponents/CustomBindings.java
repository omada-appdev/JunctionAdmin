package com.omada.junctionadmin.ui.uicomponents;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.omada.junctionadmin.utils.image.GlideApp;


public class CustomBindings {

    @BindingAdapter({"remoteImageUrl"})
    public static void loadImage(ImageView view, String remoteUrl) {
        StorageReference gsRef;

        if(remoteUrl == null || remoteUrl.equals("")){
            return;
        }
        else{
            gsRef = FirebaseStorage.getInstance().getReferenceFromUrl(remoteUrl);
        }

        GlideApp.with(view.getContext())
                .load(gsRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);

    }

    @BindingAdapter({"formattedText"})
    public static void setFormattedText(FormattedTextView textView, String text){
        textView.setFormattedText(text);
    }

}
