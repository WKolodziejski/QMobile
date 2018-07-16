package com.qacademico.qacademico.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import net.cachapa.expandablelayout.ExpandableLayout;

public class MateriaisViewHolder extends RecyclerView.ViewHolder {
    public final ImageView img;
    public final ImageButton btn;
    public final TextView title;
    public final TextView date;
    public final ExpandableLayout exp_info;
    public final TextView info;

    public MateriaisViewHolder(@NonNull View view) {
        super(view);

        img = (ImageView) view.findViewById(R.id.materiais_tipo);
        btn = (ImageButton) view.findViewById(R.id.materiais_btn);
        exp_info = (ExpandableLayout) view.findViewById(R.id.materiais_expand_description);
        title = (TextView) view.findViewById(R.id.materiais_title);
        date = (TextView) view.findViewById(R.id.materiais_date);
        info = (TextView) view.findViewById(R.id.materiais_info);
    }
}
