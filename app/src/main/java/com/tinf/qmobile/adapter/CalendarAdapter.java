package com.tinf.qmobile.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements KmStickyListener {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public Integer getHeaderPositionForItem(Integer itemPosition) {
        return null;
    }

    @Override
    public Integer getHeaderLayout(Integer headerPosition) {
        return null;
    }

    @Override
    public void bindHeaderData(View header, Integer headerPosition) {

    }

    @Override
    public Boolean isHeader(Integer itemPosition) {
        return null;
    }
}
