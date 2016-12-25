package io.github.yusukeiwaki.githubviewer.model.internal;

import io.github.yusukeiwaki.githubviewer.model.Issue;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class SearchIssueProcedure extends RealmObject {
    @PrimaryKey private long queryId;
    private int syncState;
    private SearchIssueQuery query;
    private long total_count;
    private RealmList<Issue> results;

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

    public RealmList<Issue> getResults() {
        return results;
    }

    public void setResults(RealmList<Issue> results) {
        this.results = results;
    }
}
