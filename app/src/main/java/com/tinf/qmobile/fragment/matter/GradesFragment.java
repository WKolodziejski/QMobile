package com.tinf.qmobile.fragment.matter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.GradesAdapter;
import com.tinf.qmobile.widget.divider.CustomItemDivider;

public class GradesFragment extends Fragment {

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_journals, container, false);
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    RecyclerView recycler = view.findViewById(R.id.recycler);
    recycler.addItemDecoration(new CustomItemDivider(getContext()));
    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    recycler.setAdapter(new GradesAdapter(getContext(), getArguments()));
  }

}
