package io.github.yusukeiwaki.githubviewer2.model.internal;

import io.github.yusukeiwaki.githubviewer2.model.Issue;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class SearchIssueProcedure extends RealmObject {
    @PrimaryKey private long queryId;
    private int syncState;
    private boolean reset;
    private SearchIssueQuery query;
    private long total_count;
    private RealmList<Issue> items;

    public long getQueryId() {
        return queryId;
    }

    public void setQueryId(long queryId) {
        this.queryId = queryId;
    }

    public int getSyncState() {
        return syncState;
    }

    public void setSyncState(int syncState) {
        this.syncState = syncState;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public SearchIssueQuery getQuery() {
        return query;
    }

    public void setQuery(SearchIssueQuery query) {
        this.query = query;
    }

    public long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(long total_count) {
        this.total_count = total_count;
    }

    public RealmList<Issue> getItems() {
        return items;
    }

    public void setItems(RealmList<Issue> items) {
        this.items = items;
    }
}
