package io.github.yusukeiwaki.githubviewer2.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

/**
 */
public class User extends RealmObject {
    @PrimaryKey private long id;
    private String login;
    private String avatar_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public static RealmQuery<User> queryUserById(Realm realm, long id) {
        return realm.where(User.class).equalTo("id", id);
    }
}
