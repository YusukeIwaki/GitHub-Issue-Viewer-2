package io.github.yusukeiwaki.githubviewer.login;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.UUID;

import io.github.yusukeiwaki.githubviewer.LaunchUtil;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;

/**
 */
public class GitHubLoginFragment extends AbstractLoginFragment {
    @Override
    protected int getLayout() {
        return R.layout.login_screen;
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {
        rootView.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser();
            }
        });
    }

    private void launchBrowser() {
        String callbackUrl = new Uri.Builder()
                .scheme(getString(R.string.github_oauth_callback_scheme))
                .authority(getString(R.string.github_oauth_callback_host))
                .path(getString(R.string.github_oauth_callback_path))
                .build().toString();

        String state = UUID.randomUUID().toString();
        CurrentUserData.get(getContext()).edit()
                .putString(CurrentUserData.KEY_STATE, state)
                .apply();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .path("/login/oauth/authorize")
                .appendQueryParameter("client_id", getString(R.string.github_client_id))
                .appendQueryParameter("redirect_uri", callbackUrl)
                .appendQueryParameter("scope", "repo")
                .appendQueryParameter("state", state)
                .appendQueryParameter("allow_signup", "false")
                .build();

        LaunchUtil.launchBrowser(getContext(), uri);
    }
}
