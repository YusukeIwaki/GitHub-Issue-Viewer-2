package io.github.yusukeiwaki.githubviewer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.cache.Cache;
import io.github.yusukeiwaki.githubviewer.model.User;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer.renderer.UserRenderer;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers_bolts.RealmHelper;

/**
 */
public class HomeFragment extends AbstractCurrentUserFragment {
    public HomeFragment(){}

    @Override
    protected int getLayout() {
        return R.layout.home_screen;
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onCurrentUserUpdated(User user) {
        new UserRenderer(getContext(), user).avatarInto((ImageView) rootView.findViewById(R.id.current_user_avatar));

        if (user != null) {
            final String username = user.getLogin();
            rootView.findViewById(R.id.add_query_involves_me).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addQueryInvolvesMe(username);
                }
            });
        } else {
            rootView.findViewById(R.id.add_query_involves_me).setOnClickListener(null);
            rootView.findViewById(R.id.add_query_involves_me).setClickable(false);
        }
    }

    private void addQueryInvolvesMe(final String username) {
        final long queryId = System.currentTimeMillis();
        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                SearchIssueQuery.insertRecord(realm, queryId, "me", "involves:" + username);
                return null;
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                Cache.get(getContext()).edit()
                        .putLong(Cache.KEY_QUERY_ITEM_ID, queryId)
                        .apply();
                return null;
            }
        });
    }
}
