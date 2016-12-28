package io.github.yusukeiwaki.githubviewer.service;

import android.app.IntentService;
import android.content.Intent;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer.model.SyncState;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueProcedure;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;

/**
 */
public class BackgroundFetchService extends IntentService {
    public BackgroundFetchService() {
        super(BackgroundFetchService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                RealmResults<SearchIssueProcedure> procedures = realm.where(SearchIssueProcedure.class).isNotNull("query").findAll();
                for (SearchIssueProcedure procedure : procedures) {
                    procedure.setSyncState(SyncState.NOT_SYNCED);
                }
                return null;
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                GitHubViewerService.keepAlive(getBaseContext());
                return null;
            }
        });
    }
}
