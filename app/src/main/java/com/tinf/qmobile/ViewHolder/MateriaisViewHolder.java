package com.tinf.qmobile.ViewHolder;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateriaisViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.materiais_type)          public ImageView type;
    @BindView(R.id.materiais_title)         public TextView title;
    @BindView(R.id.materiais_date)          public TextView date;
    @BindView(R.id.materiais_description)   public TextView description;
    @BindView(R.id.materiais_header)        public ConstraintLayout header;
    @BindView(R.id.materiais_offline)       public ImageView offline;

    public MateriaisViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
