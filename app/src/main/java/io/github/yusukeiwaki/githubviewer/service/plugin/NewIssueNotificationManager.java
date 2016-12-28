package io.github.yusukeiwaki.githubviewer.service.plugin;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;
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

    private Task<Notification> generateNotificationFor(final SearchIssueProcedure procedure) {
        return getIconUrlFrom(procedure.getItems())
                .onSuccessTask(new Continuation<String, Task<Bitmap>>() {
                    @Override
                    public Task<Bitmap> then(Task<String> task) throws Exception {
                        final float dp = context.getResources().getDisplayMetrics().density;
                        return getBitmapForUser(task.getResult(), (int) (48*dp));
                    }
                })
                .continueWithTask(new Continuation<Bitmap, Task<Notification>>() {
                    @Override
                    public Task<Notification> then(Task<Bitmap> task) throws Exception {

                        int numUnread = 0;
                        final long lastSeen = procedure.getQuery().getLastSeenAt();
                        StringBuilder sb = new StringBuilder();
                        sb.append(context.getResources().getString(R.string.notification_title_unread_issue));
                        for (Issue issue : procedure.getItems())  {
                            if (issue.getUpdated_at().getTime() > lastSeen) {
                                numUnread++;
                                sb.append("\n> ").append(issue.getTitle());
                            }
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setContentTitle(procedure.getQuery().getTitle())
                                .setContentText(sb.toString())
                                .setNumber(numUnread)
                                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                .setSmallIcon(R.drawable.ic_notification);

                        if (!task.isFaulted()) {
                            builder.setLargeIcon(task.getResult());
                        }

                        Notification notification = new NotificationCompat.BigTextStyle(builder)
                                .bigText(sb.toString())
                                .build();

                        return Task.forResult(notification);
                    }
                });
    }

    private Task<String> getIconUrlFrom(List<Issue> issues) {
        return Task.forError(new UnsupportedOperationException("Not implemented."));
    }

    private Task<Bitmap> getBitmapForUser(final String iconUrl, final int size) {
        final TaskCompletionSource<Bitmap> task = new TaskCompletionSource<>();

        // Picasso can be triggered only on Main Thread.
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    getBitmapForUser(iconUrl, size).continueWith(new Continuation<Bitmap, Object>() {
                        @Override
                        public Object then(Task<Bitmap> _task) throws Exception {
                            if (_task.isFaulted()) {
                                task.setError(_task.getError());
                            } else {
                                task.setResult(_task.getResult());
                            }
                            return null;
                        }
                    });
                }
            });
            return task.getTask();
        }

        Picasso.with(context)
                .load(iconUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (bitmap != null) {
                            task.trySetResult(bitmap);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        task.setError(new Exception("onBitmapFailed"));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
        return task.getTask();
    }
}
