package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.qacademico.qacademico.R;

public class EventosViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.calendario_horario) public TextView horario;
    @BindView(R.id.caledario_nome)     public TextView nome;
    @BindView(R.id.calendario_point)   public View point;

    public EventosViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
