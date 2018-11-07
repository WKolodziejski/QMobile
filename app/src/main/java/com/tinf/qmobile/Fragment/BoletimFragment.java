package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Fragment.ViewPager.NotasFragment;
import com.tinf.qmobile.R;
import com.rmondjone.locktableview.LockTableView;
import com.tinf.qmobile.WebView.SingletonWebView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.BoxStore;

public class BoletimFragment extends Fragment {
    private boolean lock_header = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boletim, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showBoletim(view);
    }

    private void showBoletim(View view) {

        SingletonWebView webView = SingletonWebView.getInstance();

        List<Materia> materias = getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position])).build().find();

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
            if (materias.get(i).etapas != null) {
                ArrayList<String> mRowDatas = new ArrayList<>();
                mRowDatas.add(materias.get(i).getName());

                for(int j = 0; j < materias.get(i).etapas.size(); j++) {
                    if (materias.get(i).etapas.get(j).getEtapa() == R.string.diarios_PrimeiraEtapa) {

                        mRowDatas.add(materias.get(i).etapas.get(j).getNota());
                        mRowDatas.add(materias.get(i).etapas.get(j).getFaltas());
                        mRowDatas.add(materias.get(i).etapas.get(j).getNotaRP());
                        mRowDatas.add(materias.get(i).etapas.get(j).getNotaFinal());
                    } else if ((materias.get(i).etapas.get(j).getEtapa() == R.string.diarios_SegundaEtapa)) {

                        mRowDatas.add(materias.get(i).etapas.get(j).getNota());
                        mRowDatas.add(materias.get(i).etapas.get(j).getFaltas());
                        mRowDatas.add(materias.get(i).etapas.get(j).getNotaRP());
                        mRowDatas.add(materias.get(i).etapas.get(j).getNotaFinal());
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

        Button lock_header_btn = (Button) view.findViewById(R.id.lock_header);
        lock_header_btn.setOnClickListener(v -> {
            lockHeader();
        });

        mLockTableView.setTableViewListener((x, y) -> {
            if (!lock_header) {
                lock_header_btn.setVisibility(x == 0 ? View.VISIBLE : View.GONE);
            }
        });

    }

    private BoxStore getBox() {
        return ((MainActivity) getActivity()).getBox();
    }

    private void lockHeader() {
        lock_header = !lock_header;
        showBoletim(getView());
    }
}
