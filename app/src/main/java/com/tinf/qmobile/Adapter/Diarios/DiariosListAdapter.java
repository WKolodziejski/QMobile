package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosListViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.List;

public class DiariosListAdapter extends RecyclerView.Adapter {
    private List<Materia> diariosList;
    private Context context;
    private OnExpandListener onExpandListener;

    public DiariosListAdapter(Context context, List<Materia> diariosList) {
        this.diariosList = diariosList;
        this.context = context;
    }

    public void update(List<Materia> diariosList) {
        this.diariosList = diariosList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_expandable, parent, false);
        return new DiariosListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final DiariosListViewHolder holder = (DiariosListViewHolder) viewHolder;

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        holder.text.setText(diariosList.get(i).getName());
        holder.expand.setExpanded(diariosList.get(i).isExpanded(), diariosList.get(i).isAnim());

        if (diariosList.get(i).isExpanded()){
            holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.expandAct.setBackgroundColor(context.getResources().getColor(diariosList.get(i).getColor()));
            holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimaryLight));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.arrow.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryLight)));
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
            params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                    (int) (0 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
            holder.table.setLayoutParams(params);

            holder.recyclerView.setAdapter(new EtapaAdapter(diariosList.get(i).etapas, context));

            boolean isEmpty = true;

            for (int j = 0; j < diariosList.get(i).etapas.size(); j++) {
                if (!diariosList.get(i).etapas.get(j).diarios.isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }

            holder.nothing.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            holder.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                    RecyclerView.VERTICAL, false));
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

        holder.expandAct.setOnClickListener(v -> {
            holder.expand.toggle();
            holder.arrow.startAnimation(rotate);

            Integer pos = (Integer) holder.table.getTag();

            diariosList.get(pos).setExpanded(!diariosList.get(pos).isExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (diariosList.get(pos).isExpanded()) {
                        holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                if (state == ExpandableLayout.State.EXPANDING && diariosList.get(pos).isExpanded()) {
                    onExpandListener.onExpand(i);
                }

                if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.COLLAPSED) {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e){
                        Log.v("Recycler", e.getMessage());
                    }
                }

                if (diariosList.get(pos).isExpanded()) {
                    holder.expandAct.setBackgroundColor(context.getResources().getColor(diariosList.get(i).getColor()));
                    holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimaryLight));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.arrow.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryLight)));
                    }
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.table.getLayoutParams();
                    params.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                            (int) (0 * context.getResources().getDisplayMetrics().density), (int) (10 * context.getResources().getDisplayMetrics().density));
                    holder.table.setLayoutParams(params);

                    holder.recyclerView.setAdapter(new EtapaAdapter(diariosList.get(i).etapas, context));

                    boolean isEmpty = true;

                    for (int j = 0; j < diariosList.get(i).etapas.size(); j++) {
                        if (!diariosList.get(i).etapas.get(j).diarios.isEmpty()) {
                            isEmpty = false;
                            break;
                        }
                    }

                    holder.nothing.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    holder.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

                    holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                            RecyclerView.VERTICAL, false));
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
            if (diariosList.get(i).etapas.size() > 0) {
                if (diariosList.get(i).isExpanded()) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < diariosList.size(); i++) {
                if (diariosList.get(i).isExpanded()) {
                    diariosList.get(i).setAnim(true);
                } else {
                    diariosList.get(i).setAnim(false);
                }
                diariosList.get(i).setExpanded(false);
            }
            Toast.makeText(context, R.string.message_collapsed, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < diariosList.size(); i++) {
                if (diariosList.get(i).etapas.size() > 0) {
                    if (diariosList.get(i).isExpanded()) {
                        diariosList.get(i).setAnim(false);
                    } else {
                        diariosList.get(i).setAnim(true);
                    }
                    diariosList.get(i).setExpanded(true);
                }
            }
            Toast.makeText(context, R.string.message_expanded, Toast.LENGTH_SHORT).show();
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
