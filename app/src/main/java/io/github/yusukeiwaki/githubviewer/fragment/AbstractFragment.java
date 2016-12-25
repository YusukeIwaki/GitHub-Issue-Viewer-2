package io.github.yusukeiwaki.githubviewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 */
public abstract class AbstractFragment extends Fragment {
    protected ActionBar activityToolbar;
    protected View rootView;

    protected abstract int getLayout();

    protected abstract void onCreateView(@Nullable Bundle savedInstanceState);

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityToolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (activityToolbar != null) {
            onSetupToolbar();
        }
        rootView = inflater.inflate(getLayout(), container, false);
        onCreateView(savedInstanceState);
        return rootView;
    }

    protected void onSetupToolbar() {
        activityToolbar.setTitle("GitHubViewer!");
        activityToolbar.setSubtitle(null);
    }
}
