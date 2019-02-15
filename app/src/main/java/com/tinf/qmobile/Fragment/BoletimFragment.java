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
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.rmondjone.locktableview.LockTableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

import static com.tinf.qmobile.Network.OnResponse.PG_BOLETIM;

public class BoletimFragment extends Fragment implements OnUpdate {
    private static String TAG = "BoletimFragment";
    private ArrayList<String> mfristData;
    private LockTableView mLockTableView;
    private ArrayList<ArrayList<String>> mTableDatas;

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

        loadData();
    }

    private void loadData() {
        List<Materia> materiaList = App.getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Client.getYear()).build().find();

        mTableDatas = new ArrayList<>();

        mTableDatas.add(mfristData);

        for (int i = 0; i < materiaList.size(); i++) {
            if (materiaList.get(i).etapas != null) {
                ArrayList<String> mRowDatas = new ArrayList<>();
                mRowDatas.add(materiaList.get(i).getName());

                for (int j = 0; j < materiaList.get(i).etapas.size(); j++) {
                    if (materiaList.get(i).etapas.get(j).getEtapa() == Etapa.Tipo.PRIMEIRA.getInt()
                            || materiaList.get(i).etapas.get(j).getEtapa() == Etapa.Tipo.SEGUNDA.getInt()) {

                        mRowDatas.add(materiaList.get(i).etapas.get(j).getNota());
                        mRowDatas.add(materiaList.get(i).etapas.get(j).getFaltas());
                        mRowDatas.add(materiaList.get(i).etapas.get(j).getNotaRP());
                        mRowDatas.add(materiaList.get(i).etapas.get(j).getNotaFinal());
                    }
                }
                mRowDatas.add(materiaList.get(i).getFaltas());
                mTableDatas.add(mRowDatas);
            }
        }
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

            LinearLayout mContentView = (LinearLayout) view.findViewById(R.id.table_boletim);

            mLockTableView = new LockTableView(getContext(), mContentView, mTableDatas);

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
        });
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_BOLETIM || pg == UPDATE_REQUEST) {
            if (getView() != null) {
                loadData();
                showBoletim(getView());
            }
        }
    }

    @Override
    public void onScrollRequest() {
        if (mLockTableView != null) {
            mLockTableView.getTableScrollView().smoothScrollToPosition(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
    }
}
