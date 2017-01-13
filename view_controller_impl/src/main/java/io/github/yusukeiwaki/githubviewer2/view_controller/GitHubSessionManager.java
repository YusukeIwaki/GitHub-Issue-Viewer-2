package io.github.yusukeiwaki.githubviewer2.view_controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class GitHubSessionManager implements SessionReader, SessionController {

  private final SharedPreferences sessionCache;
  public GitHubSessionManager(Context appContext) {
    sessionCache = appContext.getSharedPreferences("session", Context.MODE_PRIVATE);
  }

  @Override public boolean isLoggedIn() {
    return !TextUtils.isEmpty(sessionCache.getString("token", null));
  }

  @Override public void registerListener(Listener listener) {
    sessionCache.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

      }
    });
  }

  @Override public void unregisterListener(Listener listener) {

  }

  @Override public void login(String token) {

  }

  @Override public void logout() {

  }
}
