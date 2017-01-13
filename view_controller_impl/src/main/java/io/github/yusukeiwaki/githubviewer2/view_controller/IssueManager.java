package io.github.yusukeiwaki.githubviewer2.view_controller;

import java.util.List;

import io.github.yusukeiwaki.githubviewer2.model.Issue;

/**
 */
public interface IssueManager {
    interface Callback {
        void onSuccess();

        void onError(Exception e);
    }

    void refresh(Callback callback);

    void loadMore(Callback callback);

    List<Issue> getIssues();
}
