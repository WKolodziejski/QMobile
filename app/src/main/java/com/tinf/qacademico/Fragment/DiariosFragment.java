package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.Diarios.DiariosListAdapter;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Class.Materias.Materia_;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;
import io.objectbox.BoxStore;

public class DiariosFragment extends Fragment implements MainActivity.OnPageUpdated {
    DiariosListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarios, container, false);

        setDiarios(view);

        return view;
    }

    private void setDiarios(View view) {

        RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        SingletonWebView webView = SingletonWebView.getInstance();

        adapter = new DiariosListAdapter(getActivity(), getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position])).build().find());

        recyclerViewDiarios.setAdapter(adapter);
        recyclerViewDiarios.setLayoutManager(layout);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewDiarios.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewDiarios.addItemDecoration(dividerItemDecoration);

        adapter.setOnExpandListener(position -> {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(Objects.requireNonNull(getActivity())) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_ANY;
                }
            };
            if (position != 0) {
                smoothScroller.setTargetPosition(position);
                layout.startSmoothScroll(smoothScroller);
            }
        });

        ((MainActivity) Objects.requireNonNull(getActivity())).fab_expand.setOnClickListener(v -> {
            adapter.toggleAll();
        });
    }

    private BoxStore getBox() {
        return ((MainActivity) getActivity()).getBox();
    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
