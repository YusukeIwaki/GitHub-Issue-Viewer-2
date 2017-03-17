package io.github.yusukeiwaki.githubviewer2.background_fetch;

import android.app.IntentService;
import android.content.Intent;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.model.SyncState;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer2.service.GitHubViewerService;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.realm.Realm;
import io.realm.RealmResults;

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
