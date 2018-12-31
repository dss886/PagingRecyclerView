package com.dss886.pagingrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dss886 on 16/7/22.
 */
class PagingAdapterDecorator extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Some complex code of types to avoid conflicting with subclasses
    private static final int VIEW_TYPE_HEADER = 1235234534;
    private static final int VIEW_TYPE_FOOTER = 2135123453;

    private static final int STATE_HIDE = 0;
    private static final int STATE_PAGING = 1;
    private static final int STATE_LOADING = 2;
    private static final int STATE_NO_MORE_DATA = 3;
    private static final int STATE_ERROR = 4;

    private boolean mHeaderEnable = false;
    private boolean mFooterEnable = true;
    private int mHeaderState = STATE_HIDE;
    private int mFooterState = STATE_PAGING;

    private AbsPagingViewHolder mHeader;
    private AbsPagingViewHolder mFooter;

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                mHeader = mRecyclerView.createPagingViewHolder(inflater, parent, PagingRecyclerView.HEAD, onPagingListener);
                return mHeader;
            case VIEW_TYPE_FOOTER:
                mFooter = mRecyclerView.createPagingViewHolder(inflater, parent, PagingRecyclerView.FOOT, onPagingListener);
                return mFooter;
            default:
                return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(holder, position, new ArrayList<>());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        // In case STATE_HIDE this method will not be invoked.
        if (isHeader(position)) {
            if (!mHeaderEnable || mHeaderState == STATE_HIDE || mAdapter.getItemCount() == 0) {
                mHeader.onHide();
            } else if (mHeaderState == STATE_NO_MORE_DATA) {
                mHeader.onNoMoreData();
            } else if (mHeaderState == STATE_ERROR) {
                mHeader.onError();
            } else if (mHeaderState == STATE_PAGING) {
                mFooter.onPaging();
            } else {
                mHeader.onLoading();
            }
        } else if (isFooter(position)) {
            if (!mFooterEnable || mFooterState == STATE_HIDE || mAdapter.getItemCount() == 0) {
                mFooter.onHide();
            } else if (mFooterState == STATE_NO_MORE_DATA) {
                mFooter.onNoMoreData();
            } else if (mFooterState == STATE_ERROR) {
                mFooter.onError();
            } else if (mFooterState == STATE_PAGING) {
                mFooter.onPaging();
            } else {
                mFooter.onLoading();
            }
        } else {
            mAdapter.onBindViewHolder(holder, getInnerPosition(position), payloads);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (!(holder instanceof AbsPagingViewHolder)) {
            mAdapter.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (!(holder instanceof AbsPagingViewHolder)) {
            mAdapter.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (!AbsPagingViewHolder.class.isInstance(holder)) {
            if (mAdapter != null) {
                mAdapter.onViewRecycled(holder);
            }
        } else {
            super.onViewRecycled(holder);
        }
    }

    /** Friendly Methods **/

    void onPaging(int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            mHeaderState = STATE_PAGING;
            if (mHeader != null) {
                mHeader.onPaging();
            }
        } else if (direction == PagingRecyclerView.FOOT) {
            mFooterState = STATE_PAGING;
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
            mHeaderState = STATE_NO_MORE_DATA;
            if (mHeader != null) {
                mHeader.onNoMoreData();
            }
        } else if (direction == PagingRecyclerView.FOOT) {
            mFooterState = STATE_NO_MORE_DATA;
            if (mFooter != null) {
                mFooter.onNoMoreData();
            }
        }
    }

    void onLoading(int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            mHeaderState = STATE_LOADING;
            if (mHeader != null) {
                mHeader.onLoading();
            }
        }else if (direction == PagingRecyclerView.FOOT) {
            mFooterState = STATE_LOADING;
            if (mFooter != null) {
                mFooter.onLoading();
            }
        }
    }

    void onHide(int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            mHeaderState = STATE_HIDE;
            if (mHeader != null) {
                mHeader.onHide();
            }
        }else if (direction == PagingRecyclerView.FOOT) {
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
        this.mHeaderState = header ? STATE_PAGING : STATE_HIDE;
        this.mFooterEnable = footer;
        this.mFooterState = footer ? STATE_PAGING : STATE_HIDE;
    }

    void onScrolledToEdge(PagingRecyclerView recyclerView, int direction) {
        if (direction == PagingRecyclerView.HEAD) {
            if (!mHeaderEnable || mHeaderState != STATE_PAGING) return;
            mHeaderState = STATE_LOADING;
            if (mHeader != null) mHeader.onLoading();
        } else if (direction == PagingRecyclerView.FOOT){
            if (!mFooterEnable || mFooterState != STATE_PAGING) return;
            mFooterState = STATE_LOADING;
            if (mFooter != null) mFooter.onLoading();
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

}
