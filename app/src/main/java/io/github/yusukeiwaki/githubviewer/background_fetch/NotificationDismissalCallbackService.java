package io.github.yusukeiwaki.githubviewer.background_fetch;

import android.app.IntentService;
import android.content.Intent;

import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;

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
                if (query != null && query.getLastSeenAt() <= currentTime) {
                    query.setLastSeenAt(currentTime);
                }
                return null;
            }
        });
    }
}
