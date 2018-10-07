package com.example.yehyunee.tagapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yehyunee.tagapplication.R;
import com.example.yehyunee.tagapplication.data.MainItem;
import com.example.yehyunee.tagapplication.viewholder.MainBackCommentViewHolder;

import java.util.ArrayList;

public class MainBackCommentAdapter extends RecyclerView.Adapter<MainBackCommentViewHolder> {

    private ArrayList<MainItem> mItems;
    // 차후 받아올 리스트
    private Context mContext;

    public MainBackCommentAdapter(ArrayList item) {
        mItems = item;
    }

    @Override
    public MainBackCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_back_comment, parent, false);
        mContext = parent.getContext();

        MainBackCommentViewHolder commentViewHolder = new MainBackCommentViewHolder(view);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(MainBackCommentViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 포지션에 따라 클릭 리스너 구현.
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
