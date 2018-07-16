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

import com.qacademico.qacademico.Adapter.ExpandableListAdapter;
import com.qacademico.qacademico.Class.ExpandableList;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;

import java.util.List;

public class MateriaisFragment extends Fragment {
    public List<ExpandableList> expandableListList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiais, container, false);

        setMateriais(view);

        return view;
    }

    private void setMateriais(View view) {
        RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        ExpandableListAdapter adapter = new ExpandableListAdapter(expandableListList, getActivity(), Utils.MATERIAIS);

        recyclerViewDiarios.setAdapter(adapter);
        recyclerViewDiarios.setLayoutManager(layout);
    }
}
