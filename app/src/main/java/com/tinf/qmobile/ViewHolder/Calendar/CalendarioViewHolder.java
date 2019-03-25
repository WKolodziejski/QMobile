package com.tinf.qmobile.ViewHolder.Calendar;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class CalendarioViewHolder<T> extends RecyclerView.ViewHolder {

    public CalendarioViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(T calendar);

}