package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.adapter.OnHeaderYearInteractListener;
import com.tinf.qmobile.databinding.JournalHeaderYearBinding;
import com.tinf.qmobile.model.journal.HeaderYear;

public class JournalHeaderYearViewHolder extends JournalBaseViewHolder<HeaderYear> {
  private final JournalHeaderYearBinding binding;

  public JournalHeaderYearViewHolder(View view, OnHeaderYearInteractListener listener) {
    super(view);
    binding = JournalHeaderYearBinding.bind(view);

    binding.btnSort.setOnClickListener(view1 -> {
      // TODO
      listener.onClick();
    });
  }

  @Override
  public void bind(Context context, HeaderYear headerYear, boolean lookup, boolean isHeader) {
    binding.date.setText(headerYear.getYear());
  }
}
