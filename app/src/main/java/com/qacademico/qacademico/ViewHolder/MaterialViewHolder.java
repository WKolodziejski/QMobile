package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import net.cachapa.expandablelayout.ExpandableLayout;

public class MaterialViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout main_layout;
    public final ImageView extension_img;
    public final TextView nome;
    public final TextView data;
    public final ExpandableLayout expandable_description;
    public final TextView description;
    public final ImageButton desciption_btn;
    public final ImageButton download_btn;

    public MaterialViewHolder(View view) {
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
