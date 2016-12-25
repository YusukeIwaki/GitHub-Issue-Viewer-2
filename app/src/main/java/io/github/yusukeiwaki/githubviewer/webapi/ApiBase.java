package io.github.yusukeiwaki.githubviewer.webapi;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bolts.Task;
import bolts.TaskCompletionSource;
import io.github.yusukeiwaki.githubviewer.cache.CurrentUserData;
import io.github.yusukeiwaki.githubviewer.OkHttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 */
public class ApiBase {
    protected final Context context;

    protected ApiBase(Context context) {
        this.context = context;
    }

    protected final Task<JSONObject> jsonRequest(Request request) {
        final TaskCompletionSource<JSONObject> task = new TaskCompletionSource<>();
        OkHttpHelper.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                task.setError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        CurrentUserData.deleteAll(context);
                        task.setError(new Unauthorized());
                    } else {
                        task.setError(new HttpError(response.code(), response.body().string()));
                    }
                    return;
                }
                try {
                    JSONObject responseJson = new JSONObject(response.body().string());
                    task.setResult(responseJson);
                } catch (JSONException e) {
                    task.setError(e);
                }
            }
        });
        return task.getTask();
    }

    protected final Task<JSONObject> jsonGET(String url) {
        String token = CurrentUserData.get(context).getString(CurrentUserData.KEY_TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            return Task.forError(new Unauthorized());
        }

        Request request = new Request.Builder()
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + token)
                .url(url)
                .build();

        return jsonRequest(request);
    }

}
