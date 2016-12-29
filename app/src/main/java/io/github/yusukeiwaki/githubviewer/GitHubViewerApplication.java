package io.github.yusukeiwaki.githubviewer;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.github.yusukeiwaki.githubviewer.background_fetch.BackgroundFetchJob;
import io.github.yusukeiwaki.githubviewer.background_fetch.BackgroundFetchJobCreator;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 */
public class GitHubViewerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build());

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        JobManager.create(this).addJobCreator(new BackgroundFetchJobCreator());
        BackgroundFetchJob.scheduleOrUpdate();
    }
}
