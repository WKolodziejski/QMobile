package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.adapter.diarios.EtapasAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MateriaFragment extends Fragment {
    private EtapasAdapter adapter;
    private Matter matter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            matter = App.getBox().boxFor(Matter.class).get(bundle.getLong("ID"));

            adapter = new EtapasAdapter(matter, getContext());

            ((MateriaActivity) getActivity()).setTitle(matter.getTitle());
        } else {
            ((MateriaActivity) getActivity()).finish();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_color) {
            if (matter != null) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle(getString(R.string.dialog_choose_color))
                        .initialColor(matter.getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(6)
                        .lightnessSliderOnly()
                        .setPositiveButton(getString(R.string.dialog_select), (dialog, selectedColor, allColors) -> {
                            matter.setColor(selectedColor);
                            App.getBox().boxFor(Matter.class).put(matter);
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> {})
                        .build()
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Client.get().requestUpdate();
    }

}
