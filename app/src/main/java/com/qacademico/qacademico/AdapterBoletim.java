package com.qacademico.qacademico;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class AdapterBoletim extends RecyclerView.Adapter {
    private List<Boletim> notas;
    private Context context;
    OnExpandListener onExpandListener;

    public AdapterBoletim(List<Boletim> notas, Context context) {
        this.notas = notas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.table_boletim, parent, false);
        ViewHolderBoletim holder = new ViewHolderBoletim(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderBoletim holder = (ViewHolderBoletim) viewHolder;
        Boletim boletim  = notas.get(position) ;

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.materia.setText(boletim.getMateria());
        holder.Tfaltas.setText(boletim.getTfaltas());
        holder.NotaPrimeiraEtapa.setText(boletim.getNotaPrimeiraEtapa());
        holder.FaltasPrimeiraEtapa.setText(boletim.getFaltasPrimeiraEtapa());
        holder.RPPrimeiraEtapa.setText(boletim.getRPPrimeiraEtapa());
        holder.NotaFinalPrimeiraEtapa.setText(boletim.getNotaFinalPrimeiraEtapa());
        holder.NotaSegundaEtapa.setText(boletim.getNotaSegundaEtapa());
        holder.FaltasSegundaEtapa.setText(boletim.getFaltasSegundaEtapa());
        holder.RPSegundaEtapa.setText(boletim.getRPSegundaEtapa());
        holder.NotaFinalSeungaEtapa.setText(boletim.getNotaFinalSeungaEtapa());

        holder.expand.setExpanded(notas.get(position).getExpanded(), notas.get(position).getAnim());

        if (notas.get(position).getExpanded()){
            holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.materia.setTextColor(context.getResources().getColor(R.color.white));
            holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.teal_400));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
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

        notas.get(position).setAnim(false);

        holder.table.setTag(position);

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.button.startAnimation(rotate);

            Integer pos = (Integer) holder.table.getTag();

            notas.get(pos).setExpanded(!notas.get(pos).getExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (notas.get(pos).getExpanded()) {
                        holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.button.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && notas.get(pos).getExpanded()) {
                    onExpandListener.onExpand(position);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }

                if (notas.get(pos).getExpanded()) {
                    holder.materia.setTextColor(context.getResources().getColor(R.color.white));
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.teal_400));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.button.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
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
        return notas.size();
    }

    public void toggleAll() {
        int a = 0;
        int f = 0;
        for (int i = 0; i < notas.size(); i++) {
            if (notas.get(i).getExpanded()) {
                a++;
            } else {
                f++;
            }
        }
        if (a > f) {
            for (int i = 0; i < notas.size(); i++) {
                if (notas.get(i).getExpanded()) {
                    notas.get(i).setAnim(true);
                } else {
                    notas.get(i).setAnim(false);
                }
                notas.get(i).setExpanded(false);
            }
            Toast.makeText(context, context.getResources().getString(R.string.message_collapsed), Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < notas.size(); i++) {
                if (notas.get(i).getExpanded()) {
                    notas.get(i).setAnim(false);
                } else {
                    notas.get(i).setAnim(true);
                }
                notas.get(i).setExpanded(true);
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
