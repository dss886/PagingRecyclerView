package com.dss886.pagingrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dss886.pagingrecyclerview.PagingAdapterDecorator;
import com.dss886.pagingrecyclerview.PagingItem;
import com.dss886.pagingrecyclerview.PagingRecyclerView;

public class MainActivity extends AppCompatActivity {

    PagingRecyclerView recyclerView;
    MainRecyclerAdapter adapter;

    boolean testError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (PagingRecyclerView) findViewById(R.id.recycler_view);
        adapter = new MainRecyclerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PagingAdapterDecorator(this, adapter));
        recyclerView.setPageEnable(false, true);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_paging, recyclerView, false);
        recyclerView.setFooter(new PagingItem(new FooterHolder(footer), new FooterPageable()));
        recyclerView.setOnPagingListener(new PagingRecyclerView.OnPagingListener() {
            @Override
            public void onPaging(PagingRecyclerView view, int direction) {
                if (direction == PagingRecyclerView.DIRECTION_FOOT) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!testError) {
                                recyclerView.updateFooter(PagingItem.STATE_ERROR);
                                testError = true;
                            } else {
                                adapter.setNum(adapter.getItemCount() + 5);
                                recyclerView.updateFooter(PagingItem.STATE_FINISH);
                            }
                        }
                    }, 2000);
                } else {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.updateHeader(PagingItem.STATE_ERROR);
                        }
                    }, 2000);
                }
            }
        });
    }

    private class FooterHolder extends RecyclerView.ViewHolder {
        public View progress;
        public View error;
        public TextView text;
        public FooterHolder(View itemView) {
            super(itemView);
            this.progress = itemView.findViewById(R.id.progress);
            this.error = itemView.findViewById(R.id.error);
            this.text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    private class FooterPageable implements PagingItem.Pageable {
        @Override
        public void onPaging(PagingItem item) {
            FooterHolder dHolder = (FooterHolder) item.holder;
            dHolder.progress.setVisibility(View.VISIBLE);
            dHolder.error.setVisibility(View.GONE);
            dHolder.text.setText("Loading...");
            dHolder.itemView.setOnClickListener(null);
        }

        @Override
        public void onError(final PagingItem item) {
            final FooterHolder dHolder = (FooterHolder) item.holder;
            dHolder.progress.setVisibility(View.GONE);
            dHolder.error.setVisibility(View.VISIBLE);
            dHolder.text.setText("Tap to retry");
            dHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerView.updateFooter(PagingItem.STATE_PAGING);
                }
            });
        }
    }
}
