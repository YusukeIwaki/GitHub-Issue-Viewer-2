package io.github.yusukeiwaki.githubviewer2.service2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import io.github.yusukeiwaki.githubviewer2.service2.plugin.NewIssueNotificationManager;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 */
public class GitHubViewerService extends Service {

    private static final String TAG = GitHubViewerService.class.getSimpleName();

    public static void keepAlive(Context context) {
        context.startService(new Intent(context, GitHubViewerService.class));
    }

    private final ArrayList<GitHubViewerServicePlugin> plugins = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
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
            NewIssueNotificationManager.class
    };

    private void loadPlugins() {
        final Context context = getApplicationContext();
        for(Class clazz: PLUGINS){
            try {
                Constructor ctor = clazz.getConstructor(Context.class);
                Object obj = ctor.newInstance(context);

                if(obj instanceof GitHubViewerServicePlugin) {
                    GitHubViewerServicePlugin l = (GitHubViewerServicePlugin) obj;
                    plugins.add(l);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }
    }

    private void registerPlugins() {
        for (GitHubViewerServicePlugin plugin : plugins) {
            plugin.register();
        }
    }

    private void unregisterPlugins() {
        for (GitHubViewerServicePlugin plugin : plugins) {
            plugin.unregister();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
