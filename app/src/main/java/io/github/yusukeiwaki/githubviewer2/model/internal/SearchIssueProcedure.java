package io.github.yusukeiwaki.githubviewer2.model.internal;

import io.github.yusukeiwaki.githubviewer2.model.Issue;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class SearchIssueProcedure extends RealmObject {
    @PrimaryKey public long queryId;
    public int syncState;
    public boolean reset;
    public SearchIssueQuery query;
    public long total_count;
    public RealmList<Issue> items;
}
