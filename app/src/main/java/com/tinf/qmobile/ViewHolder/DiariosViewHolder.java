package com.tinf.qmobile.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiariosViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.diarios_nome) public TextView nome;
    @BindView(R.id.diarios_peso) public TextView peso;
    @BindView(R.id.diarios_max)  public TextView max;
    @BindView(R.id.diarios_nota) public TextView nota;
    @BindView(R.id.diarios_tipo) public TextView tipo;
    @BindView(R.id.diarios_data) public TextView data;

    public DiariosViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
