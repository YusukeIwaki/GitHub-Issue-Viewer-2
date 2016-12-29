package io.github.yusukeiwaki.githubviewer2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import io.github.yusukeiwaki.githubviewer2.login.LoginActivity;
import io.github.yusukeiwaki.githubviewer2.main.MainActivity;

/**
 */
public class LaunchUtil {
    public static void showMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void showLoginActivity(Context context, Uri data) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (data!=null) intent.setData(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void launchBrowser(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
