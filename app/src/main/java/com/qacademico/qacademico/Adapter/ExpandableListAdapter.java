package com.qacademico.qacademico.Adapter;

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

import com.qacademico.qacademico.Adapter.Diarios.EtapaAdapter;
import com.qacademico.qacademico.Adapter.Materiais.MateriaisAdapter;
import com.qacademico.qacademico.Class.Diarios.Etapa;
import com.qacademico.qacademico.Class.ExpandableList;
import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.ViewHolder.ExpandableListViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import static java.lang.Integer.valueOf;

public class ExpandableListAdapter extends RecyclerView.Adapter {
    private List<ExpandableList> list;
    private Context context;
    private String TAG;
    private OnExpandListener onExpandListener;

    public ExpandableListAdapter(List<ExpandableList> list, Context context, String TAG) {
        this.list = list;
        this.context = context;
        this.TAG = TAG;
    }

    public void update(List<ExpandableList> list) {
        this.list = list;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ExpandableListViewHolder holder = (ExpandableListViewHolder) viewHolder;

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.text.setText(list.get(position).getTitle());
        holder.expand.setExpanded(list.get(position).getExpanded(), list.get(position).getAnim());

        if (list.get(position).getExpanded()){
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

        list.get(position).setAnim(false);

        holder.table.setTag(position);

        if (TAG.equals(Utils.DIARIOS)) {
            holder.recyclerView.setAdapter(new EtapaAdapter((List<Etapa>) list.get(position).getList(), context));
            holder.nothing.setVisibility(list.get(position).getList().isEmpty() ? View.VISIBLE : View.GONE);
        } else if (TAG.equals(Utils.MATERIAIS)) {
            holder.recyclerView.setAdapter(new MateriaisAdapter((List<Materiais>) list.get(position).getList(), context));
            holder.nothing.setVisibility(list.get(position).getList().isEmpty() ? View.VISIBLE : View.GONE);
        }

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.arrow.startAnimation(rotate);

            Integer pos = (Integer) holder.table.getTag();

            list.get(pos).setExpanded(!list.get(pos).getExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (list.get(pos).getExpanded()) {
                        holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && list.get(pos).getExpanded()) {
                    onExpandListener.onExpand(position);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", "catch error notify()");
                    }
                }

                if (list.get(pos).getExpanded()) {
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
        return list.size();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getList().size() > 0) {
                if (list.get(i).getExpanded()) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getExpanded()) {
                    list.get(i).setAnim(true);
                } else {
                    list.get(i).setAnim(false);
                }
                list.get(i).setExpanded(false);
            }
            Toast.makeText(context, context.getResources().getString(R.string.message_collapsed), Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getList().size() > 0) {
                    if (list.get(i).getExpanded()) {
                        list.get(i).setAnim(false);
                    } else {
                        list.get(i).setAnim(true);
                    }
                    list.get(i).setExpanded(true);
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
