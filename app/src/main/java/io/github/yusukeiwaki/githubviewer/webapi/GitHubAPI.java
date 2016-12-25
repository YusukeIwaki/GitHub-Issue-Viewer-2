package io.github.yusukeiwaki.githubviewer.webapi;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONObject;

import bolts.Task;
import io.github.yusukeiwaki.githubviewer.R;
import okhttp3.FormBody;
import okhttp3.Request;

/**
 */
public class GitHubAPI extends ApiBase {
    private final Resources resources;

    public GitHubAPI(Context context) {
        super(context);
        this.resources = context.getResources();
    }

    public Task<JSONObject> createAccessToken(String code, String state) {

        FormBody body = new FormBody.Builder()
                .add("client_id", resources.getString(R.string.github_client_id))
                .add("client_secret", resources.getString(R.string.github_client_secret))
                .add("code", code)
                .add("state", state)
                .build();

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .header("Accept", "application/json")
                .build();

        return jsonRequest(request);
    }

    public Task<JSONObject> getCurrentUser() {
        return jsonGET("https://api.github.com/user");
    }
}
