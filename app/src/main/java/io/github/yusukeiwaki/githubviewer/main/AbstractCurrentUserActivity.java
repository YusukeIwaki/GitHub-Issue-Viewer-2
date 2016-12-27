package io.github.yusukeiwaki.githubviewer.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import icepick.State;
import io.github.yusukeiwaki.githubviewer.AbstractAuthStateObservingActivity;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;
import io.github.yusukeiwaki.githubviewer.model.User;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;

/**
 */
abstract class AbstractCurrentUserActivity extends AbstractAuthStateObservingActivity {
    @State long currentUserId = -1;
    private User currentUser;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            updateCurrentUserIdFromCacheIfNeeded(CurrentUserData.get(this));
            onCurrentUserUpdated(currentUser);
        } else {
            if (updateCurrentUserIdFromCacheIfNeeded(CurrentUserData.get(this))) {
                onCurrentUserUpdated(currentUser);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateCurrentUserIdFromCacheIfNeeded(CurrentUserData.get(this));
        onCurrentUserUpdated(currentUser);
    }

    private boolean updateCurrentUserIdFromCacheIfNeeded(SharedPreferences prefs) {
        final long currentUserId = prefs.getLong(CurrentUserData.KEY_USER_ID, -1);
        if (this.currentUserId == -1) {
            if (currentUserId != -1) {
                User user = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<User>() {
                    @Override
                    public User execute(Realm realm) throws Exception {
                        return realm.where(User.class).equalTo("id", currentUserId).findFirst();
                    }
                });
                if (user != null) {
                    updateCurrentUserWith(user);
                    return true;
                }
            }
        } else {
            if (currentUserId == -1) {
                updateCurrentUserWith(null);
                return true;
            }
            User user = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<User>() {
                @Override
                public User execute(Realm realm) throws Exception {
                    return realm.where(User.class).equalTo("id", currentUserId).findFirst();
                }
            });
            if (user == null) {
                updateCurrentUserWith(null);
                return true;
            }
        }
        return false;
    }

    private void updateCurrentUserWith(@Nullable User user) {
        this.currentUserId = user != null ? user.getId() : -1;
        this.currentUser = user;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener currentUserListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (CurrentUserData.KEY_USER_ID.equals(key)) {
                if (updateCurrentUserIdFromCacheIfNeeded(prefs)) {
                    onCurrentUserUpdated(currentUser);
                }
            }
        }
    };

    protected abstract void onCurrentUserUpdated(User user);

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = CurrentUserData.get(this);
        if (updateCurrentUserIdFromCacheIfNeeded(prefs)) {
            onCurrentUserUpdated(currentUser);
        }
        prefs.registerOnSharedPreferenceChangeListener(currentUserListener);
    }

    @Override
    protected void onPause() {
        CurrentUserData.get(this).unregisterOnSharedPreferenceChangeListener(currentUserListener);
        super.onPause();
    }
}
