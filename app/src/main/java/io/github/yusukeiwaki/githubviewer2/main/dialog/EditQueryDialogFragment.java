package io.github.yusukeiwaki.githubviewer2.main.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.R;
import io.github.yusukeiwaki.githubviewer2.cache.Cache;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueQuery;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.realm.Realm;

/**
 */
public class EditQueryDialogFragment extends DialogFragment {
    public EditQueryDialogFragment() {}

    public static EditQueryDialogFragment create(long id) {
        Bundle args = new Bundle();
        args.putLong("id", id);

        EditQueryDialogFragment f = new EditQueryDialogFragment();
        f.setArguments(args);
        return f;
    }

    private SearchIssueQuery originalQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            final long id = args.getLong("id");
            originalQuery = RealmHelper.executeTransactionForRead(new RealmHelper.Transaction<SearchIssueQuery>() {
                @Override
                public SearchIssueQuery execute(Realm realm) throws Exception {
                    return realm.where(SearchIssueQuery.class).equalTo("id", id).findFirst();
                }
            });
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setContentView(R.layout.dialog_edit_query);
        onSetupDialog();
    }

    private void onSetupDialog() {
        final Dialog dialog = getDialog();
        if (originalQuery != null) {
            ((TextView) dialog.findViewById(R.id.editor_title)).setText(originalQuery.title);
            ((TextView) dialog.findViewById(R.id.editor_query)).setText(originalQuery.q);
        }

        dialog.findViewById(R.id.btn_add_search_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = getDialog();
                String title = ((TextView) dialog.findViewById(R.id.editor_title)).getText().toString();
                String query = ((TextView) dialog.findViewById(R.id.editor_query)).getText().toString();

                insertQueryRecord(title, query);
            }
        });
    }

    private void insertQueryRecord(final String title, final String queryText) {
        final long id = originalQuery != null ? originalQuery.id : -1;
        final long queryId = (id == -1) ? System.currentTimeMillis() : id;
        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                SearchIssueQuery.insertOrUpdateRecord(realm, queryId, title, queryText);
                return null;
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                dismiss();

                if (id == -1) {
                    Cache.get(getContext()).edit()
                            .putLong(Cache.KEY_QUERY_ITEM_ID, queryId)
                            .apply();
                }
                return null;
            }
        });
    }
}
