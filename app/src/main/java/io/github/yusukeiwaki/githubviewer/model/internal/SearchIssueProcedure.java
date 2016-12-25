package io.github.yusukeiwaki.githubviewer.model.internal;

import io.github.yusukeiwaki.githubviewer.model.Issue;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class SearchIssueProcedure extends RealmObject {
    @PrimaryKey private long queryId;
    private SearchIssueQuery query;
    private long total_count;
    private RealmList<Issue> results;
}
