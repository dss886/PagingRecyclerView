package com.dss886.pagingrecyclerview;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by duansishu on 2018/12/31.
 */
public class DefaultPagingViewHolder extends AbsPagingViewHolder {

    private PagingRecyclerView mRecyclerView;
    private PagingRecyclerView.OnPagingListener mPagingListener;

    private ProgressBar progress;
    private View error;
    private TextView text;
    private int height;

    @SuppressWarnings("WeakerAccess")
    public DefaultPagingViewHolder(View itemView, int direction,
                                   PagingRecyclerView recyclerView,
                                   PagingRecyclerView.OnPagingListener pagingListener) {
        super(itemView, direction);
        this.mRecyclerView = recyclerView;
        this.mPagingListener = pagingListener;
        this.progress = itemView.findViewById(R.id.progress);
        this.error = itemView.findViewById(R.id.error);
        this.text = itemView.findViewById(R.id.text);
        ViewGroup.LayoutParams lp = itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
        }
        height = dp2Px(itemView.getContext(), 56);
    }

    @Override
    public void onHide() {
        setHeight(itemView, 0);
        itemView.setVisibility(View.GONE);
    }

    @Override
    public void onPaging() {
        setHeight(itemView, height);
        itemView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        text.setText(itemView.getContext().getString(R.string.paging_recycler_view_loading));
        itemView.setOnClickListener(null);
    }

    @Override
    public void onLoading() {
        setHeight(itemView, height);
        itemView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        text.setText(itemView.getContext().getString(R.string.paging_recycler_view_loading));
        itemView.setOnClickListener(null);
    }

    @Override
    public void onNoMoreData() {
        setHeight(itemView, height);
        itemView.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        text.setText(itemView.getContext().getString(R.string.paging_recycler_view_no_more_data));
        itemView.setClickable(false);
        itemView.setOnClickListener(null);
    }

    @Override
    public void onError() {
        setHeight(itemView, height);
        itemView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        text.setText(itemView.getContext().getString(R.string.paging_recycler_view_retry));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPaging();
                if (mPagingListener != null) {
                    mPagingListener.onPaging(mRecyclerView, direction);
                }
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static int dp2Px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    private static void setHeight(View view, int h) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null && (params.height != h)) {
            params.height = h;
            view.setLayoutParams(params);
        }
    }
}
