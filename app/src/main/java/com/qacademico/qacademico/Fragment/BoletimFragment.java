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
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;
import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class BoletimFragment extends Fragment {
    SingletonWebView mainWebView = SingletonWebView.getInstance();
    List<Boletim> boletim;

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

            LinearLayout mContentView = (LinearLayout) view.findViewById(R.id.table_boletim);

            ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();

            ArrayList<String> mfristData = new ArrayList<>();

            mfristData.add(getResources().getString(R.string.boletim_Materia));

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

            for (int i = 0; i < boletim.size(); i++) {
                ArrayList<String> mRowDatas = new ArrayList<>();
                mRowDatas.add(boletim.get(i).getMateria());
                mRowDatas.add(boletim.get(i).getNotaPrimeiraEtapa());
                mRowDatas.add(boletim.get(i).getNotaSegundaEtapa());
                mRowDatas.add(boletim.get(i).getFaltasPrimeiraEtapa());
                mRowDatas.add(boletim.get(i).getFaltasSegundaEtapa());
                mRowDatas.add(boletim.get(i).getRPPrimeiraEtapa());
                mRowDatas.add(boletim.get(i).getRPSegundaEtapa());
                mRowDatas.add(boletim.get(i).getNotaFinalPrimeiraEtapa());
                mRowDatas.add(boletim.get(i).getNotaFinalSegundaEtapa());
                mRowDatas.add(boletim.get(i).getTfaltas());
                mTableDatas.add(mRowDatas);
            }

            final LockTableView mLockTableView = new LockTableView(getContext(), mContentView, mTableDatas);

            mLockTableView.setLockFristColumn(true)
                    .setLockFristRow(true)
                    .setMaxColumnWidth(100)
                    .setMinColumnWidth(60)
                    //.setColumnWidth(1,30)
                    //.setColumnWidth(2,20)
                    .setMinRowHeight(20)
                    .setMaxRowHeight(80)
                    .setTextViewSize(16)
                    .setFristRowBackGroudColor(R.color.boletim_toolbar)
                    .setTableHeadTextColor(R.color.boletim_actionbar)
                    .setTableContentTextColor(R.color.colorAccent)
                    .setNullableString("-")
                    /*.setTableViewListener(new LockTableView.OnTableViewListener() {
                        @Override
                        public void onTableViewScrollChange(int x, int y) {
//                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                        }
                    })//设置横向滚动回调监听
                    .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                        @Override
                        public void onLeft(HorizontalScrollView view) {
                            Log.e("滚动边界","滚动到最左边");
                        }

                        @Override
                        public void onRight(HorizontalScrollView view) {
                            Log.e("滚动边界","滚动到最右边");
                        }
                    })//设置横向滚动边界监听
                    .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                        @Override
                        public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                            Log.e("onRefresh",Thread.currentThread().toString());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                Log.e("现有表格数据", mTableDatas.toString());
                                    //构造假数据
                                    ArrayList<ArrayList<String>> mTableDatas = new ArrayList<ArrayList<String>>();
                                    ArrayList<String> mfristData = new ArrayList<String>();
                                    mfristData.add("标题");
                                    for (int i = 0; i < 10; i++) {
                                        mfristData.add("标题" + i);
                                    }
                                    mTableDatas.add(mfristData);
                                    for (int i = 0; i < 20; i++) {
                                        ArrayList<String> mRowDatas = new ArrayList<String>();
                                        mRowDatas.add("标题" + i);
                                        for (int j = 0; j < 10; j++) {
                                            mRowDatas.add("数据" + j);
                                        }
                                        mTableDatas.add(mRowDatas);
                                    }
                                    mLockTableView.setTableDatas(mTableDatas);
                                    mXRecyclerView.refreshComplete();
                                }
                            }, 1000);
                        }

                        @Override
                        public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                            Log.e("onLoadMore",Thread.currentThread().toString());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mTableDatas.size() <= 60) {
                                        for (int i = 0; i < 10; i++) {
                                            ArrayList<String> mRowDatas = new ArrayList<String>();
                                            mRowDatas.add("标题" + (mTableDatas.size() - 1));
                                            for (int j = 0; j < 10; j++) {
                                                mRowDatas.add("数据" + j);
                                            }
                                            mTableDatas.add(mRowDatas);
                                        }
                                        mLockTableView.setTableDatas(mTableDatas);
                                    } else {
                                        mXRecyclerView.setNoMore(true);
                                    }
                                    mXRecyclerView.loadMoreComplete();
                                }
                            }, 1000);
                        }
                    })*/
                    .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                        @Override
                        public void onItemClick(View item, int position) {
                            Log.e("点击事件",position+"");
                        }
                    })
                    .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                        @Override
                        public void onItemLongClick(View item, int position) {
                            Log.e("长按事件",position+"");
                        }
                    })
                    .setOnItemSeletor(R.color.white)
                    .show();
            mLockTableView.getTableScrollView().setPullRefreshEnabled(false);
            mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
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
                    .setCustomTitle(Utils.customAlertTitle(getActivity(), R.drawable.ic_date_range_black_24dp,
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

    public void update(List<Boletim> boletim) {
        this.boletim = boletim;
        setBoletim(getView());
    }
}
