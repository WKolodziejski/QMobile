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

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;

import static com.tinf.qmobile.network.Client.pos;

public class ReportAdapter extends AbstractTableAdapter<String, Matter, String> implements OnUpdate {
    private Context context;
    private ArrayList<String> columnHeader;
    private ArrayList<Matter> rowHeader;
    private List<List<String>> cells;
    private int selected_row = -1, selected_column = -1;

    public ReportAdapter(Context context, TableView tableView) {
        this.context = context;

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

        if (!matters.isEmpty()) {
            columnHeader.add(context.getResources().getString(R.string.report_matter));
            columnHeader.add(context.getResources().getString(R.string.report_absencesTotal));
            columnHeader.add(context.getResources().getString(R.string.report_meanFinal));

            for (int i = 0; i < matters.get(0).periods.size(); i++) {
                Period period = matters.get(0).periods.get(i);

                if (!period.isSub_()) {

                    columnHeader.add(period.getTitle());

                    boolean skip = false;

                    if (i < matters.get(0).periods.size() - 1) {
                        period = matters.get(0).periods.get(i + 1);

                        if (period.isSub_()) {
                            columnHeader.add(period.getTitle());

                            period = matters.get(0).periods.get(i);

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

            for (int j = 0; j < matters.get(i).periods.size(); j++) {
                Period period = matters.get(i).periods.get(j);

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
        notifyDataSetChanged();
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
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable String cellItemModel, int columnPosition, int rowPosition) {
        if (columnPosition == 0) {
            TableCellMatterViewHolder h = (TableCellMatterViewHolder) holder;
            h.matter.setText(cells.get(rowPosition).get(columnPosition));

        } else if (columnPosition == columnHeader.size() - 1) {
            TableCellSituationViewHolder h = (TableCellSituationViewHolder) holder;
            h.situation.setText(cells.get(rowPosition).get(columnPosition));

        } else {
            TableCellViewHolder h = (TableCellViewHolder) holder;
            h.text.setText(cells.get(rowPosition).get(columnPosition));
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
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable String columnHeaderItemModel, int columnPosition) {
        if (columnPosition == 0 ||  columnPosition == columnHeader.size() - 1) {
            TableColumnMatterHeaderViewHolder h = (TableColumnMatterHeaderViewHolder) holder;
            h.matter.setText(columnHeader.get(columnPosition));
        } else {
            TableColumnHeaderViewHolder h = (TableColumnHeaderViewHolder) holder;
            h.text.setText(columnHeader.get(columnPosition));
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TableRowHeaderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_row, parent, false));
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable Matter rowHeaderItemModel, int rowPosition) {
        TableRowHeaderViewHolder h = (TableRowHeaderViewHolder) holder;
        h.badge.setBackgroundTintList(ColorStateList.valueOf(rowHeader.get(rowPosition).getColor()));

        int n = rowHeader.get(rowPosition).getJournalNotSeenCount();

        if (n > 0) {
            h.badge.setText(String.valueOf(n));
        } else {
            h.badge.setText("");
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

}
