package com.tinf.qmobile.ViewHolder;

import android.view.View;
import android.widget.TextView;
import com.tinf.qmobile.R;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiariosViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.diarios_title) public TextView title;
    @BindView(R.id.diarios_weight) public TextView weight;
    @BindView(R.id.diarios_date) public TextView date;
    @BindView(R.id.diarios_type) public TextView type;
    @BindView(R.id.diarios_grade) public TextView grade;

    public DiariosViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
