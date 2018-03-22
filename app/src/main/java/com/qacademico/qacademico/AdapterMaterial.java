package com.qacademico.qacademico;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;


public class AdapterMaterial extends RecyclerView.Adapter {
    private List<Material> materialList;
    private Context context;

    public AdapterMaterial(List<Material> materialList, Context context) {
        this.materialList = materialList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_material, parent, false);
        return new ViewHolderMaterial(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderMaterial holder = (ViewHolderMaterial) viewHolder;
        Material material = materialList.get(position);

        holder.nome.setText(material.getNomeConteudo());
        holder.data.setText(material.getData());

        if (!material.getDescricao().equals("")) {
            holder.description.setText(material.getDescricao());
        } else {
            holder.description_layout.setVisibility(View.GONE);
        }

        int color = context.getResources().getColor(R.color.colorAccent);
        Drawable img = context.getResources().getDrawable(R.drawable.ic_file);

        if (material.getExtension().equals("pdf")) {
            color = context.getResources().getColor(R.color.red_500);
            img = context.getResources().getDrawable(R.drawable.ic_pdf);
        } else if (material.getExtension().equals("docx") || material.getExtension().equals("doc")) {
            color = context.getResources().getColor(R.color.blue_500);
            img = context.getResources().getDrawable(R.drawable.ic_docs);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable bkg = context.getResources().getDrawable(R.drawable.layout_bg_round);
            bkg.setTint(color);
            holder.extension_img.setBackground(bkg);
            holder.extension_img.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        }

        holder.extension_img.setImageDrawable(img);

        holder.expandable_description.setExpanded(materialList.get(position).getExpanded(), materialList.get(position).getAnim());
        holder.expandable_body.setExpanded(!materialList.get(position).getExpanded(), materialList.get(position).getAnim());

        holder.main_layout.setTag(position);

        Integer pos = (Integer) holder.main_layout.getTag();

        holder.desciption_btn.setOnClickListener(v -> {
            holder.expandable_description.toggle();
            holder.expandable_body.toggle();

            materialList.get(pos).setExpanded(!materialList.get(pos).getExpanded());

            holder.expandable_description.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }
            });
        });

        holder.download_btn.setOnClickListener(v -> {

        });

    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }
}
