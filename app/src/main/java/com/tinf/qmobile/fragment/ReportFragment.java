package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.rmondjone.locktableview.LockTableView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.fragment.matter.GradesFragment;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tinf.qmobile.network.OnResponse.PG_BOLETIM;

public class ReportFragment extends Fragment implements OnUpdate {
    private static String TAG = "ReportFragment";
    private LockTableView table;
    private ArrayList<ArrayList<String>> data;
    private List<Matter> materiaList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "New instace created");

        setHasOptionsMenu(true);

        loadData();
    }

    private void loadData() {
        ArrayList<String> header = new ArrayList<>();

        header.add(getResources().getString(R.string.boletim_Materia));

        String[] sem1 = {
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

        String[] sem2 = {
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TFaltas)
        };

        String[] bim = {
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_QuartaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_QuartaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TFaltas)
        };

        String[] bim2 = {
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Conceito),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Conceito),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Conceito),
                getResources().getString(R.string.boletim_QuartaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_QuartaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_QuartaEtapa) + " " + getResources().getString(R.string.boletim_NotaFinal),
                getResources().getString(R.string.boletim_QuartaEtapa) + " " + getResources().getString(R.string.boletim_Conceito),
                getResources().getString(R.string.boletim_TFaltas)
        };

        String[] uni = {
                getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_RP),
                getResources().getString(R.string.boletim_NotaFinal),
                getResources().getString(R.string.boletim_TFaltas)
        };

        String[] trim = {
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_SegundaEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_TerceiraEtapa) + " " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TFaltas)
        };

        switch (User.getType()) {
            case 0 : header.addAll(Arrays.asList(sem1));
                break;
            case 1: header.addAll(Arrays.asList(bim));
                break;
            case 2: header.addAll(Arrays.asList(uni));
                break;
            case 3: header.addAll(Arrays.asList(sem2));
                break;
            case 4: header.addAll(Arrays.asList(bim2));
                break;
            case 5: header.addAll(Arrays.asList(trim));
                break;
        }

        materiaList = DataBase.get().getMatters();

        data = new ArrayList<>();

        data.add(header);

        for (int i = 0; i < materiaList.size(); i++) {
            ArrayList<String> row = new ArrayList<>();
            row.add(materiaList.get(i).getTitle());
            int size = materiaList.get(i).periods.size();

            if (User.getType() == User.Type.BIMESTRE2.get() || User.getType() == User.Type.TRIMESTRE.get())
                size--;

            for (int j = 0; j < size; j++) {
                Period period = materiaList.get(i).periods.get(j);
                row.add(period.getGrade());
                if (j % 2 == 0 || User.getType() != User.Type.BIMESTRE2.get()) {
                    row.add(period.getAbsences());
                }
                if (User.getType() == User.Type.SEMESTRE1.get() || User.getType() == User.Type.UNICO.get()) {
                    row.add(period.getGradeRP());
                    row.add(period.getGradeFinal());
                } else if (User.getType() == User.Type.BIMESTRE2.get() && j % 2 == 0) {
                    row.add(period.getGradeFinal());
                }
            }
            row.add(materiaList.get(i).getAbsences());
            data.add(row);
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

        view.post(() -> {

            LinearLayout mContentView = (LinearLayout) view.findViewById(R.id.table_boletim);

            table = new LockTableView(getContext(), mContentView, data);

            table.setLockFristColumn(true)
                    .setLockFristRow(true)
                    .setMinColumnWidth(32)
                    .setMaxColumnWidth(96)
                    .setMinRowHeight(32)
                    .setMaxRowHeight(96)
                    .setTextViewSize(15)
                    .setCellPadding(8)
                    .setFristRowBackGroudColor(R.color.colorPrimaryLight)
                    .setTableHeadTextColor(R.color.colorPrimary)
                    .setTableContentTextColor(R.color.colorPrimary)
                    .setNullableString("")
                    .setOnItemClickListenter((item, position) -> {
                        if (position != 0) {
                            Intent intent = new Intent(getContext(), MateriaActivity.class);
                            intent.putExtra("ID", materiaList.get(position - 1).id);
                            intent.putExtra("PAGE", MateriaActivity.GRADES);
                            startActivity(intent);
                        }
                    })
                    //.setOnItemLongClickListenter((item, position) -> Log.e("长按事件",position+""))
                    .setOnItemSeletor(R.color.colorPrimaryLight)
                    .show();

            table.getTableScrollView().setPullRefreshEnabled(false);
            table.getTableScrollView().setLoadingMoreEnabled(false);
            table.getTableScrollView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

            table.getTableScrollView().addOnScrollListener(new RecyclerView.OnScrollListener(){
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
            ((MainActivity) getActivity()).fab.hide();
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_list);
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_BOLETIM || pg == UPDATE_REQUEST) {
            loadData();
            table.setTableDatas(data);
        }
    }

    @Override
    public void onScrollRequest() {
        if (table != null) {
            table.getTableScrollView().smoothScrollToPosition(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

}
