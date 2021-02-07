package com.omada.junctionadmin.ui.article;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.omada.junctionadmin.data.models.external.ArticleModel;

public class ArticleDetailsEditFragment extends Fragment {

    public static ArticleDetailsEditFragment newInstance(ArticleModel model) {
        
        Bundle args = new Bundle();
        args.putParcelable("model", model);
        
        ArticleDetailsEditFragment fragment = new ArticleDetailsEditFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
