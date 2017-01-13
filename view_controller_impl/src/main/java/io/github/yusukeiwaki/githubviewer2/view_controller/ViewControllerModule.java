package io.github.yusukeiwaki.githubviewer2.view_controller;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class ViewControllerModule {
  @Provides @Singleton
  public SessionReader provideSessionReader() {
    return null;
  }

  @Provides @Singleton
  public SessionController provideSessionController() {
    return null;
  }

  @Provides @Singleton
  public OAuthCache provideOAuthCache() {
    return null;
  }

  @Provides @Singleton
  public OAuthLoginController provideOAuthLoginContoller() {
    return null;
  }
}
