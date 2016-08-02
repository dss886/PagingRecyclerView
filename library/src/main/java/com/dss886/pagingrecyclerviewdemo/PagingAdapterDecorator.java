package com.dss886.pagingrecyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dss886.pagingrecyclerview.R;

/**
 * Created by dss886 on 16/7/22.
 */
public class PagingAdapterDecorator extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Some complex code of types to avoid conflicting with subclasses
    private static final int VIEW_TYPE_HEADER = -1235234534;
    private static final int VIEW_TYPE_FOOTER = -2135123453;

    private static final int STATE_HIDE = 0;
    private static final int STATE_IDLE = 1;
    private static final int STATE_PAGING = 2;
    private static final int STATE_ERROR = 3;

    private boolean mHeaderEnable = false;
    private boolean mFooterEnable = true;
    private int mHeaderState = STATE_HIDE;
    private int mFooterState = STATE_IDLE;

    private PagingItem mHeader;
    private PagingItem mFooter;

    private Context mContext;
    private PagingRecyclerView mRecyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private PagingRecyclerView.OnPagingListener onPagingListener;

    /** Public Methods **/

    public PagingAdapterDecorator(Context context, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Construct parameter adapter must not be null!");
        }
        this.mContext = context;
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        int count = mAdapter.getItemCount();
        if (mHeaderEnable && mHeaderState != STATE_HIDE) count++;
        if (mFooterEnable && mFooterState != STATE_HIDE) count++;
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
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                if (mHeader == null) {
                    View view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
                    DefaultHolder mHeaderHolder = new DefaultHolder(view);
                    mHeader = new PagingItem(mHeaderHolder, new DefaultItemPageable(PagingRecyclerView.DIRECTION_HEAD));
                }
                return mHeader.holder;
            case VIEW_TYPE_FOOTER:
                if (mFooter == null) {
                    View view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
                    DefaultHolder mFooterHolder = new DefaultHolder(view);
                    mFooter = new PagingItem(mFooterHolder, new DefaultItemPageable(PagingRecyclerView.DIRECTION_FOOT));
                }
                return mFooter.holder;
            default:
                return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // In case STATE_HIDE this method will not be invoked.
        if (isHeader(position)) {
            if (mHeaderState == STATE_ERROR) {
                mHeader.pageable.onError(mHeader);
            } else {
                mHeader.pageable.onPaging(mHeader);
            }
        } else if (isFooter(position)) {
            if (mFooterState == STATE_ERROR) {
                mFooter.pageable.onError(mFooter);
            } else {
                mFooter.pageable.onPaging(mFooter);
            }
        } else {
            mAdapter.onBindViewHolder(holder, getInnerPosition(position));
        }
    }

    /**
     * Friendly Methods
     **/

    void setHeader(PagingItem header) {
        mHeader = header;
    }

    void setFooter(PagingItem footer) {
        mFooter = footer;
    }

    void updateHeader(int state) {
        if (state < 0 || state > 3) throw new IllegalArgumentException("Unsupported state!");
        mHeaderState = state;
        notifyDataSetChanged();
    }

    void updateFooter(int state) {
        if (state < 0 || state > 3) throw new IllegalArgumentException("Unsupported state!");
        mFooterState = state;
        notifyDataSetChanged();
    }

    int getInnerPosition(int position) {
        return isHeader(0) ? position - 1 : position;
    }

    void setPagingRecyclerView(PagingRecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    void setOnPagingListener(PagingRecyclerView.OnPagingListener onPagingListener) {
        this.onPagingListener = onPagingListener;
    }

    void setPageEnable(boolean header, boolean footer) {
        this.mHeaderEnable = header;
        this.mFooterEnable = footer;
        this.mHeaderState = header ? STATE_IDLE : STATE_HIDE;
        this.mFooterState = footer ? STATE_IDLE : STATE_HIDE;
    }

    void onScrolledToEdge(PagingRecyclerView recyclerView, int direction) {
        if (direction == PagingRecyclerView.DIRECTION_HEAD) {
            if (!mHeaderEnable || mHeaderState != STATE_IDLE) return;
            mHeaderState = STATE_PAGING;
        } else if (direction == PagingRecyclerView.DIRECTION_FOOT){
            if (!mFooterEnable || mFooterState != STATE_IDLE) return;
            mFooterState = STATE_PAGING;
        }
        onPagingListener.onPaging(recyclerView, direction);
    }

    /** Private Methods **/

    private boolean isHeader(int position) {
        return mHeaderEnable && position == 0 && mHeaderState != STATE_HIDE;
    }

    private boolean isFooter(int position) {
        return mFooterEnable && position == getItemCount() - 1 && mFooterState != STATE_HIDE;
    }

    private class DefaultHolder extends RecyclerView.ViewHolder {
        public View progress;
        public View error;
        public TextView text;
        public DefaultHolder(View itemView) {
            super(itemView);
            this.progress = itemView.findViewById(R.id.progress);
            this.error = itemView.findViewById(R.id.error);
            this.text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    private class DefaultItemPageable implements PagingItem.Pageable {

        private int direction;

        public DefaultItemPageable(int direction) {
            this.direction = direction;
        }

        @Override
        public void onPaging(PagingItem item) {
            DefaultHolder dHolder = (DefaultHolder) item.holder;
            dHolder.progress.setVisibility(View.VISIBLE);
            dHolder.error.setVisibility(View.GONE);
            dHolder.text.setText("Loading...");
            dHolder.itemView.setOnClickListener(null);
        }

        @Override
        public void onError(final PagingItem item) {
            final DefaultHolder dHolder = (DefaultHolder) item.holder;
            dHolder.progress.setVisibility(View.GONE);
            dHolder.error.setVisibility(View.VISIBLE);
            dHolder.text.setText("Tap to retry");
            dHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (direction == PagingRecyclerView.DIRECTION_HEAD) {
                        mRecyclerView.updateHeader(PagingItem.STATE_PAGING);
                    } else {
                        mRecyclerView.updateFooter(PagingItem.STATE_PAGING);
                    }
                }
            });
        }
    }

}
