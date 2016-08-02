package com.dss886.pagingrecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by dss886 on 16/7/28.
 */
public class PagingItem {

    public static final int STATE_FINISH = 1;
    public static final int STATE_PAGING = 1;
    public static final int STATE_ERROR = 3;

    public RecyclerView.ViewHolder holder;
    public Pageable pageable;

    public PagingItem(RecyclerView.ViewHolder holder, Pageable pageable) {
        this.holder = holder;
        this.pageable = pageable;
    }

    public interface Pageable {
        void onPaging(PagingItem item);

        void onError(PagingItem item);
    }
}
