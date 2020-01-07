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

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.journal.EtapasAdapter;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;

public class GradesFragment extends Fragment implements OnUpdate {
    private EtapasAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Client.get().addOnUpdateListener(this);
        adapter = new EtapasAdapter(App.getBox().boxFor(Matter.class).get(getArguments().getLong("ID")), getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grades, container, false);
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

    @Override
    public void onUpdate(int pg) {
        adapter.update(App.getBox().boxFor(Matter.class).get(getArguments().getLong("ID")));
    }

    @Override
    public void onScrollRequest() {

    }

}
