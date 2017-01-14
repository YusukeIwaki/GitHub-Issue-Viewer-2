package io.github.yusukeiwaki.githubviewer2;

import android.util.Log;

import bolts.Continuation;
import bolts.Task;

/**
 */
public class LogcatIfError implements Continuation {

    private static final String TAG = LogcatIfError.class.getSimpleName();

    @Override
    public Object then(Task task) throws Exception {
        if (task.isFaulted()) {
            Log.d(TAG, task.getError().getMessage(), task.getError());
        }
        return null;
    }
}
