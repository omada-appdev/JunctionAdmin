package com.omada.junctionadmin.ui.uicomponents;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.omada.junctionadmin.utils.image.GlideApp;


public class CustomBindings {

    /*
    Loads gs ref directly. Caching is a problem when gsRef does not change but url does
    But load is faster because we are not downloading the http url everytime
     */
    @BindingAdapter({"remoteImageGs"})
    public static void loadImageGs(ImageView view, String gsUrl) {
        StorageReference gsRef;

        if (gsUrl == null || gsUrl.equals("")) {
            return;
        } else {
            gsRef = FirebaseStorage.getInstance().getReferenceFromUrl(gsUrl);
        }

        GlideApp.with(view.getContext())
                .load(gsRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);

    }

    /*
    Loads with http url. Use when gs url does not change but http url does. Otherwise there are problems
     with caching
    */
    @BindingAdapter({"remoteImageHttp"})
    public static void loadImageUrl(ImageView view, String httpUrl) {

        GlideApp.with(view.getContext())
                .load(httpUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }


    @BindingAdapter({"formattedText"})
    public static void setFormattedText(FormattedTextView textView, String text) {
        textView.setFormattedText(text);
    }

}
