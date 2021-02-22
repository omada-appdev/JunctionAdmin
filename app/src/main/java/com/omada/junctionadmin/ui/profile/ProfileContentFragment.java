package com.omada.junctionadmin.ui.profile;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.data.models.external.BaseModel;
import com.omada.junctionadmin.data.models.external.NotificationModel;
import com.omada.junctionadmin.data.models.external.PostModel;
import com.omada.junctionadmin.data.models.external.ShowcaseModel;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardLargeBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.articlecard.ArticleCardMediumNoTitleBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.eventcard.EventCardMediumNoTitleBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.misc.LargeBoldHeaderBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.notifications.InstituteJoinResponseNotificationItemBinder;
import com.omada.junctionadmin.ui.uicomponents.binders.organizationfeed.OrganizationShowcaseThumbnailListBinder;
import com.omada.junctionadmin.ui.uicomponents.models.LargeBoldHeaderModel;
import com.omada.junctionadmin.viewmodels.FeedContentViewModel;
import com.omada.junctionadmin.viewmodels.UserProfileViewModel;

import java.util.List;

import mva3.adapter.HeaderSection;
import mva3.adapter.ItemSection;
import mva3.adapter.ListSection;
import mva3.adapter.MultiViewAdapter;
import mva3.adapter.util.Mode;
import mva3.adapter.util.OnSelectionChangedListener;

public class ProfileContentFragment extends Fragment {

    private final MultiViewAdapter adapter = new MultiViewAdapter();
    private final ListSection<BaseModel> highlightListSection = new ListSection<>();
    private final ItemSection<ListSection<ShowcaseModel>> showcaseSection = new ItemSection<>();

    private ListSection<NotificationModel> notificationListSection;
    private HeaderSection<LargeBoldHeaderModel> showcaseHeaderSection;
    private HeaderSection<LargeBoldHeaderModel> highlightHeaderSection;

    private RecyclerView recyclerView;

    private boolean refreshHighlights = true;
    private boolean refreshShowcases = true;
    private boolean refreshNotifications = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            refreshHighlights = true;
            refreshShowcases = true;
            refreshNotifications = true;
        }

        notificationListSection = new ListSection<>();
        notificationListSection.setSelectionMode(Mode.SINGLE);

        ListSection<ShowcaseModel> showcaseThumbnailListSection = new ListSection<>();
        showcaseThumbnailListSection.add(new ShowcaseModel());
        showcaseSection.setItem(showcaseThumbnailListSection);

        showcaseHeaderSection = new HeaderSection<>(new LargeBoldHeaderModel("Showcase"));
        showcaseHeaderSection.addSection(showcaseSection);
        showcaseHeaderSection.hideSection();

        highlightHeaderSection = new HeaderSection<>(new LargeBoldHeaderModel("Highlights"));
        highlightHeaderSection.addSection(highlightListSection);
        highlightHeaderSection.hideSection();

        adapter.addSection(notificationListSection);
        adapter.addSection(showcaseHeaderSection);
        adapter.addSection(highlightHeaderSection);

        notificationListSection.setOnSelectionChangedListener((item, isSelected, selectedItems) -> {
            if (isSelected) {
                notificationListSection.remove(
                        notificationListSection.getData().indexOf(item)
                );
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_content_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        FeedContentViewModel feedContentViewModel = new ViewModelProvider(getParentFragment().requireActivity()).get(FeedContentViewModel.class);
        UserProfileViewModel userProfileViewModel = new ViewModelProvider(getParentFragment().requireActivity()).get(UserProfileViewModel.class);

        adapter.registerItemBinders(
                new EventCardMediumNoTitleBinder(feedContentViewModel),
                new ArticleCardMediumNoTitleBinder(feedContentViewModel),
                new OrganizationShowcaseThumbnailListBinder(feedContentViewModel),
                new LargeBoldHeaderBinder(),
                new InstituteJoinResponseNotificationItemBinder(getViewLifecycleOwner(), userProfileViewModel)
        );

        userProfileViewModel.getLoadedOrganizationShowcases().observe(getViewLifecycleOwner(),
                this::onShowcasesLoaded
        );

        userProfileViewModel.getLoadedOrganizationHighlights().observe(getViewLifecycleOwner(),
                this::onHighlightsLoaded
        );

        userProfileViewModel.getOrganizationNotifications().observe(getViewLifecycleOwner(), notificationModels -> {
            if (notificationModels == null) {
                return;
            } else if (notificationModels.size() <= notificationListSection.size()) {
                notificationListSection.set(notificationModels);
            } else {
                notificationListSection.addAll(notificationModels.subList(notificationListSection.size(), notificationModels.size()));
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false)
        );

        recyclerView.setAdapter(adapter);
    }

    private void onHighlightsLoaded(List<PostModel> postModels) {

        if (postModels == null) {
            return;
        }
        Log.e("UserProfile", "Highlights loaded : " + postModels.size());
        if (highlightListSection.size() > postModels.size()) {
            highlightListSection.clear();
        }
        highlightListSection.addAll(postModels);
        if (highlightHeaderSection.isSectionHidden() && highlightListSection.size() > 0) {
            highlightHeaderSection.showSection();
        }
        if(highlightListSection.size() == 0) {
            highlightHeaderSection.hideSection();
        }
        refreshHighlights = false;
    }

    private void onShowcasesLoaded(List<ShowcaseModel> showcaseModels) {

        if (showcaseModels == null) {
            return;
        }
        if (showcaseModels != null && showcaseModels.size() > 0 && showcaseSection.getItem() != null && refreshShowcases) {
            if (showcaseHeaderSection.isSectionHidden()) {
                showcaseHeaderSection.showSection();
            }
            if (showcaseSection.getItem() != null &&
                    showcaseSection.getItem().size() > 0 &&
                    showcaseSection.getItem().get(0).getId() == null) {

                showcaseSection.getItem().remove(0);
            }
            showcaseSection.getItem().addAll(showcaseModels);

            recyclerView.scrollToPosition(0);
            refreshShowcases = false;
        }
    }
}
