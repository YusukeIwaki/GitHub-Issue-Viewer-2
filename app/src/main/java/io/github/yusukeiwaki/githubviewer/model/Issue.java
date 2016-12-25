package io.github.yusukeiwaki.githubviewer.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class Issue extends RealmObject {
    @PrimaryKey private long id;
    private String html_url;
    private String title;
    private User user;
    private String state;
    private User assignee;
    private RealmList<User> assignees;
    private long comments;
    private Date created_at;
    private Date updated_at;
    private Date closed_at;
}
