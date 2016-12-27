package io.github.yusukeiwaki.githubviewer.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer.widget.SideMenuQueryItemView;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmListObserver;

/**
 */
public class SideNavQueryListManager {
    private RealmListObserver<SearchIssueQuery> queryObserver;
    private LinearLayout container;

    public interface Callback {
        void onItemSelected(long itemId);
    }

    private Callback onItemClicked;
    private Callback onItemLongClicked;

    public SideNavQueryListManager(LinearLayout container) {
        this.container = container;
        queryObserver = new RealmListObserver<SearchIssueQuery>() {
            @Override
            protected RealmResults<SearchIssueQuery> queryItems(Realm realm) {
                return realm.where(SearchIssueQuery.class).findAll();
            }

            @Override
            protected void onCollectionChanged(List<SearchIssueQuery> list) {
                onRenderQueryList(list);
            }
        };
    }

    public void sub() {
        queryObserver.sub();
    }

    public void unsub() {
        queryObserver.unsub();
    }

    public void setOnItemClicked(Callback onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    public void setOnItemLongClicked(Callback onItemLongClicked) {
        this.onItemLongClicked = onItemLongClicked;
    }

    private void onRenderQueryList(List<SearchIssueQuery> queryList) {
        final Context context = container.getContext();
        container.removeAllViews();
        for (SearchIssueQuery query : queryList) {
            SideMenuQueryItemView view = new SideMenuQueryItemView(context);
            view.setItemId(query.getId());
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setTitle(query.getTitle());
            view.setUnreadCount(query.getUnreadCount());
            container.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClicked != null) {
                        onItemClicked.onItemSelected(((SideMenuQueryItemView) view).getItemId());
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemLongClicked != null) {
                        onItemLongClicked.onItemSelected(((SideMenuQueryItemView) view).getItemId());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
