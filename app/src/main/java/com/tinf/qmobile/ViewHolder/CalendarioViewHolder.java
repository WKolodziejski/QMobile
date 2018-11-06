package com.tinf.qmobile.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarioViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.calendario_dia)    public TextView dia;
    @BindView(R.id.recyvler_eventos)  public RecyclerView eventos;
    @BindView(R.id.calendario_layout) public LinearLayout layout;

    public CalendarioViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}