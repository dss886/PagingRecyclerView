package com.dss886.pagingrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by dss886 on 16/7/22.
 */
public class PagingAdapterDecorator extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Some complex code of types to avoid conflicting with subclasses
    private static final int VIEW_TYPE_HEADER = 1235234534;
    private static final int VIEW_TYPE_FOOTER = 2135123453;

    private static final int STATE_HIDE = 0;
    private static final int STATE_IDLE = 1;
    private static final int STATE_PAGING = 2;
    private static final int STATE_ERROR = 3;

    private boolean mHeaderEnable = false;
    private boolean mFooterEnable = true;
    private int mHeaderState = STATE_HIDE;
    private int mFooterState = STATE_IDLE;

    private DefaultHolder mHeader;
    private DefaultHolder mFooter;

    private Context mContext;
    private PagingRecyclerView mRecyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private PagingRecyclerView.OnPagingListener onPagingListener;

    /** Public Methods **/

    PagingAdapterDecorator(Context context, PagingRecyclerView recyclerView, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Construct parameter adapter must not be null!");
        }
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
        this.mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                if (mHeaderEnable) {
                    positionStart++;
                }
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                if (mHeaderEnable) {
                    positionStart++;
                }
                notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (mHeaderEnable) {
                    positionStart++;
                }
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (mHeaderEnable) {
                    positionStart++;
                }
                notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (mHeaderEnable) {
                    fromPosition++;
                    toPosition++;
                }
                notifyItemRangeChanged(fromPosition, toPosition, itemCount);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = mAdapter.getItemCount();
        if (mHeaderEnable) {
            count++;
        }
        if (mFooterEnable) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) return VIEW_TYPE_HEADER;
        if (isFooter(position)) return VIEW_TYPE_FOOTER;
        return mAdapter.getItemViewType(getInnerPosition(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
                mHeader = new DefaultHolder(view, PagingRecyclerView.HEAD);
                return mHeader;
            case VIEW_TYPE_FOOTER:
                view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
                mFooter = new DefaultHolder(view, PagingRecyclerView.FOOT);
                return mFooter;
            default:
                return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // In case STATE_HIDE this method will not be invoked.
        if (isHeader(position)) {
            if (!mHeaderEnable || mHeaderState == STATE_HIDE || mAdapter.getItemCount() == 0) {
                mHeader.onHide();
            } else if (mHeaderState == STATE_ERROR) {
                mHeader.onError();
            } else {
                mHeader.onPaging();
            }
        } else if (isFooter(position)) {
            if (!mFooterEnable || mFooterState == STATE_HIDE || mAdapter.getItemCount() == 0) {
                mFooter.onHide();
            } else if (mFooterState == STATE_ERROR) {
                mFooter.onError();
            } else {
                mFooter.onPaging();
            }
        } else {
            mAdapter.onBindViewHolder(holder, getInnerPosition(position));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (!(holder instanceof DefaultHolder)) {
            mAdapter.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (!(holder instanceof DefaultHolder)) {
            mAdapter.onViewDetachedFromWindow(holder);
        }
    }

    /** Friendly Methods **/

    void onPaging(int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            mHeaderState = STATE_IDLE;
            if (mHeader != null) {
                mHeader.onPaging();
            }
        } else if (direction == PagingRecyclerView.FOOT) {
            mFooterState = STATE_IDLE;
            if (mFooter != null) {
                mFooter.onPaging();
            }
        }
    }

    void onFailure(int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            mHeaderState = STATE_ERROR;
            if (mHeader != null) {
                mHeader.onError();
            }
        } else if (direction == PagingRecyclerView.FOOT) {
            mFooterState = STATE_ERROR;
            if (mFooter != null) {
                mFooter.onError();
            }
        }
    }

    void onNoMoreData(int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            mHeaderState = STATE_HIDE;
            if (mHeader != null) {
                mHeader.onHide();
            }
        } else if (direction == PagingRecyclerView.FOOT) {
            mFooterState = STATE_HIDE;
            if (mFooter != null) {
                mFooter.onHide();
            }
        }
    }

    int getInnerPosition(int position) {
        if (mHeaderEnable) {
            return position - 1;
        }
        return position;
    }

    int getFixedPosition(int position) {
        if (mHeaderEnable) {
            return position + 1;
        }
        return position;
    }

    void setOnPagingListener(PagingRecyclerView.OnPagingListener onPagingListener) {
        this.onPagingListener = onPagingListener;
    }

    void setPageEnable(boolean header, boolean footer) {
        this.mHeaderEnable = header;
        this.mHeaderState = header ? STATE_IDLE : STATE_HIDE;
        this.mFooterEnable = footer;
        this.mFooterState = footer ? STATE_IDLE : STATE_HIDE;
    }

    void onScrolledToEdge(PagingRecyclerView recyclerView, int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            if (!mHeaderEnable || mHeaderState != STATE_IDLE) return;
            mHeaderState = STATE_PAGING;
        } else if (direction == PagingRecyclerView.FOOT){
            if (!mFooterEnable || mFooterState != STATE_IDLE) return;
            mFooterState = STATE_PAGING;
        }
        if (onPagingListener != null) {
            onPagingListener.onPaging(recyclerView, direction);
        }
    }

    boolean isHeader(int position) {
        return mHeaderEnable && position == 0;
    }

    boolean isFooter(int position) {
        return mFooterEnable && position == getItemCount() - 1;
    }

    /** Private Methods **/

    private class DefaultHolder extends RecyclerView.ViewHolder {

        ProgressBar progress;
        View error;
        TextView text;
        int direction;
        int height;

        DefaultHolder(View itemView, int direction) {
            super(itemView);
            this.direction = direction;
            this.progress = itemView.findViewById(R.id.progress);
            this.error = itemView.findViewById(R.id.error);
            this.text = itemView.findViewById(R.id.text);
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            }
            height = dp2Px(itemView.getContext(), 56);
        }

        void onHide() {
            setHeight(itemView, 0);
            itemView.setVisibility(View.GONE);
        }

        void onPaging() {
            setHeight(itemView, height);
            itemView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            text.setText(itemView.getContext().getString(R.string.paging_recycler_view_loading));
            itemView.setOnClickListener(null);
        }

        void onError() {
            setHeight(itemView, height);
            itemView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            text.setText(itemView.getContext().getString(R.string.paging_recycler_view_retry));
            itemView.setOnClickListener(v -> {
                onPaging();
                if (onPagingListener != null) {
                    onPagingListener.onPaging(mRecyclerView, direction);
                }
            });
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int dp2Px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    private static void setHeight(View view, int h) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null && (params.height != h)) {
            params.height = h;
            view.setLayoutParams(params);
        }
    }

}
