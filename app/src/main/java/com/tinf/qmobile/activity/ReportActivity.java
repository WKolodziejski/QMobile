package com.tinf.qmobile.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.adapter.ReportAdapter;
import com.tinf.qmobile.databinding.ActivityReportBinding;

public class ReportActivity extends AppCompatActivity {

  private ActivityReportBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityReportBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.table.setShowHorizontalSeparators(false);
    binding.table.setShowVerticalSeparators(false);
    binding.table.getColumnHeaderRecyclerView().removeItemDecorationAt(0);
    binding.table.getRowHeaderRecyclerView().removeItemDecorationAt(0);
    binding.table.getCellRecyclerView().removeItemDecorationAt(0);
    binding.table.setAdapter(new ReportAdapter(this, binding.table, binding.empty));
  }
}
