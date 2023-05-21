package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.PerformanceAdapter;
import com.tinf.qmobile.databinding.FragmentPerformanceBinding;

public class PerformanceFragment extends Fragment {
  private FragmentPerformanceBinding binding;

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_performance, container, false);
    binding = FragmentPerformanceBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    //binding.recycler.setHasFixedSize(true);
    binding.recycler.setItemViewCacheSize(20);
    binding.recycler.setDrawingCacheEnabled(true);
    binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.recycler.setItemAnimator(null);
    binding.recycler.setAdapter(new PerformanceAdapter(getContext()));
  }

}
