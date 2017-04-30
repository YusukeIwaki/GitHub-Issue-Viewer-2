package io.github.yusukeiwaki.githubviewer2.service.plugin;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.LogcatIfError;
import io.github.yusukeiwaki.githubviewer2.model.Issue;
import io.github.yusukeiwaki.githubviewer2.model.SyncState;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer2.webapi.GitHubAPI;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 */
public class SearchIssueProcedureObserver extends AbstractRealmModelObserver<SearchIssueProcedure> {
    public SearchIssueProcedureObserver(Context context, GitHubAPI gitHubAPI) {
        super(context, gitHubAPI);
        restartPendingProcedures();
    }

    private void restartPendingProcedures() {
        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                RealmResults<SearchIssueProcedure> results = realm.where(SearchIssueProcedure.class).equalTo("syncState", SyncState.SYNCING).findAll();
                for (SearchIssueProcedure procedure : results) {
                    procedure.syncState = SyncState.NOT_SYNCED;
                }
                return null;
            }
        }).continueWith(new LogcatIfError());
    }

    @Override
    protected RealmQuery<SearchIssueProcedure> query(Realm realm) {
        return realm.where(SearchIssueProcedure.class).equalTo("syncState", SyncState.NOT_SYNCED);
    }

    @Override
    protected void handleItems(@NonNull List<SearchIssueProcedure> items) {
        SearchIssueProcedure item = items.get(0);
        new MyTask(item).execute(item.queryId);
    }

    private class MyTask extends AbstractPluginTask<SearchIssueProcedure> {
        private String queryText;
        private String sort;
        private String order;
        private int page;
        private boolean shouldResetResult;

        public MyTask(SearchIssueProcedure item) {
            super(item);
            SearchIssueQuery query = item.query;
            queryText = query.q;
            sort = query.sort;
            order = query.order;
            shouldResetResult = item.reset;
            page = shouldResetResult ? 1 : query.page;
        }

        @Override
        protected Task<Void> handleItem(final long primaryKey) {
            return gitHubAPI.searchIssues(queryText, sort, order, page)
                    .onSuccessTask(new Continuation<JSONObject, Task<Void>>() {
                        @Override
                        public Task<Void> then(Task<JSONObject> task) throws Exception {
                            final JSONObject resultJson = task.getResult();
                            return RealmHelper.executeTransaction(new RealmHelper.Transaction() {
                                @Override
                                public Object execute(Realm realm) throws Exception {
                                    resultJson.put("queryId", primaryKey);
                                    resultJson.put("query", new JSONObject()
                                            .put("id", primaryKey)
                                            .put("page", page + 1));
                                    if (!shouldResetResult) {
                                        JSONArray itemsJson = resultJson.getJSONArray("items");

                                        SearchIssueProcedure procedure = realm.where(SearchIssueProcedure.class).equalTo("queryId", primaryKey).findFirst();
                                        for(Issue issue : procedure.items) {
                                            itemsJson.put(new JSONObject().put("id", issue.id));
                                        }
                                    }
                                    realm.createOrUpdateObjectFromJson(SearchIssueProcedure.class, resultJson);

                                    return null;
                                }
                            });
                        }
                    });
        }

        @Override
        protected String getPrimaryKeyColName() {
            return "queryId";
        }

        @Override
        protected Class<SearchIssueProcedure> getClassForUpdate() {
            return SearchIssueProcedure.class;
        }
    };
}
