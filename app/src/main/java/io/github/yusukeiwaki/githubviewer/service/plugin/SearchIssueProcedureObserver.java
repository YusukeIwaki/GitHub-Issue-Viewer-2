package io.github.yusukeiwaki.githubviewer.service.plugin;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer.LogcatIfError;
import io.github.yusukeiwaki.githubviewer.model.Issue;
import io.github.yusukeiwaki.githubviewer.model.SyncState;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer.service.PluginTask;
import io.github.yusukeiwaki.githubviewer.webapi.GitHubAPI;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;

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
                    procedure.setSyncState(SyncState.NOT_SYNCED);
                }
                return null;
            }
        }).continueWith(new LogcatIfError());
    }

    @Override
    protected RealmQuery<SearchIssueProcedure> query(Realm realm) {
        return realm.where(SearchIssueProcedure.class).equalTo("syncState", SyncState.NOT_SYNCED);
    }

    private class MyTask extends PluginTask<SearchIssueProcedure> {
        private String queryText;
        private String sort;
        private String order;
        private int page;
        private boolean shouldResetResult;

        public MyTask(SearchIssueProcedure item) {
            super(item);
            SearchIssueQuery query = item.getQuery();
            queryText = query.getQ();
            sort = query.getSort();
            order = query.getOrder();
            page = query.getPage();
            shouldResetResult = page <=1;
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
                                    if (!shouldResetResult) {
                                        JSONArray itemsJson = resultJson.getJSONArray("items");

                                        SearchIssueProcedure procedure = realm.where(SearchIssueProcedure.class).equalTo("queryId", primaryKey).findFirst();
                                        int i = 0;
                                        for(Issue issue : procedure.getItems()) {
                                            itemsJson.put(i, new JSONObject().put("id", issue.getId()));
                                            i++;
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

    @Override
    protected void handleItem(@NonNull SearchIssueProcedure item) {
        new MyTask(item).execute(item.getQueryId());
    }
}
