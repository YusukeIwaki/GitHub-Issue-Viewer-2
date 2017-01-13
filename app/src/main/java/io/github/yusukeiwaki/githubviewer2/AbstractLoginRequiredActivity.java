package io.github.yusukeiwaki.githubviewer2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import io.github.yusukeiwaki.githubviewer2.helper.SessionHelper;

/**
 */
abstract class AbstractLoginRequiredActivity extends AbstractActivity
    implements SessionHelper.Callback {
  private SessionHelper sessionHelper;

  @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    sessionHelper = new SessionHelper(this).init(savedInstanceState);
  }

  @Override protected void onRestart() {
    super.onRestart();
    sessionHelper.restart();
  }

  @Override protected void onResume() {
    super.onResume();
    sessionHelper.onResume();
  }

  @Override protected void onPause() {
    sessionHelper.onPause();
    super.onPause();
  }

  @Override public final void onSessionExists() {
    // do nothing.
  }
}
