package io.github.yusukeiwaki.githubviewer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.model.SyncState;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer.service.GitHubViewerService;
import io.realm.Realm;
import io.realm.RealmQuery;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmObjectObserver;

/**
 */
public class SearchResultFragment extends AbstractMainFragment {
    public SearchResultFragment(){}

    private SearchIssueQuery searchIssueQuery;
    private RealmObjectObserver<SearchIssueProcedure> searchProcedureObserver;
    private IssueListAdapter issueListAdapter;

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
        final long queryId = args.getLong("queryItemId");
        searchIssueQuery = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<SearchIssueQuery>() {
            @Override
            public SearchIssueQuery execute(Realm realm) throws Exception {
                return realm.where(SearchIssueQuery.class).equalTo("id", queryId).findFirst();
            }
        });
        searchProcedureObserver = new RealmObjectObserver<SearchIssueProcedure>() {
            @Override
            protected RealmQuery<SearchIssueProcedure> query(Realm realm) {
                return realm.where(SearchIssueProcedure.class).equalTo("queryId", queryId);
            }

            @Override
            protected void onChange(SearchIssueProcedure searchIssueProcedure) {
                onRenderSearchProcedure(searchIssueProcedure);
            }
        };
    }

    @Override
    protected void onSetupToolbar() {
        activityToolbar.setTitle(searchIssueQuery.getTitle());
    }

    @Override
    protected int getLayout() {
        return R.layout.search_result_screen;
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {
        issueListAdapter = new IssueListAdapter();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(issueListAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchLatestResults();
            }
        });
        if (savedInstanceState == null) {
            fetchLatestResults();
        }
        searchProcedureObserver.sub();
    }

    @Override
    public void onDestroyView() {
        searchProcedureObserver.unsub();
        super.onDestroyView();
    }

    private void fetchLatestResults() {
        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                realm.createOrUpdateObjectFromJson(SearchIssueProcedure.class, new JSONObject()
                        .put("queryId", searchIssueQuery.getId())
                        .put("syncState", SyncState.NOT_SYNCED)
                        .put("query", new JSONObject()
                                .put("id", searchIssueQuery.getId()))
                );
                return null;
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                GitHubViewerService.keepAlive(getContext());
                return null;
            }
        });
    }

    private void onRenderSearchProcedure(SearchIssueProcedure procedure) {
        if (procedure == null) return;

        int syncState = procedure.getSyncState();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        if (syncState == SyncState.NOT_SYNCED || syncState == SyncState.SYNCING) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        issueListAdapter.updateIssueList(procedure.getItems());
    }
}
