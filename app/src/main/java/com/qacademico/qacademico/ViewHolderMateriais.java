package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ViewHolderMateriais extends RecyclerView.ViewHolder {
    final TextView materia;
    final ExpandableLayout expand;
    final ImageView button;
    final LinearLayout expandAct;
    final RecyclerView recyclerView;
    final LinearLayout table;

    public ViewHolderMateriais(View view) {
        super(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_material);
        materia = (TextView) view.findViewById(R.id.materia);
        expand = (ExpandableLayout) view.findViewById(R.id.expandable_layout_materiais);
        button = (ImageView) view.findViewById(R.id.openMateriais);
        expandAct = (LinearLayout) view.findViewById(R.id.openViewMateriais);
        table = (LinearLayout) view.findViewById(R.id.tables_materiais);
    }
}
