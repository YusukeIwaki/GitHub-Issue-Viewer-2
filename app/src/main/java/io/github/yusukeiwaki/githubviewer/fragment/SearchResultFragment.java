package io.github.yusukeiwaki.githubviewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers.RealmHelper;

/**
 */
public class SearchResultFragment extends AbstractFragment {
    public SearchResultFragment(){}

    private SearchIssueQuery searchIssueQuery;

    public static SearchResultFragment create(long queryItemId) {
        Bundle args = new Bundle();
        args.putLong("queryItemId", queryItemId);

        SearchResultFragment f = new SearchResultFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        final long itemId = args.getLong("queryItemId");
        searchIssueQuery = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<SearchIssueQuery>() {
            @Override
            public SearchIssueQuery execute(Realm realm) throws Throwable {
                return realm.where(SearchIssueQuery.class).equalTo("id", itemId).findFirst();
            }
        });
    }

    @Override
    protected void onSetupToolbar() {
        activityToolbar.setTitle(searchIssueQuery.getTitle());
    }

    @Override
    protected int getLayout() {
        return R.layout.content_main;
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {

    }
}
