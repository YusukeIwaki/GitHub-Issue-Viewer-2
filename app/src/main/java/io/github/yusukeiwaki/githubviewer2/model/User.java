package io.github.yusukeiwaki.githubviewer2.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

/**
 */
public class User extends RealmObject {
    @PrimaryKey public long id;
    public String login;
    public String avatar_url;

    public static RealmQuery<User> queryUserById(Realm realm, long id) {
        return realm.where(User.class).equalTo("id", id);
    }
}
