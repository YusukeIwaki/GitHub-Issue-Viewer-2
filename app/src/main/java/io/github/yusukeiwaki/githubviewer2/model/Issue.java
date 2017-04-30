package io.github.yusukeiwaki.githubviewer2.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class Issue extends RealmObject {
    private final static String REPO_URL_PREFIX = "https://api.github.com/repos/";

    @PrimaryKey public long id;
    public String html_url;
    public String repository_url;
    public String title;
    public long number;
    public RealmList<Label> labels;
    public User user;
    public String state;
    public User assignee;
    public RealmList<User> assignees;
    public long comments;
    public Date created_at;
    public Date updated_at;
    public Date closed_at;
    public String pull_request;//JSON

    public String getRepositoryName() {
        return repository_url.substring(REPO_URL_PREFIX.length());
    }

    public boolean isTheSame(Issue other) {
        return title.equals(other.title)
                && state.equals(other.state)
                && updated_at.equals(other.updated_at);
    }
}
