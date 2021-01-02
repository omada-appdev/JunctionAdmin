package com.omada.junctionadmin.ui.uicomponents.binders.institutefeed;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.ShowcaseModel;
import com.omada.junctionadmin.ui.uicomponents.binders.organizationfeed.OrganizationShowcaseThumbnailBinder;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;
import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;

public class InstituteShowcaseThumbnailListBinder extends ItemBinder<ListSection<ShowcaseModel>, InstituteShowcaseThumbnailListBinder.ShowcaseThumbnailListViewHolder> {

    private final MultiViewAdapter adapter = new MultiViewAdapter();
    private boolean addedSection = false;


    public InstituteShowcaseThumbnailListBinder(FeedContentViewModel viewModel){
        adapter.registerItemBinders(new OrganizationShowcaseThumbnailBinder(viewModel));
    }

    @Override
    public ShowcaseThumbnailListViewHolder createViewHolder(ViewGroup parent) {
        View view = inflate(parent, R.layout.organization_profile_showcases_layout);
        return new ShowcaseThumbnailListViewHolder(view);
    }

    @Override
    public void bindViewHolder(ShowcaseThumbnailListViewHolder holder, ListSection<ShowcaseModel> item) {

        holder.recyclerView.setLayoutManager(
                new GridLayoutManager(holder.recyclerView.getContext(), 3, RecyclerView.HORIZONTAL, false)
        );

        if(!addedSection) {
            adapter.addSection(item);
            addedSection = true;
        }
        holder.recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof ListSection && ((ListSection<?>) item).get(0) instanceof ShowcaseModel;
    }

    public static class ShowcaseThumbnailListViewHolder extends ItemViewHolder<ListSection<ShowcaseModel>> {

        private final RecyclerView recyclerView;

        public ShowcaseThumbnailListViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recycler_view);
        }
    }
}