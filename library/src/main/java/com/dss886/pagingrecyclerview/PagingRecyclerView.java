package com.dss886.pagingrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dss886 on 16/7/25.
 */
public class PagingRecyclerView extends RecyclerView {

    public static final int HEAD = 1;
    public static final int FOOT = 2;

    private PagingAdapterDecorator mAdapter;
    private PagingScrollListener mInnerScrollListener;
    private OnScrollListener mScrollListener;

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
            addOnScrollListener(mInnerScrollListener = new PagingScrollListener(mAdapter));
        } else {
            Log.w("PagingRecyclerView", "You are using a custom LayoutManager and OnScrollListener cannot be set automatically, you need to implement and add it by yourself.");
        }
        super.setLayoutManager(layout);
    }

    @Override
    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        super.addOnScrollListener(listener);
        mScrollListener = listener;
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
        if (mInnerScrollListener != null) {
            mInnerScrollListener.setAdapter(mAdapter);
        }
        super.setAdapter(mAdapter);
    }

    public void setOnPagingListener(OnPagingListener listener) {
        if (mAdapter == null){
            throw new IllegalArgumentException("Please set adapter before setting OnPagingListener!");
        }
        mAdapter.setOnPagingListener(listener);
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
        mScrollListener.onScrollStateChanged(this, getScrollState());
    }

    /**
     * Override this method to custom your own paging item
     */
    protected AbsPagingViewHolder createPagingViewHolder(LayoutInflater inflater, ViewGroup parent, int direction, PagingRecyclerView.OnPagingListener pagingListener) {
        View view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
        return new DefaultPagingViewHolder(view, direction, this, pagingListener);
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

    public void onHide(int direction) {
        mAdapter.onHide(direction);
    }

    public void onLoading(int direction) {
        mAdapter.onLoading(direction);
    }

    public int getFixedPosition(int position) {
        return mAdapter.getFixedPosition(position);
    }

    public interface OnPagingListener {
        void onPaging(PagingRecyclerView view, int direction);
    }

}
