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
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.holder.report.TableCellMatterViewHolder;
import com.tinf.qmobile.holder.report.TableCellViewHolder;
import com.tinf.qmobile.holder.report.TableColumnHeaderViewHolder;
import com.tinf.qmobile.holder.report.TableColumnMatterHeaderViewHolder;
import com.tinf.qmobile.holder.report.TableRowHeaderViewHolder;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;

import static com.tinf.qmobile.network.Client.pos;

public class ReportTableAdapter extends AbstractTableAdapter<String, Matter, String> {
    private Context context;
    private ArrayList<String> columnHeader;
    private ArrayList<Matter> rowHeader;
    private List<List<String>> cells;

    public ReportTableAdapter(Context context, TableView tableView) {
        this.context = context;
        this.columnHeader = new ArrayList<>();

        columnHeader.add(context.getResources().getString(R.string.boletim_Materia));

        switch (User.getType()) {
            case 0 :
                String[] sem1 = {
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_RP),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_RP),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_TFaltas)
                };
                columnHeader.addAll(Arrays.asList(sem1));
                break;
            case 1:
                String[] bim = {
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_QuartaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_QuartaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_TFaltas)
                };
                columnHeader.addAll(Arrays.asList(bim));
                break;
            case 2:
                String[] uni = {
                        context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_RP),
                        context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_TFaltas)
                };
                columnHeader.addAll(Arrays.asList(uni));
                break;
            case 3:
                String[] sem2 = {
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_TFaltas)
                };
                columnHeader.addAll(Arrays.asList(sem2));
                break;
            case 4:
                String[] bim2 = {
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Conceito),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Conceito),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Conceito),
                        context.getResources().getString(R.string.boletim_QuartaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_QuartaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_QuartaEtapa) + " " + context.getResources().getString(R.string.boletim_NotaFinal),
                        context.getResources().getString(R.string.boletim_QuartaEtapa) + " " + context.getResources().getString(R.string.boletim_Conceito),
                        context.getResources().getString(R.string.boletim_TFaltas)
                };
                columnHeader.addAll(Arrays.asList(bim2));
                break;
            case 5:
                String[] trim = {
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_PrimeiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_SegundaEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Nota),
                        context.getResources().getString(R.string.boletim_TerceiraEtapa) + " " + context.getResources().getString(R.string.boletim_Faltas),
                        context.getResources().getString(R.string.boletim_TFaltas)
                };
                columnHeader.addAll(Arrays.asList(trim));
                break;
        }

        DataObserver observer = data -> {
            List<Matter> matters = new ArrayList<>(DataBase.get().getBoxStore()
                    .boxFor(Matter.class)
                    .query()
                    .order(Matter_.title_)
                    .equal(Matter_.year_, User.getYear(pos))
                    .and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build()
                    .find());

            rowHeader = new ArrayList<>();
            cells = new ArrayList<>();

            for (int i = 0; i < matters.size(); i++) {
                ArrayList<String> row = new ArrayList<>();

                rowHeader.add(matters.get(i));
                row.add(matters.get(i).getTitle());

                int size = matters.get(i).periods.size();

                if (User.getType() == User.Type.BIMESTRE2.get() || User.getType() == User.Type.TRIMESTRE.get())
                    size--;

                for (int j = 0; j < size; j++) {
                    Period period = matters.get(i).periods.get(j);
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
                row.add(matters.get(i).getAbsences());
                cells.add(row);
            }

            setAllItems(columnHeader, rowHeader, cells);
            notifyDataSetChanged();
        };

        DataBase.get().getBoxStore()
                .subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        DataBase.get().getBoxStore()
                .subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        tableView.setTableViewListener(new ITableViewListener() {
            @Override
            public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
                if (column == 0) {
                    Intent intent = new Intent(context, MateriaActivity.class);
                    intent.putExtra("ID", rowHeader.get(row).id);
                    intent.putExtra("PAGE", MateriaActivity.GRADES);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

            }

            @Override
            public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

            }

            @Override
            public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

            }

            @Override
            public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
                Intent intent = new Intent(context, MateriaActivity.class);
                intent.putExtra("ID", rowHeader.get(row).id);
                intent.putExtra("PAGE", MateriaActivity.GRADES);
                context.startActivity(intent);
            }

            @Override
            public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

            }


        });
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
         else
             return new TableCellViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_cell_common, parent, false));
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable String cellItemModel, int columnPosition, int rowPosition) {
        if (columnPosition == 0) {
            TableCellMatterViewHolder h = (TableCellMatterViewHolder) holder;
            h.matter.setText(cells.get(rowPosition).get(columnPosition));
        } else {
            TableCellViewHolder h = (TableCellViewHolder) holder;
            h.text.setText(cells.get(rowPosition).get(columnPosition));
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new TableColumnMatterHeaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.table_column_matter, parent, false));
        else
        return new TableColumnHeaderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.table_column, parent, false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable String columnHeaderItemModel, int columnPosition) {
        if (columnPosition == 0) {
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

        int n = rowHeader.get(rowPosition).getNotSeenCount();

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

}
