package io.github.yusukeiwaki.githubviewer2.view_controller;

import bolts.Task;

/**
 */
public interface OAuthLoginController {
    Task<String> createAccessToken(String code, String state);
}
