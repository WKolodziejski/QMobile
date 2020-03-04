package com.tinf.qmobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.evrencoskun.tableview.TableView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.adapter.ReportAdapter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tinf.qmobile.network.Client.pos;

public class ReportFragment extends Fragment implements OnUpdate {
    @BindView(R.id.report_table)    TableView table;
    //@BindView(R.id.fab_report)      ExtendedFloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(User.getYears()[pos]);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        table.setAdapter(new ReportAdapter(getContext(), table));
        table.getCellRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                ((MainActivity) getActivity()).refreshLayout.setEnabled(p == 0);
                /*if (dy < 0 && !fab.isShown())
                    fab.show();
                else if(dy > 0 && fab.isShown())
                    fab.hide();*/
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        /*String[] list = User.getReportList(getContext());
        boolean[] checked = new boolean[list.length];

        for (int i = 0; i < checked.length; i++)
            checked[i] = true;

        fab.setOnClickListener(view1 -> new MaterialAlertDialogBuilder(getActivity())
                .setMultiChoiceItems(list, checked, (dialogInterface, i, isChecked) -> {
                    if (isChecked) {
                        table.showColumn(i);
                    } else {
                        table.hideColumn(i);
                    }
                    checked[i] = isChecked;
                })
                .setCancelable(true)
                .create()
                .show());*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_list);
    }

    @Override
    public void onScrollRequest() {
        if (table != null) {
            table.scrollToRowPosition(0);
            table.scrollToColumnPosition(0);
        }
    }

    @Override
    public void onDateChanged() {
        getActivity().setTitle(User.getYears()[pos]);
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

    @Override
    public void onDetach() {
        super.onDetach();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Client.get().addOnUpdateListener(this);
    }

}
