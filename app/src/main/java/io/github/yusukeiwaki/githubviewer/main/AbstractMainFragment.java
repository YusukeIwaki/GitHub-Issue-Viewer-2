package io.github.yusukeiwaki.githubviewer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.yusukeiwaki.githubviewer.AbstractFragment;
import io.github.yusukeiwaki.githubviewer.R;

/**
 */
abstract class AbstractMainFragment extends AbstractFragment {
    protected ActionBar activityToolbar;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityToolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (activityToolbar != null) {
            onSetupToolbar();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void onSetupToolbar() {
        activityToolbar.setTitle(R.string.app_name);
        activityToolbar.setSubtitle(null);
    }
}
