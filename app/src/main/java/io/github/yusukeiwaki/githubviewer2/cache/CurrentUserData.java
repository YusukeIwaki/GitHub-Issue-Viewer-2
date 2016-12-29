package io.github.yusukeiwaki.githubviewer2.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 */
public class CurrentUserData {
    public static final String KEY_STATE = "state";
    public static final String KEY_CODE = "code";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_TOKEN = "token";

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences("current_user_data", Context.MODE_PRIVATE);
    }

    public static void deleteAll(Context context) {
        CurrentUserData.get(context).edit()
                .remove(CurrentUserData.KEY_USER_ID)
                .remove(CurrentUserData.KEY_TOKEN)
                .apply();
    }
}
