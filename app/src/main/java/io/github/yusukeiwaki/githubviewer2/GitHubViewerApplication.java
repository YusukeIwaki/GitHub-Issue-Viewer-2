package io.github.yusukeiwaki.githubviewer2;

import android.app.Application;
import io.github.yusukeiwaki.githubviewer2.view_controller.OAuthCache;

/**
 */
public class GitHubViewerApplication extends Application {
  public static OAuthCache getOAuthCache() {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    AppModule.init(this);
  }
}
