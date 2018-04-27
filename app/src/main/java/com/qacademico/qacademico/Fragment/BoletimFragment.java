package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.BoletimAdapter;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class BoletimFragment extends Fragment {
    SingletonWebView mainWebView = SingletonWebView.getInstance();
    List<Boletim> boletim;
    BoletimAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            boletim = (List<Boletim>) getArguments().getSerializable("Boletim");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boletim, container, false);

        setBoletim(view);

        return view;
    }

    private void setBoletim(View view) {
        if (boletim != null) {
            AdaptiveTableLayout table = (AdaptiveTableLayout) view.findViewById(R.id.tableBoletim);
            adapter = new BoletimAdapter(getActivity().getApplicationContext(), boletim);

            table.setAdapter(adapter);

            adapter.setOnHeaderClick(() -> {
                table.setHeaderFixed(!table.isHeaderFixed());
            });

            setFABListener();
        }
    }

    private void setFABListener() {
        if (mainWebView.pg_boletim_loaded && mainWebView.data_boletim != null) {

            ((MainActivity) getActivity()).fab_data.setOnClickListener(v -> {

                ((MainActivity) getActivity()).fab_data.setClickable(true);

                ((MainActivity) getActivity()).fab_isOpen = true;
                ((MainActivity) getActivity()).clickButtons(null);

                View theView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

                final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                year.setMinValue(0);
                year.setMaxValue(mainWebView.data_boletim.length - 1);
                year.setValue(mainWebView.data_position_boletim);
                year.setDisplayedValues(mainWebView.data_boletim);
                year.setWrapSelectorWheel(false);

                final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                periodo.setMinValue(0);
                periodo.setMaxValue(mainWebView.periodo_boletim.length - 1);
                periodo.setValue(mainWebView.periodo_position_boletim);
                periodo.setDisplayedValues(mainWebView.periodo_boletim);
                periodo.setWrapSelectorWheel(false);

                new AlertDialog.Builder(getActivity()).setView(theView)
                        .setCustomTitle(Utils.customAlertTitle(getActivity(), R.drawable.ic_date_range_black_24dp,
                                R.string.dialog_date_change, R.color.teal_400))
                        .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                            mainWebView.data_position_boletim = year.getValue();
                            mainWebView.periodo_position_boletim = periodo.getValue();

                            if (mainWebView.data_position_boletim == Integer.parseInt(mainWebView.data_boletim[0])) {
                                mainWebView.html.loadUrl(url + pg_boletim);
                            } else {
                                mainWebView.html.loadUrl(url + pg_boletim + "&COD_MATRICULA=-1&cmbanos="
                                        + mainWebView.data_boletim[mainWebView.data_position_boletim]
                                        + "&cmbperiodos=" + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim] + "&Exibir+Boletim");
                            }
                        }).setNegativeButton(R.string.dialog_cancel, null)
                        .show();//
            });
        } else {
            ((MainActivity) getActivity()).fab_data.setClickable(false);
            ((MainActivity) getActivity()).fab_data.setOnClickListener(null);
        }
    }

    public void update(List<Boletim> boletim) {
        if (adapter != null) {
            this.boletim = boletim;
            adapter.update(this.boletim);
            setFABListener();
        } else {
            setBoletim(getView());
        }
    }
}
