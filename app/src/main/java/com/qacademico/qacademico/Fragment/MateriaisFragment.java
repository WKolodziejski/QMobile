package com.qacademico.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.Diarios.DiariosListAdapter;
import com.qacademico.qacademico.Adapter.Materiais.MateriaisListAdapter;
import com.qacademico.qacademico.Class.Diarios.DiariosList;
import com.qacademico.qacademico.Class.Materiais.MateriaisList;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

public class MateriaisFragment extends Fragment implements MainActivity.OnPageUpdated {
    MateriaisListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiais, container, false);

        if (((MainActivity)getActivity()).materiaisList != null) {
            showMateriais(view);
        }

        return view;
    }

    private void showMateriais(View view) {
        RecyclerView recyclerViewMateriais = (RecyclerView) view.findViewById(R.id.recycler_materiais);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new MateriaisListAdapter(((MainActivity)getActivity()).materiaisList, getActivity());

        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);

        adapter.setOnDowloadListener(link -> {
            Log.i("Materiais", link);
            SingletonWebView.getInstance().downloadMaterial(getContext(), link);
        });
    }

    @Override
    public void onPageUpdate(List<?> list) {
        if (list.get(0) instanceof MateriaisList) {
            ((MainActivity) getActivity()).materiaisList = (List<MateriaisList>) list;
            showMateriais(getView());
        }
    }
}
