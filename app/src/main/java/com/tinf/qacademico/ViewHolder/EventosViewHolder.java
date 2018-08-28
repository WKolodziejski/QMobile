package com.tinf.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinf.qacademico.R;

public class EventosViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.calendario_title)      public TextView title;
    @BindView(R.id.caledario_description) public TextView description;
    @BindView(R.id.calendario_point)      public View point;

    public EventosViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
