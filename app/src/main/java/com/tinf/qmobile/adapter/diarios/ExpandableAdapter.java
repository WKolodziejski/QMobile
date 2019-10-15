package com.tinf.qmobile.adapter.diarios;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.DiariosListViewHolder;
import com.tinf.qmobile.holder.ExpandableViewHolder;
import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.model.matter.Matter;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter {
    private List<Matter> matters;
    private Context context;
    private DiariosListAdapter.OnExpandListener onExpandListener;
    private View.OnClickListener open, expand;
    private RotateAnimation rotate;

    public ExpandableAdapter(Context context, List<Matter> matters, View.OnClickListener open) {
        this.context = context;
        this.matters = matters;
        this.open = open;
        this.expand = expand;

        rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpandableViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_expandable_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ExpandableViewHolder holder = (ExpandableViewHolder) viewHolder;

        holder.title.setText(matters.get(i).getTitle());
        holder.expand.setExpanded(matters.get(i).isExpanded, matters.get(i).shouldAnimate);
        holder.badge.setBackgroundTintList(ColorStateList.valueOf(matters.get(i).getColor()));

        setView(holder, i);

        holder.details.setTag(i);
        holder.layout.setTag(i);

        holder.details.setTextColor(matters.get(i).getColor());

        holder.details.setOnClickListener(open);
        holder.layout.setOnClickListener(view -> {
            holder.expand.toggle();
            holder.arrow.startAnimation(rotate);

            Integer pos = (Integer) view.getTag();

            matters.get(pos).isExpanded = !matters.get(pos).isExpanded;

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (matters.get(pos).isExpanded) {
                        holder.arrow.setImageResource(R.drawable.ic_less);
                    } else {
                        holder.arrow.setImageResource(R.drawable.ic_more);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

            Integer pos = (Integer) holder.layout.getTag();
            setView(holder, pos);

            if (state == ExpandableLayout.State.EXPANDED && matters.get(pos).isExpanded) {
                onExpandListener.onExpand(pos);
            }
        });
    }

    private void setView(ExpandableViewHolder holder, int i) {
        if (matters.get(i).isExpanded) {
            if (matters.get(i).periods.size() > 0) {

                List<Journal> diarios = matters.get(i).periods.get(getLast(i)).journals;

                if (diarios.isEmpty()) {
                    holder.arrow.setImageResource(R.drawable.ic_less);
                    holder.title.setTextColor(matters.get(i).getColor());
                    //holder.nothing.setVisibility(View.VISIBLE);
                    //holder.open.setVisibility(View.GONE);
                    holder.recyclerView.setVisibility(View.GONE);
                } else {
                    holder.recyclerView.post(() -> {
                        JournalAdapter2 adapter = new JournalAdapter2(context, diarios);
                        adapter.setHasStableIds(true);

                        //holder.nothing.setVisibility(View.GONE);

                        //holder.open.setVisibility(View.VISIBLE);

                        holder.recyclerView.setHasFixedSize(true);
                        holder.recyclerView.setItemViewCacheSize(20);
                        holder.recyclerView.setDrawingCacheEnabled(true);
                        holder.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        holder.recyclerView.setAdapter(adapter);
                        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        holder.recyclerView.setVisibility(View.VISIBLE);

                        holder.arrow.setImageResource(R.drawable.ic_less);
                        holder.title.setTextColor(matters.get(i).getColor());
                    });
                }
            } else {
                holder.arrow.setImageResource(R.drawable.ic_less);
                holder.title.setTextColor(matters.get(i).getColor());
                //holder.nothing.setVisibility(View.VISIBLE);
                //holder.open.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        } else {
            holder.arrow.setImageResource(R.drawable.ic_more);
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.recyclerView.removeAllViewsInLayout();
        }
    }

    private int getLast(int i) {
        int k = 0;
        for (int j = 0; j < matters.get(i).periods.size(); j++) {
            if (!matters.get(i).periods.get(j).journals.isEmpty()) {
                k = j;
            }
        }
        return k;
    }

    public void update(List<Matter> materiaList){
        this.matters = materiaList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return matters.size();
    }

    @Override
    public long getItemId(int i) {
        return matters.get(i).hashCode();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < matters.size(); i++) {
            if (!matters.get(i).periods.get(getLast(i)).journals.isEmpty()) {
                if (matters.get(i).isExpanded) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < matters.size(); i++) {
                matters.get(i).shouldAnimate = matters.get(i).isExpanded;
                matters.get(i).isExpanded = false;
                notifyItemChanged(i);
            }
            Toast.makeText(context, R.string.diarios_collapsed, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < matters.size(); i++) {
                if (!matters.get(i).periods.get(getLast(i)).journals.isEmpty()) {
                    matters.get(i).shouldAnimate = !matters.get(i).isExpanded;
                    matters.get(i).isExpanded = true;
                    notifyItemChanged(i);
                }
            }
            Toast.makeText(context, R.string.diarios_expanded, Toast.LENGTH_SHORT).show();
        }
        //notifyDataSetChanged();
    }

    public void setOnExpandListener(DiariosListAdapter.OnExpandListener onExpandListener){
        this.onExpandListener = onExpandListener;
    }

    public interface OnExpandListener {
        void onExpand(int position);
    }

}
