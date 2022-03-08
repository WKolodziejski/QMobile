package com.tinf.qmobile.holder.clazz;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import com.tinf.qmobile.databinding.ClassHeaderBinding;
import com.tinf.qmobile.model.matter.Period;

public class ClassHeaderViewHolder extends ClassBaseViewHolder<Period> {
    private final ClassHeaderBinding binding;

    public ClassHeaderViewHolder(View view) {
        super(view);
        binding = ClassHeaderBinding.bind(view);
    }

    @Override
    public void bind(Context context, Period period) {
        binding.period.setText(period.getTitle());
        binding.badge.setBackgroundTintList(ColorStateList.valueOf(period.matter.getTarget().getColor()));
    }

}
