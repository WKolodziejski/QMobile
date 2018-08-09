package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EtapaViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.aux)                     public TextView aux;
    @BindView(R.id.recycler_diarios_item)   public RecyclerView recyclerView;

    public EtapaViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
