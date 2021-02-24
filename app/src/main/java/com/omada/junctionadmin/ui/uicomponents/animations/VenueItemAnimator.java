package com.omada.junctionadmin.ui.uicomponents.animations;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.itemanimators.DefaultAnimator;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;


public class VenueItemAnimator extends DefaultAnimator<SlideDownAlphaAnimator> {

    private final int elevation;

    public VenueItemAnimator(int elevation) {
        this.elevation = elevation;
    }

    @Override public void addAnimationPrepare(RecyclerView.ViewHolder holder) {
        holder.itemView.setTranslationY(-holder.itemView.getHeight());
        holder.itemView.setAlpha(0);
        holder.itemView.setElevation(0);
    }

    @Override public ViewPropertyAnimatorCompat addAnimation(RecyclerView.ViewHolder holder) {
        return ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(getMoveDuration())
                .setInterpolator(getInterpolator());
    }

    @Override public void addAnimationCleanup(RecyclerView.ViewHolder holder) {
        holder.itemView.setTranslationY(0);
        holder.itemView.setAlpha(1);
        ViewCompat.setElevation(holder.itemView, elevation);
    }

    @Override public long getAddDelay(long remove, long move, long change) {
        return 0;
    }

    @Override public long getRemoveDelay(long remove, long move, long change) {
        return remove / 2;
    }

    @Override public ViewPropertyAnimatorCompat removeAnimation(RecyclerView.ViewHolder holder) {
        ViewCompat.setElevation(holder.itemView, 0);
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(holder.itemView);
        return animation.setDuration(getRemoveDuration())
                .alpha(0)
                .translationY(-holder.itemView.getHeight())
                .setInterpolator(getInterpolator());
    }

    @Override public void removeAnimationCleanup(RecyclerView.ViewHolder holder) {
        holder.itemView.setTranslationY(0);
        holder.itemView.setAlpha(1);
        holder.itemView.setElevation(elevation);
    }
}