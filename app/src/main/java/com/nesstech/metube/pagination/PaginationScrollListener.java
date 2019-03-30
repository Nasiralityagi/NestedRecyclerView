package com.nesstech.metube.pagination;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private LayoutManager layoutManager;

    protected PaginationScrollListener(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItemPosition = 0;
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
            firstVisibleItemPosition = llm.findFirstVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager llm = (GridLayoutManager) layoutManager;
            llm.setSpanCount(2);
            firstVisibleItemPosition = llm.findFirstVisibleItemPosition();
        }
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    public abstract boolean isLoading();

    public abstract boolean isLastPage();

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

}
