package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.rmondjone.locktableview.LockTableView;
import com.tinf.qmobile.Utilities.User;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataSubscriptionList;

public class BoletimFragment extends Fragment {
    private static String TAG = "BoletimFragment";
    private ArrayList<String> mfristData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "New instace created");

        mfristData = new ArrayList<>();

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boletim, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "View created");
        showBoletim(view);
    }

    private void showBoletim(View view) {

        view.post(() -> {

            Query<Materia> query = App.getBox().boxFor(Materia.class).query().order(Materia_.name)
                    .equal(Materia_.year, Client.getYear()).build();

            ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();

            query.subscribe(new DataSubscriptionList()).observer(data -> {

                mTableDatas.add(mfristData);

                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).etapas != null) {
                        ArrayList<String> mRowDatas = new ArrayList<>();
                        mRowDatas.add(data.get(i).getName());

                        for (int j = 0; j < data.get(i).etapas.size(); j++) {
                            if (data.get(i).etapas.get(j).getEtapa() == Etapa.Tipo.PRIMEIRA.getInt()
                                    || data.get(i).etapas.get(j).getEtapa() == Etapa.Tipo.SEGUNDA.getInt()) {

                                mRowDatas.add(data.get(i).etapas.get(j).getNota());
                                mRowDatas.add(data.get(i).etapas.get(j).getFaltas());
                                mRowDatas.add(data.get(i).etapas.get(j).getNotaRP());
                                mRowDatas.add(data.get(i).etapas.get(j).getNotaFinal());
                            }
                        }
                        mRowDatas.add(data.get(i).getFaltas());
                        mTableDatas.add(mRowDatas);
                    }
                }
            });

            LinearLayout mContentView = (LinearLayout) view.findViewById(R.id.table_boletim);

            LockTableView mLockTableView = new LockTableView(getContext(), mContentView, mTableDatas);

            mLockTableView
                    .setLockFristColumn(true)
                    .setLockFristRow(true)
                    .setMaxColumnWidth(96)
                    .setMinColumnWidth(24)
                    .setMinRowHeight(32)
                    .setMaxRowHeight(64)
                    .setTextViewSize(15)
                    .setCellPadding(8)
                    .setFristRowBackGroudColor(R.color.colorAccent)
                    .setTableHeadTextColor(R.color.white)
                    .setTableContentTextColor(R.color.colorAccent)
                    .setNullableString("-")
                    //.setOnItemClickListenter((item, position) -> Log.e("点击事件",position+""))
                    //.setOnItemLongClickListenter((item, position) -> Log.e("长按事件",position+""))
                    .setOnItemSeletor(R.color.white)
                    .show();

            mLockTableView.getTableScrollView().setPullRefreshEnabled(false);
            mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);

            mLockTableView.getTableScrollView().addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(p == 0);
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            ((NotasFragment) getParentFragment()).setOnTopScrollRequestedBListener(() -> {
                mLockTableView.getTableScrollView().smoothScrollToPosition(0);
            });
        });
    }

}
