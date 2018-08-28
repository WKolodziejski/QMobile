package com.tinf.qacademico.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.tinf.qacademico.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateriaisViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.materiais_tipo)   public ImageView img;
    @BindView(R.id.materiais_btn)    public ImageButton btn;
    @BindView(R.id.materiais_title)  public TextView title;
    @BindView(R.id.materiais_date)   public TextView date;
    @BindView(R.id.materiais_header) public CardView header;

    public MateriaisViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
