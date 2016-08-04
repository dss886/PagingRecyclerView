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

    public static final int DIRECTION_HEAD = 0;
    public static final int DIRECTION_FOOT = 1;

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
        if (!(adapter instanceof PagingAdapterDecorator)) {
            throw new IllegalArgumentException("You may need to use a PagingAdapterDecorator to wrap your adapter!");
        }
        this.mAdapter = (PagingAdapterDecorator) adapter;
        this.mAdapter.setPagingRecyclerView(this);
        super.setAdapter(adapter);

        if (getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager grid = (GridLayoutManager) getLayoutManager();
            int spanCount = grid.getSpanCount();
            GridLayoutManager.SpanSizeLookup lookup = grid.getSpanSizeLookup();
            grid.setSpanSizeLookup(new PagingSpanSizeLookup(mAdapter, spanCount, lookup));
        }
    }

    public void setOnPagingListener(OnPagingListener listener) {
        if (mAdapter == null){
            throw new IllegalArgumentException("Please set adapter before setting OnPagingListener!");
        }
        mAdapter.setOnPagingListener(listener);
        getViewTreeObserver().addOnGlobalLayoutListener(new FirstShowListener());
    }

    public void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }

    public void setPageEnable(boolean header, boolean footer) {
        mAdapter.setPageEnable(header, footer);
    }

    public void setHeader(PagingItem header) {
        mAdapter.setHeader(header);
    }

    public void setFooter(PagingItem footer) {
        mAdapter.setFooter(footer);
    }

    public void updateHeader(int state) {
        mAdapter.updateHeader(state);
        mListener.onScrollStateChanged(this, getScrollState());
    }

    public void updateFooter(int state) {
        mAdapter.updateFooter(state);
        mListener.onScrollStateChanged(this, getScrollState());
    }

    public int getFixedPostion(int position) {
        return mAdapter.getInnerPosition(position);
    }

    /**
     * OnScrollStateChanged will not be called when the first time PagingRecyclerView shows.
     * We need to add a listener to detect this and call it manually.
     */
    private class FirstShowListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        @SuppressWarnings("deprecation")
        public void onGlobalLayout() {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mListener.onScrollStateChanged(PagingRecyclerView.this, getScrollState());
            }
        }
    }

    public interface OnPagingListener{
        void onPaging(PagingRecyclerView view, int direction);
    }
}
