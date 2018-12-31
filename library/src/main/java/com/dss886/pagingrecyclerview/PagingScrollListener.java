package com.dss886.pagingrecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by dss886 on 16/7/25.
 */
public class PagingScrollListener extends RecyclerView.OnScrollListener {

    private PagingAdapterDecorator mAdapter;

    // Cache the arrays of the temp positions to avoiding create arrays every times.
    private int[] staggerFirst;
    private int[] staggerLast;

    private int firstVisibleItemPosition;
    private int lastVisibleItemPosition;

    PagingScrollListener(PagingAdapterDecorator adapter) {
        mAdapter = adapter;
    }

    public void setAdapter(PagingAdapterDecorator adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // This listener will be used only in the case of LinearLayoutManager and
        // StaggeredGridLayoutManager, where GridLayoutManager is a subclass of LinearLayoutManager.
        // In other cases, developers must implement and add their own listener.
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
            firstVisibleItemPosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) lm;
            if (staggerFirst == null) staggerFirst = new int[staggeredGridLayoutManager.getSpanCount()];
            if (staggerLast == null) staggerLast = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findFirstVisibleItemPositions(staggerFirst);
            staggeredGridLayoutManager.findLastVisibleItemPositions(staggerLast);
            firstVisibleItemPosition = getTargetPosition(staggerFirst, true);
            lastVisibleItemPosition = getTargetPosition(staggerLast, false);
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if (visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                PagingRecyclerView pagingRecyclerView = (PagingRecyclerView) recyclerView;
                if (mAdapter != null) {
                    if (firstVisibleItemPosition <= 0) {
                        mAdapter.onScrolledToEdge(pagingRecyclerView, PagingRecyclerView.HEAD);
                    }
                    if (lastVisibleItemPosition >= totalItemCount - 1) {
                        mAdapter.onScrolledToEdge(pagingRecyclerView, PagingRecyclerView.FOOT);
                    }
                }
            }
        }
    }

    private int getTargetPosition(int[] positionList, boolean isHead) {
        int target = positionList[0];
        for (int value : positionList) {
            if ((isHead && value < target) || (!isHead && value > target)) {
                target = value;
            }
        }
        return target;
    }

}
