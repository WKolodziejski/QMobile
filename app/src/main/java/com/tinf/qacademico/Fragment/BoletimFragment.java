package com.tinf.qacademico.Fragment;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.tinf.qacademico.WebView.SingletonWebView;
import com.rmondjone.locktableview.LockTableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BoletimFragment extends Fragment implements MainActivity.OnPageUpdated {
    SingletonWebView webView = SingletonWebView.getInstance();
    public List<Materia> materias;
    public boolean lock_header = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boletim, container, false);

        if (webView.infos.data_boletim != null && webView.infos.periodo_boletim != null) {
            materias = Data.loadMaterias(getContext(), webView.infos.data_boletim[webView.data_position_boletim]);
        }

        setBoletim(view);

        return view;
    }

    private void setBoletim(View view) {
        if (materias != null) {

            if (materias.size() != 0) {

                LinearLayout mContentView = (LinearLayout) view.findViewById(R.id.table_boletim);

                ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();

                ArrayList<String> mfristData = new ArrayList<>();

                mfristData.add(getResources().getString(R.string.boletim_Materia));

                String[] header = {
                        getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                        getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                        getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_RP),
                        getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                        getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                        getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                        getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_RP),
                        getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                        getResources().getString(R.string.boletim_TFaltas)
                };

                mfristData.addAll(Arrays.asList(header));

                mTableDatas.add(mfristData);

                for (int i = 0; i < materias.size(); i++) {
                    if (materias.get(i).getEtapas() != null) {
                        ArrayList<String> mRowDatas = new ArrayList<>();
                        mRowDatas.add(materias.get(i).getName());
                        if (materias.get(i).getEtapas().size() > 0) {
                            mRowDatas.add(materias.get(i).getEtapas().get(0).getNota());
                            mRowDatas.add(materias.get(i).getEtapas().get(0).getFaltas());
                            mRowDatas.add(materias.get(i).getEtapas().get(0).getNotaRP());
                            mRowDatas.add(materias.get(i).getEtapas().get(0).getNotaFinal());
                            if (materias.get(i).getEtapas().size() > 1) {
                                mRowDatas.add(materias.get(i).getEtapas().get(1).getNota());
                                mRowDatas.add(materias.get(i).getEtapas().get(1).getFaltas());
                                mRowDatas.add(materias.get(i).getEtapas().get(1).getNotaRP());
                                mRowDatas.add(materias.get(i).getEtapas().get(1).getNotaFinal());
                            }
                        }
                        mRowDatas.add(materias.get(i).getTotalFaltas());
                        mTableDatas.add(mRowDatas);
                    }
                }


                LockTableView mLockTableView = new LockTableView(getContext(), mContentView, mTableDatas);

                mLockTableView
                        .setLockFristColumn(lock_header)
                        .setLockFristRow(true)
                        .setMaxColumnWidth(96)
                        .setMinColumnWidth(32)
                        .setMinRowHeight(16)
                        .setMaxRowHeight(64)
                        .setTextViewSize(15)
                        .setFristRowBackGroudColor(R.color.colorAccent)
                        .setTableHeadTextColor(R.color.white)
                        .setTableContentTextColor(R.color.colorAccent)
                        .setNullableString("-")
                        .setOnItemClickListenter((item, position) -> Log.e("点击事件",position+""))
                        .setOnItemLongClickListenter((item, position) -> Log.e("长按事件",position+""))
                        .setOnItemSeletor(R.color.white)
                        .show();
                mLockTableView.getTableScrollView().setPullRefreshEnabled(false);
                mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);

                Button lock_header_btn = (Button) view.findViewById(R.id.lock_header);
                lock_header_btn.setOnClickListener(v -> {
                    lockHeader();
                });
                
                mLockTableView.setTableViewListener((x, y) -> {
                    if (!lock_header) {
                        lock_header_btn.setVisibility(x == 0 ? View.VISIBLE : View.GONE);
                    }
                });

            } else {
                Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                        .setTitle(getResources().getString(R.string.title_boletim));
            }
        } else {
            Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                    .setTitle(getResources().getString(R.string.title_boletim));
        }
    }

    /*public void openDateDialog() {
        if (webView.infos.data_boletim != null) {

            View theView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

            final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
            year.setMinValue(0);
            year.setMaxValue(webView.infos.data_boletim.length - 1);
            year.setValue(webView.data_position_boletim);
            year.setDisplayedValues(webView.infos.data_boletim);
            year.setWrapSelectorWheel(false);

            final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
            periodo.setMinValue(0);
            periodo.setMaxValue(webView.infos.periodo_boletim.length - 1);
            periodo.setValue(webView.periodo_position_boletim);
            periodo.setDisplayedValues(webView.infos.periodo_boletim);
            periodo.setWrapSelectorWheel(false);

            new AlertDialog.Builder(getActivity()).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(getActivity()), R.drawable.ic_date_range_black_24dp,
                            R.string.dialog_date_change, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        if (Utils.isConnected(Objects.requireNonNull(getContext()))) {
                            webView.data_position_boletim = year.getValue();
                            webView.periodo_position_boletim = periodo.getValue();

                            if (webView.data_position_boletim == Integer.parseInt(webView.infos.data_boletim[0])) {

                                webView.loadUrl(URL + PG_BOLETIM);

                            } else {
                                webView.loadUrl(URL + PG_BOLETIM + "&COD_MATRICULA=-1&cmbanos="
                                        + webView.infos.data_boletim[webView.data_position_boletim]
                                        + "&cmbperiodos=1&Exibir+Boletim");
                            }
                        } else {
                            if (Data.loadList(Objects.requireNonNull(getContext()),
                                    Utils.BOLETIM, webView.infos.data_boletim[year.getValue()],
                                    webView.infos.periodo_boletim[periodo.getValue()]) != null) {

                                webView.data_position_boletim = year.getValue();
                                webView.periodo_position_boletim = periodo.getValue();

                                materias = (List<Boletim>) Data.loadList(Objects.requireNonNull(getContext()),
                                        Utils.BOLETIM, webView.infos.data_boletim[webView.data_position_boletim],
                                        webView.infos.periodo_boletim[webView.periodo_position_boletim]);

                                onPageUpdate(materias);

                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }
    }*/

    public void lockHeader() {
        lock_header = !lock_header;
        setBoletim(getView());
    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
