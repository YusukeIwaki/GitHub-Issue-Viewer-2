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
    private String repository_url;
    private String title;
    private long number;
    private RealmList<Label> labels;
    private User user;
    private String state;
    private User assignee;
    private RealmList<User> assignees;
    private long comments;
    private Date created_at;
    private Date updated_at;
    private Date closed_at;
    private String pull_request;//JSON

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getRepository_url() {
        return repository_url;
    }

    public void setRepository_url(String repository_url) {
        this.repository_url = repository_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public RealmList<Label> getLabels() {
        return labels;
    }

    public void setLabels(RealmList<Label> labels) {
        this.labels = labels;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public RealmList<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(RealmList<User> assignees) {
        this.assignees = assignees;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(Date closed_at) {
        this.closed_at = closed_at;
    }

    public String getPull_request() {
        return pull_request;
    }

    public void setPull_request(String pull_request) {
        this.pull_request = pull_request;
    }
}
