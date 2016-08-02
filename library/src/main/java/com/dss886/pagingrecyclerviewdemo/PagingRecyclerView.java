package com.dss886.pagingrecyclerviewdemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

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
        addOnScrollListener(new PagingScrollListener());
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
    }

    public void setOnPagingListener(OnPagingListener listener) {
        if (mAdapter == null){
            throw new IllegalArgumentException("Please set adapter before setting OnPagingListener!");
        }
        mAdapter.setOnPagingListener(listener);
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

    public interface OnPagingListener{
        void onPaging(PagingRecyclerView view, int direction);
    }
}
