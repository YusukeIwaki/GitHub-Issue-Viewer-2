package io.github.yusukeiwaki.githubviewer2.view_controller;

/**
 * ログインしてるかどうか、変化通知を受ける、あたりのインターフェース
 */
public interface SessionReader {
    /**
     * ログインしているかどうかをワンショットで返す
     */
    boolean isLoggedIn();

    /**
     * ログイン状態の変化リスナー
     */
    interface Listener {
        void onSessionChanged();
    }

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);
}
