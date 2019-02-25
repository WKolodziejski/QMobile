package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.Adapter.Diarios.EtapasAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MateriaFragment extends Fragment {
    private EtapasAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {

            int year = bundle.getInt("YEAR");
            int period = bundle.getInt("PERIOD");
            String name = bundle.getString("TITLE");

            Matter materia = App.getBox().boxFor(Matter.class).query().order(Matter_.title)
                    .equal(Matter_.title, name).and()
                    .equal(Matter_.year, year).and()
                    .equal(Matter_.period, period)
                    .build().findFirst();

            adapter = new EtapasAdapter(materia, getContext());

            ((MateriaActivity) getActivity()).setTitle(materia.getTitle());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_materia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_materia);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

}
