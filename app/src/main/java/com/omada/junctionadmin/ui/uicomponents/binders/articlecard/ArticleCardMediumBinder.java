package com.omada.junctionadmin.ui.uicomponents.binders.articlecard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.databinding.ArticleCardLargeLayoutBinding;
import com.omada.junctionadmin.databinding.ArticleCardMediumLayoutBinding;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class ArticleCardMediumBinder extends ItemBinder<ArticleModel, ArticleCardMediumBinder.ArticleCardViewHolder> {


    private final FeedContentViewModel viewModel;

    public ArticleCardMediumBinder(FeedContentViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public ArticleCardViewHolder createViewHolder(ViewGroup parent) {
        ArticleCardMediumLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.article_card_medium_layout, parent, false);
        return new ArticleCardViewHolder(binding);
    }

    @Override
    public void bindViewHolder(final ArticleCardViewHolder holder, ArticleModel item) {
        holder.binding.setArticleModel(item);
        holder.binding.setViewModel(viewModel);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof ArticleModel;
    }

    public static class ArticleCardViewHolder extends ItemViewHolder<ArticleModel>{

        ArticleCardMediumLayoutBinding binding;

        public ArticleCardViewHolder (ArticleCardMediumLayoutBinding containerBinding) {
            super(containerBinding.getRoot());
            binding = containerBinding;
        }

        public ArticleCardMediumLayoutBinding getBinding() {
            return binding;
        }
    }
}
