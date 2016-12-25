package io.github.yusukeiwaki.githubviewer.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer.LaunchUtil;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;
import io.github.yusukeiwaki.githubviewer.model.AbstractAuthStateObservingActivity;
import io.github.yusukeiwaki.githubviewer.webapi.GitHubAPI;

/**
 */
public class LoginActivity extends AbstractAuthStateObservingActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        parseIntentBeforeCreate(getIntent());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_framelayout);
    }

    private void parseIntentBeforeCreate(Intent intent) {
        if (intent == null) return;

        Uri data = intent.getData();
        if (data == null) return;
        if (!getString(R.string.github_oauth_callback_scheme).equals(data.getScheme())) return;
        if (!getString(R.string.github_oauth_callback_host).equals(data.getAuthority())) return;
        if (!getString(R.string.github_oauth_callback_path).equals(data.getPath())) return;

        SharedPreferences prefs = CurrentUserData.get(this);
        String state = prefs.getString("state", null);
        if (state == null) return;
        if (!state.equals(data.getQueryParameter("state"))) return;

        String code = data.getQueryParameter("code");
        if (TextUtils.isEmpty(code)) return;

        prefs.edit()
                .putString(CurrentUserData.KEY_CODE, code)
                .commit();
    }

    @Override
    protected void onAuthRequired() {
        SharedPreferences prefs = CurrentUserData.get(this);
        final String code = prefs.getString(CurrentUserData.KEY_CODE, null);
        final String state = prefs.getString(CurrentUserData.KEY_STATE, null);

        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(state)) {
            showFragment(new GitHubLoginFragment());
        } else {
            new GitHubAPI(this).createAccessToken(code, state)
                    .onSuccess(new Continuation<JSONObject, Object>() {
                        @Override
                        public Object then(Task<JSONObject> task) throws Exception {
                            JSONObject result = task.getResult();
                            String token = result.getString("access_token");
                            CurrentUserData.get(LoginActivity.this).edit()
                                    .remove(CurrentUserData.KEY_CODE)
                                    .remove(CurrentUserData.KEY_STATE)
                                    .putString(CurrentUserData.KEY_TOKEN, token)
                                    .apply();
                            return null;
                        }
                    })
                    .continueWith(new Continuation<Object, Object>() {
                        @Override
                        public Object then(Task<Object> task) throws Exception {
                            if (task.isFaulted()) {
                                Snackbar.make(findViewById(R.id.container), R.string.dialog_title_failed_to_authorize, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.retry, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                showFragment(new GitHubLoginFragment());
                                            }
                                        })
                                        .show();
                            }
                            return null;
                        }
                    });
        }
    }

    @Override
    protected void onTokenVerified() {
        LaunchUtil.showMainActivity(this);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }
}
