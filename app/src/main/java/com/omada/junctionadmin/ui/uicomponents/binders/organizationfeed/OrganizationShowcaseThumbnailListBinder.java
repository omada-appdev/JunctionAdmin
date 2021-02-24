package com.omada.junctionadmin.ui.uicomponents.binders.organizationfeed;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;

import mva3.adapter.ItemBinder;
import mva3.adapter.ItemViewHolder;
import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;

public class OrganizationShowcaseThumbnailListBinder extends ItemBinder<ListSection<ShowcaseModel>, OrganizationShowcaseThumbnailListBinder.ShowcaseThumbnailListViewHolder> {

    MultiViewAdapter adapter = new MultiViewAdapter();
    private boolean addedSection = false;


    public OrganizationShowcaseThumbnailListBinder(FeedContentViewModel viewModel){
        adapter.registerItemBinders(new OrganizationShowcaseThumbnailBinder(viewModel));
    }

    @Override
    public ShowcaseThumbnailListViewHolder createViewHolder(ViewGroup parent) {
        View view = inflate(parent, R.layout.organization_profile_showcases_layout);
        return new ShowcaseThumbnailListViewHolder(view);
    }

    @Override
    public void bindViewHolder(ShowcaseThumbnailListViewHolder holder, ListSection<ShowcaseModel> item) {

        Log.e("Showcases", "bindViewHolder");
        holder.recyclerView.setLayoutManager(
                new LinearLayoutManager(holder.recyclerView.getContext(), RecyclerView.HORIZONTAL, false)
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