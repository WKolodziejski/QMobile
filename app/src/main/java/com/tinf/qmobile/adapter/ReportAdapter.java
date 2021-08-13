package com.tinf.qmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
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

import static com.tinf.qmobile.network.Client.pos;

public class ReportAdapter extends AbstractTableAdapter<String, Matter, String> implements OnUpdate {
    private final Context context;
    private ArrayList<String> columnHeader;
    private ArrayList<Matter> rowHeader;
    private List<List<String>> cells;
    private int selected_row = -1, selected_column = -1;
    //private OnReport onReport;

    public interface OnReport {
        void onHeader(String[] header);
    }

    public ReportAdapter(Context context, TableView tableView/*, OnReport onReport*/) {
        this.context = context;
        //this.onReport = onReport;

        Client.get().addOnUpdateListener(this);

        DataObserver observer = data -> loadList();

        DataBase.get().getBoxStore().subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        DataBase.get().getBoxStore().subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        tableView.setTableViewListener(new ITableViewListener() {

            @Override
            public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
                if (column == 0) {
                    Intent intent = new Intent(context, MatterActivity.class);
                    intent.putExtra("ID", rowHeader.get(row).id);
                    intent.putExtra("PAGE", MatterActivity.GRADES);
                    context.startActivity(intent);
                }

                tableView.getSelectionHandler().clearSelection();

                Log.d(String.valueOf(column), String.valueOf(row));
            }

            @Override
            public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

            }

            @Override
            public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

            }

            @Override
            public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
                if (column == selected_column) {
                    tableView.getSelectionHandler().clearSelection();
                    selected_column = -1;
                } else {
                    selected_column = column;
                    selected_row = -1;
                }
            }

            @Override
            public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

            }

            @Override
            public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

            }

            @Override
            public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
                if (row == selected_row) {
                    tableView.getSelectionHandler().clearSelection();
                    selected_row = -1;
                } else {
                    selected_row = row;
                    selected_column = -1;
                }
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

        columnHeader = new ArrayList<>();
        rowHeader = new ArrayList<>();
        cells = new ArrayList<>();

        int s = 0;
        int k = 0;

        for (int i = 0; i < matters.size(); i++) {
            int ss = matters.get(i).periods.size();

            if (ss > s) {
                s = ss;
                k = i;
            }
        }

        if (!matters.isEmpty()) {
            columnHeader.add(context.getResources().getString(R.string.report_matter));
            columnHeader.add(context.getResources().getString(R.string.report_absencesTotal));
            columnHeader.add(context.getResources().getString(R.string.report_meanFinal));

            for (int i = 0; i < s; i++) {
                Period period = matters.get(k).periods.get(i);

                if (!period.isSub_()) {

                    columnHeader.add(period.getTitle());

                    boolean skip = false;

                    if (i < s - 1) {
                        period = matters.get(k).periods.get(i + 1);

                        if (period.isSub_()) {
                            columnHeader.add(period.getTitle());

                            period = matters.get(k).periods.get(i);

                            columnHeader.add(context.getResources().getString(R.string.report_mean) + " " + period.getTitle());

                            skip = true;
                        }
                    }

                    columnHeader.add(context.getResources().getString(R.string.report_absences) + " " + period.getTitle());

                    if (skip)
                        i++;
                }
            }

            columnHeader.add(context.getResources().getString(R.string.report_situation));
        }

        for (int i = 0; i < matters.size(); i++) {
            ArrayList<String> row = new ArrayList<>();

            rowHeader.add(matters.get(i));
            row.add(matters.get(i).getTitle());
            row.add(matters.get(i).getAbsences());
            row.add(matters.get(i).getMean());

            for (int j = 0; j < s; j++) {
                Period period = null;

                List<Period> periods = matters.get(i).periods;

                if (!periods.isEmpty()) {
                    if (j < periods.size()) {
                        period = periods.get(j);
                    }
                }

                if (period == null) {
                    period = new Period();
                }

                if (!period.isSub_()) {

                    row.add(period.getGrade());

                    boolean skip = false;

                    if (j < matters.get(i).periods.size() - 1) {
                        period = matters.get(i).periods.get(j + 1);

                        if (period.isSub_()) {
                            row.add(period.getGrade());
                            row.add(matters.get(i).getMean());
                            skip = true;
                        }

                        period = matters.get(i).periods.get(j);

                    }

                    row.add(period.getAbsences());

                    if (skip)
                        j++;
                }
            }

            row.add(matters.get(i).getSituation());

            cells.add(row);
        }
        
        setAllItems(columnHeader, rowHeader, cells);
        //notifyDataSetChanged();
        //onReport.onHeader(columnHeader.toArray(new String[columnHeader.size()]));
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return position;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new TableCellMatterViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_matter, parent, false));

        else if (viewType == columnHeader.size() - 1)
            return new TableCellSituationViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_situation, parent, false));

        else
            return new TableCellViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_cell_common, parent, false));
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable String cellItemModel, int c, int r) {
        if (c == 0) {
            TableCellMatterViewHolder h = (TableCellMatterViewHolder) holder;
            h.binding.matter.setText(cells.get(r).get(c));

        } else if (c == columnHeader.size() - 1) {
            TableCellSituationViewHolder h = (TableCellSituationViewHolder) holder;
            h.binding.situation.setText(cells.get(r).get(c));
            h.binding.situation.setTextColor(getSituationColor(cells.get(r).get(c)));
        } else {
            TableCellViewHolder h = (TableCellViewHolder) holder;
            h.binding.text.setText(cells.get(r).get(c));
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0 || viewType == columnHeader.size() - 1)
            return new TableColumnMatterHeaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_column_matter, parent, false));
        else
            return new TableColumnHeaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_column, parent, false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable String columnHeaderItemModel, int c) {
        if (c == 0 || c == columnHeader.size() - 1) {
            TableColumnMatterHeaderViewHolder h = (TableColumnMatterHeaderViewHolder) holder;
            h.binding.matter.setText(columnHeader.get(c));
        } else {
            TableColumnHeaderViewHolder h = (TableColumnHeaderViewHolder) holder;
            h.binding.text.setText(columnHeader.get(c));
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TableRowHeaderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_row, parent, false));
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable Matter rowHeaderItemModel, int r) {
        TableRowHeaderViewHolder h = (TableRowHeaderViewHolder) holder;
        h.binding.badge.setBackgroundTintList(ColorStateList.valueOf(rowHeader.get(r).getColor()));

        int n = rowHeader.get(r).getJournalNotSeenCount();

        if (n > 0) {
            h.binding.badge.setText(String.valueOf(n));
        } else {
            h.binding.badge.setText("");
        }
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.table_corner, null, false);
    }

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onDateChanged() {
        loadList();
    }

    private int getSituationColor(String s) {
        if (s.contains("Aprovado"))
            return context.getResources().getColor(R.color.approved);

        if (s.contains("Reprovado"))
            return context.getResources().getColor(R.color.disapproved);

        if (s.contains("Falta"))
            return context.getResources().getColor(R.color.disapproved);

        return context.getResources().getColor(R.color.colorPrimary);
    }

}
