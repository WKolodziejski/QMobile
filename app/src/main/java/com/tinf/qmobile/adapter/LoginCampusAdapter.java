package com.tinf.qmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.LoginCampusViewHolder;

import java.util.List;

public class LoginCampusAdapter extends RecyclerView.Adapter<LoginCampusViewHolder> {
    private final Context context;
    private final OnClick onClick;
    private final AsyncListDiffer<String> list;

    public LoginCampusAdapter(Context context, OnClick onClick) {
        this.context = context;
        this.onClick = onClick;
        this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public LoginCampusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LoginCampusViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.login_campus_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LoginCampusViewHolder holder, int i) {
        holder.bind(list.getCurrentList().get(i), onClick);
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    public void onUpdate(List<String> list) {
        this.list.submitList(list);
    }

    public interface OnClick {
        void onClick(int i);
    }

}
