package io.github.yusukeiwaki.githubviewer.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class User extends RealmObject {
    @PrimaryKey private long id;
    private String login;
    private String avatar_url;
}
