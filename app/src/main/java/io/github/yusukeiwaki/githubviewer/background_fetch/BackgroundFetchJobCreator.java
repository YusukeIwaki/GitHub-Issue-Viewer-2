package io.github.yusukeiwaki.githubviewer.background_fetch;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 */
public class BackgroundFetchJobCreator implements JobCreator {
    @Override
    public Job create(String tag) {
        if (BackgroundFetchJob.TAG.equals(tag)) {
            return new BackgroundFetchJob();
        }
        return null;
    }
}
