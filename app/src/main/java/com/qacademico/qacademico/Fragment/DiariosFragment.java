package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import com.qacademico.qacademico.Adapter.ExpandableListAdapter;
import com.qacademico.qacademico.Class.ExpandableList;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;
import static com.qacademico.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.qacademico.qacademico.Utilities.Utils.URL;

public class DiariosFragment extends Fragment implements MainActivity.OnPageUpdated {
    SingletonWebView webView = SingletonWebView.getInstance();
    ExpandableListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageFinishedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarios, container, false);

        if(webView.infos.data_diarios != null && ((MainActivity) Objects.requireNonNull(getActivity())).diariosList == null) {
            ((MainActivity) Objects.requireNonNull(getActivity())).diariosList = (List<ExpandableList>) Data.loadList(getContext(), Utils.DIARIOS,
                    webView.infos.data_diarios[0], null);
        } else if (((MainActivity) Objects.requireNonNull(getActivity())).navigation.getSelectedItemId() == R.id.navigation_notas) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showErrorConnection();
        }

        setDiarios(view);

        return view;
    }

    private void setDiarios(View view) {
        if (((MainActivity) Objects.requireNonNull(getActivity())).diariosList != null) {

            if (((MainActivity)getActivity()).diariosList.size() != 0) {

                /*Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                        .setTitle(webView.infos.data_diarios[webView.data_position_diarios]);*/
                ((MainActivity) Objects.requireNonNull(getActivity())).showExpandBtn();
                ((MainActivity) Objects.requireNonNull(getActivity())).hideEmptyLayout();
                ((MainActivity) Objects.requireNonNull(getActivity())).dismissErrorConnection();
                ((MainActivity) Objects.requireNonNull(getActivity())).dismissLinearProgressbar();

                RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
                RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                adapter = new ExpandableListAdapter(((MainActivity)getActivity()).diariosList, getActivity(), Utils.DIARIOS);

                recyclerViewDiarios.setAdapter(adapter);
                recyclerViewDiarios.setLayoutManager(layout);
                recyclerViewDiarios.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewDiarios.getContext(),
                        LinearLayoutManager.VERTICAL);
                recyclerViewDiarios.addItemDecoration(dividerItemDecoration);*/

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
                Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                        .setTitle(getResources().getString(R.string.title_diarios));
                ((MainActivity) Objects.requireNonNull(getActivity())).showEmptyLayout();
                ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
            }
        } else {
            Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                    .setTitle(getResources().getString(R.string.title_diarios));
            ((MainActivity) Objects.requireNonNull(getActivity())).showRoundProgressbar();
            ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
        }
    }

    public void openDateDialog() {
        if (webView.infos.data_diarios != null) {

            View theView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

            final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
            year.setMinValue(0);
            year.setMaxValue(webView.infos.data_diarios.length - 1);
            year.setValue(webView.data_position_diarios);
            year.setDisplayedValues(webView.infos.data_diarios);
            year.setWrapSelectorWheel(false);

            NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
            periodo.setVisibility(View.GONE);

            TextView slash = (TextView) theView.findViewById(R.id.slash);
            slash.setVisibility(View.GONE);

            new AlertDialog.Builder(getActivity()).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(getActivity()), R.drawable.ic_date_range_black_24dp,
                            R.string.dialog_date_change, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        if (Utils.isConnected(Objects.requireNonNull(getContext()))) {
                            webView.data_position_diarios = year.getValue();

                            Log.v("Ano selecionado", String.valueOf(
                                    webView.infos.data_diarios[webView.data_position_diarios]));

                            webView.scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                                    + (webView.data_position_diarios + 1) + "].selected = true; document.forms['frmConsultar'].submit();";

                            webView.loadUrl(URL + PG_DIARIOS);

                            Log.i("SCRIPT", "" + webView.scriptDiario);
                        } else {
                            if (Data.loadList(Objects.requireNonNull(getContext()),
                                    Utils.DIARIOS, webView.infos.data_diarios[year.getValue()], null) != null) {

                                webView.data_position_diarios = year.getValue();

                                ((MainActivity)getActivity()).diariosList = (List<ExpandableList>) Data.loadList(Objects.requireNonNull(getContext()),
                                        Utils.DIARIOS, webView.infos.data_diarios[webView.data_position_diarios], null);

                                onPageUpdate(((MainActivity)getActivity()).diariosList);

                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }
    }

    @Override
    public void onPageUpdate(List<?> list) {
        ((MainActivity) Objects.requireNonNull(getActivity())).diariosList = (List<ExpandableList>) list;

        if (adapter != null) {
            adapter.update(((MainActivity)getActivity()).diariosList);
            Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                    .setTitle(webView.infos.data_diarios[webView.data_position_diarios]);
        } else {
            setDiarios(getView());
        }
    }
}
