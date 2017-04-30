package io.github.yusukeiwaki.githubviewer2.background_fetch;

import android.app.IntentService;
import android.content.Intent;

import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.realm.Realm;

/**
 */
public class NotificationDismissalCallbackService extends IntentService {
    public NotificationDismissalCallbackService() {
        super(NotificationDismissalCallbackService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final long queryId = intent.getLongExtra("queryId", -1);
        if (queryId == -1) return;

        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                long currentTime = System.currentTimeMillis();
                SearchIssueQuery query = realm.where(SearchIssueQuery.class).equalTo("id", queryId).findFirst();
                if (query != null && query.lastSeenAt <= currentTime) {
                    query.lastSeenAt = currentTime;
                }
                return null;
            }
        });
    }
}
