package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import androidx.annotation.NonNull;

import com.tinf.qmobile.databinding.PeriodHeaderBinding;
import com.tinf.qmobile.model.matter.Period;

public class PeriodHeaderViewHolder extends JournalBaseViewHolder<Period> {
    private final PeriodHeaderBinding binding;

    public PeriodHeaderViewHolder(@NonNull View view) {
        super(view);
        binding = PeriodHeaderBinding.bind(view);
    }

    @Override
    public void bind(Context context, Period header, boolean lookup) {
        binding.title.setText(header.getTitle());
        binding.badge.setBackgroundTintList(ColorStateList.valueOf(header.matter.getTarget().getColor()));
    }

}
