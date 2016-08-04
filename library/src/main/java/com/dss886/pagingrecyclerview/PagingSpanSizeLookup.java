package com.dss886.pagingrecyclerview;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by duansishu on 16/8/4.
 */
public class PagingSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private PagingAdapterDecorator adapter;
    private int spanCount;
    private GridLayoutManager.SpanSizeLookup innerSizeLookup;

    public PagingSpanSizeLookup(PagingAdapterDecorator adapter, int spanCount
            , GridLayoutManager.SpanSizeLookup innerSizeLookup) {
        this.adapter = adapter;
        this.spanCount = spanCount;
        this.innerSizeLookup = innerSizeLookup;
    }

    @Override
    public int getSpanSize(int position) {
        if (adapter.isHeader(position) || adapter.isFooter(position)) {
            return spanCount;
        } else {
            return innerSizeLookup.getSpanSize(position);
        }
    }
}
