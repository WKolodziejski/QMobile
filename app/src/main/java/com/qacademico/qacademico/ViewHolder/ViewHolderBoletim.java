package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ViewHolderBoletim extends RecyclerView.ViewHolder{
    public final TextView materia;
    public final TextView Tfaltas;
    public final TextView NotaPrimeiraEtapa;
    public final TextView FaltasPrimeiraEtapa;
    public final TextView RPPrimeiraEtapa;
    public final TextView NotaFinalPrimeiraEtapa;
    public final TextView NotaSegundaEtapa;
    public final TextView FaltasSegundaEtapa;
    public final TextView RPSegundaEtapa;
    public final TextView NotaFinalSeungaEtapa;
    public final ExpandableLayout expand;
    public final ImageView button;
    public final LinearLayout expandAct;
    public final LinearLayout table;

    public ViewHolderBoletim(View view) {
        super(view);

        materia = (TextView) view.findViewById(R.id.materiaBoletim);
        Tfaltas = (TextView) view.findViewById(R.id.Tfaltas);
        NotaPrimeiraEtapa = (TextView) view.findViewById(R.id.NotaPrimeiraEtapa);
        FaltasPrimeiraEtapa = (TextView) view.findViewById(R.id.FaltasPrimeiraEtapa);
        RPPrimeiraEtapa = (TextView) view.findViewById(R.id.RPPrimeiraEtapa);
        NotaFinalPrimeiraEtapa = (TextView) view.findViewById(R.id.NotaFinalPrimeiraEtapa);
        NotaSegundaEtapa = (TextView) view.findViewById(R.id.NotaSegundaEtapa);
        FaltasSegundaEtapa = (TextView) view.findViewById(R.id.FaltasSegundaEtapa);
        RPSegundaEtapa = (TextView) view.findViewById(R.id.RPSegundaEtapa);
        NotaFinalSeungaEtapa = (TextView) view.findViewById(R.id.NotaFinalSegundaEtapa);
        expand = (ExpandableLayout) view.findViewById(R.id.expandable_layout_boletim);
        button = (ImageView) view.findViewById(R.id.openBoletim);
        expandAct = (LinearLayout) view.findViewById(R.id.openViewBoletim);
        table = (LinearLayout) view.findViewById(R.id.tables_boletim);
    }
}
