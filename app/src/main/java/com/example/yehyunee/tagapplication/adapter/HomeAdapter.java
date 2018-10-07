package com.example.yehyunee.tagapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yehyunee.tagapplication.PopUpCardActivity;

import com.example.yehyunee.tagapplication.R;
import com.example.yehyunee.tagapplication.data.MainItem;
import com.example.yehyunee.tagapplication.viewholder.MainViewHolder;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<MainViewHolder>{
    private ArrayList<MainItem> mItems;
    // 차후 받아올 리스트
    private Context mContext;

    public HomeAdapter(ArrayList item) {
        mItems = item;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_main_vertical, parent, false);
        mContext = parent.getContext();

        MainViewHolder mainViewHolder = new MainViewHolder(view);
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 포지션에 따라 클릭 리스너 구현.
                Intent intent = new Intent(mContext, PopUpCardActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
//        return mItems.size();
        return 4;
    }
}
