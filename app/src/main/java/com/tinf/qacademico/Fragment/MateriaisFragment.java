package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.Materiais.MateriaisListAdapter;
import com.tinf.qacademico.Class.Materiais.MateriaisList;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;

public class MateriaisFragment extends Fragment implements MainActivity.OnPageUpdated {
    private SingletonWebView webView = SingletonWebView.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_materiais, container, false);
    }

    private void showMateriais(View view, List<MateriaisList> materiaisList) {

        ((MainActivity) getActivity()).setTitle(webView.data_year[webView.year_position]);

        RecyclerView recyclerViewMateriais = (RecyclerView) view.findViewById(R.id.recycler_materiais);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        MateriaisListAdapter adapter = new MateriaisListAdapter(materiaisList, getActivity());

        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);

        recyclerViewMateriais.addOnScrollListener(new RecyclerView.OnScrollListener(){
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

        ((MainActivity) getActivity()).setOnTopScrollRequestedListener(() -> {
            recyclerViewMateriais.smoothScrollToPosition(0);
        });

        adapter.setOnDowloadListener(link -> {
            Log.i("Materiais", link);
            SingletonWebView.getInstance().downloadMaterial(link);
        });
    }

    @Override
    public void onPageUpdate(List<?> list) {
        if (list != null && list.size() > 0) {
            if (list.get(0) instanceof MateriaisList) {
                showMateriais(getView(), (List<MateriaisList>) list);
            }
        } else {
            getLayoutInflater().inflate(R.layout.layout_empty,  null);
        }
    }
}
