package io.github.yusukeiwaki.githubviewer2.service;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.model.SyncState;
import io.realm.Realm;
import io.realm.RealmObject;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;

/**
 */
public abstract class PluginTask<T extends RealmObject> {
    protected final T item;

    public PluginTask(T item) {
        this.item = item;
    }

    protected abstract Task<Void> handleItem(long primaryKey);

    protected abstract String getPrimaryKeyColName();

    protected abstract Class<T> getClassForUpdate();

    public void execute(final long primaryKey) {
        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                realm.createOrUpdateObjectFromJson(getClassForUpdate(), new JSONObject()
                        .put(getPrimaryKeyColName(), primaryKey)
                        .put("syncState", SyncState.SYNCING)
                );
                return null;
            }
        }).onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return handleItem(primaryKey);
            }
        }).onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return RealmHelper.executeTransaction(new RealmHelper.Transaction() {
                    @Override
                    public Object execute(Realm realm) throws Exception {
                        realm.createOrUpdateObjectFromJson(getClassForUpdate(), new JSONObject()
                                .put(getPrimaryKeyColName(), primaryKey)
                                .put("syncState", SyncState.SYNCED));
                        return null;
                    }
                });
            }
        }).continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                return RealmHelper.executeTransaction(new RealmHelper.Transaction() {
                    @Override
                    public Object execute(Realm realm) throws Exception {
                        realm.createOrUpdateObjectFromJson(getClassForUpdate(), new JSONObject()
                                .put(getPrimaryKeyColName(), primaryKey)
                                .put("syncState", SyncState.SYNC_ERROR));
                        return null;
                    }
                });
            }
        });
    }
}
