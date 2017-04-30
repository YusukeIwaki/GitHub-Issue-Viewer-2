package io.github.yusukeiwaki.githubviewer2.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class Label extends RealmObject {
    @PrimaryKey public long id;
    public String url;
    public String name;
    public String color;
}
