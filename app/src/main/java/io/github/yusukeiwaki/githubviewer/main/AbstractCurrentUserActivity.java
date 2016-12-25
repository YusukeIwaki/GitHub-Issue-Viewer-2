package io.github.yusukeiwaki.githubviewer.main;

import android.content.SharedPreferences;
import android.os.Bundle;

import icepick.State;
import io.github.yusukeiwaki.githubviewer.AbstractAuthStateObservingActivity;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;
import io.github.yusukeiwaki.githubviewer.model.User;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers.RealmHelper;

/**
 */
abstract class AbstractCurrentUserActivity extends AbstractAuthStateObservingActivity {
    @State long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = renderCurrentUser(CurrentUserData.get(this));
    }

    private long renderCurrentUser(SharedPreferences prefs) {
        final long currentUserId = prefs.getLong(CurrentUserData.KEY_USER_ID, -1);
        if (currentUserId != -1) {
            User user = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<User>() {
                @Override
                public User execute(Realm realm) throws Throwable {
                    return realm.where(User.class).equalTo("id", currentUserId).findFirst();
                }
            });
            if (user != null) {
                onRenderCurrentUser(user);
                return currentUserId;
            }
        }
        return -1;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener currentUserListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (CurrentUserData.KEY_USER_ID.equals(key)) {
                long userId = prefs.getLong(key, -1);
                if (userId != currentUserId) {
                    currentUserId = renderCurrentUser(prefs);
                }
            }
        }
    };

    protected abstract void onRenderCurrentUser(User user);

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = CurrentUserData.get(this);
        currentUserId = renderCurrentUser(prefs);
        prefs.registerOnSharedPreferenceChangeListener(currentUserListener);
    }

    @Override
    protected void onStop() {
        CurrentUserData.get(this).unregisterOnSharedPreferenceChangeListener(currentUserListener);
        super.onStop();
    }
}
