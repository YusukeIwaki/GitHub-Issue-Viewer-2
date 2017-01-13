package io.github.yusukeiwaki.githubviewer2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 */
public abstract class AbstractFragment extends Fragment {
  protected View rootView;

  protected abstract int getLayout();

  protected abstract void onCreateView(@Nullable Bundle savedInstanceState);

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(getLayout(), container, false);
    onCreateView(savedInstanceState);
    return rootView;
  }
}

