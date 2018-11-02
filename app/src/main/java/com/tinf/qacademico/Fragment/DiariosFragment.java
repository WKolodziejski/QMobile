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
import java.util.Objects;
import io.objectbox.BoxStore;

public class DiariosFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDiarios(view);
    }

    private void showDiarios(View view) {

        RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        SingletonWebView webView = SingletonWebView.getInstance();

        DiariosListAdapter adapter = new DiariosListAdapter(getActivity(), getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position])).build().find());

        recyclerViewDiarios.setAdapter(adapter);
        recyclerViewDiarios.setLayoutManager(layout);

        recyclerViewDiarios.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                ((MainActivity) getActivity()).refreshLayout.setEnabled(p == 0);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

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
}
