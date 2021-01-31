package com.omada.junctionadmin.ui.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omada.junctionadmin.R;
import com.omada.junctionadmin.viewmodels.CreatePostViewModel;

public class CreateFragment extends Fragment {

    private CreatePostViewModel createPostViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        createPostViewModel = new ViewModelProvider(requireActivity()).get(CreatePostViewModel.class);
        return inflater.inflate(R.layout.create_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.create_event_button).setOnClickListener(v -> {
            createPostViewModel.goToCreateEvent();
        });

        view.findViewById(R.id.create_article_button).setOnClickListener(v -> {
            createPostViewModel.goToCreateArticle();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
