package io.github.yusukeiwaki.githubviewer.webapi;

/**
 */
public class HttpError extends Exception {
    public int code;
    public String body;

    public HttpError(int code, String body) {
        super(body);
        this.code = code;
        this.body = body;
    }
}
