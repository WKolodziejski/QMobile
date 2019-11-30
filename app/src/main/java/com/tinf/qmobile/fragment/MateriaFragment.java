package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.adapter.journal.EtapasAdapter;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateriaFragment extends Fragment implements OnResponse {
    private EtapasAdapter adapter;
    private Matter matter;
    //@BindView(R.id.refresh_matter) SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            matter = App.getBox().boxFor(Matter.class).get(bundle.getLong("ID"));

            //Client.get().addOnResponseListener(this);
            //Client.get().load(matter);
            adapter = new EtapasAdapter(matter, getContext());

            ((MateriaActivity) getActivity()).setTitle(matter.getTitle());
        } else {
            ((MateriaActivity) getActivity()).finish();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materia, container, false);
        //ButterKnife.bind(this, view);
        return view;
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

        //refreshLayout.setOnRefreshListener(() -> Client.get().load(matter));
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

    @Override
    public void onStart(int pg, int pos) {
        /*if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
        }*/
    }

    @Override
    public void onFinish(int pg, int pos) {
        /*if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }*/
    }

    @Override
    public void onError(int pg, String error) {
        /*if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }*/
    }

    @Override
    public void onAccessDenied(int pg, String message) {

    }

}
