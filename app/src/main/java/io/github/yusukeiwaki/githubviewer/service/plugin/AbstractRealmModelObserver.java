package io.github.yusukeiwaki.githubviewer.service.plugin;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import io.github.yusukeiwaki.githubviewer.service.GitHubViewerServicePlugin;
import io.github.yusukeiwaki.githubviewer.webapi.GitHubAPI;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmListObserver;

/**
 */
abstract class AbstractRealmModelObserver<T extends RealmObject> extends RealmListObserver<T> implements GitHubViewerServicePlugin {

    protected final Context context;

    protected final GitHubAPI gitHubAPI;

    protected AbstractRealmModelObserver(Context context, GitHubAPI gitHubAPI) {
        this.context = context;
        this.gitHubAPI = gitHubAPI;
    }

    protected abstract RealmQuery<T> query(Realm realm);

    protected abstract void handleItem(@NonNull T item);

    @Override
    protected RealmResults<T> queryItems(Realm realm) {
        return query(realm).findAll();
    }

    @Override
    protected void onCollectionChanged(List<T> list) {
        if (list.isEmpty()) return;

        handleItem(list.get(0));
    }

    @Override
    public void register() {
        sub();
    }

    @Override
    public void unregister() {
        unsub();
    }
}
