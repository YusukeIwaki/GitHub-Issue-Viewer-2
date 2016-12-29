package io.github.yusukeiwaki.githubviewer2.model;

/**
 */
public interface SyncState {
    int NOT_SYNCED = 0;
    int SYNCING = 1;
    int SYNCED = 2;
    int SYNC_ERROR = 3;
}
