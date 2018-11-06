package com.tinf.qmobile.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EtapaViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.etapa)                 public TextView etapa;
    @BindView(R.id.recycler_diarios_item) public RecyclerView recyclerView;

    public EtapaViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}