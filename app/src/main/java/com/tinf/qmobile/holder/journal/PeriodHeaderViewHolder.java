package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.matter.Period;

import butterknife.BindView;

public class PeriodHeaderViewHolder extends JournalBaseViewHolder<Period> {
    @BindView(R.id.period_title)        TextView title;
    @BindView(R.id.period_color_badge)  TextView badge;

    public PeriodHeaderViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Context context, Period header, JournalAdapter adapter) {
        title.setText(header.getTitle());
        badge.setBackgroundTintList(ColorStateList.valueOf(header.matter.getTarget().getColor()));
    }

}
