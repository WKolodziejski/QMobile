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
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.HorarioViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class HorarioAdapter extends RecyclerView.Adapter {
    private List<Horario> horarios;
    private Context context;
    OnExpandListener onExpandListener;

    public HorarioAdapter(List<Horario> horarios, Context context) {
        this.horarios = horarios;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.table_horario, parent, false);
        //HorarioViewHolder holder = new HorarioViewHolder(view);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final HorarioViewHolder holder = (HorarioViewHolder) viewHolder;
        final Horario horario  = horarios.get(position) ;

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.dia.setText(horario.getDia());

        holder.expand.setExpanded(horarios.get(position).getExpanded(), horarios.get(position).getAnim());

        if (horarios.get(position).getExpanded()){
            holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.dia.setTextColor(context.getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                Drawable color = context.getResources().getDrawable(R.drawable.layout_bg_header_top);
                color.setTint(context.getResources().getColor(R.color.blue_400));
                holder.expandAct.setBackground(color);
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (10 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density),
                    (int) (10 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);
        } else {
            holder.button.setImageResource(R.drawable.ic_expand_more_black_24dp);
            holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.dia.setTextColor(context.getResources().getColor(R.color.colorAccent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                    (int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);
        }

        horarios.get(position).setAnim(false);

        holder.expand.setTag(position);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        holder.recyclerView.setAdapter(new MateriaAdapter(horario.getMateriasList(), context));

        holder.recyclerView.setLayoutManager(layout);

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.button.startAnimation(rotate);

            Integer pos = (Integer) holder.expand.getTag();

            horarios.get(pos).setExpanded(!horarios.get(pos).getExpanded());

            if (horarios.get(pos).getExpanded()) {
                expandLayoutMargin(holder.table);
            } else {
                collapseLayoutMargin(holder.table);
            }

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (horarios.get(pos).getExpanded()) {
                        holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.button.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && horarios.get(pos).getExpanded()) {
                    onExpandListener.onExpand(position);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }

                if (horarios.get(pos).getExpanded()) {
                    holder.dia.setTextColor(context.getResources().getColor(R.color.white));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        Drawable color = context.getResources().getDrawable(R.drawable.layout_bg_header_top);
                        color.setTint(context.getResources().getColor(R.color.blue_400));
                        holder.expandAct.setBackground(color);
                    }
                } else {
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.dia.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    private void expandLayoutMargin(LinearLayout mView){
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mView.getLayoutParams();
        int margin = (int)(10 * context.getResources().getDisplayMetrics().density);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                params.setMargins((int) (margin * interpolatedTime), (int) (margin * interpolatedTime), (int) (margin * interpolatedTime), (int) (margin * interpolatedTime));
                mView.setLayoutParams(params);
            }
        };
        a.setDuration(250);
        mView.startAnimation(a);
    }

    private void collapseLayoutMargin(LinearLayout mView){
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mView.getLayoutParams();
        int margin = (int) (10 * context.getResources().getDisplayMetrics().density);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                params.setMargins(margin - (int)(margin * interpolatedTime), margin - (int)(margin * interpolatedTime),
                        margin - (int)(margin * interpolatedTime), margin - (int)(margin * interpolatedTime));
                mView.setLayoutParams(params);
            }
        };
        a.setDuration(250);
        mView.startAnimation(a);
    }

    public void toggleAll() {
        int a = 0;
        int f = 0;
        for (int i = 0; i < horarios.size(); i++) {
            if (horarios.get(i).getExpanded()) {
                a++;
            } else {
                f++;
            }
        }
        if (a > f) {
            for (int i = 0; i < horarios.size(); i++) {
                if (horarios.get(i).getExpanded()) {
                    horarios.get(i).setAnim(true);
                } else {
                    horarios.get(i).setAnim(false);
                }
                horarios.get(i).setExpanded(false);
            }
            Toast.makeText(context, context.getResources().getString(R.string.message_collapsed), Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < horarios.size(); i++) {
                if (horarios.get(i).getExpanded()) {
                    horarios.get(i).setAnim(false);
                } else {
                    horarios.get(i).setAnim(true);
                }
                horarios.get(i).setExpanded(true);
            }
            Toast.makeText(context, context.getResources().getString(R.string.message_expanded), Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    public void setOnExpandListener(OnExpandListener onExpandListener){
        this.onExpandListener = onExpandListener;
    }

    public interface OnExpandListener {
        void onExpand(int position);
    }
}