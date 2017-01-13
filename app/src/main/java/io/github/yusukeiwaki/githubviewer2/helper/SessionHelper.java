package io.github.yusukeiwaki.githubviewer2.helper;

import android.os.Bundle;
import io.github.yusukeiwaki.githubviewer2.AppModule;
import io.github.yusukeiwaki.githubviewer2.view_controller.SessionReader;

public class SessionHelper {
  private final SessionReader sessionReader = AppModule.getInstance().provideSessionReader();
  private final Callback callback;
  private boolean isPreviousLoggedIn;
  private final SessionReader.Listener sessionListener = new SessionReader.Listener() {
    @Override public void onSessionChanged() {
      handleCallback(false);
    }
  };

  public SessionHelper(Callback callback) {
    this.callback = callback;
  }

  private void handleCallback(boolean force) {
    boolean isLoggedIn = sessionReader.isLoggedIn();
    if (isLoggedIn) {
      if (!isPreviousLoggedIn || force) {
        callback.onSessionExists();
      }
    } else {
      if (isPreviousLoggedIn || force) {
        callback.onNoSession();
      }
    }
    isPreviousLoggedIn = isLoggedIn;
  }

  public SessionHelper init(Bundle savedInstanceState) {
    handleCallback(savedInstanceState == null);
    return this;
  }

  public void restart() {
    handleCallback(true);
  }

  public void onResume() {
    handleCallback(false);
    sessionReader.registerListener(sessionListener);
  }

  public void onPause() {
    sessionReader.unregisterListener(sessionListener);
  }

  public interface Callback {
    void onSessionExists();

    void onNoSession();
  }
}
