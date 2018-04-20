package com.qacademico.qacademico.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.R;

import java.util.List;

public class BoletimAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> {
    private LayoutInflater inflater;
    private String[][] boletim;
    private Resources res;

    public BoletimAdapter(Context context, String[][] boletim) {
        inflater = LayoutInflater.from(context);
        this.boletim = boletim;
        this.res = context.getResources();
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
        String itemData = boletim[row - 1][column]; // skip headers

        if (TextUtils.isEmpty(itemData)) {
            itemData = "";
        }

        itemData = itemData.trim();
        vh.tvText.setVisibility(View.VISIBLE);
        vh.tvText.setText(itemData);
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
        String[] header = {res.getString(R.string.boletim_Nota), res.getString(R.string.boletim_Faltas),
                res.getString(R.string.boletim_NotaFinal), res.getString(R.string.boletim_RP)};

        vh.tvText.setText(header[column - 1]);  // skip left top header
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        vh.tvText.setText(boletim[row - 1][0]);
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        vh.tvText.setText(res.getString(R.string.boletim_Materia));
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
        return boletim.length + 1;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }


    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;
        View vLine;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            vLine = itemView.findViewById(R.id.vLine);
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
}
