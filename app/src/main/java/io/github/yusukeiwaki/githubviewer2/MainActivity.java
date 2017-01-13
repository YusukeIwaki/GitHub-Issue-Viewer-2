package io.github.yusukeiwaki.githubviewer2;

public class MainActivity extends AbstractLoginRequiredActivity {

  @Override public void onNoSession() {
    LaunchUtil.showLoginActivity(this, null);
  }
}
