package io.github.yusukeiwaki.githubviewer.main;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;
import io.github.yusukeiwaki.githubviewer.main.dialog.EditQueryDialogFragment;
import io.github.yusukeiwaki.githubviewer.model.User;
import io.github.yusukeiwaki.githubviewer.renderer.UserRenderer;

/**
 */
public class SideNavManager {
    public interface DrawerHandler {
        void closeDrawer();
    }

    private final MainActivity activity;
    private final ViewGroup rootView;
    private final DrawerHandler drawerHandler;


    public SideNavManager(MainActivity activity, ViewGroup rootView, DrawerHandler drawerHandler) {
        this.activity = activity;
        this.rootView = rootView;
        this.drawerHandler = drawerHandler;
    }

    public void setup() {
        rootView.findViewById(R.id.btn_add_search_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditQueryDialogFragment().show(activity.getSupportFragmentManager(), EditQueryDialogFragment.class.getSimpleName());
            }
        });
        rootView.findViewById(R.id.nav_item_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_title_logout)
                        .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CurrentUserData.deleteAll(activity);
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                drawerHandler.closeDrawer();
                            }
                        })
                        .show();
            }
        });
    }

    public void updateCurrentUser(User currentUser) {
        new UserRenderer(rootView.getContext(), currentUser)
                .avatarInto((ImageView) rootView.findViewById(R.id.current_user_avatar))
                .usernameInto((TextView) rootView.findViewById(R.id.current_user_name));
    }
}
