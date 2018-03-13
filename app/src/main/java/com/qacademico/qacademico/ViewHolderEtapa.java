package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderEtapa extends RecyclerView.ViewHolder{
    final TextView aux;
    final RecyclerView recyclerView;

    public ViewHolderEtapa(View view) {
        super(view);

        aux = (TextView) view.findViewById(R.id.aux);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_diarios_item);
    }
}
