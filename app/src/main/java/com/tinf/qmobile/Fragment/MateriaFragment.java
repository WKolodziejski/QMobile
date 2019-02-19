package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.Adapter.Diarios.EtapasAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.tinf.qmobile.Network.Client.pos;

public class MateriaFragment extends Fragment {
    private EtapasAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {

            int year = bundle.getInt("YEAR");
            int period = bundle.getInt("PERIOD");
            String name = bundle.getString("NAME");

            Materia materia = App.getBox().boxFor(Materia.class).query().order(Materia_.name)
                    .equal(Materia_.name, name).and()
                    .equal(Materia_.year, year).and()
                    .equal(Materia_.period, period)
                    .build().findFirst();

            adapter = new EtapasAdapter(materia, getContext());

            ((MateriaActivity) getActivity()).setTitle(materia.getName());
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
