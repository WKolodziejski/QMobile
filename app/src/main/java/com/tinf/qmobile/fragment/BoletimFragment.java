package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import com.rmondjone.locktableview.LockTableView;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

import static com.tinf.qmobile.App.getBox;
import static com.tinf.qmobile.activity.settings.SettingsActivity.NIGHT;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_BOLETIM;

public class BoletimFragment extends Fragment implements OnUpdate {
    private static String TAG = "BoletimFragment";
    private LockTableView mLockTableView;
    private ArrayList<ArrayList<String>> mTableDatas;
    private List<Matter> materiaList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "New instace created");

        setHasOptionsMenu(true);

        loadData();
    }

    private void loadData() {
        ArrayList<String> mfristData = new ArrayList<>();

        mfristData.add(getResources().getString(R.string.boletim_Materia));

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
                "3 " + getResources().getString(R.string.boletim_Nota),
                "3 " + getResources().getString(R.string.boletim_Faltas),
                "4 " + getResources().getString(R.string.boletim_Nota),
                "4 " + getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_TFaltas)
        };

        String[] uni = {
                getResources().getString(R.string.boletim_Nota),
                getResources().getString(R.string.boletim_Faltas),
                getResources().getString(R.string.boletim_RP),
                getResources().getString(R.string.boletim_NotaFinal),
                getResources().getString(R.string.boletim_TFaltas)
        };

        switch (User.getType()) {
            case 0 : mfristData.addAll(Arrays.asList(sem1));
                break;
            case 1: mfristData.addAll(Arrays.asList(bim));
                break;
            case 2: mfristData.addAll(Arrays.asList(uni));
                break;
            case 3: mfristData.addAll(Arrays.asList(sem2));
                break;
        }

        materiaList = getBox().boxFor(Matter.class).query().order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos)).and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build().find();

        mTableDatas = new ArrayList<>();

        mTableDatas.add(mfristData);

        for (int i = 0; i < materiaList.size(); i++) {
            ArrayList<String> mRowDatas = new ArrayList<>();
            mRowDatas.add(materiaList.get(i).getTitle());

            for (int j = 0; j < materiaList.get(i).periods.size(); j++) {
                Period period = materiaList.get(i).periods.get(j);
                Log.d(TAG, materiaList.get(i).getTitle() + " " + materiaList.get(i).periods.get(j).getTitle());
                mRowDatas.add(period.getGrade());
                mRowDatas.add(period.getAbsences());
                //if (User.getType() == User.Type.SEMESTRE1.get() || User.getType() == User.Type.BIMESTRE.get()) {
                    mRowDatas.add(period.getGradeRP());
                    mRowDatas.add(period.getGradeFinal());
                //}
            }
            mRowDatas.add(materiaList.get(i).getAbsences());
            mTableDatas.add(mRowDatas);
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

            mLockTableView = new LockTableView(getContext(), mContentView, mTableDatas);

            mLockTableView
                    .setLockFristColumn(true)
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
                            startActivity(intent);
                        }
                    })
                    //.setOnItemLongClickListenter((item, position) -> Log.e("长按事件",position+""))
                    .setOnItemSeletor(R.color.colorPrimaryLight)
                    .show();

            mLockTableView.getTableScrollView().setPullRefreshEnabled(false);
            mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
            mLockTableView.getTableScrollView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

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
            mLockTableView.setTableDatas(mTableDatas);
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
