package io.github.yusukeiwaki.githubviewer2.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import io.github.yusukeiwaki.githubviewer2.R;
import io.github.yusukeiwaki.githubviewer2.fecade.Fecade;
import io.github.yusukeiwaki.githubviewer2.model.SyncState;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueQuery;
import io.realm.Realm;
import io.realm.RealmQuery;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmObjectObserver;

/**
 */
public class SearchResultFragment extends AbstractMainFragment {
    public SearchResultFragment(){}

    private SearchIssueQuery searchIssueQuery;
    private RealmObjectObserver<SearchIssueProcedure> searchProcedureObserver;
    private RecyclerView recyclerView;
    private IssueListAdapter issueListAdapter;
    private LoadMoreScrollListener loadMoreScrollListener;

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
        searchIssueQuery = Fecade.forIssueList().getQueryById(queryId);
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

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.recyclerview_column_count), StaggeredGridLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(issueListAdapter);

        loadMoreScrollListener = new LoadMoreScrollListener(layoutManager, 15, LoadMoreScrollListener.DIRECTION_DOWN) {
            @Override
            public void requestMoreItem() {
                Fecade.forIssueList().fetchMoreResults(getContext(), searchIssueQuery.getId());
            }
        };

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fecade.forIssueList().fetchLatestResults(getContext(), searchIssueQuery.getId());
            }
        });
        if (savedInstanceState == null) {
            Fecade.forIssueList().fetchLatestResults(getContext(), searchIssueQuery.getId());
        }
        searchProcedureObserver.sub();
    }

    @Override
    public void onStart() {
        super.onStart();

        final long queryId = searchIssueQuery.getId();
        Fecade.forIssueList().markQueryAsRead(queryId);
    }

    @Override
    public void onDestroyView() {
        searchProcedureObserver.unsub();
        super.onDestroyView();
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
            if (loadMoreScrollListener != null) {
                loadMoreScrollListener.setLoadingDone();
                if (syncState == SyncState.SYNCED) {
                    recyclerView.removeOnScrollListener(loadMoreScrollListener);
                    if (procedure.getItems().size() < procedure.getTotal_count()) recyclerView.addOnScrollListener(loadMoreScrollListener);
                }
            }
        }

        issueListAdapter.updateIssueList(procedure.getItems());
    }
}
