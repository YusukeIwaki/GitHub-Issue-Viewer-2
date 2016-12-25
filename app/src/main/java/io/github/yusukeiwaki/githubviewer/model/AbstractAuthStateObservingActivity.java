package io.github.yusukeiwaki.githubviewer.model;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import icepick.State;
import io.github.yusukeiwaki.githubviewer.AbstractActivity;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;

/**
 */
public abstract class AbstractAuthStateObservingActivity extends AbstractActivity {
    @State String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            updateTokenFromCacheIfNeeded(CurrentUserData.get(this));
            onTokenUpdated();
        } else {
            if (updateTokenFromCacheIfNeeded(CurrentUserData.get(this))) {
                onTokenUpdated();
            }
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener currentUserDataListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (CurrentUserData.KEY_TOKEN.equals(key)) {
                if (updateTokenFromCacheIfNeeded(prefs)) {
                    onTokenUpdated();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = CurrentUserData.get(this);
        if (updateTokenFromCacheIfNeeded(prefs)) {
            onTokenUpdated();
        }
        prefs.registerOnSharedPreferenceChangeListener(currentUserDataListener);
    }

    @Override
    protected void onStop() {
        CurrentUserData.get(this).unregisterOnSharedPreferenceChangeListener(currentUserDataListener);
        super.onStop();
    }

    private boolean updateTokenFromCacheIfNeeded(SharedPreferences cache) {
        String token = cache.getString(CurrentUserData.KEY_TOKEN, null);

        if (this.token == null) {
            if (token != null) {
                updateTokenWith(token);
                return true;
            }
        } else {
            if (!this.token.equals(token)) {
                updateTokenWith(token);
                return true;
            }
        }

        return false;
    }

    private void updateTokenWith(String token) {
        this.token = token;
    }

    private void onTokenUpdated() {
        if (TextUtils.isEmpty(token)) {
            onAuthRequired();
        } else {
            onTokenVerified();
        }
    }

    protected abstract void onAuthRequired();

    protected abstract void onTokenVerified();
}
