package io.github.yusukeiwaki.githubviewer2.model.internal;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class SearchIssueQuery extends RealmObject {
    @PrimaryKey public long id;
    public String title;
    public String q;
    public String sort;
    public String order;
    public int page;
    public long lastSeenAt;
    public int unreadCount;

    public static void insertOrUpdateRecord(Realm realm, long queryId, String title, String queryText) throws JSONException {
        realm.createOrUpdateObjectFromJson(SearchIssueQuery.class, new JSONObject()
                .put("id", queryId)
                .put("title", title)
                .put("q", queryText)
                .put("order", "desc")
                .put("page", 1)
                .put("sort", "updated"));
    }
}
