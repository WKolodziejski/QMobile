package com.qacademico.qacademico.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.ViewHolderMateriais;

import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.List;

public class AdapterMateriais extends RecyclerView.Adapter {
    private List<Materiais> materiaisList;
    private Context context;

    public AdapterMateriais(List<Materiais> materiaisList, Context context) {
        this.materiaisList = materiaisList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.table_materiais, parent, false);
        return new ViewHolderMateriais(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderMateriais holder = (ViewHolderMateriais) viewHolder;
        Materiais materiais = materiaisList.get(position);

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.materia.setText(materiais.getNomeMateria());

        holder.expand.setExpanded(materiaisList.get(position).getExpanded(), materiaisList.get(position).getAnim());

        if (materiaisList.get(position).getExpanded()){
            holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.materia.setTextColor(context.getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                Drawable color = context.getResources().getDrawable(R.drawable.layout_bg_header_top);
                color.setTint(context.getResources().getColor(R.color.cyan_400));
                holder.expandAct.setBackground(color);
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                    (int) (0 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);
        } else {
            holder.button.setImageResource(R.drawable.ic_expand_more_black_24dp);
            holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.materia.setTextColor(context.getResources().getColor(R.color.colorAccent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                    (int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);
        }

        materiaisList.get(position).setAnim(false);

        holder.expand.setTag(position);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        holder.recyclerView.setAdapter(new AdapterMaterial(materiais.getMaterialList(), context));

        holder.recyclerView.setLayoutManager(layout);

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.button.startAnimation(rotate);

            Integer pos = (Integer) holder.expand.getTag();

            materiaisList.get(pos).setExpanded(!materiaisList.get(pos).getExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (materiaisList.get(pos).getExpanded()) {
                        holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.button.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && materiaisList.get(pos).getExpanded()) {
                    //onExpandListener.onExpand(position);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }

                if (materiaisList.get(pos).getExpanded()) {
                    holder.materia.setTextColor(context.getResources().getColor(R.color.white));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        Drawable color = context.getResources().getDrawable(R.drawable.layout_bg_header_top);
                        color.setTint(context.getResources().getColor(R.color.cyan_400));
                        holder.expandAct.setBackground(color);
                    }
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
                    params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                            (int) (0 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
                    holder.table.setLayoutParams(params);
                } else {
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.materia.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                    }
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
                    params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                            (int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density));
                    holder.table.setLayoutParams(params);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return materiaisList.size();
    }
}
