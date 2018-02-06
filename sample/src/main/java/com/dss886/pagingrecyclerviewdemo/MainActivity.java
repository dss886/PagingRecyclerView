package com.dss886.pagingrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.dss886.pagingrecyclerview.PagingRecyclerView;

public class MainActivity extends AppCompatActivity {

    PagingRecyclerView recyclerView;
    MainRecyclerAdapter adapter;

    boolean testError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new MainRecyclerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setPageEnable(false, true);
        recyclerView.setOnPagingListener((view, direction) -> {
            if (direction == PagingRecyclerView.FOOT) {
                recyclerView.postDelayed(() -> {
                    if (!testError) {
                        recyclerView.onFailure(direction);
                        testError = true;
                    } else {
                        adapter.setNum(adapter.getItemCount() + 5);
                        recyclerView.onPaging(direction);
                    }
                }, 1000);
            } else {
                recyclerView.postDelayed(() -> recyclerView.onFailure(direction), 1000);
            }
        });
    }

}
