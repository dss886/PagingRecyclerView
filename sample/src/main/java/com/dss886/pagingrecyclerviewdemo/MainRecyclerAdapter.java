package com.dss886.pagingrecyclerviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dss886 on 16/7/22.
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int num = 15;

    MainRecyclerAdapter(Context context) {
        mContext = context;
    }

    void setNum(int num) {
        this.num = num;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return num;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MainViewHolder)holder).textView.setText("position = " + position);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        MainViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }

}
