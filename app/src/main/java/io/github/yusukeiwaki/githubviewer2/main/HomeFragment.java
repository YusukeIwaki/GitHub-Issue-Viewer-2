package io.github.yusukeiwaki.githubviewer2.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.R;
import io.github.yusukeiwaki.githubviewer2.cache.Cache;
import io.github.yusukeiwaki.githubviewer2.databinding.HomeScreenBinding;
import io.github.yusukeiwaki.githubviewer2.model.User;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.realm.Realm;

/**
 */
public class HomeFragment extends AbstractCurrentUserFragment {
    private HomeScreenBinding binding;

    public HomeFragment(){}

    @Override
    protected int getLayout() {
        return R.layout.home_screen;
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {
        binding = HomeScreenBinding.bind(rootView);
    }

    @Override
    protected void onCurrentUserUpdated(User user) {
        binding.setUser(user);

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
                SearchIssueQuery.insertRecord(realm, queryId, "Me", "involves:" + username);
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
