package com.dss886.pagingrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

/**
 * Created by dss886 on 16/7/25.
 */
public class PagingRecyclerView extends RecyclerView {

    public static final int HEAD = 1;
    public static final int FOOT = 2;

    private PagingAdapterDecorator mAdapter;
    private OnScrollListener mListener;

    public PagingRecyclerView(Context context) {
        super(context);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        boolean supportLayoutManager = layout instanceof LinearLayoutManager
                || layout instanceof StaggeredGridLayoutManager;
        if (supportLayoutManager) {
            addOnScrollListener(new PagingScrollListener());
        } else {
            Log.w("PagingRecyclerView", "You are using a custom LayoutManager and OnScrollListener cannot be set automatically, you need to implement and add it by yourself.");
        }
        super.setLayoutManager(layout);
    }

    @Override
    public void addOnScrollListener(OnScrollListener listener) {
        super.addOnScrollListener(listener);
        mListener = listener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        //noinspection unchecked
        this.mAdapter = new PagingAdapterDecorator(getContext(), this, adapter);
        if (getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager grid = (GridLayoutManager) getLayoutManager();
            int spanCount = grid.getSpanCount();
            GridLayoutManager.SpanSizeLookup lookup = grid.getSpanSizeLookup();
            grid.setSpanSizeLookup(new PagingSpanSizeLookup(mAdapter, spanCount, lookup));
        }
        super.setAdapter(mAdapter);
    }

    public void setOnPagingListener(OnPagingListener listener) {
        if (mAdapter == null){
            throw new IllegalArgumentException("Please set adapter before setting OnPagingListener!");
        }
        mAdapter.setOnPagingListener(listener);
        getViewTreeObserver().addOnGlobalLayoutListener(new FirstShowListener());
    }

    @Override
    public void scrollToPosition(int position) {
        if (mAdapter.isHeader(0)) {
            position++;
        }
        super.scrollToPosition(position);
    }

    public void setPageEnable(boolean header, boolean footer) {
        mAdapter.setPageEnable(header, footer);
        mListener.onScrollStateChanged(this, getScrollState());
    }

    public void onPaging(int direction) {
        mAdapter.onPaging(direction);
    }

    public void onFailure(int direction) {
        mAdapter.onFailure(direction);
    }

    public void onNoMoreData(int direction) {
        mAdapter.onNoMoreData(direction);
    }

    public int getFixedPosition(int position) {
        return mAdapter.getFixedPosition(position);
    }

    /**
     * OnScrollStateChanged will not be called when the first time PagingRecyclerView shows.
     * We need to add a listener to detect this and call it manually.
     */
    private class FirstShowListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mListener.onScrollStateChanged(PagingRecyclerView.this, getScrollState());
            }
        }
    }

    public interface OnPagingListener{
        void onPaging(PagingRecyclerView view, int direction);
    }
}
