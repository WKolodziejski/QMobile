package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

public class EtapaViewHolder extends RecyclerView.ViewHolder{
    public final TextView aux;
    public final RecyclerView recyclerView;

    public EtapaViewHolder(View view) {
        super(view);

        aux = (TextView) view.findViewById(R.id.aux);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_diarios_item);
    }
}
