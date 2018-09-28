package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.rmondjone.locktableview.LockTableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.BoxStore;

public class BoletimFragment extends Fragment implements MainActivity.OnPageUpdated {
    boolean lock_header = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boletim, container, false);

        setBoletim(view);

        return view;
    }

    private void setBoletim(View view) {

        List<Materia> materias = Data.loadMaterias(getContext());

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
                //.setOnItemClickListenter((item, position) -> Log.e("点击事件",position+""))
                //.setOnItemLongClickListenter((item, position) -> Log.e("长按事件",position+""))
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

    }

    private BoxStore getBox() {
        return ((MainActivity) getActivity()).getBox();
    }

    public void lockHeader() {
        lock_header = !lock_header;
        setBoletim(getView());
    }

    @Override
    public void onPageUpdate(List<?> list) {
        setBoletim(getView());
    }
}
