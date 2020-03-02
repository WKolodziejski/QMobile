package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.journal.Header;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PeriodHeaderViewHolder extends JournalBaseViewHolder<Header> {
    @BindView(R.id.period_title)        TextView title;
    @BindView(R.id.period_color_badge)  TextView badge;

    public PeriodHeaderViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Header header, JournalAdapter adapter) {
        title.setText(header.getPeriod().getTitle());
        badge.setBackgroundTintList(ColorStateList.valueOf(header.getPeriod().matter.getTarget().getColor()));
    }

}
