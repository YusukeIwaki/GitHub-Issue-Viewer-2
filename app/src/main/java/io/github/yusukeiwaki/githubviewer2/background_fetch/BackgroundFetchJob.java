package io.github.yusukeiwaki.githubviewer2.background_fetch;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer2.model.SyncState;
import io.github.yusukeiwaki.githubviewer2.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer2.service.GitHubViewerService;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmListObserver;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 */
public class BackgroundFetchJob extends Job {

    public static final String TAG = BackgroundFetchJob.class.getSimpleName();
    private RealmListObserver observer;

    private class ResultRef<T> {
        T result;
        Exception error;

        public boolean isSuccessful() {
            return error == null;
        }
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ResultRef<Boolean> resultRef = new ResultRef<>();
        resultRef.result = false;

        RealmHelper.executeTransaction(new RealmHelper.Transaction() {
            @Override
            public Object execute(Realm realm) throws Exception {
                RealmResults<SearchIssueProcedure> procedures = realm.where(SearchIssueProcedure.class).isNotNull("query").findAll();
                for (SearchIssueProcedure procedure : procedures) {
                    procedure.setSyncState(SyncState.NOT_SYNCED);
                }
                return null;
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                observer = new RealmListObserver<SearchIssueProcedure>() {
                    @Override
                    protected RealmResults<SearchIssueProcedure> queryItems(Realm realm) {
                        return realm.where(SearchIssueProcedure.class).isNotNull("query").findAll();
                    }

                    @Override
                    protected void onCollectionChanged(List<SearchIssueProcedure> list) {
                        boolean success = false;
                        for (SearchIssueProcedure procedure : list) {
                            switch (procedure.getSyncState()) {
                                case SyncState.NOT_SYNCED:
                                case SyncState.SYNCING:
                                    return;
                                case SyncState.SYNCED:
                                    success = true;
                                    break;
                                default: break;
                            }
                        }

                        resultRef.result = success;
                        countDownLatch.countDown();
                        unsub();
                    }
                };
                observer.sub();
                GitHubViewerService.keepAlive(getContext());
                return null;
            }
        });

        try {
            countDownLatch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            resultRef.error = e;
            if (observer != null) {
                observer.unsub();
            }
        }

        return resultRef.isSuccessful() && resultRef.result ? Result.SUCCESS : Result.FAILURE;
    }

    public static int scheduleOrUpdate() {
        return new JobRequest.Builder(TAG)
                .setPeriodic(1800000) //30min.
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
}
