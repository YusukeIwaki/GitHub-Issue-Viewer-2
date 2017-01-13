package io.github.yusukeiwaki.githubviewer2.model;

import java.util.List;

/**
 */
public class Issue {
    public interface Type {
        int PULL_REQUEST = 1;
        int ISSUE = 2;
    }
    public interface Status {
        int OPEN = 1;
        int MERGED = 2;
        int CLOSED = 3;
    }

    private String title;
    private String repoName;
    private String url;
    private int type;
    private int status;
    private int number;
    private long lastUpdatedAt;
    private User author;
    private List<Comment> comments;
}
