package io.github.yusukeiwaki.githubviewer2.service2.plugin;

import android.content.Context;
import android.support.annotation.NonNull;
import io.github.yusukeiwaki.githubviewer2.service2.GitHubViewerServicePlugin;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.util.List;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmListObserver;

/**
 */
abstract class AbstractRealmModelObserver<T extends RealmObject> extends RealmListObserver<T> implements GitHubViewerServicePlugin {

    protected final Context context;

    protected AbstractRealmModelObserver(Context context) {
        this.context = context;
    }

    protected abstract RealmQuery<T> query(Realm realm);

    protected abstract void handleItems(@NonNull List<T> items);

    @Override
    protected RealmResults<T> queryItems(Realm realm) {
        return query(realm).findAll();
    }

    @Override
    protected void onCollectionChanged(List<T> list) {
        if (list.isEmpty()) return;

        handleItems(list);
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
