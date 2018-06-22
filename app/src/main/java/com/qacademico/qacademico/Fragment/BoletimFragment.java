package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;
import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class BoletimFragment extends Fragment implements MainActivity.OnPageUpdated {
    SingletonWebView mainWebView = SingletonWebView.getInstance();
    public boolean show_by_semestre = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageFinishedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boletim, container, false);

        setBoletim(view);

        return view;
    }

    private void setBoletim(View view) {
        if (((MainActivity) Objects.requireNonNull(getActivity())).boletimList != null) {

            if (((MainActivity) Objects.requireNonNull(getActivity())).boletimList.size() != 0) {

                ((MainActivity) Objects.requireNonNull(getActivity())).hideEmptyLayout();
                ((MainActivity) Objects.requireNonNull(getActivity())).dismissErrorConnection();

                LinearLayout mContentView = (LinearLayout) view.findViewById(R.id.table_boletim);

                ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();

                ArrayList<String> mfristData = new ArrayList<>();

                mfristData.add(getResources().getString(R.string.boletim_Materia));

                if (show_by_semestre) {

                    Toast.makeText(getContext(), "Sort by period", Toast.LENGTH_SHORT).show();

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

                    for (int i = 0; i < ((MainActivity) Objects.requireNonNull(getActivity())).boletimList.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<>();
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getMateria());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getFaltasPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getRPPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaFinalPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getFaltasSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getRPSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaFinalSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getTfaltas());
                        mTableDatas.add(mRowDatas);
                    }
                } else {

                    Toast.makeText(getContext(), "Sort by type", Toast.LENGTH_SHORT).show();

                    String[] header = {
                            getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                            getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                            getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                            getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                            getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_RP),
                            getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_RP),
                            getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                            getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                            getResources().getString(R.string.boletim_TFaltas)
                    };

                    mfristData.addAll(Arrays.asList(header));

                    mTableDatas.add(mfristData);

                    for (int i = 0; i < ((MainActivity) Objects.requireNonNull(getActivity())).boletimList.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<>();
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getMateria());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getFaltasPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getFaltasSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getRPPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getRPSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaFinalPrimeiraEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getNotaFinalSegundaEtapa());
                        mRowDatas.add(((MainActivity) Objects.requireNonNull(getActivity())).boletimList.get(i).getTfaltas());
                        mTableDatas.add(mRowDatas);
                    }
                }

                final LockTableView mLockTableView = new LockTableView(getContext(), mContentView, mTableDatas);

                mLockTableView
                        .setLockFristColumn(true)
                        .setLockFristRow(true)
                        .setMaxColumnWidth(100)
                        .setMinColumnWidth(60)
                        .setMinRowHeight(20)
                        .setMaxRowHeight(80)
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

            } else {
                ((MainActivity) Objects.requireNonNull(getActivity())).showEmptyLayout();
            }
        } else {
            ((MainActivity) Objects.requireNonNull(getActivity())).showRoundProgressbar();
        }
    }

    public void openDateDialog() {
        if (mainWebView.pg_boletim_loaded && mainWebView.data_boletim != null) {

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
                    .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(getActivity()), R.drawable.ic_date_range_black_24dp,
                            R.string.dialog_date_change, R.color.boletim_dialog))
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
        }
    }

    public void changeColumnMode() {
        setBoletim(getView());
    }

    @Override
    public void onPageUpdate(List<?> list) {
        if (mainWebView.data_boletim != null && mainWebView.periodo_boletim != null) {
            Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity()))
                    .getSupportActionBar()).setTitle(getResources().getString(R.string.title_boletim)
                    + "・" + mainWebView.data_boletim[mainWebView.data_position_boletim] + " / "
                    + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim]); //mostra o ano no título
        }

        ((MainActivity) Objects.requireNonNull(getActivity())).boletimList = (List<Boletim>) list;
        setBoletim(getView());
    }
}
