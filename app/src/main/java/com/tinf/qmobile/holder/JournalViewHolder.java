package com.tinf.qmobile.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.journal_view_title_text) public TextView title;
    @BindView(R.id.journal_view_weight)     public TextView weight;
    @BindView(R.id.journal_view_date)       public TextView date;
    @BindView(R.id.journal_view_max)        public TextView max;
    @BindView(R.id.journal_view_grade)      public TextView grade;
    @BindView(R.id.journal_view_layout)     public LinearLayout layout;

    public JournalViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
