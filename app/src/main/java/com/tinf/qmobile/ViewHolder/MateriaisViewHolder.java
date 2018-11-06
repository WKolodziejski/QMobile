package com.tinf.qmobile.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateriaisViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.materiais_tipo)   public ImageView img;
    //@BindView(R.id.materiais_btn)    public ImageButton btn;
    @BindView(R.id.materiais_title)  public TextView title;
    @BindView(R.id.materiais_date)   public TextView date;
    @BindView(R.id.materiais_header) public LinearLayout header;

    public MateriaisViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
