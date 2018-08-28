package com.tinf.qacademico.Adapter.Diarios;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
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

import com.tinf.qacademico.Class.Diarios.DiariosList;
import com.tinf.qacademico.R;
import com.tinf.qacademico.ViewHolder.ExpandableListViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import static java.lang.Integer.valueOf;

public class DiariosListAdapter extends RecyclerView.Adapter {
    private List<DiariosList> diariosList;
    private Context context;
    private OnExpandListener onExpandListener;

    public DiariosListAdapter(List<DiariosList> diariosList, Context context) {
        this.diariosList = diariosList;
        this.context = context;
    }

    public void update(List<DiariosList> diariosList) {
        this.diariosList = diariosList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_expandable, parent, false);
        return new ExpandableListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ExpandableListViewHolder holder = (ExpandableListViewHolder) viewHolder;

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.text.setText(diariosList.get(i).getTitle());
        holder.expand.setExpanded(diariosList.get(i).getExpanded(), diariosList.get(i).getAnim());

        if (diariosList.get(i).getExpanded()){
            holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
            holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimaryLight));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.arrow.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryLight)));
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                    (int) (0 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);
        } else {
            holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
            holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.arrow.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                    (int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);
        }

        diariosList.get(i).setAnim(false);

        holder.table.setTag(i);

        holder.recyclerView.setAdapter(new EtapaAdapter(diariosList.get(i).getEtapas(), context));
        holder.nothing.setVisibility(diariosList.get(i).getEtapas().isEmpty() ? View.VISIBLE : View.GONE);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.arrow.startAnimation(rotate);

            Integer pos = (Integer) holder.table.getTag();

            diariosList.get(pos).setExpanded(!diariosList.get(pos).getExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (diariosList.get(pos).getExpanded()) {
                        holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && diariosList.get(pos).getExpanded()) {
                    onExpandListener.onExpand(i);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }

                if (diariosList.get(pos).getExpanded()) {
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
                    holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.arrow.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryLight)));
                    }
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
                    params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                            (int) (0 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
                    holder.table.setLayoutParams(params);
                } else {
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.arrow.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
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
        return diariosList.size();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < diariosList.size(); i++) {
            if (diariosList.get(i).getEtapas().size() > 0) {
                if (diariosList.get(i).getExpanded()) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < diariosList.size(); i++) {
                if (diariosList.get(i).getExpanded()) {
                    diariosList.get(i).setAnim(true);
                } else {
                    diariosList.get(i).setAnim(false);
                }
                diariosList.get(i).setExpanded(false);
            }
            Toast.makeText(context, context.getResources().getString(R.string.message_collapsed), Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < diariosList.size(); i++) {
                if (diariosList.get(i).getEtapas().size() > 0) {
                    if (diariosList.get(i).getExpanded()) {
                        diariosList.get(i).setAnim(false);
                    } else {
                        diariosList.get(i).setAnim(true);
                    }
                    diariosList.get(i).setExpanded(true);
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
