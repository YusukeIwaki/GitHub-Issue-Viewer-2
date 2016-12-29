package io.github.yusukeiwaki.githubviewer2.main;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public abstract class LoadMoreScrollListener extends RecyclerView.OnScrollListener{
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;

    private final StaggeredGridLayoutManager staggeredGridLayoutManager;
    private boolean isLoading;
    private final int threshold;
    private final int directionSig;

    public LoadMoreScrollListener(StaggeredGridLayoutManager layoutManager, int loadThreshold, int direction){
        staggeredGridLayoutManager = layoutManager;
        threshold = loadThreshold;

        setLoadingDone();
        directionSig = direction==DIRECTION_UP? -1 : 1;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        final int visibleItemCount = recyclerView.getChildCount();
        final int totalItemCount = staggeredGridLayoutManager.getItemCount();
        final int firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];

        if (!isLoading && firstVisibleItem + visibleItemCount >= totalItemCount - threshold
                && visibleItemCount < totalItemCount //スクロールしないような場合は除外
                && directionSig *dy>0) { //遡る方向へのスクロールのみ
            isLoading = true;
            requestMoreItem();
        }
    }

    public void setLoadingDone(){
        isLoading = false;
    }

    public abstract void requestMoreItem();

}
