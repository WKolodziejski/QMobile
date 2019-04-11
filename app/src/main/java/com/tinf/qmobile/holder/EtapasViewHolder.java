package com.tinf.qmobile.holder;

import android.view.View;
import android.widget.TextView;

import com.tinf.qmobile.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EtapasViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.etapa_title)     public TextView title;
    @BindView(R.id.recycler_etapas) public RecyclerView recyclerView;

    public EtapasViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
