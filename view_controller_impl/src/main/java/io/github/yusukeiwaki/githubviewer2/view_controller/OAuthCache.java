package io.github.yusukeiwaki.githubviewer2.view_controller;

/**
 */
public interface OAuthCache {
    String getCode();

    void setCode(String state);

    String getState();

    void setState(String state);

    void clear();
}
