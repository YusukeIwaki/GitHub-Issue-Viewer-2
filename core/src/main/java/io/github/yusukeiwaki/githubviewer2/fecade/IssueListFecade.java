package io.github.yusukeiwaki.githubviewer2.fecade;

import android.content.Context;
import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.LogcatIfError;
import io.github.yusukeiwaki.githubviewer2.model.SyncState;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer2.service.GitHubAPIService;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;
import org.json.JSONObject;

public class IssueListFecade {
  private static final IssueListFecade INSTANCE = new IssueListFecade();

  public static IssueListFecade getInstance() {
    return INSTANCE;
  }

  public SearchIssueQuery getQueryById(final long queryId) {
    return RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<SearchIssueQuery>() {
      @Override
      public SearchIssueQuery execute(Realm realm) throws Exception {
        return realm.where(SearchIssueQuery.class).equalTo("id", queryId).findFirst();
      }
    });
  }

  public void markQueryAsRead(final long queryId) {
    RealmHelper.executeTransaction(new RealmHelper.Transaction() {
      @Override
      public Object execute(Realm realm) throws Exception {
        SearchIssueQuery query = realm.where(SearchIssueQuery.class).equalTo("id", queryId).findFirst();
        if (query != null) {
          query.setLastSeenAt(System.currentTimeMillis());
        }
        return null;
      }
    }).continueWith(new LogcatIfError());;
  }

  public void fetchLatestResults(final Context context, final long queryId) {
    RealmHelper.executeTransaction(new RealmHelper.Transaction() {
      @Override
      public Object execute(Realm realm) throws Exception {
        realm.createOrUpdateObjectFromJson(SearchIssueProcedure.class, new JSONObject()
            .put("queryId", queryId)
            .put("syncState", SyncState.NOT_SYNCED)
            .put("reset", true)
            .put("query", new JSONObject()
                .put("id", queryId))
        );
        return null;
      }
    }).onSuccess(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        GitHubAPIService.keepAlive(context);
        return null;
      }
    });
  }

  public void fetchMoreResults(final Context context, final long queryId) {
    RealmHelper.executeTransaction(new RealmHelper.Transaction() {
      @Override
      public Object execute(Realm realm) throws Exception {
        realm.createOrUpdateObjectFromJson(SearchIssueProcedure.class, new JSONObject()
            .put("queryId", queryId)
            .put("syncState", SyncState.NOT_SYNCED)
            .put("reset", false)
        );
        return null;
      }
    }).onSuccess(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        GitHubAPIService.keepAlive(context);
        return null;
      }
    });
  }

}
