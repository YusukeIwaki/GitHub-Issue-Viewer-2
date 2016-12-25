package io.github.yusukeiwaki.githubviewer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 */
public class Cache {
    public static final String KEY_QUERY_ITEM_ID = "query_item_id";

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences("cache", Context.MODE_PRIVATE);
    }
}
