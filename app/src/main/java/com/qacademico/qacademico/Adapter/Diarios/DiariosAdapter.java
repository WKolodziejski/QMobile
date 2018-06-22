package com.qacademico.qacademico.Adapter.Diarios;

import android.content.Context;
import android.content.res.ColorStateList;
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
import android.widget.Toast;

import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.DiariosViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import static java.lang.Integer.valueOf;

public class DiariosAdapter extends RecyclerView.Adapter {
    private List<Diarios> diarios;
    private Context context;
    OnExpandListener onExpandListener;

    public DiariosAdapter(List<Diarios> diarios, Context context) {
        this.diarios = diarios;
        this.context = context;
    }

    public void update(List<Diarios> diarios) {
        this.diarios = diarios;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.table_diarios, parent, false);
        return new DiariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final DiariosViewHolder holder = (DiariosViewHolder) viewHolder;
        Diarios trabalhos = diarios.get(position) ;

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.materia.setText(trabalhos.getNomeMateria());
        holder.expand.setExpanded(diarios.get(position).getExpanded(), diarios.get(position).getAnim());

        if (diarios.get(position).getExpanded()){
            holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.diarios_list));
            holder.materia.setTextColor(context.getResources().getColor(R.color.white));
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

        diarios.get(position).setAnim(false);

        holder.table.setTag(position);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        holder.recyclerView.setAdapter(new EtapaAdapter(trabalhos.getEtapaList(), context));

        holder.recyclerView.setLayoutManager(layout);

        holder.nothing.setVisibility(trabalhos.getEtapaList().isEmpty() ? View.VISIBLE : View.GONE);

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.button.startAnimation(rotate);

            Integer pos = (Integer) holder.table.getTag();

            diarios.get(pos).setExpanded(!diarios.get(pos).getExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (diarios.get(pos).getExpanded()) {
                        holder.button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.button.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && diarios.get(pos).getExpanded()) {
                    onExpandListener.onExpand(position);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }

                if (diarios.get(pos).getExpanded()) {
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.diarios_list));
                    holder.materia.setTextColor(context.getResources().getColor(R.color.white));
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
        return diarios.size();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < diarios.size(); i++) {
            if (diarios.get(i).getEtapaList().size() > 0) {
                if (diarios.get(i).getExpanded()) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < diarios.size(); i++) {
                if (diarios.get(i).getExpanded()) {
                    diarios.get(i).setAnim(true);
                } else {
                    diarios.get(i).setAnim(false);
                }
                diarios.get(i).setExpanded(false);
            }
            Toast.makeText(context, context.getResources().getString(R.string.message_collapsed), Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < diarios.size(); i++) {
                if (diarios.get(i).getEtapaList().size() > 0) {
                    if (diarios.get(i).getExpanded()) {
                        diarios.get(i).setAnim(false);
                    } else {
                        diarios.get(i).setAnim(true);
                    }
                    diarios.get(i).setExpanded(true);
                }
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
