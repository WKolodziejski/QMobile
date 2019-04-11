package com.tinf.qmobile.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateriaisListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.material_text)           public TextView materia;
    @BindView(R.id.recycler_list_materiais) public RecyclerView recyclerView;

    public MateriaisListViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
