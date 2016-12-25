package io.github.yusukeiwaki.githubviewer.main;

import android.view.View;
import android.widget.ImageView;

import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.cache.Cache;
import io.github.yusukeiwaki.githubviewer.model.User;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer.renderer.UserRenderer;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers.RealmHelper;
import rx.functions.Action0;

/**
 */
public class HomeFragment extends AbstractCurrentUserFragment {
    public HomeFragment(){}

    @Override
    protected int getLayout() {
        return R.layout.home_screen;
    }

    @Override
    protected void onRenderCurrentUser(User user) {
        new UserRenderer(getContext(), user).avatarInto((ImageView) rootView.findViewById(R.id.current_user_avatar));
        final String username = user.getLogin();
        rootView.findViewById(R.id.add_query_involves_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQueryInvolvesMe(username);
            }
        });
    }

    private void addQueryInvolvesMe(final String username) {
        final long queryId = System.currentTimeMillis();
        RealmHelper.rxExecuteTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Throwable {
                SearchIssueQuery.insertRecord(realm, queryId, "me", "involves:" + username);
                return null;
            }
        }).subscribe(new Action0() {
            @Override
            public void call() {
                Cache.get(getContext()).edit()
                        .putLong(Cache.KEY_QUERY_ITEM_ID, queryId)
                        .apply();
            }
        });
    }
}
