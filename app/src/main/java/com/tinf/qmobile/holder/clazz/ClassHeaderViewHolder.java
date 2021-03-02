package com.tinf.qmobile.holder.clazz;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.matter.Period;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassHeaderViewHolder extends ClassBaseViewHolder<Period> {
    @BindView(R.id.class_period)                public TextView title;
    @BindView(R.id.class_period_color_badge)    public TextView badge;

    public ClassHeaderViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Period period) {
        title.setText(period.getTitle());
        badge.setBackgroundTintList(ColorStateList.valueOf(period.matter.getTarget().getColor()));
    }

}
