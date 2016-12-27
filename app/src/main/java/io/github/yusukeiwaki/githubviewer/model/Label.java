package io.github.yusukeiwaki.githubviewer.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class Label extends RealmObject {
    @PrimaryKey private long id;
    private String url;
    private String name;
    private String color;
}
