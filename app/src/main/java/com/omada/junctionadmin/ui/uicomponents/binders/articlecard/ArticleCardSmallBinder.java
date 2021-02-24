package com.omada.junctionadmin.ui.uicomponents.binders.articlecard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.ArticleModel;
import com.omada.junctionadmin.databinding.ArticleCardLargeLayoutBinding;
import com.omada.junctionadmin.databinding.ArticleCardSmallLayoutBinding;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;


public class ArticleCardSmallBinder extends ItemBinder<ArticleModel, ArticleCardSmallBinder.ArticleCardViewHolder> {


    private final FeedContentViewModel viewModel;

    public ArticleCardSmallBinder(FeedContentViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public ArticleCardViewHolder createViewHolder(ViewGroup parent) {
        ArticleCardSmallLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.article_card_small_layout, parent, false);
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

        ArticleCardSmallLayoutBinding binding;

        public ArticleCardViewHolder (ArticleCardSmallLayoutBinding containerBinding) {
            super(containerBinding.getRoot());
            binding = containerBinding;
        }

        public ArticleCardSmallLayoutBinding getBinding() {
            return binding;
        }
    }
}
