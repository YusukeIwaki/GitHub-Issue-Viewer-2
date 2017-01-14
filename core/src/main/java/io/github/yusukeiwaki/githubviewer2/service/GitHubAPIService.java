package io.github.yusukeiwaki.githubviewer2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import io.github.yusukeiwaki.githubviewer2.service.plugin.SearchIssueProcedureObserver;
import io.github.yusukeiwaki.githubviewer2.webapi.GitHubAPI;

/**
 */
public class GitHubAPIService extends Service {

    private static final String TAG = GitHubAPIService.class.getSimpleName();

    public static void keepAlive(Context context) {
        context.startService(new Intent(context, GitHubAPIService.class));
    }

    private GitHubAPI gitHubAPI;

    private final ArrayList<GitHubAPIServicePlugin> plugins = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        gitHubAPI = new GitHubAPI(getBaseContext());
        loadPlugins();
        registerPlugins();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterPlugins();
        super.onDestroy();
    }

    private static final Class[] PLUGINS = {
            SearchIssueProcedureObserver.class
    };

    private void loadPlugins() {
        final Context context = getApplicationContext();
        for(Class clazz: PLUGINS){
            try {
                Constructor ctor = clazz.getConstructor(Context.class, GitHubAPI.class);
                Object obj = ctor.newInstance(context, gitHubAPI);

                if(obj instanceof GitHubAPIServicePlugin) {
                    GitHubAPIServicePlugin l = (GitHubAPIServicePlugin) obj;
                    plugins.add(l);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }
    }

    private void registerPlugins() {
        for (GitHubAPIServicePlugin plugin : plugins) {
            plugin.register();
        }
    }

    private void unregisterPlugins() {
        for (GitHubAPIServicePlugin plugin : plugins) {
            plugin.unregister();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
