package com.qacademico.qacademico.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Class.Material;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.MaterialViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;


public class MaterialAdapter extends RecyclerView.Adapter {
    private List<Material> materialList;
    private Context context;

    public MaterialAdapter(List<Material> materialList, Context context) {
        this.materialList = materialList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MaterialViewHolder holder = (MaterialViewHolder) viewHolder;
        Material material = materialList.get(position);

        holder.nome.setText(material.getNomeConteudo());
        holder.data.setText(material.getData());

        if (!material.getDescricao().equals("") || material.getDescricao().equals(material.getNomeConteudo())) {
            holder.description.setText(material.getDescricao());
        } else {
            holder.expandable_description.setVisibility(View.GONE);
            holder.desciption_btn.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable bkg = context.getResources().getDrawable(R.drawable.layout_bg_round);
            bkg.setTint(material.getTint());
            holder.extension_img.setBackground(bkg);
            holder.extension_img.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        }

        holder.extension_img.setImageDrawable(material.getIcon());

        holder.expandable_description.setExpanded(materialList.get(position).getExpanded(), materialList.get(position).getAnim());

        if (holder.expandable_description.isExpanded()) {
            holder.expandable_description.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            holder.expandable_description.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.main_layout.setTag(position);

        Integer pos = (Integer) holder.main_layout.getTag();

        holder.desciption_btn.setOnClickListener(v -> {
            holder.expandable_description.toggle();

            materialList.get(pos).setExpanded(!materialList.get(pos).getExpanded());

            holder.expandable_description.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING) {
                    holder.expandable_description.setBackgroundColor(context.getResources().getColor(R.color.white));
                }

                if (state == ExpandableLayout.State.COLLAPSED) {
                    holder.expandable_description.setBackgroundColor(Color.TRANSPARENT);
                }

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
