package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import com.tinf.qmobile.databinding.TableRowBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableRowHeaderViewHolder extends TableBaseViewHolder {
    private final TableRowBinding binding;

    public TableRowHeaderViewHolder(View view) {
        super(view);
        binding = TableRowBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        int n = matter.getJournalNotSeenCount();
        binding.badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));
        binding.badge.setText(n > 0 ? String.valueOf(n) : "");
    }

}
