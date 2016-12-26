package io.github.yusukeiwaki.githubviewer.service;

import org.json.JSONObject;

import io.github.yusukeiwaki.githubviewer.model.SyncState;
import io.realm.Realm;
import io.realm.RealmObject;
import jp.co.crowdworks.realm_java_helpers.RealmHelper;
import rx.Completable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 */
public abstract class PluginTask<T extends RealmObject> {
    protected final T item;

    public PluginTask(T item) {
        this.item = item;
    }

    protected abstract Completable handleItem(long primaryKey);

    protected abstract String getPrimaryKeyColName();

    protected abstract Class<T> getClassForUpdate();

    public void execute(final long primaryKey) {
        RealmHelper.rxExecuteTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Throwable {
                realm.createOrUpdateObjectFromJson(getClassForUpdate(), new JSONObject()
                        .put(getPrimaryKeyColName(), primaryKey)
                        .put("syncState", SyncState.SYNCING)
                );
                return null;
            }
        }).andThen(handleItem(primaryKey)).subscribe(new Action0() {
            @Override
            public void call() {
                RealmHelper.rxExecuteTransaction(new RealmHelper.Transaction() {
                    @Override
                    public Object execute(Realm realm) throws Throwable {
                        realm.createOrUpdateObjectFromJson(getClassForUpdate(), new JSONObject()
                                .put(getPrimaryKeyColName(), primaryKey)
                                .put("syncState", SyncState.SYNCED));
                        return null;
                    }
                }).subscribe();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                RealmHelper.rxExecuteTransaction(new RealmHelper.Transaction() {
                    @Override
                    public Object execute(Realm realm) throws Throwable {
                        realm.createOrUpdateObjectFromJson(getClassForUpdate(), new JSONObject()
                                .put(getPrimaryKeyColName(), primaryKey)
                                .put("syncState", SyncState.SYNC_ERROR));
                        return null;
                    }
                }).subscribe();
            }
        });
    }
}
