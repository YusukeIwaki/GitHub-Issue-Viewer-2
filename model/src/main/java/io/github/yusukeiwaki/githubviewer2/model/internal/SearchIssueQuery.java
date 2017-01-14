package io.github.yusukeiwaki.githubviewer2.model.internal;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */
public class SearchIssueQuery extends RealmObject {
    @PrimaryKey private long id;
    private String title;
    private String q;
    private String sort;
    private String order;
    private int page;
    private long lastSeenAt;
    private int unreadCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(long lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public static void insertRecord(Realm realm, long queryId, String title, String queryText) throws JSONException {
        realm.createOrUpdateObjectFromJson(SearchIssueQuery.class, new JSONObject()
                .put("id", queryId)
                .put("title", title)
                .put("q", queryText)
                .put("order", "desc")
                .put("page", 1)
                .put("sort", "updated"));
    }
}
