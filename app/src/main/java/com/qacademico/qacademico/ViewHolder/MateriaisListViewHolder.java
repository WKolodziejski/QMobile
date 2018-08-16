package com.qacademico.qacademico.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateriaisListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.material_header)         public LinearLayout header;
    @BindView(R.id.material_text)           public TextView materia;
    @BindView(R.id.recycler_list_materiais) public RecyclerView recyclerView;

    public MateriaisListViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
