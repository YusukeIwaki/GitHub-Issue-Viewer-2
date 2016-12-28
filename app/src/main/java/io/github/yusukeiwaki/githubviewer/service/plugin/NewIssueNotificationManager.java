package io.github.yusukeiwaki.githubviewer.service.plugin;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

import bolts.Continuation;
import bolts.Task;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.model.Issue;
import io.github.yusukeiwaki.githubviewer.model.internal.SearchIssueProcedure;
import io.github.yusukeiwaki.githubviewer.webapi.GitHubAPI;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 */
public class NewIssueNotificationManager extends AbstractRealmModelObserver<SearchIssueProcedure> {
    public NewIssueNotificationManager(Context context, GitHubAPI gitHubAPI) {
        super(context, gitHubAPI);
    }

    @Override
    protected RealmQuery<SearchIssueProcedure> query(Realm realm) {
        return realm.where(SearchIssueProcedure.class).isNotNull("query");

    }

    @Override
    protected void handleItems(@NonNull List<SearchIssueProcedure> items) {
        for (final SearchIssueProcedure procedure : items) {
            final int notificationId = getNotificationIdFor(procedure);
            if (shouldNotify(procedure)) {
                generateNotificationFor(procedure).onSuccess(new Continuation<Notification, Object>() {
                    @Override
                    public Object then(Task<Notification> task) throws Exception {
                        NotificationManagerCompat.from(context).notify(notificationId, task.getResult());
                        return null;
                    }
                });
            } else {
                NotificationManagerCompat.from(context).cancel(notificationId);
            }
        }
    }

    private boolean shouldNotify(SearchIssueProcedure procedure) {
        final long lastSeen = procedure.getQuery().getLastSeenAt();
        for (Issue issue : procedure.getItems())  {
            if (issue.getUpdated_at().getTime() > lastSeen) return true;
        }
        return false;
    }

    private int getNotificationIdFor(SearchIssueProcedure procedure) {
        return (int) (procedure.getQueryId() % Integer.MAX_VALUE);
    }

    private Task<Notification> generateNotificationFor(SearchIssueProcedure procedure) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(procedure.getQuery().getTitle())
                .setContentText("new message")
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher);

        return Task.forResult(builder.build());
    }
}
