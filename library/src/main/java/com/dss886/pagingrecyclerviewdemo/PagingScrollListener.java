package com.dss886.pagingrecyclerviewdemo;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by dss886 on 16/7/25.
 */
public class PagingScrollListener extends RecyclerView.OnScrollListener {

    // Cache the arrays of the temp positions to avoiding create arrays every times.
    private int[] staggerFirst;
    private int[] staggerLast;

    private int firstVisibleItemPosition;
    private int lastVisibleItemPosition;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // This listener will be used only in the case of LinearLayoutManager and
        // StaggeredGridLayoutManager, where GridLayoutManager is a subclass of LinearLayoutManager.
        // In other cases, developers must implement and add their own listener.
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            if (staggerFirst == null) staggerFirst = new int[staggeredGridLayoutManager.getSpanCount()];
            if (staggerLast == null) staggerLast = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findFirstVisibleItemPositions(staggerFirst);
            staggeredGridLayoutManager.findLastVisibleItemPositions(staggerLast);
            firstVisibleItemPosition = getTargetPosition(staggerFirst, true);
            lastVisibleItemPosition = getTargetPosition(staggerLast, false);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
            PagingAdapterDecorator adapter = (PagingAdapterDecorator) recyclerView.getAdapter();
            PagingRecyclerView pagingRecyclerView = (PagingRecyclerView) recyclerView;
            if (firstVisibleItemPosition <= 0) {
                adapter.onScrolledToEdge(pagingRecyclerView, PagingRecyclerView.DIRECTION_HEAD);
            } else if (lastVisibleItemPosition >= totalItemCount - 1) {
                adapter.onScrolledToEdge(pagingRecyclerView, PagingRecyclerView.DIRECTION_FOOT);
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
