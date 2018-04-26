package com.qacademico.qacademico.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.WebView.JavaScriptWebView;

import java.util.List;

public class BoletimAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> {
    private LayoutInflater inflater;
    private List<Boletim> boletim;
    private Resources res;
    private OnHeaderClick onHeaderClick;

    public BoletimAdapter(Context context, List<Boletim> boletim) {
        inflater = LayoutInflater.from(context);
        this.boletim = boletim;
        this.res = context.getResources();
    }

    public void update(List<Boletim> boletim) {
        this.boletim = boletim;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(inflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(inflater.inflate(R.layout.item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(inflater.inflate(R.layout.item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(inflater.inflate(R.layout.item_header_left_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        final TestViewHolder vh = (TestViewHolder) viewHolder;
        row--;
        String primeiraEtapa = "";
        String segundaEtapa = "";

        switch (column) {
            case 1:
                primeiraEtapa = boletim.get(row).getNotaPrimeiraEtapa();
                segundaEtapa = boletim.get(row).getNotaSegundaEtapa();
                break;
            case 2:
                primeiraEtapa = boletim.get(row).getFaltasPrimeiraEtapa();
                segundaEtapa = boletim.get(row).getFaltasSegundaEtapa();
                break;
            case 3:
                primeiraEtapa = boletim.get(row).getRPPrimeiraEtapa();
                segundaEtapa = boletim.get(row).getRPSegundaEtapa();
                break;
            case 4:
                primeiraEtapa = boletim.get(row).getNotaFinalPrimeiraEtapa();
                segundaEtapa = boletim.get(row).getNotaFinalSegundaEtapa();
                break;
        }

        primeiraEtapa = primeiraEtapa.trim();
        segundaEtapa = segundaEtapa.trim();
        vh.tvText1.setVisibility(View.VISIBLE);
        vh.tvText1.setText(primeiraEtapa);
        vh.tvText2.setVisibility(View.VISIBLE);
        vh.tvText2.setText(segundaEtapa);
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
        String[] header = {res.getString(R.string.boletim_Nota), res.getString(R.string.boletim_Faltas),
                res.getString(R.string.boletim_RP), res.getString(R.string.boletim_NotaFinal)};

        vh.tvText.setText(header[column - 1]);  // skip left top header
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        vh.tvText.setText(boletim.get(row - 1).getMateria());
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        vh.tvText.setText(res.getString(R.string.boletim_Materia));
        vh.tvText.setOnClickListener(v -> onHeaderClick.onHeaderClick());
    }

    @Override
    public int getColumnWidth(int column) {
        return res.getDimensionPixelSize(R.dimen.column_width);
    }

    @Override
    public int getHeaderColumnHeight() {
        return res.getDimensionPixelSize(R.dimen.column_header_height);
    }

    @Override
    public int getRowHeight(int row) {
        return res.getDimensionPixelSize(R.dimen.row_height);
    }

    @Override
    public int getHeaderRowWidth() {
        return res.getDimensionPixelSize(R.dimen.row_header_width);
    }

    @Override
    public int getRowCount() {
        return boletim.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }


    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText1;
        TextView tvText2;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText1 = (TextView) itemView.findViewById(R.id.tvText1);
            tvText2 = (TextView) itemView.findViewById(R.id.tvText2);
        }
    }

    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    public void setOnHeaderClick(OnHeaderClick onHeaderClick){
        this.onHeaderClick = onHeaderClick;
    }

    public interface OnHeaderClick {
        void onHeaderClick();
    }
}
