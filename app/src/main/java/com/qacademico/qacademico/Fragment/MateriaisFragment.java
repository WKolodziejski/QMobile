package com.qacademico.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.ExpandableListAdapter;
import com.qacademico.qacademico.Class.ExpandableList;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;

import java.util.List;
import java.util.Objects;

public class MateriaisFragment extends Fragment {
    public List<ExpandableList> materiaisList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiais, container, false);

        return view;
    }
}
