package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.Materiais.MateriaisListAdapter;
import com.tinf.qmobile.Class.Materiais.MateriaisList;
import com.tinf.qmobile.Interfaces.Fragments.OnUpdate;
import com.tinf.qmobile.Interfaces.WebView.OnPageLoad;
import com.tinf.qmobile.R;
import com.tinf.qmobile.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;

import static com.tinf.qmobile.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class MateriaisFragment extends Fragment implements OnPageLoad.Materiais, OnUpdate {
    private SingletonWebView webView = SingletonWebView.getInstance();
    private RecyclerView recyclerViewMateriais;
    private List<MateriaisList> materiaisList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_materiais, container, false);
    }

    private void showMateriais(View view) {

        ((MainActivity) getActivity()).setTitle(webView.data_year[webView.year_position]);

        recyclerViewMateriais = (RecyclerView) view.findViewById(R.id.recycler_materiais);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewMateriais.getContext(),
                LinearLayoutManager.VERTICAL);

        MateriaisListAdapter adapter = new MateriaisListAdapter(materiaisList, getActivity());

        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);
        recyclerViewMateriais.addItemDecoration(dividerItemDecoration);

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

        adapter.setOnDowloadListener(link -> {
            Log.i("Materiais", link);
            SingletonWebView.getInstance().downloadMaterial(link);
        });
    }

    @Override
    public void onPageFinish(List<MateriaisList> list) {
        getActivity().runOnUiThread(() -> {
            if (!list.isEmpty()) {
                materiaisList = list;
                showMateriais(getView());
            } else {
                getLayoutInflater().inflate(R.layout.layout_empty,  null);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
        webView.setOnMateriaisLoadListener(this);
        if (materiaisList == null) {
            webView.loadUrl(URL + PG_MATERIAIS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
        webView.setOnMateriaisLoadListener(this);
        if (materiaisList == null) {
            webView.loadUrl(URL + PG_MATERIAIS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(null);
        webView.setOnMateriaisLoadListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(null);
        webView.setOnMateriaisLoadListener(null);
    }

    @Override
    public void onUpdate(String url_p) {}

    @Override
    public void requestScroll() {
        if (recyclerViewMateriais != null) {
            recyclerViewMateriais.smoothScrollToPosition(0);
        }
    }
}
