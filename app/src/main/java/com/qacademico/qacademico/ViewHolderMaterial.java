package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ViewHolderMaterial extends RecyclerView.ViewHolder {
    final LinearLayout main_layout;
    final ImageView extension_img;
    final TextView nome;
    final TextView data;
    final ExpandableLayout expandable_description;
    final TextView description;
    final ImageButton desciption_btn;
    final ImageButton download_btn;

    public ViewHolderMaterial(View view) {
        super(view);

        main_layout = (LinearLayout) view.findViewById(R.id.materiais_layout);
        extension_img = (ImageView) view.findViewById(R.id.materiais_extension);
        nome = (TextView) view.findViewById(R.id.materiais_nome);
        data = (TextView) view.findViewById(R.id.materiais_data);
        expandable_description = (ExpandableLayout) view.findViewById(R.id.materiais_description_expand);
        description = (TextView) view.findViewById(R.id.materiais_description_txt);
        desciption_btn = (ImageButton) view.findViewById(R.id.materiais_description_btn);
        download_btn = (ImageButton) view.findViewById(R.id.materiais_download_btn);
    }
}
