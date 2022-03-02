package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.network.Client.pos;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.JournalEmptyBinding;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.report.TableBaseViewHolder;
import com.tinf.qmobile.holder.report.TableCellAbsencesViewHolder;
import com.tinf.qmobile.holder.report.TableCellGradeViewHolder;
import com.tinf.qmobile.holder.report.TableCellMatterViewHolder;
import com.tinf.qmobile.holder.report.TableCellSituationViewHolder;
import com.tinf.qmobile.holder.report.TableCellViewHolder;
import com.tinf.qmobile.holder.report.TableColumnHeaderViewHolder;
import com.tinf.qmobile.holder.report.TableColumnMatterHeaderViewHolder;
import com.tinf.qmobile.holder.report.TableRowHeaderViewHolder;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;

public class ReportAdapter extends AbstractTableAdapter<String, Matter, String> implements OnUpdate {
    private final Context context;
    private ArrayList<String> columnHeader;
    private ArrayList<Matter> rowHeader;
    private List<List<String>> cells;
    private final JournalEmptyBinding empty;

    public ReportAdapter(Context context, TableView tableView, JournalEmptyBinding empty) {
        this.context = context;
        this.empty = empty;

        Client.get().addOnUpdateListener(this);

        DataObserver observer = data -> loadList();

        DataBase.get().getBoxStore().subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
                .observer(observer);

        DataBase.get().getBoxStore().subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        tableView.setTableViewListener(new ITableViewListener() {

            @Override
            public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
                tableView.getSelectionHandler().clearSelection();

                if (column == 0) {
                    Intent intent = new Intent(context, MatterActivity.class);
                    intent.putExtra("ID", rowHeader.get(row).id);
                    intent.putExtra("PAGE", JOURNAL);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

            }

            @Override
            public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

            }

            @Override
            public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
                tableView.getSelectionHandler().clearSelection();
            }

            @Override
            public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

            }

            @Override
            public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

            }

            @Override
            public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
                tableView.getSelectionHandler().clearSelection();
            }

            @Override
            public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
            }

            @Override
            public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
            }
        });
    }

    private void loadList() {
        columnHeader = new ArrayList<>();
        rowHeader = new ArrayList<>();
        cells = new ArrayList<>();

        List<Matter> matters = new ArrayList<>(
                DataBase.get().getBoxStore()
                        .boxFor(Matter.class)
                        .query()
                        .order(Matter_.title_)
                        .equal(Matter_.year_, User.getYear(pos))
                        .and()
                        .equal(Matter_.period_, User.getPeriod(pos))
                        .build()
                        .find());

        int columns = 0;
        int index = 0;

        for (int i = 0; i < matters.size(); i++) {
            int c = matters.get(i).periods.size();

            if (c > columns) {
                columns = c;
                index = i;
            }
        }

        if (!matters.isEmpty()) {
            Matter m = matters.get(index);
            boolean[] subPeriods = new boolean[columns];

            columnHeader.add(context.getResources().getString(R.string.report_matter));
            columnHeader.add(context.getResources().getString(R.string.report_absencesTotal));
            columnHeader.add(context.getResources().getString(R.string.report_meanFinal));

            for (int i = 0; i < columns; i++) {
                Period period = m.periods.get(i);

                subPeriods[i] = period.isSub_();

                columnHeader.add(period.getTitle());

                if (!period.isSub_())
                    columnHeader.add(context.getResources().getString(R.string.report_absences) + " " + period.getTitle());
            }

            columnHeader.add(context.getResources().getString(R.string.report_situation));

            for (int i = 0; i < matters.size(); i++) {
                ArrayList<String> row = new ArrayList<>();
                Matter matter = matters.get(i);

                rowHeader.add(matter);
                row.add(matter.getTitle());
                row.add(matter.getAbsencesString());
                row.add(matter.getMean());

                for (int j = 0; j < columns; j++) {
                    List<Period> periods = matter.periods;

                    Period period;

                    if (j < periods.size()) {
                        period = periods.get(j);
                    } else {
                        period = new Period();

                        if (subPeriods[j])
                            period.setSub();
                    }

                    row.add(period.getGrade());

                    if (!period.isSub_())
                        row.add(period.getAbsences());
                }

                row.add(matter.getSituation());

                cells.add(row);
            }
        }

        setAllItems(columnHeader, rowHeader, cells);
        empty.getRoot().setVisibility(matters.isEmpty() ? View.VISIBLE : View.GONE);
        notifyDataSetChanged();
    }

    @Override
    public int getColumnHeaderItemViewType(int i) {
        return i;
    }

    @Override
    public int getRowHeaderItemViewType(int i) {
        return i;
    }

    @Override
    public int getCellItemViewType(int i) {
        return i;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new TableCellMatterViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_matter, parent, false));

        if (viewType == columnHeader.size() - 1)
            return new TableCellSituationViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_situation, parent, false));

        if (viewType == 1)
            return new TableCellAbsencesViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_absences, parent, false));

        if (viewType == 2)
            return new TableCellGradeViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_grade, parent, false));

        return new TableCellViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_cell_common, parent, false));
    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, String cellItemModel, int c, int r) {
        try {
            ((TableBaseViewHolder) holder).bind(context, rowHeader.get(r), cells.get(r).get(c));
        } catch (Exception ignored) { }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0 || viewType == columnHeader.size() - 1)
            return new TableColumnMatterHeaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_column_matter, parent, false));

        return new TableColumnHeaderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_column, parent, false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, String columnHeaderItemModel, int c) {
        try {
            ((TableBaseViewHolder) holder).bind(context, null, columnHeader.get(c));
        } catch (Exception ignored) { }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        return new TableRowHeaderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_row, parent, false));
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Matter rowHeaderItemModel, int r) {
        try {
            ((TableBaseViewHolder) holder).bind(context, rowHeader.get(r), null);
        } catch (Exception ignored) { }
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.table_corner, null, false);
    }

    @Override
    public void onDateChanged() {
        loadList();
    }

}
