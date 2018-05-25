package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.Diarios.DiariosAdapter;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

import static com.qacademico.qacademico.Utilities.Utils.pg_diarios;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class DiariosFragment extends Fragment {
    SingletonWebView mainWebView = SingletonWebView.getInstance();
    List<Diarios> diarios;
    DiariosAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            diarios = (List<Diarios>) getArguments().getSerializable("Diarios");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarios, container, false);

        setDiarios(view);

        return view;
    }

    private void setDiarios(View view) {
        if (diarios != null) {

            if (diarios.size() != 0) {
                RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
                RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                adapter = new DiariosAdapter(diarios, getActivity());

                recyclerViewDiarios.setAdapter(adapter);
                recyclerViewDiarios.setLayoutManager(layout);
                recyclerViewDiarios.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

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
            } else {
                Toast.makeText(getContext(), "Vazio", Toast.LENGTH_SHORT).show();
            }
        } else {
            ((MainActivity) Objects.requireNonNull(getActivity())).showErrorConnection();
        }
    }

    public void openDateDialog() {
        if (mainWebView.pg_diarios_loaded && mainWebView.data_diarios != null) {

            View theView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

            final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
            year.setMinValue(0);
            year.setMaxValue(mainWebView.data_diarios.length - 1);
            year.setValue(mainWebView.data_position_diarios);
            year.setDisplayedValues(mainWebView.data_diarios);
            year.setWrapSelectorWheel(false);

            NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
            periodo.setVisibility(View.GONE);

            TextView slash = (TextView) theView.findViewById(R.id.slash);
            slash.setVisibility(View.GONE);

            new AlertDialog.Builder(getActivity()).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(getActivity()), R.drawable.ic_date_range_black_24dp,
                            R.string.dialog_date_change, R.color.diarios_dialog))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        mainWebView.data_position_diarios = year.getValue();

                        Log.v("Ano selecionado", String.valueOf(
                                mainWebView.data_diarios[mainWebView.data_position_diarios]));
                        mainWebView.html.loadUrl(url + pg_diarios);
                        mainWebView.scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                                + (mainWebView.data_position_diarios + 1) + "].selected = true; document.forms['frmConsultar'].submit();";
                        Log.i("SCRIPT", "" + mainWebView.scriptDiario);
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }
    }

    public void update(List<Diarios> diarios) {
        if (adapter != null) {
            this.diarios = diarios;
            adapter.update(this.diarios);
        } else {
            setDiarios(getView());
        }
    }
}
