package io.github.yusukeiwaki.githubviewer.main;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;
import icepick.State;
import io.github.yusukeiwaki.githubviewer.LaunchUtil;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.cache.Cache;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;
import io.github.yusukeiwaki.githubviewer.main.dialog.EditQueryDialogFragment;
import io.github.yusukeiwaki.githubviewer.model.User;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.githubviewer.renderer.UserRenderer;
import io.github.yusukeiwaki.githubviewer.webapi.GitHubAPI;
import io.realm.Realm;
import jp.co.crowdworks.realm_java_helpers.RealmHelper;
import rx.functions.Action0;

public class MainActivity extends AbstractCurrentUserActivity {

    private SideNavQueryListManager queryListManager;

    @State long currentQueryItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.btn_add_search_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditQueryDialogFragment().show(getSupportFragmentManager(), EditQueryDialogFragment.class.getSimpleName());
            }
        });
        findViewById(R.id.nav_item_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.dialog_title_logout)
                        .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CurrentUserData.deleteAll(MainActivity.this);
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                closeDrawerIfNeeded();
                            }
                        })
                        .show();
            }
        });
        queryListManager = new SideNavQueryListManager((LinearLayout) findViewById(R.id.side_query_item_container));
        queryListManager.setOnItemClicked(new SideNavQueryListManager.Callback() {
            @Override
            public void onItemSelected(long itemId) {
                Cache.get(MainActivity.this).edit()
                        .putLong(Cache.KEY_QUERY_ITEM_ID, itemId)
                        .apply();
                closeDrawerIfNeeded();
            }
        });
        queryListManager.setOnItemLongClicked(new SideNavQueryListManager.Callback() {
            @Override
            public void onItemSelected(final long itemId) {
                new AlertDialog.Builder(MainActivity.this)
                        .setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{getString(R.string.edit), getString(R.string.delete)}), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    editQuery(itemId);
                                } else if (which == 1) {
                                    deleteQuery(itemId);
                                }
                            }
                        })
                        .show();
            }
        });

        currentQueryItemId = showFragmentForCurrentQueryItemId(Cache.get(this));
    }

    private long showFragmentForCurrentQueryItemId(SharedPreferences prefs) {
        final long itemId = prefs.getLong(Cache.KEY_QUERY_ITEM_ID, -1);
        if (itemId != -1) {
            SearchIssueQuery query = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<SearchIssueQuery>() {
                @Override
                public SearchIssueQuery execute(Realm realm) throws Throwable {
                    return realm.where(SearchIssueQuery.class).equalTo("id", itemId).findFirst();
                }
            });
            if (query != null) {
                showFragment(SearchResultFragment.create(itemId));
                return itemId;
            }
        }
        showFragment(new HomeFragment());
        return -1;
    }

    @Override
    protected void onRenderCurrentUser(User currentUser) {
        new UserRenderer(this, currentUser)
                .avatarInto((ImageView) findViewById(R.id.current_user_avatar))
                .usernameInto((TextView) findViewById(R.id.current_user_name));
    }

    private SharedPreferences.OnSharedPreferenceChangeListener cacheListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (Cache.KEY_QUERY_ITEM_ID.equals(key)) {
                long itemId = prefs.getLong(key, -1);
                if (itemId != currentQueryItemId) {
                    currentQueryItemId = showFragmentForCurrentQueryItemId(prefs);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        queryListManager.sub();
        Cache.get(this).registerOnSharedPreferenceChangeListener(cacheListener);
    }

    @Override
    protected void onStop() {
        Cache.get(this).unregisterOnSharedPreferenceChangeListener(cacheListener);
        queryListManager.unsub();
        super.onStop();
    }

    @Override
    protected void onAuthRequired() {
        LaunchUtil.showLoginActivity(this, null);
    }

    @Override
    protected void onTokenVerified() {
        new GitHubAPI(this).getCurrentUser()
                .onSuccess(new Continuation<JSONObject, Object>() {
                    @Override
                    public Object then(Task<JSONObject> task) throws Exception {
                        final JSONObject userJson = task.getResult();
                        final long userId = userJson.getLong("id");
                        RealmHelper.rxExecuteTransaction(new RealmHelper.Transaction() {
                            @Override
                            public Object execute(Realm realm) throws Throwable {
                                realm.createOrUpdateObjectFromJson(User.class, userJson);
                                return null;
                            }
                        }).subscribe(new Action0() {
                            @Override
                            public void call() {
                                CurrentUserData.get(MainActivity.this).edit()
                                        .putLong(CurrentUserData.KEY_USER_ID, userId)
                                        .apply();
                            }
                        });
                        return null;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawerIfNeeded()) {
            super.onBackPressed();
        }
    }

    private boolean closeDrawerIfNeeded() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    private void editQuery(long id) {
        EditQueryDialogFragment.create(id).show(getSupportFragmentManager(), EditQueryDialogFragment.class.getSimpleName());
    }

    private void deleteQuery(final long id) {
        RealmHelper.rxExecuteTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Throwable {
                return realm.where(SearchIssueQuery.class).equalTo("id", id).findAll().deleteAllFromRealm();
            }
        }).subscribe(new Action0() {
            @Override
            public void call() {
                SharedPreferences prefs = Cache.get(MainActivity.this);
                if (prefs.getLong(Cache.KEY_QUERY_ITEM_ID, -1) == id) {
                    prefs.edit()
                            .remove(Cache.KEY_QUERY_ITEM_ID)
                            .apply();
                }
                closeDrawerIfNeeded();
            }
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container, fragment)
                .commit();
    }
}
