package io.github.yusukeiwaki.githubviewer2.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import io.github.yusukeiwaki.githubviewer2.AbstractActivity;
import io.github.yusukeiwaki.githubviewer2.AppModule;
import io.github.yusukeiwaki.githubviewer2.R;
import io.github.yusukeiwaki.githubviewer2.helper.SessionHelper;
import io.github.yusukeiwaki.githubviewer2.view_controller.OAuthCache;
import io.github.yusukeiwaki.githubviewer2.view_controller.OAuthLoginController;
import io.github.yusukeiwaki.githubviewer2.view_controller.SessionController;

/**
 * ログインウィンドウを開く。
 * ログインのコールバックもこいつが受ける
 */
public class LoginActivity extends AbstractActivity implements SessionHelper.Callback {

  private SessionHelper sessionHelper;
  private OAuthCache oauthCache = AppModule.getInstance().provideOAuthCache();
  private OAuthLoginController loginManager = AppModule.getInstance().provideOAuthLoginContoller();
  private SessionController sessionController = AppModule.getInstance().provideSessionController();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handleCallbackIntent(getIntent());
    setContentView(R.layout.simple_framelayout);
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

  private void handleCallbackIntent(Intent intent) {
    if (intent == null) return;

    Uri data = intent.getData();
    if (data == null) return;
    if (!getString(R.string.github_oauth_callback_scheme).equals(data.getScheme())) return;
    if (!getString(R.string.github_oauth_callback_host).equals(data.getAuthority())) return;
    if (!getString(R.string.github_oauth_callback_path).equals(data.getPath())) return;

    String state = oauthCache.getState();
    if (state == null) return;
    if (!state.equals(data.getQueryParameter("state"))) return;

    String code = data.getQueryParameter("code");
    if (TextUtils.isEmpty(code)) return;

    oauthCache.setCode(code);
  }

  @Override public void onBackPressed() {
    moveTaskToBack(true);
  }

  @Override public void onSessionExists() {
    finish();
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }

  @Override public void onNoSession() {
    final String code = oauthCache.getCode();
    final String state = oauthCache.getState();

    if (TextUtils.isEmpty(code) || TextUtils.isEmpty(state)) {
      showFragment(new GitHubLoginFragment());
    } else {
      loginManager.createAccessToken(code, state).onSuccess(task -> {
        oauthCache.clear();
        sessionController.login(task.getResult());
        return null;
      }).continueWith(task -> {
        if (task.isFaulted()) {
          Snackbar.make(findViewById(R.id.container), R.string.dialog_title_failed_to_authorize,
              Snackbar.LENGTH_INDEFINITE)
              .setAction(R.string.retry, view -> showFragment(new GitHubLoginFragment()))
              .show();
        }
        return null;
      });
    }
  }

  private void showFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
  }
}
